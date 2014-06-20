package ru.ydn.wicket.wicketorientdb;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import org.apache.wicket.Application;
import org.apache.wicket.IApplicationListener;
import org.apache.wicket.WicketRuntimeException;

import com.orientechnologies.orient.server.OServer;
import com.orientechnologies.orient.server.OServerMain;
import com.orientechnologies.orient.server.config.OServerConfiguration;

public class EmbeddOrientDbApplicationListener implements IApplicationListener {
	
	private File configFile;
	private String config;
	private OServerConfiguration serverConfiguration;
	
	private OServer server;

	public EmbeddOrientDbApplicationListener()
	{
		
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
	public void onAfterInitialized(Application arg0) {
		try {
			server = OServerMain.create();
			if(configFile!=null)
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
		} catch (Exception e) {
			throw new WicketRuntimeException("Can't start OrientDB Embedded Server", e);
		}
	}

	@Override
	public void onBeforeDestroyed(Application arg0) {
		server.shutdown();
	}

}
