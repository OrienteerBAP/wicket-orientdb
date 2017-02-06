package ru.ydn.wicket.wicketorientdb.components;

import com.orientechnologies.orient.core.hook.ORecordHook;

/**
 *  support returning hook position in queue of executing
 */
public interface IHookPosition extends ORecordHook {

	//returns hook position in queue of executing
	public HOOK_POSITION getPosition();
}
