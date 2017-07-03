package ru.ydn.wicket.wicketorientdb.filter;

import com.google.common.collect.Lists;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.util.lang.Args;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import ru.ydn.wicket.wicketorientdb.junit.WicketOrientDbTesterScope;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

import java.util.Arrays;
import java.util.List;

import static ru.ydn.wicket.wicketorientdb.filter.ITesterFilterConstants.*;

public class WicketOrientDbFilterTesterScope extends WicketOrientDbTesterScope {

    @Override
    public Statement apply(final Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                WicketTester tester = create();
                List<OClass> testClasses = Lists.newArrayList();
                try {
                    testClasses = initTestClasses();
                    base.evaluate();
                } finally {
                    deleteClasses(testClasses);
                    tester.destroy();
                }
            }
        };
    }


    private List<OClass> initTestClasses() {
        return new DBClosure<List<OClass>>() {
            @Override
            protected List<OClass> execute(ODatabaseDocument db) {
                OClass testClass = createOClass(db, TEST_CLASS_NAME, true);
                OClass linkClass = createOClass(db, LINK_TEST_CLASS_NAME, false);
                List<ODocument> documentsForTestClass = createDocumentsWithPrimaryTypesForOClass(testClass);
                db.commit();
                List<ODocument> documentsForLinkClass = createDocumentsWithPrimaryTypesForOClass(linkClass);
                db.commit();
                createLinksForDocuments(documentsForTestClass, documentsForLinkClass);
                db.commit();
                return Arrays.asList(testClass, linkClass);
            }
        }.execute();
    }

    private OClass createOClass(ODatabaseDocument db, String className, boolean complexTypes) {
        OClass oClass = db.getMetadata().getSchema().createClass(className);
        oClass.createProperty(STRING_FIELD, OType.STRING);
        oClass.createProperty(NUMBER_FIELD, OType.INTEGER);
        oClass.createProperty(DATE_FIELD, OType.DATE);
        oClass.createProperty(DATETIME_FIELD, OType.DATETIME);
        if (complexTypes) {
            oClass.createProperty(LINK_FIELD, OType.LINK);
        }
        db.commit();
        return oClass;
    }

    private List<ODocument> createDocumentsWithPrimaryTypesForOClass(OClass oClass) {
        List<ODocument> documents = Lists.newArrayList();
        ODocument document = new ODocument(oClass);

        document.field(STRING_FIELD, STR_VALUE_1);
        document.field(NUMBER_FIELD, NUM_VALUE_1);
        document.field(DATE_FIELD, DATE_VALUE_1);
        document.field(DATETIME_FIELD, DATETIME_VALUE_1);
        documents.add(document);
        document.save();

        document = new ODocument(oClass);
        document.field(STRING_FIELD, STR_VALUE_2);
        document.field(NUMBER_FIELD, NUM_VALUE_2);
        document.field(DATE_FIELD, DATE_VALUE_2);
        document.field(DATETIME_FIELD, DATETIME_VALUE_2);
        documents.add(document);
        document.save();

        document = new ODocument(oClass);
        document.field(STRING_FIELD, STR_VALUE_3);
        document.field(NUMBER_FIELD, NUM_VALUE_3);
        document.field(DATE_FIELD, DATE_VALUE_3);
        document.field(DATETIME_FIELD, DATETIME_VALUE_3);
        documents.add(document);
        document.save();

        document = new ODocument(oClass);
        document.field(STRING_FIELD, STR_VALUE_4);
        document.field(NUMBER_FIELD, NUM_VALUE_4);
        document.field(DATE_FIELD, DATE_VALUE_4);
        document.field(DATETIME_FIELD, DATETIME_VALUE_4);
        documents.add(document);
        document.save();
        return documents;
    }

    private void createLinksForDocuments(List<ODocument> documents, List<ODocument> linkedDocs) {
        Args.isTrue(documents.size() == linkedDocs.size(), "documents.size() == linkedDocs.size()");
        for (int i = 0; i < documents.size(); i++) {
            ODocument document = documents.get(i);
            document.field(LINK_FIELD, linkedDocs.get(i).getIdentity().toString());
            document.save();
        }
    }

    private void deleteClasses(final List<OClass> classes) {
        new DBClosure<Void>() {

            @Override
            protected Void execute(ODatabaseDocument db) {
                for (OClass oClass : classes) {
                    db.getMetadata().getSchema().dropClass(oClass.getName());
                }
                return null;
            }
        }.execute();
    }

}
