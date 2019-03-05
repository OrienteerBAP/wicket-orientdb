package ru.ydn.wicket.wicketorientdb.filter;

import com.google.common.collect.Lists;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.CollectionModel;
import org.apache.wicket.model.util.ListModel;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import ru.ydn.wicket.wicketorientdb.model.ODocumentLinksQueryDataProvider;
import ru.ydn.wicket.wicketorientdb.model.ODocumentModel;
import ru.ydn.wicket.wicketorientdb.model.OQueryDataProvider;
import ru.ydn.wicket.wicketorientdb.model.OQueryModel;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.FilterCriteriaManager;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.IFilterCriteria;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.IFilterCriteriaManager;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.*;
import static ru.ydn.wicket.wicketorientdb.filter.ITesterFilterConstants.*;

public class TestFilters {
    @ClassRule
    public static WicketOrientDbFilterTesterScope wicket = new WicketOrientDbFilterTesterScope();

    private OQueryModel<ODocument> queryModel;

    @Before
    public void setUpOQueryModel() {
        queryModel = new OQueryModel<>("SELECT FROM " + TEST_CLASS_NAME);
    }

    @After
    public void destroyOQueryModel() {
        queryModel.detach();
        queryModel = null;
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testEqualsFilterCriteria() {
        IFilterCriteriaManager manager = new FilterCriteriaManager(wicket.getProperty(NUMBER_FIELD));
        IFilterCriteria equalsFilterCriteria = manager.createEqualsFilterCriteria(Model.of(NUM_VALUE_1), Model.of(true));
        manager.addFilterCriteria(equalsFilterCriteria);
        String numField = wicket.getProperty(NUMBER_FIELD).getObject().getName();
        queryModel.addFilterCriteriaManager(numField, manager);
        assertTrue(queryModel.getObject().size() == 1);
        assertTrue(queryModel.getObject().get(0).field(NUMBER_FIELD).equals(NUM_VALUE_1));
        queryModel.detach();

        manager = new FilterCriteriaManager(wicket.getProperty(STRING_FIELD));
        equalsFilterCriteria = manager.createEqualsFilterCriteria(Model.of(STR_VALUE_1), Model.of(true));
        manager.addFilterCriteria(equalsFilterCriteria);
        String strField = wicket.getProperty(STRING_FIELD).getObject().getName();
        queryModel.addFilterCriteriaManager(strField, manager);
        assertTrue(queryModel.getObject().size() == 1);
        assertTrue(queryModel.getObject().get(0).field(STRING_FIELD).equals(STR_VALUE_1));
    }

    @Test
    public void testEqualsToDateTimeFilterCriteria() throws ParseException {
        IModel<OProperty> property = wicket.getProperty(DATETIME_FIELD);
        IFilterCriteriaManager manager = new FilterCriteriaManager(property);
        IModel<String> model = Model.of(DATETIME_VALUE_1);
        manager.addFilterCriteria(manager.createEqualsFilterCriteria(model, Model.of(true)));
        queryModel.addFilterCriteriaManager(property.getObject().getName(), manager);
        assertTrue(queryModel.size() == 1);
        queryModel.clearFilterCriteriaManagers();
        queryModel.detach();

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        IModel<Date> dateModel = Model.of(dateFormat.parse(DATETIME_VALUE_1));
        manager.addFilterCriteria(manager.createEqualsFilterCriteria(dateModel, Model.of(true)));
        queryModel.addFilterCriteriaManager(property.getObject().getName(), manager);
        assertTrue(queryModel.size() == 1);
    }

    @Test
    public void testEqualsToDateFilterCriteria() throws ParseException {
        IModel<OProperty> property = wicket.getProperty(DATE_FIELD);
        IFilterCriteriaManager manager = new FilterCriteriaManager(property);
        IModel<String> model = Model.of(DATE_VALUE_1);
        manager.addFilterCriteria(manager.createEqualsFilterCriteria(model, Model.of(true)));
        queryModel.addFilterCriteriaManager(property.getObject().getName(), manager);
        assertTrue(queryModel.size() == 1);
        queryModel.clearFilterCriteriaManagers();
        queryModel.detach();

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        IModel<Date> dateModel = Model.of(dateFormat.parse(DATE_VALUE_1));
        manager.addFilterCriteria(manager.createEqualsFilterCriteria(dateModel, Model.of(true)));
        queryModel.addFilterCriteriaManager(property.getObject().getName(), manager);
        assertTrue(queryModel.size() == 1);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCollectionFilterCriteria() {
        List<Integer> models = Lists.newArrayList();
        models.add(NUM_VALUE_1);
        models.add(NUM_VALUE_2);
        IModel<Collection<Integer>> collectionModel = new CollectionModel<>(models);
        IFilterCriteriaManager manager = new FilterCriteriaManager(wicket.getProperty(NUMBER_FIELD));
        manager.addFilterCriteria(manager.createCollectionFilterCriteria(collectionModel, Model.of(true)));
        String field = wicket.getProperty(NUMBER_FIELD).getObject().getName();
        queryModel.addFilterCriteriaManager(field, manager);
        queryModel.setSort(NUMBER_FIELD, SortOrder.ASCENDING);
        assertTrue(queryModel.getObject().size() == 2);
        assertTrue(queryModel.getObject().get(0).field(NUMBER_FIELD).equals(NUM_VALUE_1));
        assertTrue(queryModel.getObject().get(1).field(NUMBER_FIELD).equals(NUM_VALUE_2));
    }

    @Test
    public void testRangeFilterCriteria() {
        List<Integer> models = Lists.newArrayList();
        models.add(NUM_VALUE_1);
        models.add(NUM_VALUE_3);
        IModel<List<Integer>> listModel = new ListModel<>(models);
        IFilterCriteriaManager manager = new FilterCriteriaManager(wicket.getProperty(NUMBER_FIELD));
        manager.addFilterCriteria(manager.createRangeFilterCriteria(listModel, Model.of(true)));
        String field = wicket.getProperty(NUMBER_FIELD).getObject().getName();
        queryModel.addFilterCriteriaManager(field, manager);
        queryModel.setSort(NUMBER_FIELD, SortOrder.ASCENDING);
        assertTrue("size must be 3, but it is - " + queryModel.size(), queryModel.size() == 3);
        assertTrue(queryModel.getObject().get(0).field(NUMBER_FIELD).equals(NUM_VALUE_1));
        assertTrue(queryModel.getObject().get(1).field(NUMBER_FIELD).equals(NUM_VALUE_2));
        assertTrue(queryModel.getObject().get(2).field(NUMBER_FIELD).equals(NUM_VALUE_3));
    }

    @Test
    public void testRangeFilterCriteriaSecondNull() {
        List<Integer> models = Lists.newArrayList();
        models.add(NUM_VALUE_3);
        models.add(null);
        IModel<List<Integer>> listModel = new ListModel<>(models);
        IFilterCriteriaManager manager = new FilterCriteriaManager(wicket.getProperty(NUMBER_FIELD));
        manager.addFilterCriteria(manager.createRangeFilterCriteria(listModel, Model.of(true)));
        String field = wicket.getProperty(NUMBER_FIELD).getObject().getName();
        queryModel.addFilterCriteriaManager(field, manager);
        queryModel.setSort(NUMBER_FIELD, SortOrder.ASCENDING);
        assertTrue("size must be 2, but it is - " + queryModel.size(), queryModel.size() == 2);
    }

    @Test
    public void testRangeFilterCriteriaFirstNull() {
        List<Integer> models = Lists.newArrayList();
        models.add(null);
        models.add(NUM_VALUE_2);
        IModel<List<Integer>> listModel = new ListModel<>(models);
        IFilterCriteriaManager manager = new FilterCriteriaManager(wicket.getProperty(NUMBER_FIELD));
        manager.addFilterCriteria(manager.createRangeFilterCriteria(listModel, Model.of(true)));
        String field = wicket.getProperty(NUMBER_FIELD).getObject().getName();
        queryModel.addFilterCriteriaManager(field, manager);
        queryModel.setSort(NUMBER_FIELD, SortOrder.ASCENDING);
        assertTrue("size must be 2, but it is - " + queryModel.size(), queryModel.size() == 2);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testProvider() {
        IFilterCriteriaManager manager = new FilterCriteriaManager(wicket.getProperty(NUMBER_FIELD));
        IFilterCriteria equalsFilterCriteria = manager.createEqualsFilterCriteria(Model.of(NUM_VALUE_1), Model.of(true));
        manager.addFilterCriteria(equalsFilterCriteria);
        String numField = wicket.getProperty(NUMBER_FIELD).getObject().getName();
        queryModel.addFilterCriteriaManager(numField, manager);
        OQueryDataProvider provider = new OQueryDataProvider(queryModel);
        assertTrue(queryModel.size() == queryModel.getObject().size());
        assertTrue(provider.size() == queryModel.getObject().size());
    }

    @Test
    public void testLinkFilter() {
        IModel<OProperty> property = wicket.getProperty(NUMBER_FIELD);
        IFilterCriteriaManager manager = new FilterCriteriaManager(property);
        IFilterCriteria equalsFilterCriteria = manager.createEqualsFilterCriteria(Model.of(NUM_VALUE_1), Model.of(true));
        manager.addFilterCriteria(equalsFilterCriteria);
        queryModel.addFilterCriteriaManager(property.getObject().getName(), manager);
        ODocument document = queryModel.getObject().get(0).field(LINK_FIELD);
        queryModel.clearFilterCriteriaManagers();
        queryModel.detach();

        property = wicket.getProperty(LINK_FIELD);
        manager = new FilterCriteriaManager(property);
        equalsFilterCriteria = manager.createEqualsFilterCriteria(new ODocumentModel(document), Model.of(true));
        manager.addFilterCriteria(equalsFilterCriteria);
        queryModel.addFilterCriteriaManager(property.getObject().getName(), manager);
        assertTrue(queryModel.size() == 1);
        assertTrue(queryModel.getObject().get(0).field(STRING_FIELD).equals(STR_VALUE_1));
    }

    @Test
    public void testLinkCollectionFilter() {
        IFilterCriteriaManager manager = new FilterCriteriaManager(wicket.getProperty(NUMBER_FIELD));
        List<Integer> models = Lists.newArrayList();
        models.add(NUM_VALUE_1);
        models.add(NUM_VALUE_2);
        IModel<Collection<Integer>> collectionModel = new CollectionModel<>(models);
        IFilterCriteria collectionFilterCriteria = manager.createCollectionFilterCriteria(collectionModel, Model.of(true));
        manager.addFilterCriteria(collectionFilterCriteria);
        String numField = wicket.getProperty(NUMBER_FIELD).getObject().getName();
        queryModel.addFilterCriteriaManager(numField, manager);

        manager = new FilterCriteriaManager(wicket.getProperty(LINK_FIELD));
        ODocument doc1 = queryModel.getObject().get(0).field(LINK_FIELD);
        ODocument doc2 = queryModel.getObject().get(1).field(LINK_FIELD);
        IFilterCriteria criteria = manager.createLinkCollectionFilterCriteria(new CollectionModel<>(Arrays.asList(doc1, doc2)), true, Model.of(true));
        queryModel.detach();

        manager.addFilterCriteria(criteria);
        queryModel.clearFilterCriteriaManagers();
        queryModel.addFilterCriteriaManager(wicket.getProperty(LINK_FIELD).getObject().getName(), manager);
        assertTrue(queryModel.getObject().size() == 2);
    }

    @Test
    public void testContainsTextFilterCriteria() {
        IModel<OProperty> property = wicket.getProperty(STRING_FIELD);
        IFilterCriteriaManager manager = new FilterCriteriaManager(property);
        IModel<String> model = Model.of("summer");
        manager.addFilterCriteria(manager.createContainsStringFilterCriteria(model, Model.of(true)));
        queryModel.addFilterCriteriaManager(property.getObject().getName(), manager);
        assertTrue(queryModel.size() == 1);
        assertTrue(queryModel.getObject().get(0).field(STRING_FIELD).equals(STR_VALUE_2));
    }


    @Test
    public void testFilterCriteriaManager() {
        IFilterCriteriaManager manager = new FilterCriteriaManager(wicket.getProperty(STRING_FIELD));
        assertFalse(manager.isFilterApply());
        manager.addFilterCriteria(manager.createEqualsFilterCriteria(Model.of(NUM_VALUE_1), Model.of(true)));
        assertTrue(manager.isFilterApply());
        manager.clearFilterCriterias();
        assertFalse(manager.isFilterApply());

        IModel<Integer> model = Model.of(NUM_VALUE_1);
        manager.addFilterCriteria(manager.createEqualsFilterCriteria(model, Model.of(true)));
        assertTrue(manager.isFilterApply());
        model.setObject(null);
        assertFalse(manager.isFilterApply());
    }

    @Test
    public void testEqualsFilterCriteriaForNull() {
        for (IModel<OProperty> property : wicket.getProperties()) {
            testNewManager(property);
        }
    }

    private void testNewManager(IModel<OProperty> property) {
        IFilterCriteriaManager manager = new FilterCriteriaManager(property);
        manager.addFilterCriteria(manager.createEqualsFilterCriteria(Model.of(), Model.<Boolean>of(true)));
        queryModel.addFilterCriteriaManager(property.getObject().getName(), manager);
        assertTrue(queryModel.size() == 4);
        queryModel.detach();
        queryModel.clearFilterCriteriaManagers();
    }

    @Test
    public void testDateTimeCollection() throws ParseException {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<Date> list = Lists.newArrayList();
        list.add(format.parse(DATETIME_VALUE_1));
        list.add(format.parse(DATETIME_VALUE_2));
        IModel<OProperty> property = wicket.getProperty(DATETIME_FIELD);
        IFilterCriteriaManager manager = new FilterCriteriaManager(property);
        manager.addFilterCriteria(manager.createCollectionFilterCriteria(new CollectionModel<>(list), Model.of(true)));
        queryModel.addFilterCriteriaManager(property.getObject().getName(), manager);
        assertTrue(queryModel.getObject().size() == 2);
    }

    @Test
    public void testEmbeddedMapKey() {
        String key = MAP_KEYS.get(0);
        IModel<OProperty> property = wicket.getProperty(EMBEDDED_MAP_FIELD);
        IFilterCriteriaManager manager = new FilterCriteriaManager(property);
        manager.addFilterCriteria(manager.createMapContainsKeyCriteria(Model.of(key), Model.<Boolean>of(true)));
        queryModel.addFilterCriteriaManager(EMBEDDED_MAP_FIELD, manager);
        assertTrue("size must be 1 but it is - " + queryModel.getObject().size() , queryModel.getObject().size() == 1);
    }

    @Test
    public void testLinkMapKey() {
        String key = MAP_KEYS.get(0);
        IModel<OProperty> property = wicket.getProperty(LINK_MAP_FIELD);
        IFilterCriteriaManager manager = new FilterCriteriaManager(property);
        manager.addFilterCriteria(manager.createMapContainsKeyCriteria(Model.of(key), Model.<Boolean>of(true)));
        queryModel.addFilterCriteriaManager(LINK_MAP_FIELD, manager);
        assertTrue("size must be 1 but it is - " + queryModel.getObject().size() , queryModel.getObject().size() == 1);
    }

    @Test
    public void testEmbeddedMapValue() {
        IModel<OProperty> property = wicket.getProperty(EMBEDDED_MAP_FIELD);
        IFilterCriteriaManager manager = new FilterCriteriaManager(property);
        manager.addFilterCriteria(manager.createMapContainsValueCriteria(Model.of(STR_VALUE_1), Model.<Boolean>of(true)));
        queryModel.addFilterCriteriaManager(EMBEDDED_MAP_FIELD, manager);
        assertTrue("size must be 1 but it is - " + queryModel.getObject().size() , queryModel.getObject().size() == 1);
    }

    @Test
    public void testLinkMapValue() {
        String key = MAP_KEYS.get(0);
        IModel<OProperty> property = wicket.getProperty(LINK_MAP_FIELD);
        IFilterCriteriaManager manager = new FilterCriteriaManager(property);
        manager.addFilterCriteria(manager.createMapContainsKeyCriteria(Model.of(key), Model.<Boolean>of(true)));
        queryModel.addFilterCriteriaManager(LINK_MAP_FIELD, manager);
        ODocument document = queryModel.getObject().get(0);
        Map<String, ODocument> map = document.field(LINK_MAP_FIELD);
        manager.clearFilterCriterias();
        queryModel.detach();
        manager.addFilterCriteria(manager.createMapContainsValueCriteria(Model.of(map.get(STR_VALUE_1)), Model.<Boolean>of(true)));
        assertTrue("size must be 1, but it is - " + queryModel.size(), queryModel.size() == 1);
    }

    @Test
    public void testEmbeddedContainsValueFilter() {
        IFilterCriteriaManager manager = new FilterCriteriaManager(wicket.getProperty(EMBEDDED_FIELD));
        IFilterCriteria criteria = manager.createEmbeddedContainsValueCriteria(Model.of(STR_VALUE_2), Model.of(true));
        manager.addFilterCriteria(criteria);
        queryModel.addFilterCriteriaManager(EMBEDDED_FIELD, manager);
        assertTrue("size must be 1, but it is - " + queryModel.size(), queryModel.size() == 1);
    }

    @Test
    public void testEmbeddedContainsKeyFilter() {
        IFilterCriteriaManager manager = new FilterCriteriaManager(wicket.getProperty(EMBEDDED_FIELD));
        IFilterCriteria criteria = manager.createEmbeddedContainsKeyCriteria(Model.of(STRING_FIELD), Model.of(true));
        manager.addFilterCriteria(criteria);
        queryModel.addFilterCriteriaManager(EMBEDDED_FIELD, manager);
        assertTrue("size must be more than 0, but it is - " + queryModel.size(), queryModel.size() > 0);
    }

    @Test
    public void testEmbeddedListFilter() {
        IFilterCriteriaManager manager = new FilterCriteriaManager(wicket.getProperty(EMBEDDED_LIST_FIELD));
        List<String> list = new ArrayList<>();
        list.add(STR_VALUE_2);
        IModel<Collection<String>> model = new CollectionModel<String>(list);
        IFilterCriteria criteria = manager.createEmbeddedCollectionCriteria(Model.of(STRING_FIELD), model, Model.of(true));
        manager.addFilterCriteria(criteria);
        queryModel.addFilterCriteriaManager(EMBEDDED_LIST_FIELD, manager);
        assertTrue("size must be more than 0, but it is - " + queryModel.size(), queryModel.size() > 0);
    }

    @Test
    public void testEmbeddedSetFilter() {
        IFilterCriteriaManager manager = new FilterCriteriaManager(wicket.getProperty(EMBEDDED_SET_FIELD));
        List<String> list = new ArrayList<>();
        list.add(STR_VALUE_2);
        IModel<Collection<String>> model = new CollectionModel<String>(list);
        IFilterCriteria criteria = manager.createEmbeddedCollectionCriteria(Model.of(STRING_FIELD), model, Model.of(true));
        manager.addFilterCriteria(criteria);
        queryModel.addFilterCriteriaManager(EMBEDDED_SET_FIELD, manager);
        assertTrue("size must be more than 0, but it is - " + queryModel.size(), queryModel.size() > 0);
    }


    @Test
    public void testEmbeddedListContainsValueFilter() {
        IFilterCriteriaManager manager = new FilterCriteriaManager(wicket.getProperty(EMBEDDED_LIST_STRING_FIELD));
        IFilterCriteria criteria = manager.createEmbeddedCollectionContainsValueCriteria(Model.of(STR_VALUE_1), Model.of(true));
        manager.addFilterCriteria(criteria);
        queryModel.addFilterCriteriaManager(EMBEDDED_LIST_STRING_FIELD, manager);
        assertTrue("size must 1, but it is - " + queryModel.size(), queryModel.size() == 1);
    }

    @Test
    public void testODocumentLinkQueryProvider() {
        IFilterCriteriaManager manager = new FilterCriteriaManager(wicket.getProperty(NUMBER_FIELD));
        IFilterCriteria equalsFilterCriteria = manager.createEqualsFilterCriteria(Model.of(NUM_VALUE_1), Model.of(true));
        manager.addFilterCriteria(equalsFilterCriteria);
        String numField = wicket.getProperty(NUMBER_FIELD).getObject().getName();
        queryModel.addFilterCriteriaManager(numField, manager);
        ODocument doc = queryModel.getObject().get(0);
        IModel<OProperty> property = wicket.getProperty(LINK_LIST_FIELD);
        ODocumentLinksQueryDataProvider provider = new ODocumentLinksQueryDataProvider(new ODocumentModel(doc), property);
        OQueryModel<ODocument> state = provider.getFilterState();
        state.addFilterCriteriaManager(numField, manager);
        state.detach();
        assertTrue("size must be 1, but it is " + provider.size(), provider.size() == 1);
        assertTrue(state.getObject().get(0) != null);
    }

    @Test
    public void testClassInstanceOfFilter() {
        OQueryModel<ODocument> queryModel = new OQueryModel<>("SELECT FROM " + PARENT_CLASS_NAME);

        IFilterCriteriaManager manager = new FilterCriteriaManager("@class");
        IFilterCriteria criteria = manager.createClassInstanceOfCriteria(Model.of(PARENT_CLASS_NAME), Model.of(true));
        manager.addFilterCriteria(criteria);

        queryModel.addFilterCriteriaManager("@class", manager);

        List<ODocument> docs = queryModel.getObject();
        assertFalse(docs.isEmpty());
        assertEquals(8, docs.size());

        queryModel.detach();
        queryModel.clearFilterCriteriaManagers();

        criteria = manager.createClassInstanceOfCriteria(Model.of(CHILD_CLASS_NAME), Model.of(true));
        manager.addFilterCriteria(criteria);
        queryModel.addFilterCriteriaManager("@class", manager);

        docs = queryModel.getObject();
        assertFalse(docs.isEmpty());
        assertEquals(4, docs.size());
    }

}
