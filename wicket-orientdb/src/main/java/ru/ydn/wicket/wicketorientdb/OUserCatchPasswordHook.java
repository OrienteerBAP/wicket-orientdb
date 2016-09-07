package ru.ydn.wicket.wicketorientdb;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.hook.ODocumentHookAbstract;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.security.OSecurityManager;

import ru.ydn.wicket.wicketorientdb.components.IHookPosition;


/**
*	Strange workaround to support changing system users passwords in web interface
*/
public class OUserCatchPasswordHook extends ODocumentHookAbstract implements IHookPosition {

	
	public OUserCatchPasswordHook(ODatabaseDocument database){
		super(database);
		setIncludeClasses("OUser");
	}
	
	@Override
	public RESULT onRecordBeforeUpdate(final ODocument iDocument) {
		String name = iDocument.field("name");
		String password = iDocument.field("password");
		
		if (password.startsWith(OSecurityManager.ALGORITHM_PREFIX)){
			return RESULT.RECORD_NOT_CHANGED;
		}
		
		IOrientDbSettings settings = OrientDbWebApplication.get().getOrientDbSettings();
		if (	settings.getDBInstallatorUserName()!=null && 
				settings.getDBInstallatorUserName().equals(name)){
			settings.setDBInstallatorUserPassword(password);
		}
		if (settings.getDBUserName()!=null && 
				settings.getDBUserName().equals(name)){
			settings.setDBUserPassword(password);
		}
		OrientDbWebSession session = OrientDbWebSession.get();
		if (session.getUsername()!=null && session.getUsername().equals(name)){
			session.setUser(name, password);
		}
		return RESULT.RECORD_NOT_CHANGED;
	}
	
	@Override
	public DISTRIBUTED_EXECUTION_MODE getDistributedExecutionMode() {
		return DISTRIBUTED_EXECUTION_MODE.TARGET_NODE;
	}

	@Override
	public HOOK_POSITION getPosition() {
		return HOOK_POSITION.FIRST;
	}
}
