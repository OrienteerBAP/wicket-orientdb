package ru.ydn.wicket.wicketorientdb;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.hook.ODocumentHookAbstract;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class TestHook extends ODocumentHookAbstract {
	
	public TestHook(ODatabaseDocument database) {
		super(database);
		setIncludeClasses("TestHooks");
	}
	
	@Override
	public void onRecordAfterRead(ODocument iDocument) {
		iDocument.field("name", "HOOK");
	}

	@Override
	public DISTRIBUTED_EXECUTION_MODE getDistributedExecutionMode() {
		return DISTRIBUTED_EXECUTION_MODE.SOURCE_NODE;
	}

}
