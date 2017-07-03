package ru.ydn.wicket.wicketorientdb.filter;

import com.orientechnologies.orient.core.record.impl.ODocument;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import ru.ydn.wicket.wicketorientdb.model.OQueryModel;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.IFilterCriteria;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.number.EqualsNumberFilter;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.number.RangeNumberFilter;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.number.ValuesNumberFilter;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.string.ContainsStringFilter;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.string.EqualsStringFilter;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.string.StartOrEndStringFilter;

import java.util.Arrays;

import static org.junit.Assert.assertTrue;
import static ru.ydn.wicket.wicketorientdb.filter.ITesterFilterConstants.*;

public class TestFilters {

    @ClassRule
    public static WicketOrientDbFilterTesterScope wicket = new WicketOrientDbFilterTesterScope();

    private OQueryModel<ODocument> queryModel;
    private IFilterCriteria filterCriteria;

    @Before
    public void setUpOQueryModel() {
        queryModel = new OQueryModel<>("SELECT FROM " + TEST_CLASS_NAME);
        filterCriteria = queryModel.getFilterCriteria();
    }

    @After
    public void destroyOQueryModel() {
        queryModel.detach();
        filterCriteria = null;
        queryModel = null;
    }

    @Test
    public void testNumberEqualsFilter() {
        filterCriteria.setFilter(new EqualsNumberFilter(NUMBER_FIELD, NUM_VALUE_1, true));
        assertTrue(queryModel.getObject().size() == 1);
        assertTrue(NUM_VALUE_1 == (Integer) queryModel.getObject().get(0).field(NUMBER_FIELD));
        queryModel.detach();

        filterCriteria.setFilter(new EqualsNumberFilter(NUMBER_FIELD, NUM_VALUE_2, true));
        assertTrue(queryModel.getObject().size() == 1);
        assertTrue("expected: " + NUM_VALUE_2 + " get: " + queryModel.getObject().get(0).field(NUMBER_FIELD),
                NUM_VALUE_2 == (Integer) queryModel.getObject().get(0).field(NUMBER_FIELD));
        queryModel.detach();

        filterCriteria.setFilter(new EqualsNumberFilter(NUMBER_FIELD, NUM_VALUE_3, false));
        assertTrue(queryModel.getObject().size() == DOCUMENTS_NUM - 1);
        for (ODocument document : queryModel.getObject()) {
            assertTrue((Integer) document.field(NUMBER_FIELD) != NUM_VALUE_3);
        }
    }

    @Test
    public void testNumberRangeFilter() {
        filterCriteria.setFilter(new RangeNumberFilter(NUMBER_FIELD, NUM_VALUE_1, NUM_VALUE_3, true));
        assertTrue(queryModel.getObject().size() == DOCUMENTS_NUM - 1);
        for (ODocument document : queryModel.getObject()) {
            Integer number = document.field(NUMBER_FIELD);
            assertTrue("Error in range filtering", number == NUM_VALUE_1 || number == NUM_VALUE_2 || number == NUM_VALUE_3);
        }
        queryModel.detach();

        filterCriteria.setFilter(new RangeNumberFilter(NUMBER_FIELD, NUM_VALUE_1, NUM_VALUE_3, false));
        assertTrue(queryModel.getObject().size() == 1);
        assertTrue((Integer) queryModel.getObject().get(0).field(NUMBER_FIELD) == NUM_VALUE_4);
    }

    @Test
    public void testNumberValuesFilter() {
        filterCriteria.setFilter(new ValuesNumberFilter(NUMBER_FIELD,
                Arrays.asList(NUM_VALUE_1, NUM_VALUE_4), true));
        assertTrue(queryModel.getObject().size() == 2);
        for (ODocument document : queryModel.getObject()) {
            Integer number = document.field(NUMBER_FIELD);
            assertTrue("Error in values join filtering",
                    number == NUM_VALUE_1 || number == NUM_VALUE_4);
        }
        queryModel.detach();

        filterCriteria.setFilter(new ValuesNumberFilter(NUMBER_FIELD, Arrays.asList(NUM_VALUE_1, NUM_VALUE_4), false));
        assertTrue(queryModel.getObject().size() == 2);
        for (ODocument document : queryModel.getObject()) {
            Integer number = document.field(NUMBER_FIELD);
            assertTrue("Error in values no join filtering",
                    number == NUM_VALUE_2 || number == NUM_VALUE_3);
        }
        assertTrue((Integer) queryModel.getObject().get(0).field(NUMBER_FIELD) == NUM_VALUE_2);
        assertTrue((Integer) queryModel.getObject().get(1).field(NUMBER_FIELD) == NUM_VALUE_3);
    }

