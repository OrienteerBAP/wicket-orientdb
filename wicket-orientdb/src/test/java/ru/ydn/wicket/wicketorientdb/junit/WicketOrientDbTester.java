package ru.ydn.wicket.wicketorientdb.junit;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.wicket.protocol.http.mock.MockHttpServletRequest;
import org.apache.wicket.protocol.http.mock.MockHttpServletResponse;
import org.apache.wicket.request.Url;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.util.tester.WicketTester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orientechnologies.orient.core.db.ODatabaseDocumentInternal;
import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.OMetadata;
import com.orientechnologies.orient.core.metadata.schema.OSchema;

import junit.framework.AssertionFailedError;
import ru.ydn.wicket.wicketorientdb.LazyAuthorizationRequestCycleListener;
import ru.ydn.wicket.wicketorientdb.OrientDbWebApplication;
import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

public class WicketOrientDbTester extends WicketTester
{
	private static final Logger LOG = LoggerFactory.getLogger(WicketOrientDbTester.class);

	public WicketOrientDbTester(OrientDbWebApplication application)
	{
		this(application, null, null);
	}
	
	public WicketOrientDbTester(OrientDbWebApplication application, String username, String password)
	{
		super(application);
		if(!Strings.isEmpty(username) && !Strings.isEmpty(password)) signIn(username, password);
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
	
	public ODatabaseDocumentInternal getDatabaseDocumentInternal() {
		return getSession().getDatabaseDocumentInternal();
	}
	
	/**
	 * Use getDatabaseSession instead
	 * @return ODatabaseDocument
	 */
	@Deprecated
	public ODatabaseDocument getDatabase()
	{
		return getSession().getDatabase();
	}
	
	public ODatabaseSession getDatabaseSession() {
		return getSession().getDatabaseSession();
	}
	
	public OMetadata getMetadata()
	{
		return getDatabaseSession().getMetadata();
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
			request.setHeader(LazyAuthorizationRequestCycleListener.AUTHORIZATION_HEADER, "Basic "+Base64.getEncoder().encodeToString((username+":"+password).getBytes()));
		}
		if(!processRequest(request))
		{
			throw new IOException("Request was not sucessfully sent");
		}
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
	
	public static class IterativeTestResult<T, R> {
		private int iterations = 0;
		private long startTime=0;
		private long finishTime=0;
		
		private long nextStartTime=0;
		private List<R> listResults;
		private long maxDuration=0;
		private T maxDurationInput;
		
		IterativeTestResult(int size) {
			listResults = new ArrayList<>(size);
		}
		
		void start() {
			startTime = System.currentTimeMillis();
		}
		
		void nextStart() {
			nextStartTime = System.currentTimeMillis();
			iterations++;
		}
		
		void nextFinish(T input, R result) {
			listResults.add(result);
			long duration = System.currentTimeMillis() - nextStartTime;
			if(duration>maxDuration) {
				maxDuration = duration;
				maxDurationInput = input;
			}
		}
		
		void finish() {
			finishTime = System.currentTimeMillis();
		}
		
		public long getTotalDuration() {
			return finishTime-startTime;
		}
		
		public long getAvgDuration() {
			return iterations>0 ? getTotalDuration() / iterations : 0L;
		}
		
		public long getMaxDuration() {
			return maxDuration;
		}
		
		public T getMaxDurationObject() {
			return maxDurationInput;
		}
		
		public List<R> getResults() {
			return listResults;
		}
		
		public void assertTotalDurationLess(long duration) {
			long expectedDuration = getTotalDuration();
			if(duration<expectedDuration) {
				throw new AssertionFailedError("Expected total duration ("+expectedDuration+"ms) is less then in test: "+duration+"ms");
			}
		}
		
		public void assertAvgDurationLess(long duration) {
			long expectedDuration = getAvgDuration();
			if(duration<expectedDuration) {
				throw new AssertionFailedError("Expected avg duration ("+expectedDuration+"ms) is less then in test: "+duration+"ms");
			}
		}
		
		public void assertMaxDurationLess(long duration) {
			long expectedDuration = getMaxDuration();
			if(duration<expectedDuration) {
				throw new AssertionFailedError("Expected max duration ("+expectedDuration+"ms) is less then in test: "+duration+"ms");
			}
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("Iterations: ").append(iterations).append('\n')
			  .append("Total duration: ").append(getTotalDuration()).append("ms\n")
			  .append("Avg duration: ").append(getAvgDuration()).append("ms\n")
			  .append("Max duration: ").append(getMaxDuration()).append("ms\n")
			  .append("Max duration object: ").append(getMaxDurationObject());
			return sb.toString();
		}
		
		public void log(String message) {
			log(LOG, message);
		}
		
		public void log(Logger log, String message) {
			log.info(message+"\n"+toString());
		}
	}
	
	public <T, R> IterativeTestResult<T, R> iterativelyTest(Iterable<T> data, Function<T, R> func) {
		IterativeTestResult<T, R> result = new IterativeTestResult<>(data instanceof Collection? ((Collection<T>)data).size() : 100);
		result.start();
		try {
			R r;
			for (T element : data) {
				result.nextStart();
				r = func.apply(element);
				result.nextFinish(element, r);
			}
		} finally {
			result.finish();
		}
		return result;
		
	}
}
