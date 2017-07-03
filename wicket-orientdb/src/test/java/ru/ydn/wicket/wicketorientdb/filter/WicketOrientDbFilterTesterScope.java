package ru.ydn.wicket.wicketorientdb.filter;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import ru.ydn.wicket.wicketorientdb.junit.WicketOrientDbTesterScope;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

import static ru.ydn.wicket.wicketorientdb.filter.ITesterFilterConstants.*;


public class WicketOrientDbFilterTesterScope extends WicketOrientDbTesterScope {

    private WicketTester tester;

    @Override
    public Statement apply(final Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                tester = create();
                try {
                    initTestOClass(TEST_CLASS_NAME);
                    base.evaluate();
                } finally {
                    deleteOClass();
                    tester.destroy();
                    tester = null;
                }
            }
        };
    }


    private void initTestOClass(final String className) {
        new DBClosure<Void>() {
            @Override
            protected Void execute(ODatabaseDocument db) {
                OClass oClass = db.getMetadata().getSchema().createClass(className);
                oClass.createProperty(STRING_FIELD, OType.STRING);
                oClass.createProperty(NUMBER_FIELD, OType.INTEGER);
                oClass.createProperty(DATE_FIELD, OType.DATE);
                oClass.createProperty(DATETIME_FIELD, OType.DATETIME);
                db.commit();
                createDocumentsForOClass(oClass);
                db.commit();
                return null;
            }
        }.execute();
    }

    private void createDocumentsForOClass(OClass oClass) {
        ODocument document = new ODocument(oClass);
        document.field(STRING_FIELD, STR_VALUE_1);
        document.field(NUMBER_FIELD, NUM_VALUE_1);
        document.field(DATE_FIELD, DATE_VALUE_1);
        document.field(DATETIME_FIELD, DATETIME_VALUE_1);
        document.save();
        document = new ODocument(oClass);
        document.field(STRING_FIELD, STR_VALUE_2);
        document.field(NUMBER_FIELD, NUM_VALUE_2);
        document.field(DATE_FIELD, DATE_VALUE_2);
        document.field(DATETIME_FIELD, DATETIME_VALUE_2);
        document.save();
        document = new ODocument(oClass);
        document.field(STRING_FIELD, STR_VALUE_3);
        document.field(NUMBER_FIELD, NUM_VALUE_3);
        document.field(DATE_FIELD, DATE_VALUE_3);
        document.field(DATETIME_FIELD, DATETIME_VALUE_3);
        document.save();
        document = new ODocument(oClass);
        document.field(STRING_FIELD, STR_VALUE_4);
        document.field(NUMBER_FIELD, NUM_VALUE_4);
        document.field(DATE_FIELD, DATE_VALUE_4);
        document.field(DATETIME_FIELD, DATETIME_VALUE_4);
        document.save();
    }

    private void deleteOClass() {
        new DBClosure<Void>() {

            @Override
            protected Void execute(ODatabaseDocument db) {
                db.getMetadata().getSchema().dropClass(TEST_CLASS_NAME);
                return null;
            }
        }.execute();
    }

}
