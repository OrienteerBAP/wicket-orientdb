package ru.ydn.wicket.wicketorientdb;

import java.util.Locale;

import org.junit.ClassRule;
import org.junit.Test;

import com.google.common.base.Converter;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.security.OSecurityUser;
import com.orientechnologies.orient.core.metadata.security.OUser;
import com.orientechnologies.orient.core.record.impl.ODocument;

import ru.ydn.wicket.wicketorientdb.converter.OClassClassNameConverter;
import ru.ydn.wicket.wicketorientdb.converter.ODocumentConverter;
import ru.ydn.wicket.wicketorientdb.converter.ODocumentORIDConverter;
import ru.ydn.wicket.wicketorientdb.converter.OIndexNameConverter;
import ru.ydn.wicket.wicketorientdb.converter.OPropertyFullNameConverter;
import ru.ydn.wicket.wicketorientdb.junit.WicketOrientDbTesterScope;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;
import ru.ydn.wicket.wicketorientdb.utils.DocumentWrapperTransformer;

import static org.junit.Assert.*;
import static ru.ydn.wicket.wicketorientdb.model.AbstractNamingModel.buitify;

public class MainUtilsTest
{
	@ClassRule
	public static WicketOrientDbTesterScope wicket = new WicketOrientDbTesterScope();
	
	@Test
	public void testBuitify() throws Exception
	{
		assertEquals("Test", buitify("test"));
		assertEquals("Test", buitify("Test"));
		assertEquals("My Test", buitify("myTest"));
		assertEquals("My Test", buitify("my test"));
		assertEquals("M Test", buitify("mTest"));
		assertEquals("My Test", buitify("my_test"));
		assertEquals("My Test", buitify("my-test"));
		assertEquals("My Test", buitify("my__test"));
		assertEquals("My Test", buitify("my--test"));
		assertEquals("Allow", buitify("_allow"));
		assertEquals("Allow", buitify("__allow"));
		assertEquals("Allow Read", buitify("_allowRead"));
	}
	
	@Test
	public void testDBClosure() throws Exception
	{
		DBClosure<OSecurityUser> adminClosure = new DBClosure<OSecurityUser>() {
			
			private static final long serialVersionUID = 1L;

			@Override
			protected OSecurityUser execute(ODatabaseDocument db) {
				assertEquals(db, ODatabaseRecordThreadLocal.instance().get());
				return db.getUser();
			}
		};
		assertEquals(wicket.getTester().getMetadata().getSecurity().getUser("admin").getIdentity(), adminClosure.execute().getIdentity());
		DBClosure<OSecurityUser> readerClosure = new DBClosure<OSecurityUser>("reader", "reader") {

			private static final long serialVersionUID = 1L;

			@Override
			protected OSecurityUser execute(ODatabaseDocument db) {
				assertEquals(db, ODatabaseRecordThreadLocal.instance().get());
				return db.getUser();
			}
		};
		assertEquals(wicket.getTester().getMetadata().getSecurity().getUser("reader").getIdentity(), readerClosure.execute().getIdentity());
	}
	
	@Test
	public void testConverters() throws Exception
	{
		OSchema schema = wicket.getTester().getSchema();
		testConverter(OClassClassNameConverter.INSTANCE, schema.getClass("OUser"), "OUser");
		testConverter(OPropertyFullNameConverter.INSTANCE, schema.getClass("Ouser").getProperty("name"), "OUser.name");
		testConverter(OIndexNameConverter.INSTANCE, schema.getClass("Ouser").getClassIndex("OUser.name"), "OUser.name");
		ORID orid = new ORecordId("#5:0"); //Admin ORID
		ODocument document = orid.getRecord();
		testConverter(ODocumentORIDConverter.INSTANCE, document, orid);
	}
	
	public <F, T> void testConverter(Converter<F, T> converter, F fromObject, T toObject)
	{
		assertEquals(toObject, converter.convert(fromObject));
		assertEquals(fromObject, converter.reverse().convert(toObject));
	}
	
	@Test
	public void testDocumentWrapper() throws Exception
	{
		ORID orid = new ORecordId("#5:0"); //Admin ORID
		ODocument adminDocument = orid.getRecord();
		OUser admin = wicket.getTester().getMetadata().getSecurity().getUser("admin");
		DocumentWrapperTransformer<OUser> transformer = new DocumentWrapperTransformer<OUser>(OUser.class);
		assertEquals(admin, transformer.apply(adminDocument));
	}
	
	@Test
	public void testDocumentConverter() throws Exception
	{
		ORID orid = new ORecordId("#5:0"); //Admin ORID
		ODocument adminDocument = orid.getRecord();
		ODocumentConverter converter = new ODocumentConverter();
		assertEquals(adminDocument, converter.convertToObject("#5:0", Locale.getDefault()));
		assertEquals(orid, converter.convertToOIdentifiable("#5:0", Locale.getDefault()));
		assertEquals("#5:0", converter.convertToString(adminDocument, Locale.getDefault()));
	}
	
	@Test
	public void testOrientDBVersion() throws Exception
	{
		String version = wicket.getTester().getApplication().getOrientDBVersion();
		System.out.println("OrientDb Version: "+version);
		assertNotNull(version);
	}
	
}
