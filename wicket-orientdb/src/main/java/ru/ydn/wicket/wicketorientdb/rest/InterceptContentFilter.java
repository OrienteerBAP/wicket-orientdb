package ru.ydn.wicket.wicketorientdb.rest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.util.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility filter to catch content prior wicket
 * 
 * @deprecated Should be fixed in Wicket 7.6.0
 */
@Deprecated
public class InterceptContentFilter implements Filter{
	
	public static final String HTTP_REQUEST_ATTR_CONTENT = "intercepted-content";

	private static final Logger LOG = LoggerFactory.getLogger(InterceptContentFilter.class);
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		if(!(request instanceof HttpServletRequest)) chain.doFilter(request, response);
		else {
			HttpServletRequest httpServletRequest = (HttpServletRequest)request;
			String method = httpServletRequest.getMethod();
			if("post".equalsIgnoreCase(method) || "put".equalsIgnoreCase(method)) {
				InputStream is = request.getInputStream();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				IOUtils.copy(is, baos);
				request.setAttribute(HTTP_REQUEST_ATTR_CONTENT, baos.toByteArray());
				chain.doFilter(request, response);
				request.removeAttribute(HTTP_REQUEST_ATTR_CONTENT);
			} else {
				 chain.doFilter(request, response);
			}
		}
		
	}

	@Override
	public void destroy() {
		
	}
	
	public static byte[] getContent(ServletRequest request) {
		return (byte[]) request.getAttribute(HTTP_REQUEST_ATTR_CONTENT);
	}
	
	public static boolean isContentPresent(ServletRequest request) {
		return getContent(request)!=null;
	}
	
	public static InputStream getContentAsInputStream(ServletRequest request) {
		byte[] content = getContent(request);
		return new ByteArrayInputStream(content!=null?content: new byte[0]);
	}

}
