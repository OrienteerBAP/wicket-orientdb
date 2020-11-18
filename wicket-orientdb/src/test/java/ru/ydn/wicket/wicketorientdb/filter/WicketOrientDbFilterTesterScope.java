package ru.ydn.wicket.wicketorientdb.filter;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.lang.Args;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import ru.ydn.wicket.wicketorientdb.junit.WicketOrientDbTesterScope;
import ru.ydn.wicket.wicketorientdb.model.OPropertyModel;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

import java.util.*;

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

    public Collection<IModel<OProperty>> getProperties() {
        return Collections.unmodifiableCollection(properties.values());
    }

    private void initPropertiesMap(Map<String, OProperty> classProperties) {
        for (String key : classProperties.keySet()) {
            properties.put(key, new OPropertyModel(classProperties.get(key)));
        }
    }

    private List<OClass> initTestClasses() {
        return new DBClosure<List<OClass>>() {
			private static final long serialVersionUID = 1L;

			@Override
            protected List<OClass> execute(ODatabaseSession db) {
                OClass testClass = createOClass(db, TEST_CLASS_NAME, true);
                OClass linkClass = createOClass(db, LINK_TEST_CLASS_NAME, false);

                OClass parent = createOClass(db, PARENT_CLASS_NAME, false);
                OClass child = createOClass(db, CHILD_CLASS_NAME, false);

                child.addSuperClass(parent);

                createDocumentsWithPrimaryTypesForOClass(parent, true);
                createDocumentsWithPrimaryTypesForOClass(child, true);

                List<ODocument> documentsForTestClass = createDocumentsWithPrimaryTypesForOClass(testClass, true);
                List<ODocument> documentsForLinkClass = createDocumentsWithPrimaryTypesForOClass(linkClass, true);
                createLinksForDocuments(documentsForTestClass, documentsForLinkClass);
                createLinkListForDocument(documentsForTestClass, createListOfDocuments(documentsForLinkClass));
                createMapDocsForDocuments(documentsForTestClass, documentsForLinkClass, false);
                createEmbeddedFieldsForDocuments(documentsForTestClass,
                        createDocumentsWithPrimaryTypesForOClass(testClass, false));
                createEmbeddedCollectionFieldsForDocuments(documentsForTestClass,
                        createListOfDocuments(createDocumentsWithPrimaryTypesForOClass(testClass, false)), true);
                createEmbeddedCollectionFieldsForDocuments(documentsForTestClass,
                        createListOfDocuments(createDocumentsWithPrimaryTypesForOClass(testClass, false)), false);
                createMapDocsForDocuments(documentsForTestClass,
                        createDocumentsWithPrimaryTypesForOClass(testClass, false), true);
                createEmbeddedStringCollectionForDocuments(documentsForTestClass,
                        createListOfStringForDocuments(), true);
                createEmbeddedStringCollectionForDocuments(documentsForTestClass,
                        createListOfStringForDocuments(), false);
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
            oClass.createProperty(LINK_LIST_FIELD, OType.LINKLIST);
            oClass.createProperty(LINK_SET_FIELD, OType.LINKSET);
            oClass.createProperty(LINK_MAP_FIELD, OType.LINKMAP);
            oClass.createProperty(EMBEDDED_FIELD, OType.EMBEDDED);
            oClass.createProperty(EMBEDDED_LIST_FIELD, OType.EMBEDDEDLIST);
            oClass.createProperty(EMBEDDED_SET_FIELD, OType.EMBEDDEDSET);
            oClass.createProperty(EMBEDDED_MAP_FIELD, OType.EMBEDDEDMAP);
            oClass.createProperty(EMBEDDED_LIST_STRING_FIELD, OType.EMBEDDEDLIST);
            oClass.createProperty(EMBEDDED_SET_STRING_FIELD, OType.EMBEDDEDSET);
        }
        initPropertiesMap(oClass.propertiesMap());
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

    private void createEmbeddedStringCollectionForDocuments(List<ODocument> documents, List<String> strings, boolean list) {
        Args.isTrue(documents.size() == strings.size(), "documents.size() == strings.size()");
        String field = list ? EMBEDDED_LIST_STRING_FIELD : EMBEDDED_SET_STRING_FIELD;
        for (int i = 0; i < documents.size(); i++) {
            ODocument document = documents.get(i);
            document.field(field, strings.get(i));
            document.save();
        }
    }

    private void createEmbeddedCollectionFieldsForDocuments(List<ODocument> documents, List<List<ODocument>> embeddedList, boolean list) {
        Args.isTrue(documents.size() == embeddedList.size(), "documents.size() == embeddedList.size()");
        String field = list ? EMBEDDED_LIST_FIELD : EMBEDDED_SET_FIELD;
        for (int i = 0; i < documents.size(); i++) {
            ODocument document = documents.get(i);
            document.field(field, embeddedList.get(i), OType.EMBEDDEDLIST);
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

    private List<String> createListOfStringForDocuments() {
        List<String> list = new ArrayList<>(4);
        list.add(STR_VALUE_1);
        list.add(STR_VALUE_2);
        list.add(STR_VALUE_3);
        list.add(STR_VALUE_4);
        return list;
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

			private static final long serialVersionUID = 1L;

			@Override
            protected Void execute(ODatabaseSession db) {
                for (OClass oClass : classes) {
                	db.command("DELETE FROM " + oClass.getName()).close();
                    db.getMetadata().getSchema().dropClass(oClass.getName());
                }
                return null;
            }
        }.execute();
    }

}