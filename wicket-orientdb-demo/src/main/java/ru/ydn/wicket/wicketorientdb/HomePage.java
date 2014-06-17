package ru.ydn.wicket.wicketorientdb;

import org.apache.wicket.devutils.debugbar.DebugBar;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
//import org.apache.wicket.devutils.debugbar.DebugBar;

public class HomePage extends WebPage {
	private static final long serialVersionUID = 1L;

	public HomePage(final PageParameters parameters) {
		super(parameters);

//		add(new WicketConsolePanel("orientdb"));
		add(new DebugBar("debugBar"));
    }
}
