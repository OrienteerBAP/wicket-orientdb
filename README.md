wicket-orientdb
===============

Everything you need to work with Apache Wicket and OrientDB.
Library contains several functional part which can be used separatly or all together.

Initial setup of application
-----------------------

You should inherit WebApplication class from `OrientDbWebApplication` to use OrientDB and its authentication with in your application.

```java
public class WicketApplication extends OrientDbWebApplication
```

Specify connection properties
```java
		getOrientDbSettings().setDBUrl("local:localhost/"+DB_NAME);
		getOrientDbSettings().setDefaultUserName("admin");
		getOrientDbSettings().setDefaultUserPassword("admin");
```

Embedded OrientDB Server
------------------------

If you need to run OrientDB in embedded mode please use `EmbeddOrientDbApplicationListener`

```java
public class WicketApplication extends OrientDbWebApplication
{
@Override
	public void init()
	{
		super.init();
		getApplicationListeners().add(new EmbeddOrientDbApplicationListener(WicketApplication.class.getResource("db.config.xml"))
		{

			@Override
			public void onAfterServerStartupAndActivation() throws Exception {
				OServerAdmin serverAdmin = new OServerAdmin("localhost/"+DB_NAME).connect("root", "WicketOrientDB");
				if(!serverAdmin.existsDatabase())
			    serverAdmin.createDatabase(DB_NAME, "graph", "local");
			    
			}
			
		});
		getOrientDbSettings().setDBUrl("local:localhost/"+DB_NAME);
		getOrientDbSettings().setDefaultUserName("admin");
		getOrientDbSettings().setDefaultUserPassword("admin");
	}
}
```

PropertyModel
-------------

To gain access to Orient DB document from your application please use `ODocumentPropertyModel` instead of common `PropertyModel`.
Important! Following issue have been created to introduce support of custom properties models into wicket: 
https://issues.apache.org/jira/browse/WICKET-5623
If you interested in using PropertyModel as usual in wicket, please, take a look to following pull request: https://github.com/apache/wicket/pull/74
















