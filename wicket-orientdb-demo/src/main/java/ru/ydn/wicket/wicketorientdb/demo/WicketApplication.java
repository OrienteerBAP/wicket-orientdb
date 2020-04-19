package ru.ydn.wicket.wicketorientdb.demo;

import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.ODatabaseType;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.authroles.authentication.pages.SignInPage;
import org.apache.wicket.markup.html.WebPage;
import ru.ydn.wicket.wicketorientdb.AbstractDataInstallator;
import ru.ydn.wicket.wicketorientdb.EmbeddOrientDbApplicationListener;
import ru.ydn.wicket.wicketorientdb.OrientDbWebApplication;
import ru.ydn.wicket.wicketorientdb.utils.OSchemaHelper;

import java.sql.Date;
import java.util.Random;

/**
 * Application object for your web application.
 */
public class WicketApplication extends OrientDbWebApplication
{
	public static final String DB_NAME = "WicketOrientDb";
	public static final String CLASS_NAME = "TestData";
	public static final String PROP_NAME = "name";
	public static final String PROP_INT = "number";
	public static final String PROP_DATE = "date";
	
	/**
	 * @see org.apache.wicket.Application#getHomePage()
	 */
	@Override
	public Class<? extends WebPage> getHomePage()
	{
		return HomePage.class;
	}

	/**
	 * @see org.apache.wicket.Application#init()
	 */
	@Override
	public void init()
	{
		super.init();
		getApplicationListeners().add(new EmbeddOrientDbApplicationListener(WicketApplication.class.getResource("db.config.xml"))
		{

			@Override
			public void onAfterServerStartupAndActivation(OrientDbWebApplication app) throws Exception {
				OrientDB orientDB = getServer().getContext();
				orientDB.createIfNotExists(getOrientDbSettings().getDbName(), getOrientDbSettings().getDbType());
			}
			
		});
		getOrientDbSettings().setDbName(DB_NAME);
		getOrientDbSettings().setDbType(ODatabaseType.MEMORY);
		getOrientDbSettings().setGuestUserName("admin");
		getOrientDbSettings().setGuestPassword("admin");
		getApplicationListeners().add(new AbstractDataInstallator() {
			
			
			@Override
			protected void installData(OrientDbWebApplication app, ODatabaseSession db) {
				OSchemaHelper helper = OSchemaHelper.bind(db);
				helper.oClass(CLASS_NAME)
						.oProperty(PROP_NAME, OType.STRING)
						.oProperty(PROP_INT, OType.INTEGER)
						.oProperty(PROP_DATE, OType.DATE);
				if(helper.getOClass().count()==0) {
					Random random = new Random();
					Date today = new Date(System.currentTimeMillis());
					int delta = 365*24*60*60;
					for (int i=0; i<50; i++) {
						ODocument doc = new ODocument(helper.getOClass());
						doc.field(PROP_NAME, "Name for #"+i);
						doc.field(PROP_INT, i);
						doc.field(PROP_DATE, new Date(today.getTime()+(random.nextInt(2*delta)-delta)*1000));
						doc.save();
					}
				}
				
			}
		});
	}

	@Override
	protected Class<? extends WebPage> getSignInPageClass() {
		return SignInPage.class;
	}
}
