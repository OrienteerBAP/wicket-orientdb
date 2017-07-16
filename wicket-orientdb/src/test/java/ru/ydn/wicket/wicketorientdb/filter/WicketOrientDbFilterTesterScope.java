package ru.ydn.wicket.wicketorientdb.filter;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.lang.Args;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import ru.ydn.wicket.wicketorientdb.junit.WicketOrientDbTesterScope;
import ru.ydn.wicket.wicketorientdb.model.OPropertyModel;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static ru.ydn.wicket.wicketorientdb.filter.ITesterFilterConstants.*;

public class WicketOrientDbFilterTesterScope extends WicketOrientDbTesterScope {

    private final Map<String, IModel<OProperty>> properties = Maps.newHashMap();

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
                    deleteClassesAndDocuments(testClasses);
                    tester.destroy();
                }
            }
        };
    }

    public IModel<OProperty> getProperty(String name) {
        return properties.get(name);
    }

    private List<OClass> initTestClasses() {
        return new DBClosure<List<OClass>>() {
            @Override
            protected List<OClass> execute(ODatabaseDocument db) {
                OClass testClass = createOClass(db, TEST_CLASS_NAME, true);
                OClass linkClass = createOClass(db, LINK_TEST_CLASS_NAME, false);
                List<ODocument> documentsForTestClass = createDocumentsWithPrimaryTypesForOClass(testClass, true);
                List<ODocument> documentsForLinkClass = createDocumentsWithPrimaryTypesForOClass(linkClass, true);
                createLinksForDocuments(documentsForTestClass, documentsForLinkClass);
                createLinkListForDocument(documentsForTestClass, createListOfDocuments(documentsForLinkClass));
                createMapDocsForDocuments(documentsForTestClass, documentsForLinkClass, false);
                createEmbeddedFieldsForDocuments(documentsForTestClass,
                        createDocumentsWithPrimaryTypesForOClass(testClass, false));
                createEmbeddedListFieldsForDocuments(documentsForTestClass,
                        createListOfDocuments(createDocumentsWithPrimaryTypesForOClass(testClass, false)));
                createMapDocsForDocuments(documentsForTestClass,
                        createDocumentsWithPrimaryTypesForOClass(testClass, false), true);
                db.commit();
                return Arrays.asList(testClass, linkClass);
            }
        }.execute();
    }

    private OClass createOClass(ODatabaseDocument db, String className, boolean complexTypes) {
        OClass oClass = db.getMetadata().getSchema().createClass(className);
        properties.put(STRING_FIELD, new OPropertyModel(oClass.createProperty(STRING_FIELD, OType.STRING)));
        properties.put(NUMBER_FIELD, new OPropertyModel(oClass.createProperty(NUMBER_FIELD, OType.INTEGER)));
        properties.put(DATE_FIELD, new OPropertyModel(oClass.createProperty(DATE_FIELD, OType.DATE)));
        properties.put(DATETIME_FIELD, new OPropertyModel(oClass.createProperty(DATETIME_FIELD, OType.DATETIME)));
        if (complexTypes) {
            properties.put(LINK_FIELD, new OPropertyModel(oClass.createProperty(LINK_FIELD, OType.LINK)));
            oClass.createProperty(LINK_LIST_FIELD, OType.LINKLIST);
            oClass.createProperty(LINK_SET_FIELD, OType.LINKSET);
            oClass.createProperty(LINK_MAP_FIELD, OType.LINKMAP);
            oClass.createProperty(EMBEDDED_FIELD, OType.EMBEDDED);
            oClass.createProperty(EMBEDDED_LIST_FIELD, OType.EMBEDDEDLIST);
            oClass.createProperty(EMBEDDED_SET_FIELD, OType.EMBEDDEDSET);
            oClass.createProperty(EMBEDDED_MAP_FIELD, OType.EMBEDDEDMAP);
        }
        db.commit();
        return oClass;
    }

    private List<ODocument> createDocumentsWithPrimaryTypesForOClass(OClass oClass, boolean save) {
        List<ODocument> documents = Lists.newArrayList();
        ODocument document = new ODocument(oClass);

        document.field(STRING_FIELD, STR_VALUE_1);
        document.field(NUMBER_FIELD, NUM_VALUE_1);
        document.field(DATE_FIELD, DATE_VALUE_1);
        document.field(DATETIME_FIELD, DATETIME_VALUE_1);
        documents.add(document);
        if (save) document.save();

        document = new ODocument(oClass);
        document.field(STRING_FIELD, STR_VALUE_2);
        document.field(NUMBER_FIELD, NUM_VALUE_2);
        document.field(DATE_FIELD, DATE_VALUE_2);
        document.field(DATETIME_FIELD, DATETIME_VALUE_2);
        documents.add(document);
        if (save) document.save();

        document = new ODocument(oClass);
        document.field(STRING_FIELD, STR_VALUE_3);
        document.field(NUMBER_FIELD, NUM_VALUE_3);
        document.field(DATE_FIELD, DATE_VALUE_3);
        document.field(DATETIME_FIELD, DATETIME_VALUE_3);
        documents.add(document);
        if (save) document.save();

        document = new ODocument(oClass);
        document.field(STRING_FIELD, STR_VALUE_4);
        document.field(NUMBER_FIELD, NUM_VALUE_4);
        document.field(DATE_FIELD, DATE_VALUE_4);
        document.field(DATETIME_FIELD, DATETIME_VALUE_4);
        documents.add(document);
        if (save) document.save();
        return documents;
    }

    private void createLinksForDocuments(List<ODocument> documents, List<ODocument> links) {
        Args.isTrue(documents.size() == links.size(), "documents.size() == linkedDocs.size()");
        for (int i = 0; i < documents.size(); i++) {
            ODocument document = documents.get(i);
            document.field(LINK_FIELD, links.get(i).getIdentity().toString(), OType.LINK);
            document.save();
        }
    }

    private void createEmbeddedFieldsForDocuments(List<ODocument> documents, List<ODocument> embedded) {
        Args.isTrue(documents.size() == embedded.size(), "documents.size() == embedded.size()");
        for (int i = 0; i < documents.size(); i++) {
            ODocument document = documents.get(i);

            document.field(EMBEDDED_FIELD, embedded.get(i), OType.EMBEDDED);
            document.save();
        }
    }

    private void createEmbeddedListFieldsForDocuments(List<ODocument> documents, List<List<ODocument>> embeddedList) {
        Args.isTrue(documents.size() == embeddedList.size(), "documents.size() == embeddedList.size()");
        for (int i = 0; i < documents.size(); i++) {
            ODocument document = documents.get(i);
            document.field(EMBEDDED_LIST_FIELD, embeddedList.get(i), OType.EMBEDDEDLIST);
            document.save();
        }
    }

    private void createLinkListForDocument(List<ODocument> documents, List<List<ODocument>> linkList) {
        Args.isTrue(documents.size() == linkList.size(), "documents.size() == linkList.size()");
        for (int i = 0; i < documents.size(); i++) {
            ODocument document = documents.get(i);
            document.field(LINK_LIST_FIELD, linkList.get(i), OType.LINKLIST);
            document.save();
        }
    }

    private void createMapDocsForDocuments(List<ODocument> documents, List<ODocument> mapDocs, boolean embedded) {
        Args.isTrue(documents.size() == mapDocs.size(), "documents.size() == mapDocs.size()");

        for (int i = 0; i < documents.size(); i++) {
            ODocument document = documents.get(i);
            Map<String, ODocument> map = Maps.newHashMap();
            map.put(MAP_KEYS.get(i), mapDocs.get(i));
            if (embedded) {
                document.field(EMBEDDED_MAP_FIELD, map, OType.EMBEDDEDMAP);
            } else document.field(LINK_MAP_FIELD, map, OType.LINKMAP);
            document.save();
        }
    }

    private List<List<ODocument>> createListOfDocuments(List<ODocument> docs) {
        List<List<ODocument>> listOfLinks = Lists.newArrayList();
        listOfLinks.add(createListOfDocuments(docs, LIST_ORDER_1));
        listOfLinks.add(createListOfDocuments(docs, LIST_ORDER_2));
        listOfLinks.add(createListOfDocuments(docs, LIST_ORDER_3));
        listOfLinks.add(createListOfDocuments(docs, LIST_ORDER_4));
        return listOfLinks;
    }

    private List<ODocument> createListOfDocuments(List<ODocument> docs, List<Integer> order) {
        Args.isTrue(docs.size() == order.size(), "docs.size() == order.size()");
        List<ODocument> documents = Lists.newArrayList();
        for (Integer i : order) {
            documents.add(docs.get(i - 1));
        }
        return documents;
    }

    private void deleteClassesAndDocuments(final List<OClass> classes) {
        new DBClosure<Void>() {

            @Override
            protected Void execute(ODatabaseDocument db) {
                for (OClass oClass : classes) {
                    db.command(new OCommandSQL("DELETE FROM " + oClass.getName())).execute();
                    db.getMetadata().getSchema().dropClass(oClass.getName());
                }
                return null;
            }
        }.execute();
    }

}