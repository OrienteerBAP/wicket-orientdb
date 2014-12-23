package ru.ydn.wicket.wicketorientdb.rest;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.wicket.Session;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;

public class PersonalCookieManager extends CookieHandler
{
	private Cache<String, CookieManager> cache = CacheBuilder.newBuilder()
													.maximumSize(100)
													.expireAfterAccess(15, TimeUnit.MINUTES)
														.build(new CacheLoader<String, CookieManager>() {
															@Override
															public CookieManager load(
																	String key)
																	throws Exception {
																return new CookieManager();
															}
														});
	
	@Override
	public Map<String, List<String>> get(URI uri,
			Map<String, List<String>> requestHeaders) throws IOException {
		return getPersonalCookieManager().get(uri, requestHeaders);
	}

	@Override
	public void put(URI uri, Map<String, List<String>> responseHeaders)
			throws IOException {
		getPersonalCookieManager().put(uri, responseHeaders);
	}
	
	protected CookieManager getPersonalCookieManager()
	{
		try
		{
			OrientDbWebSession session = OrientDbWebSession.get();
			session.bind();
			String id = session.getId();
			if(session.isSignedIn()) id=session.getUsername()+'-'+id;
			return cache.get(id, new Callable<CookieManager>() {

				@Override
				public CookieManager call() throws Exception {
					return new CookieManager();
				}
			});
		} catch (ExecutionException e)
		{
			throw new IllegalStateException("Cookie Manager should be always calculated");
		}
	}

}
