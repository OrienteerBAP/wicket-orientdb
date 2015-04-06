package ru.ydn.wicket.wicketorientdb.model;

import java.io.Serializable;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.type.ODocumentWrapper;

public class ModelUtils {
	
	private ModelUtils() {
	}
	
	@SuppressWarnings("unchecked")
	public static <K> IModel<K> model(K o)
    {
    	if(o instanceof ODocument) return (IModel<K>)new ODocumentModel((ODocument)o);
    	else if(o instanceof ODocumentWrapper) return (IModel<K>)new ODocumentWrapperModel<ODocumentWrapper>((ODocumentWrapper)o);
    	else if(o instanceof Serializable) return (IModel<K>)Model.of((Serializable)o);
    	else throw new WicketRuntimeException(ModelUtils.class.getSimpleName()+" can't work with non serializable objects: "+o);
    }
}
