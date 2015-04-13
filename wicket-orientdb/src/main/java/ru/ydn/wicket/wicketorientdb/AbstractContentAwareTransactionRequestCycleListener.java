package ru.ydn.wicket.wicketorientdb;

import org.apache.wicket.MetaDataKey;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.cycle.AbstractRequestCycleListener;
import org.apache.wicket.request.cycle.RequestCycle;

/**
 * {@link org.apache.wicket.request.cycle.IRequestListener} for wrapping interesting content by methods start() and end().
 * Usefull for transactions
 */
public abstract class AbstractContentAwareTransactionRequestCycleListener extends AbstractRequestCycleListener
{
	private final static MetaDataKey<Boolean> IN_PROGRESS_KEY = new MetaDataKey<Boolean>(){private static final long serialVersionUID = 1L;};

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
	
	/**
	 * Is current 'transaction' in progress?
	 * @param cycle {@link RequestCycle}
	 * @return true - if we are within a transaction, false - if not
	 */
	public boolean isInProgress(RequestCycle cycle)
	{
		Boolean inProgress = cycle.getMetaData(IN_PROGRESS_KEY);
		return inProgress!=null?inProgress:false;
	}
	
	/**
	 * Change current state of 'transaction'
	 * @param cycle current {@link RequestCycle}
	 * @param inProgress state to set
	 */
	public void setInProgress(RequestCycle cycle, boolean inProgress)
	{
		cycle.setMetaData(IN_PROGRESS_KEY, inProgress);
	}
	
	/**
	 * Request starts.
	 * @param cycle - current {@link RequestCycle}
	 */
	public abstract void start(RequestCycle cycle);
	
	/**
	 * Request ends.
	 * @param cycle current {@link RequestCycle}
	 */
	public abstract void end(RequestCycle cycle);
	
	/**
	 * Predicate method to identify is that our content or not.
	 * @param cycle current {@link RequestCycle}
	 * @param handler current {@link IRequestHandler}
	 * @return true if content should wrapped to a transaction, falst - if content is not interesting
	 */
	public abstract boolean isOurContent(RequestCycle cycle, IRequestHandler handler);
}
