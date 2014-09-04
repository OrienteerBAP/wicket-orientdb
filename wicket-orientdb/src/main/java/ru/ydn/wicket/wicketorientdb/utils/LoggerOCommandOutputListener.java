package ru.ydn.wicket.wicketorientdb.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orientechnologies.orient.core.command.OCommandOutputListener;

public class LoggerOCommandOutputListener implements OCommandOutputListener
{
	private static final Logger LOG = LoggerFactory.getLogger(LoggerOCommandOutputListener.class);
	public static final LoggerOCommandOutputListener INSTANCE = new LoggerOCommandOutputListener();
	
	@Override
	public void onMessage(String iText) {
		LOG.info(iText);
	}

}
