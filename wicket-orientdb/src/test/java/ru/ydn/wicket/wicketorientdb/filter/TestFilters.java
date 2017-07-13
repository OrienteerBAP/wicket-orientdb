package ru.ydn.wicket.wicketorientdb.filter;

import com.google.common.collect.Lists;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ydn.wicket.wicketorientdb.model.OQueryDataProvider;
import ru.ydn.wicket.wicketorientdb.model.OQueryModel;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.FilterCriteriaManager;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.FilterCriteriaType;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.IFilterCriteria;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.IFilterCriteriaManager;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static ru.ydn.wicket.wicketorientdb.filter.ITesterFilterConstants.*;

public class TestFilters {

    private static final Logger LOG = LoggerFactory.getLogger(TestFilters.class);

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
        manager.setFilterCriteria(FilterCriteriaType.EQUALS, equalsFilterCriteria);
        String numField = wicket.getProperty(NUMBER_FIELD).getObject().getName();
        queryModel.addFilterCriteriaManager(numField, manager);
        assertTrue(queryModel.getObject().size() == 1);
        assertTrue(queryModel.getObject().get(0).field(NUMBER_FIELD).equals(NUM_VALUE_1));
        queryModel.detach();

        manager = new FilterCriteriaManager(wicket.getProperty(STRING_FIELD));
        equalsFilterCriteria = manager.createEqualsFilterCriteria(Model.of(STR_VALUE_1), Model.of(true));
        manager.setFilterCriteria(FilterCriteriaType.EQUALS, equalsFilterCriteria);
        String strField = wicket.getProperty(STRING_FIELD).getObject().getName();
        queryModel.addFilterCriteriaManager(strField, manager);
        assertTrue(queryModel.getObject().size() == 1);
        assertTrue(queryModel.getObject().get(0).field(STRING_FIELD).equals(STR_VALUE_1));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testListFilterCriteria() {
        List<IModel<Integer>> models = Lists.newArrayList();
        models.add(Model.of(NUM_VALUE_1));
        models.add(Model.of(NUM_VALUE_2));
        IFilterCriteriaManager manager = new FilterCriteriaManager(wicket.getProperty(NUMBER_FIELD));
        manager.setFilterCriteria(FilterCriteriaType.LIST, manager.createListFilterCriteria(models, Model.of(true)));
        String field = wicket.getProperty(NUMBER_FIELD).getObject().getName();
        queryModel.addFilterCriteriaManager(field, manager);
        queryModel.setSort(NUMBER_FIELD, SortOrder.ASCENDING);
        assertTrue(queryModel.getObject().size() == 2);
        assertTrue(queryModel.getObject().get(0).field(NUMBER_FIELD).equals(NUM_VALUE_1));
        assertTrue(queryModel.getObject().get(1).field(NUMBER_FIELD).equals(NUM_VALUE_2));
    }

    @Test
    public void testRangeFilterCriteria() {
        List<IModel<Integer>> models = Lists.newArrayList();
        models.add(Model.of(NUM_VALUE_1));
        models.add(Model.of(NUM_VALUE_3));
        IFilterCriteriaManager manager = new FilterCriteriaManager(wicket.getProperty(NUMBER_FIELD));
        manager.setFilterCriteria(FilterCriteriaType.LIST, manager.createRangeFilterCriteria(models, Model.of(true)));
        String field = wicket.getProperty(NUMBER_FIELD).getObject().getName();
        queryModel.addFilterCriteriaManager(field, manager);
        queryModel.setSort(NUMBER_FIELD, SortOrder.ASCENDING);
        assertTrue(queryModel.getObject().size() == 3);
        assertTrue(queryModel.getObject().get(0).field(NUMBER_FIELD).equals(NUM_VALUE_1));
        assertTrue(queryModel.getObject().get(1).field(NUMBER_FIELD).equals(NUM_VALUE_2));
        assertTrue(queryModel.getObject().get(2).field(NUMBER_FIELD).equals(NUM_VALUE_3));
    }


    @Test
    public void testStartOrEndStringFilterCriteria() {
        IFilterCriteriaManager manager = new FilterCriteriaManager(wicket.getProperty(STRING_FIELD));
        IFilterCriteria criteria = manager.createStartOrEndStringFilterCriteria(Model.of("s"), true, Model.of(true));
        manager.setFilterCriteria(FilterCriteriaType.STRING_START, criteria);
        String field = wicket.getProperty(STRING_FIELD).getObject().getName();
        queryModel.addFilterCriteriaManager(field, manager);
        queryModel.setSort(NUMBER_FIELD, SortOrder.ASCENDING);
        assertTrue(queryModel.getObject().size() == 3);
        assertTrue(queryModel.getObject().get(0).field(STRING_FIELD).equals(STR_VALUE_1));
        assertTrue(queryModel.getObject().get(1).field(STRING_FIELD).equals(STR_VALUE_2));
        assertTrue(queryModel.getObject().get(2).field(STRING_FIELD).equals(STR_VALUE_4));
        queryModel.detach();
        manager.clearFilterCriterias();

        criteria = manager.createStartOrEndStringFilterCriteria(Model.of("1"), false, Model.of(true));
        manager.setFilterCriteria(FilterCriteriaType.STRING_END, criteria);
        assertTrue(queryModel.getObject().size() == 1);
        assertTrue(queryModel.getObject().get(0).field(STRING_FIELD).equals(STR_VALUE_1));

    }

    @Test
    @SuppressWarnings("unchecked")
    public void testProvider() {
        IFilterCriteriaManager manager = new FilterCriteriaManager(wicket.getProperty(NUMBER_FIELD));
        IFilterCriteria equalsFilterCriteria = manager.createEqualsFilterCriteria(Model.of(NUM_VALUE_1), Model.of(true));
        manager.setFilterCriteria(FilterCriteriaType.EQUALS, equalsFilterCriteria);
        String numField = wicket.getProperty(NUMBER_FIELD).getObject().getName();
        queryModel.addFilterCriteriaManager(numField, manager);
        OQueryDataProvider provider = new OQueryDataProvider(queryModel);
        assertTrue(queryModel.size() == queryModel.getObject().size());
        assertTrue(provider.size() == queryModel.getObject().size());
    }
}
