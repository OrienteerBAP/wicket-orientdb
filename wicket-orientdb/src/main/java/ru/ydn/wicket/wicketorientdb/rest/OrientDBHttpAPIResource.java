package ru.ydn.wicket.wicketorientdb.rest;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Authenticator;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.AbstractResource;
import org.apache.wicket.request.resource.SharedResourceReference;
import org.apache.wicket.util.encoding.UrlEncoder;
import org.apache.wicket.util.io.IOUtils;
import org.apache.wicket.util.lang.Args;
import org.apache.wicket.util.string.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orientechnologies.orient.server.OServer;
import com.orientechnologies.orient.server.network.OServerNetworkListener;
import com.orientechnologies.orient.server.network.protocol.http.ONetworkProtocolHttpAbstract;

import ru.ydn.wicket.wicketorientdb.IOrientDbSettings;
import ru.ydn.wicket.wicketorientdb.OrientDbWebApplication;
import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;


public class OrientDBHttpAPIResource extends AbstractResource
{
	public static final String MOUNT_PATH = "/orientdb";
	public static final String ORIENT_DB_KEY=OrientDBHttpAPIResource.class.getSimpleName();
	
	private static final Logger LOG = LoggerFactory.getLogger(OrientDBHttpAPIResource.class);
	
	@SuppressWarnings("restriction")
	private static class MultiUserCache implements sun.net.www.protocol.http.AuthCache{
	     public void put(String pkey, sun.net.www.protocol.http.AuthCacheValue value){

	     }
	     public sun.net.www.protocol.http.AuthCacheValue get(String pkey, String skey){
	         return null;
	     }
	     public void remove(String pkey, sun.net.www.protocol.http.AuthCacheValue entry){

	     }
	}
	
	
	@Override
	protected ResourceResponse newResourceResponse(Attributes attributes) {
		final WebRequest request = (WebRequest) attributes.getRequest();
		final HttpServletRequest httpRequest = (HttpServletRequest) request.getContainerRequest();
		final PageParameters params = attributes.getParameters();
		final ResourceResponse response = new ResourceResponse();
		if(response.dataNeedsToBeWritten(attributes))
		{
			String orientDbHttpURL = OrientDbWebApplication.get().getOrientDbSettings().getOrientDBRestApiUrl();
			if(orientDbHttpURL!=null)
			{
				
				StringBuilder sb = new StringBuilder(orientDbHttpURL);
				
				for(int i=0; i<params.getIndexedCount();i++)
				{
					//replace provided database name
					String segment = i==1?OrientDbWebSession.get().getDatabase().getName():params.get(i).toString();
					sb.append(UrlEncoder.PATH_INSTANCE.encode(segment, "UTF8")).append('/');
				}
				if(sb.charAt(sb.length()-1)=='/')sb.setLength(sb.length()-1);
				String queryString = request.getUrl().getQueryString();
				if(!Strings.isEmpty(queryString)) sb.append('?').append(queryString);
				
				final String url = sb.toString();
				final StringWriter sw = new StringWriter();
				final PrintWriter out = new PrintWriter(sw);
				HttpURLConnection con=null;
				try
				{
					URL orientURL = new URL(url);
					con = (HttpURLConnection)orientURL.openConnection();
					con.setDoInput(true);
					con.setUseCaches(false);
					
					String method = httpRequest.getMethod();
					con.setRequestMethod(method);
					con.setUseCaches(false);
					if("post".equalsIgnoreCase(method) || "put".equalsIgnoreCase(method))
					{
						con.setDoOutput(true);
						IOUtils.copy(httpRequest.getInputStream(), con.getOutputStream());
					}
					IOUtils.copy(con.getInputStream(), out, "UTF-8");
					response.setStatusCode(con.getResponseCode());
					response.setContentType(con.getContentType());
				} catch (IOException e)
				{
					LOG.error("Can't communicate with OrientDB REST", e);
					if(con!=null)
					{
						try
						{
							response.setError(con.getResponseCode(), con.getResponseMessage());
							InputStream errorStream = con.getErrorStream();
							if(errorStream!=null) IOUtils.copy(errorStream, out, "UTF-8");
						} catch (IOException e1)
						{
							LOG.error("Can't response by error", e1);
						}
					}
				}
				finally
				{
					if(con!=null)con.disconnect();
				}
				response.setWriteCallback(new WriteCallback() {
					
					@Override
					public void writeData(Attributes attributes) throws IOException {
						attributes.getResponse().write(sw.toString());
					}
				});
			}
			else
			{
				response.setError(HttpServletResponse.SC_BAD_GATEWAY, "OrientDB REST URL is not specified");
			}
		}
		return response;
	}
	
	public static void mountOrientDbRestApi(WebApplication app)
	{
		mountOrientDbRestApi(new OrientDBHttpAPIResource(), app);
	}
	
	@SuppressWarnings("restriction")
	public static void mountOrientDbRestApi(OrientDBHttpAPIResource resource, WebApplication app)
	{
		app.getSharedResources().add(ORIENT_DB_KEY, resource);
		app.mountResource(MOUNT_PATH, new SharedResourceReference(ORIENT_DB_KEY));
		Authenticator.setDefault(new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
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
					username = settings.getDBUserName();
					password = settings.getDBUserPassword();
				}
				return new PasswordAuthentication (username, password.toCharArray());
			}
			
		});
		 CookieHandler.setDefault(new PersonalCookieManager());
		 sun.net.www.protocol.http.AuthCacheValue.setAuthCache(new MultiUserCache());
	}

}
