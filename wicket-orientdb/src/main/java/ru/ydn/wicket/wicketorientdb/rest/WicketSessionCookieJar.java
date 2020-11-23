package ru.ydn.wicket.wicketorientdb.rest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.wicket.util.io.IClusterable;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

/**
 * OKHttp {@link CookieJar} which separates cookies per user's sessions 
 */
public class WicketSessionCookieJar implements CookieJar {
	
	public static final String OK_HTTP_COOKIES = "OKHttpClientCookies";
	
	protected static class SessionCookies extends HashMap<String, Map<String, String>> implements IClusterable {
		public SessionCookies() {
			
		}
		
		public SessionCookies(Map<String, Map<String, String>> map) {
			super(map);
		}
		
		public SessionCookies addCookies(HttpUrl url, List<Cookie> cookies) {
			Map<String, String> cookiesMap = get(url.toString());
			if(cookiesMap==null) {
				cookiesMap = new HashMap<String, String>();
				put(url.toString(), cookiesMap);
			}
			
			for (Cookie cookie : cookies) {
				cookiesMap.put(cookie.name(), cookie.toString());
			}
			return this;
		}
		
		public List<Cookie> getCookies(HttpUrl url) {
			Map<String, String> cookiesMap = get(url.toString());
			if(cookiesMap==null) return Collections.EMPTY_LIST;
			
			List<Cookie> ret = new ArrayList<Cookie>();
			cookiesMap.forEach((name, cookie) -> {
				ret.add(Cookie.parse(url, cookie));
			});
			return ret;
		}
	}
	
	protected SessionCookies getSessionCookies() {
		OrientDbWebSession session = OrientDbWebSession.get();
		SessionCookies sessionCookies = (SessionCookies) session.getAttribute(OK_HTTP_COOKIES);
		if(sessionCookies==null) {
			sessionCookies = new SessionCookies();
			setSessionCookies(sessionCookies);
		}
		return sessionCookies;
	}
	
	protected void setSessionCookies(SessionCookies sessionCookies) {
		OrientDbWebSession session = OrientDbWebSession.get();
		session.setAttribute(OK_HTTP_COOKIES, sessionCookies);
	}
	
	protected void setSessionCookies(Map<String, Map<String, String>> map) {
		setSessionCookies(map == null || map instanceof SessionCookies?(SessionCookies)map: new SessionCookies(map));
	}

	@Override
	public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
		setSessionCookies(getSessionCookies()
								.addCookies(url, cookies));
	}

	@Override
	public List<Cookie> loadForRequest(HttpUrl url) {
		return getSessionCookies().getCookies(url);
	}

}
