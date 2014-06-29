package ru.ydn.wicket.wicketorientdb;

import org.apache.wicket.MetaDataKey;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.cycle.AbstractRequestCycleListener;
import org.apache.wicket.request.cycle.RequestCycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractContentAwareTransactionRequestCycleListener extends AbstractRequestCycleListener
{
	private final static Logger log = LoggerFactory.getLogger(AbstractContentAwareTransactionRequestCycleListener.class);
	private final static MetaDataKey<Boolean> IN_PROGRESS_KEY = new MetaDataKey<Boolean>(){};
	
	@Override
	public void onRequestHandlerResolved(RequestCycle cycle,
			IRequestHandler handler) {
		if(isInProgress(cycle)) return;
		if(isOurContent(cycle, handler))
		{
			setInProgress(cycle, true);
			start(cycle);
		}
	}
	
	@Override
	public void onEndRequest(RequestCycle cycle) {
		if(isInProgress(cycle))
		{
			end(cycle);
		}
	}
	
	public boolean isInProgress(RequestCycle cycle)
	{
		Boolean inProgress = cycle.getMetaData(IN_PROGRESS_KEY);
		return inProgress!=null?inProgress:false;
	}
	
	public void setInProgress(RequestCycle cycle, boolean inProgress)
	{
		cycle.setMetaData(IN_PROGRESS_KEY, inProgress);
	}
	
	public abstract void start(RequestCycle cycle);
	
	public abstract void end(RequestCycle cycle);
	
	public abstract boolean isOurContent(RequestCycle cycle, IRequestHandler handler);
}
