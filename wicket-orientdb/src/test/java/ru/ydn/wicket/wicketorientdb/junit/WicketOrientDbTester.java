package ru.ydn.wicket.wicketorientdb.junit;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.mock.MockHttpServletRequest;
import org.apache.wicket.protocol.http.mock.MockHttpServletResponse;
import org.apache.wicket.request.Url;
import org.apache.wicket.util.crypt.Base64;
import org.apache.wicket.util.tester.WicketTester;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.OMetadata;
import com.orientechnologies.orient.core.metadata.schema.OSchema;

import ru.ydn.wicket.wicketorientdb.LazyAuthorizationRequestCycleListener;
import ru.ydn.wicket.wicketorientdb.OrientDbWebApplication;
import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

public class WicketOrientDbTester extends WicketTester
{

	public WicketOrientDbTester(OrientDbWebApplication application)
	{
		super(application);
	}

	@Override
	public OrientDbWebApplication getApplication() {
		return (OrientDbWebApplication) super.getApplication();
	}
	
	@Override
	public OrientDbWebSession getSession()
	{
		return (OrientDbWebSession)super.getSession();
	}
	
	public ODatabaseDocument getDatabase()
	{
		return getSession().getDatabase();
	}
	
	public OMetadata getMetadata()
	{
		return getDatabase().getMetadata();
	}
	
	public OSchema getSchema()
	{
		return getMetadata().getSchema();
	}
	
	public boolean signIn(String username, String password)
	{
		return getSession().signIn(username, password);
	}
	
	public void signOut()
	{
		getSession().signOut();
	}
	
	public boolean isSignedIn()
	{
		return getSession().isSignedIn();
	}
	
	public String executeUrl(String _url, final String method, final String content) throws Exception
	{
		return executeUrl(_url, method, content, null, null);
	}
	
	public String executeUrl(String _url, final String method, final String content, String username, String password) throws Exception
	{
		MockHttpServletRequest request = new MockHttpServletRequest(getApplication(), getHttpSession(), getServletContext())
		{
			{
				setMethod(method);
			}

			@Override
			public ServletInputStream getInputStream() throws IOException {
				if(content==null) return super.getInputStream();
				else
				{
					final StringReader sr = new StringReader(content);
					return new ServletInputStream() {
						@Override
						public int read() throws IOException {
							return sr.read();
						}
					};
				}
			}
		};
		
		Url url = Url.parse(_url, Charset.forName(request.getCharacterEncoding()));
		request.setUrl(url);
		request.setMethod(method);
		if(username!=null && password!=null)
		{
			request.setHeader(LazyAuthorizationRequestCycleListener.AUTHORIZATION_HEADER, "Basic "+Base64.encodeBase64String((username+":"+password).getBytes()));
		}
		processRequest(request);
		MockHttpServletResponse response = getLastResponse();
		int status = response.getStatus();
		if(status>=HttpServletResponse.SC_OK+100)
		{
			throw new IOException("Code: "+response.getStatus()+" Message: "+response.getErrorMessage()+" Content: "+response.getDocument());
		}
		else
		{
			return response.getDocument();
		}
	}
}
