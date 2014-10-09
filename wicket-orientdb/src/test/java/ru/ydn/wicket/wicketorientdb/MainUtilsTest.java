package ru.ydn.wicket.wicketorientdb;

import java.util.Locale;

import org.junit.Test;

import com.google.common.base.Converter;
import com.orientechnologies.orient.core.db.record.ODatabaseRecord;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.security.OUser;
import com.orientechnologies.orient.core.record.impl.ODocument;

import ru.ydn.wicket.wicketorientdb.converter.ODocumentConverter;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;
import ru.ydn.wicket.wicketorientdb.utils.DocumentWrapperTransformer;
import ru.ydn.wicket.wicketorientdb.utils.OClassClassNameConverter;
import ru.ydn.wicket.wicketorientdb.utils.ODocumentORIDConverter;
import ru.ydn.wicket.wicketorientdb.utils.OIndexNameConverter;
import ru.ydn.wicket.wicketorientdb.utils.OPropertyFullNameConverter;
import static org.junit.Assert.*;
import static ru.ydn.wicket.wicketorientdb.model.AbstractNamingModel.buitify;

public class MainUtilsTest extends AbstractTestClass
{
	@Test
	public void testBuitify() throws Exception
	{
		assertEquals("Test", buitify("test"));
		assertEquals("Test", buitify("Test"));
		assertEquals("My Test", buitify("myTest"));
		assertEquals("My Test", buitify("my test"));
		assertEquals("M Test", buitify("mTest"));
		assertEquals("Allow", buitify("_allow"));
		assertEquals("Allow", buitify("__allow"));
		assertEquals("Allow Read", buitify("_allowRead"));
	}
	
	@Test
	public void testDBClosure() throws Exception
	{
		DBClosure<OUser> adminClosure = new DBClosure<OUser>() {

			@Override
			protected OUser execute(ODatabaseRecord db) {
				return db.getUser();
			}
		};
		assertEquals(getMetadata().getSecurity().getUser("admin"), adminClosure.execute());
		DBClosure<OUser> readerClosure = new DBClosure<OUser>("reader", "reader") {

			@Override
			protected OUser execute(ODatabaseRecord db) {
				return db.getUser();
			}
		};
		assertEquals(getMetadata().getSecurity().getUser("reader"), readerClosure.execute());
	}
	
	@Test
	public void testConverters() throws Exception
	{
		testConverter(OClassClassNameConverter.INSTANCE, getSchema().getClass("OUser"), "OUser");
		testConverter(OPropertyFullNameConverter.INSTANCE, getSchema().getClass("Ouser").getProperty("name"), "OUser.name");
		testConverter(OIndexNameConverter.INSTANCE, getSchema().getClass("Ouser").getClassIndex("OUser.name"), "OUser.name");
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
		OUser admin = getMetadata().getSecurity().getUser("admin");
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
	
}
