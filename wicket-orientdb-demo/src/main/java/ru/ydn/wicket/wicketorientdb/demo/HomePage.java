package ru.ydn.wicket.wicketorientdb.demo;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.authroles.authentication.panel.SignInPanel;
import org.apache.wicket.devutils.debugbar.DebugBar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.orientechnologies.orient.core.record.impl.ODocument;

import ru.ydn.wicket.wicketorientdb.components.table.DocumentPropertyColumn;
import ru.ydn.wicket.wicketorientdb.model.OQueryDataProvider;

/**
 * Demo home page
 */
public class HomePage extends WebPage {
	private static final long serialVersionUID = 1L;

	public HomePage(final PageParameters parameters) {
		super(parameters);

		add(new DebugBar("debugBar"));
		add(new Label("dbName", new PropertyModel<String>(this, "session.database.name")));
		add(new Label("dbUrl", new PropertyModel<String>(this, "session.database.URL")));
		add(new Label("dbUserName", new PropertyModel<String>(this, "session.database.user.name")));
		add(new Label("signedIn", new PropertyModel<String>(this, "session.signedIn")));
		add(new Label("signedInUser", new PropertyModel<String>(this, "session.user.name")));
//		((OrientDbWebSession)getSession()).getDatabase().getUser().
		add(new SignInPanel("signInPanel"));
		OQueryDataProvider<ODocument> provider = new OQueryDataProvider<>("select from "+WicketApplication.CLASS_NAME);
		List<IColumn<ODocument, String>> columns = new ArrayList<>();
		columns.add(new DocumentPropertyColumn(Model.of(WicketApplication.PROP_NAME), WicketApplication.PROP_NAME, WicketApplication.PROP_NAME));
		columns.add(new DocumentPropertyColumn(Model.of(WicketApplication.PROP_INT), WicketApplication.PROP_INT, WicketApplication.PROP_INT));
		columns.add(new DocumentPropertyColumn(Model.of(WicketApplication.PROP_DATE), WicketApplication.PROP_DATE, WicketApplication.PROP_DATE));
		DefaultDataTable<ODocument, String> table = new DefaultDataTable<>("table", columns, provider, 15);
		add(table);
    }
}
