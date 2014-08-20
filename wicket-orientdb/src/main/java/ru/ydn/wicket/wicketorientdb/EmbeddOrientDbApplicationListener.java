package ru.ydn.wicket.wicketorientdb;

import java.io.File;
import java.net.URL;

import org.apache.wicket.Application;
import org.apache.wicket.IApplicationListener;
import org.apache.wicket.WicketRuntimeException;

import com.orientechnologies.orient.server.OServer;
import com.orientechnologies.orient.server.OServerMain;
import com.orientechnologies.orient.server.config.OServerConfiguration;

public class EmbeddOrientDbApplicationListener implements IApplicationListener {
	
	private URL url;
	private File configFile;
	private String config;
	protected OServerConfiguration serverConfiguration;
	
	protected OServer server;

	public EmbeddOrientDbApplicationListener()
	{
		
	}
	
	public EmbeddOrientDbApplicationListener(URL url)
	{
		this.url=url;
	}
	
	public EmbeddOrientDbApplicationListener(File configFile)
	{
		this.configFile=configFile;
	}
	
	public EmbeddOrientDbApplicationListener(String config)
	{
		this.config=config;
	}
	
	public EmbeddOrientDbApplicationListener(OServerConfiguration serverConfiguration)
	{
		this.serverConfiguration = serverConfiguration;
	}
	
	@Override
	public void onAfterInitialized(Application app) {
		try {
			server = OServerMain.create();
			if(url!=null)
			{
				server.startup(url.openStream());
			}
			else if(configFile!=null)
			{
				server.startup(configFile);
			}
			else if(config!=null)
			{
				server.startup(config);
			}
			else if(serverConfiguration!=null)
			{
				server.startup(serverConfiguration);
			}
			else
			{
				server.startup();
			}
			server.activate();
			onAfterServerStartupAndActivation((OrientDbWebApplication)app);
		} catch (Exception e) {
			throw new WicketRuntimeException("Can't start OrientDB Embedded Server", e);
		}
	}
	
	public void onAfterServerStartupAndActivation(OrientDbWebApplication app) throws Exception
	{
		
	}

	@Override
	public void onBeforeDestroyed(Application arg0) {
		server.shutdown();
	}

}
