package ru.ydn.wicket.wicketorientdb.rest;

import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.wicket.request.HttpHeaderCollection;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.AbstractResource;
import org.apache.wicket.util.io.IOUtils;

import lombok.experimental.ExtensionMethod;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpMethod;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;
import ru.ydn.wicket.wicketorientdb.OrientDbWebApplication;
import ru.ydn.wicket.wicketorientdb.utils.LombokExtensions;

/**
 * Wicket Resource  for providing reverse-proxy functionalities
 */

@ExtensionMethod({LombokExtensions.class})
@Slf4j
public class ReverseProxyResource extends AbstractResource {
	
	private static final Set<String> HEADERS_TO_BLOCK = 
					new HashSet<String>(Arrays.asList("Set-Cookie".toLowerCase()));
	
	static {
		HEADERS_TO_BLOCK.addAll(INTERNAL_HEADERS);
	}
	
	private String baseUrlStr;
	private transient HttpUrl baseUrl;

	public ReverseProxyResource() {
	}
	
	public ReverseProxyResource(String baseUrlStr) {
		this.baseUrlStr = baseUrlStr;
	}

	@Override
	protected ResourceResponse newResourceResponse(Attributes attributes) {
		final ResourceResponse response = new ResourceResponse();
		if(response.dataNeedsToBeWritten(attributes))
		{
			OkHttpClient okHttpClient = obtainOkHttpClient();
			Request request = mapRequest(attributes);
			Response okHttpResponse;
			try {
				okHttpResponse = okHttpClient.newCall(request).execute();
			} catch (IOException e) {
				String message = "Can't communicate with "+request.url();
				log.error(message, e);
				response.setError(HttpServletResponse.SC_BAD_GATEWAY, message);
				return response;
			}
			if(okHttpResponse.isSuccessful()) {
				response.setStatusCode(okHttpResponse.code());
			} else {
				response.setError(okHttpResponse.code(), okHttpResponse.message());
			}
			final ResponseBody okHttpResponseBody = okHttpResponse.body();
			response.setContentType(okHttpResponseBody.contentType().toString());
			Map<String, List<String>> headersMap = okHttpResponse.headers().toMultimap();
			HttpHeaderCollection headersToreturn = response.getHeaders();
			for (Map.Entry<String, List<String>> headerEntry: headersMap.entrySet()) {
				if(HEADERS_TO_BLOCK.contains(headerEntry.getKey())) continue;
				for (String value : headerEntry.getValue()) {
					headersToreturn.addHeader(headerEntry.getKey(), value);
				}
			}
			response.setWriteCallback(new WriteCallback() {
				@Override
				public void writeData(Attributes attributes) throws IOException {
					IOUtils.copy(okHttpResponseBody.byteStream(), attributes.getResponse().getOutputStream());
				}
			});
		}
		return response;
	}
	
	protected OkHttpClient obtainOkHttpClient() {
		return OrientDbWebApplication.get().getOrientDbSettings().getOkHttpClient();
	}
	
	protected HttpUrl getBaseUrl(Attributes attributes) {
		if(baseUrl==null) {
			if(baseUrlStr==null) throw new IllegalStateException("BaseUrl was not specified");
			baseUrl = HttpUrl.get(baseUrlStr);
		}
		return baseUrl;
	}
	
	protected HttpUrl mapUrl(Attributes attributes) {
		HttpUrl.Builder builder = getBaseUrl(attributes).newBuilder();
		WebRequest webRequest = (WebRequest)attributes.getRequest();
		Url url = webRequest.getUrl();
		PageParameters pageParameters = attributes.getParameters();
		for(int i=0; i<pageParameters.getIndexedCount();i++) {
			builder.addPathSegment(pageParameters.get(i).toString());
		}
//		builder.addEncodedPathSegments(url.getPath());
		builder.fragment(url.getFragment());
		builder.encodedQuery(webRequest.asHttpServletRequest().getQueryString());
		onMapUrl(attributes, builder);
		return builder.build();
	}
	
	protected void onMapUrl(Attributes attributes, HttpUrl.Builder builder) {
		
	}
	
	protected RequestBody mapRequestBody(final Attributes attributes, final String method) {
		if(!HttpMethod.permitsRequestBody(method)) return null;
		String contentType = attributes.getRequest().asHttpServletRequest().getContentType();
		final MediaType mediaType = contentType!=null?MediaType.parse(contentType):null;
		if(!enforceContentLength() 
				|| attributes.getRequest().asHttpServletRequest().getContentLength()>=0) {
			return new RequestBody() {
			
				@Override
				public void writeTo(BufferedSink sink) throws IOException {
					try (Source source = Okio.source(attributes.getRequest().asHttpServletRequest().getInputStream())) {
			          sink.writeAll(source);
			        }
				}
				
				@Override
				public MediaType contentType() {
					return mediaType;
				}
				
				@Override
				public long contentLength() throws IOException {
					return attributes.getRequest().asHttpServletRequest().getContentLength();
				}
			};
		} else {
			try {
				byte[] data = IOUtils.toByteArray(attributes.getRequest().asHttpServletRequest().getInputStream());
				return RequestBody.create(mediaType, data);
			} catch (IOException e) {
				throw new IllegalStateException("Can't read data from input request", e);
			}
		}
	}
	
	protected boolean enforceContentLength() {
		return false;
	}
	
	protected Headers mapHeaders(Attributes attributes) {
		Headers.Builder builder = new Headers.Builder();
		HttpServletRequest request = attributes.getRequest().asHttpServletRequest();
		Enumeration<String> headerNames = request.getHeaderNames();
		while(headerNames.hasMoreElements()) {
			String headerName = headerNames.nextElement();
			Enumeration<String> headerValues = request.getHeaders(headerName);
			while(headerValues.hasMoreElements()) {
				builder.add(headerName, headerValues.nextElement());
			}
		}
		builder.removeAll("Host");
		onMapHeaders(attributes, builder);
		return builder.build();
	}
	
	protected void onMapHeaders(Attributes attributes, Headers.Builder builder) {
		
	}
	
	protected Request mapRequest(Attributes attributes) {
		Request.Builder builder = new Request.Builder();
		builder.headers(mapHeaders(attributes));
		builder.url(mapUrl(attributes));
		String method = attributes.getRequest().asHttpServletRequest().getMethod();
		builder.method(method, mapRequestBody(attributes, method));
		if(isDebugLoggingEnabled(attributes)) {
			DynamicInterceptor.addNetworkInterceptors(builder, new HttpLoggingInterceptor(log::info)
					.setLevel(HttpLoggingInterceptor.Level.BODY));
		}
		onMapRequest(attributes, builder);
		return builder.build();
	}
	
	protected void onMapRequest(Attributes attributes, Request.Builder builder) {
		
	}
	
	protected boolean isDebugLoggingEnabled(Attributes attributes) {
		return attributes.getRequest().getRequestParameters().getParameterValue("_debug").toBoolean(false);
	}

}
