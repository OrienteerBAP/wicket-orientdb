package ru.ydn.wicket.wicketorientdb.utils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxRequestTarget.IJavaScriptResponse;
import org.apache.wicket.markup.html.form.Form;

/**
 * Update Form EncType to "multipart/form-data" of there is something which require multipart is in target ajax response
 */
public class FixFormEncTypeListener implements AjaxRequestTarget.IListener {

	@Override
	public void onAfterRespond(Map<String, Component> map, IJavaScriptResponse response) {
		Set<Form<?>> formsToUpdate = new HashSet<>();
		for (Map.Entry<String, Component> entry : map.entrySet()) {
			Component component = entry.getValue();
			Form<?> form = component.findParent(Form.class);
			if(form!=null && form.isMultiPart()) formsToUpdate.add(form.getRootForm());
		}
		
		for (Form<?> form : formsToUpdate) {
			response.addJavaScript("{var e = document.getElementById('"+form.getMarkupId()+"'); e.encoding= 'multipart/form-data'; e.encType=e.encoding;}");
		}
	}
}
