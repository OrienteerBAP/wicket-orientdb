package ru.ydn.wicket.wicketorientdb.rest;

import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.resource.SharedResourceReference;

import okhttp3.Credentials;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import ru.ydn.wicket.wicketorientdb.IOrientDbSettings;
import ru.ydn.wicket.wicketorientdb.LazyAuthorizationRequestCycleListener;
import ru.ydn.wicket.wicketorientdb.OrientDbWebApplication;
import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

/**
 * Bridge to OrientDB REST API
 */
public class OrientDBHttpAPIResource extends ReverseProxyResource {
	
	private static final long serialVersionUID = 1L;
	public static final String MOUNT_PATH = "/orientdb";
	public static final String ORIENT_DB_KEY=OrientDBHttpAPIResource.class.getSimpleName();
	@Override
	protected HttpUrl getBaseUrl(Attributes attributes) {
		return HttpUrl.get(OrientDbWebApplication.get().getOrientDbSettings().getOrientDBRestApiUrl());
	}
	
	@Override
	protected void onMapUrl(Attributes attributes, HttpUrl.Builder builder) {
		builder.setPathSegment(1, OrientDbWebSession.get().getDatabaseSession().getName());
	}
	
	@Override
	protected void onMapHeaders(Attributes attributes, Headers.Builder builder) {
		if(builder.get(LazyAuthorizationRequestCycleListener.AUTHORIZATION_HEADER)==null) {
			String username;
			String password;
			OrientDbWebSession session = OrientDbWebSession.get();
			if(session.isSignedIn())
			{
				username = session.getUsername();
				password = session.getPassword();
			}
			else
			{
				IOrientDbSettings settings = OrientDbWebApplication.get().getOrientDbSettings();
				username = settings.getGuestUserName();
				password = settings.getGuestPassword();
			}
			builder.add(LazyAuthorizationRequestCycleListener.AUTHORIZATION_HEADER, Credentials.basic(username, password));
		}
		builder.add("Accept-Encoding", "identity");
	}
	
	@Override
	protected boolean enforceContentLength() {
		return true;
	}
	
	public static void mountOrientDbRestApi(WebApplication app)
	{
		mountOrientDbRestApi(new OrientDBHttpAPIResource(), app);
	}
	
	public static void mountOrientDbRestApi(WebApplication app, String... mountPaths) {
		mountOrientDbRestApi(new OrientDBHttpAPIResource(), app, mountPaths);
	}
	
	/**
	 * Mounts OrientDB REST API Bridge to an app
	 * @param resource {@link LegacyOrientDBHttpAPIResource} to mount
	 * @param app {@link WebApplication} to mount to
	 */
	public static void mountOrientDbRestApi(OrientDBHttpAPIResource resource, WebApplication app) {
		mountOrientDbRestApi(resource, app, MOUNT_PATH);
	}
	
	/**
	 * Mounts OrientDB REST API Bridge to an app
	 * @param resource {@link LegacyOrientDBHttpAPIResource} to mount
	 * @param app {@link WebApplication} to mount to
	 * @param mountPaths array of paths to mount to
	 */
	public static void mountOrientDbRestApi(OrientDBHttpAPIResource resource, WebApplication app, String... mountPaths)
	{
		app.getSharedResources().add(ORIENT_DB_KEY, resource);
		 for (String mountPath : mountPaths) {
			 app.mountResource(mountPath, new SharedResourceReference(ORIENT_DB_KEY));
		}
	}
	
	
}
