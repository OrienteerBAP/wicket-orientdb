package ru.ydn.wicket.wicketorientdb.filter;

import com.google.common.collect.Lists;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.CollectionModel;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import ru.ydn.wicket.wicketorientdb.model.ODocumentModel;
import ru.ydn.wicket.wicketorientdb.model.OQueryDataProvider;
import ru.ydn.wicket.wicketorientdb.model.OQueryModel;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.FilterCriteriaManager;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.IFilterCriteria;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.IFilterCriteriaManager;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
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
    public void testEqualsToDateFilterCriteria() throws ParseException {
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
    @SuppressWarnings("unchecked")
    public void testCollectionFilterCriteria() {
        List<IModel<?>> models = Lists.newArrayList();
        models.add(Model.of(NUM_VALUE_1));
        models.add(Model.of(NUM_VALUE_2));
        IModel<Collection<IModel<?>>> collectionModel = new CollectionModel<>(models);
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
        List<IModel<?>> models = Lists.newArrayList();
        models.add(Model.of(NUM_VALUE_1));
        models.add(Model.of(NUM_VALUE_3));
        IModel<Collection<IModel<?>>> collectionModel = new CollectionModel<>(models);
        IFilterCriteriaManager manager = new FilterCriteriaManager(wicket.getProperty(NUMBER_FIELD));
        manager.addFilterCriteria(manager.createRangeFilterCriteria(collectionModel, Model.of(true)));
        String field = wicket.getProperty(NUMBER_FIELD).getObject().getName();
        queryModel.addFilterCriteriaManager(field, manager);
        queryModel.setSort(NUMBER_FIELD, SortOrder.ASCENDING);
        assertTrue(queryModel.getObject().size() == 3);
        assertTrue(queryModel.getObject().get(0).field(NUMBER_FIELD).equals(NUM_VALUE_1));
        assertTrue(queryModel.getObject().get(1).field(NUMBER_FIELD).equals(NUM_VALUE_2));
        assertTrue(queryModel.getObject().get(2).field(NUMBER_FIELD).equals(NUM_VALUE_3));
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
        List<IModel<?>> models = Lists.newArrayList();
        models.add(Model.of(NUM_VALUE_1));
        models.add(Model.of(NUM_VALUE_2));
        IModel<Collection<IModel<?>>> collectionModel = new CollectionModel<>(models);
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
}
