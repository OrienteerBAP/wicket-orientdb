package ru.ydn.wicket.wicketorientdb.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orientechnologies.orient.core.command.OCommandOutputListener;

/**
 * Utility {@link OCommandOutputListener} for logging of messages to log
 */
public class LoggerOCommandOutputListener implements OCommandOutputListener
{
	private static final Logger LOG = LoggerFactory.getLogger(LoggerOCommandOutputListener.class);
	public static final LoggerOCommandOutputListener INSTANCE = new LoggerOCommandOutputListener();
	
	private final Logger logger;
	
	public LoggerOCommandOutputListener()
	{
		this(LOG);
	}
	
	public LoggerOCommandOutputListener(Logger logger)
	{
		this.logger = logger;
	}
	
	@Override
	public void onMessage(String iText) {
		logger.info(iText);
	}

}
