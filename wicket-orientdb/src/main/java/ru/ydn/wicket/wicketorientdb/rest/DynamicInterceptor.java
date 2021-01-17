package ru.ydn.wicket.wicketorientdb.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.connection.RealCall;
import okhttp3.internal.http.RealInterceptorChain;

/**
 * OkHttp {@link Interceptor} which allows dynamical interceptors linked to a request 
 */
public class DynamicInterceptor implements Interceptor {
	
	public static final DynamicInterceptor INSTANCE = new DynamicInterceptor();
	
	static class DynamicInterceptorCollection {
		List<Interceptor> interceptors = new ArrayList<Interceptor>();
		List<Interceptor> networkInterceptors = new ArrayList<Interceptor>();
		Boolean userIntercpetorsHandled = false;
	}

	@Override
	public Response intercept(Chain chain) throws IOException {
		final DynamicInterceptorCollection collection = chain.request().tag(DynamicInterceptorCollection.class);
		if(collection==null 
				|| (collection.userIntercpetorsHandled && collection.networkInterceptors.isEmpty())
				|| (!collection.userIntercpetorsHandled && collection.interceptors.isEmpty())) {
			if(collection!=null) collection.userIntercpetorsHandled=true;
			return chain.proceed(chain.request());
		}
		final RealInterceptorChain original = (RealInterceptorChain) chain;
		List<Interceptor> interceptors = new ArrayList<Interceptor>();
		interceptors.addAll(collection.userIntercpetorsHandled?collection.networkInterceptors:collection.interceptors);
		interceptors.add(new Interceptor() {

			@Override
			public Response intercept(Chain chain) throws IOException {
				collection.userIntercpetorsHandled = true;
				return original.proceed(chain.request());
			}
			
		});
		RealInterceptorChain subChain = new RealInterceptorChain((RealCall)original.call(), 
																  interceptors, 
																  0, 
																  original.getExchange$okhttp(), 
																  original.request(),
																  original.connectTimeoutMillis(),
																  original.readTimeoutMillis(),
																  original.writeTimeoutMillis());
		/*RealInterceptorChain subChain = new RealInterceptorChain(interceptors, 
																 original.transmitter(), 
																 original.exchange(),
																 0,
																 original.request(),
																 original.call(),
																 original.connectTimeoutMillis(),
																 original.readTimeoutMillis(),
																 original.writeTimeoutMillis());*/
		return subChain.proceed(original.request());
	}
	
	public static void addInterceptors(Request.Builder requestBuilder, Interceptor... interceptors) {
		lookupOrCreateCollection(requestBuilder).interceptors.addAll(Arrays.asList(interceptors));
	}
	
	public static void addNetworkInterceptors(Request.Builder requestBuilder, Interceptor... interceptors) {
		lookupOrCreateCollection(requestBuilder).networkInterceptors.addAll(Arrays.asList(interceptors));
	}
	
	private static DynamicInterceptorCollection lookupOrCreateCollection(Request.Builder requestBuilder) {
		DynamicInterceptorCollection collection = requestBuilder.build().tag(DynamicInterceptorCollection.class);
		if(collection==null) {
			collection = new DynamicInterceptorCollection();
			requestBuilder.tag(DynamicInterceptorCollection.class, collection);
		}
		return collection;
	}

}