    @Test
    public void testStringEqualsFilter() {
        filterCriteria.setFilter(new EqualsStringFilter(STRING_FIELD, STR_VALUE_1, true));
        assertTrue(queryModel.getObject().size() == 1);
        assertTrue(queryModel.getObject().get(0).field(STRING_FIELD).equals(STR_VALUE_1));
        queryModel.detach();

        filterCriteria.setFilter(new EqualsStringFilter(STRING_FIELD, STR_VALUE_1, false));
        assertTrue(queryModel.getObject().size() == DOCUMENTS_NUM - 1);
        for (ODocument document : queryModel.getObject()) {
            String stringField = document.field(STRING_FIELD);
            assertTrue(stringField.equals(STR_VALUE_2) || stringField.equals(STR_VALUE_3)
                    || stringField.equals(STR_VALUE_4));
        }
    }

    @Test
    public void testStartOrEndStringFilter() {
        filterCriteria.setFilter(new StartOrEndStringFilter(STRING_FIELD, "string", true, true));
        assertTrue(queryModel.getObject().size() == 1);
        assertTrue(queryModel.getObject().get(0).field(STRING_FIELD).equals(STR_VALUE_1));
        queryModel.detach();

        filterCriteria.setFilter(new StartOrEndStringFilter(STRING_FIELD, "string", false, true));
        assertTrue(queryModel.getObject().size() == 0);
        queryModel.detach();

        filterCriteria.setFilter(new StartOrEndStringFilter(STRING_FIELD, "2", false, true));
        assertTrue(queryModel.getObject().size() == 1);
        assertTrue(queryModel.getObject().get(0).field(STRING_FIELD).equals(STR_VALUE_2));
        queryModel.detach();

        filterCriteria.setFilter(new StartOrEndStringFilter(STRING_FIELD, "string", true, false));
        assertTrue(queryModel.getObject().size() == DOCUMENTS_NUM - 1);
        for (ODocument document : queryModel.getObject()) {
            String stringField = document.field(STRING_FIELD);
            assertTrue(stringField.equals(STR_VALUE_2) || stringField.equals(STR_VALUE_3)
                    || stringField.equals(STR_VALUE_4));
        }
        queryModel.detach();

        filterCriteria.setFilter(new StartOrEndStringFilter(STRING_FIELD, "2", false, false));
        assertTrue(queryModel.getObject().size() == DOCUMENTS_NUM - 1);
        for (ODocument document : queryModel.getObject()) {
            String stringField = document.field(STRING_FIELD);
            assertTrue(stringField.equals(STR_VALUE_1) || stringField.equals(STR_VALUE_3)
                    || stringField.equals(STR_VALUE_4));
        }
    }

    @Test
    public void testContainsStringFilter() {
        filterCriteria.setFilter(new ContainsStringFilter(STRING_FIELD, "value", true));
        assertTrue(queryModel.getObject().size() == DOCUMENTS_NUM);
        queryModel.detach();

        filterCriteria.setFilter(new ContainsStringFilter(STRING_FIELD, "string", true));
        assertTrue(queryModel.getObject().size() == 1);
        assertTrue(queryModel.getObject().get(0).field(STRING_FIELD).equals(STR_VALUE_1));
        queryModel.detach();

        filterCriteria.setFilter(new ContainsStringFilter(STRING_FIELD, "string", false));
        assertTrue(queryModel.getObject().size() == DOCUMENTS_NUM - 1);
        for (ODocument document : queryModel.getObject()) {
            String stringField = document.field(STRING_FIELD);
            assertTrue(stringField.equals(STR_VALUE_2) || stringField.equals(STR_VALUE_3)
                    || stringField.equals(STR_VALUE_4));
        }
    }


}
