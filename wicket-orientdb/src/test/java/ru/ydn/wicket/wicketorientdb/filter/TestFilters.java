package ru.ydn.wicket.wicketorientdb.filter;

import com.google.common.collect.Maps;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import ru.ydn.wicket.wicketorientdb.model.OQueryModel;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.date.EqualsDateFilterCriteria;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.date.RangeOfDateFilterCriteria;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.date.ValuesOfDateFilterCriteria;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.link.EqualsLinkFilterCriteria;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.number.EqualsNumberFilterCriteria;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.number.RangeOfNumberFilterCriteria;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.number.ValuesOfNumberFilterCriteria;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.string.ContainsStringFilterCriteria;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.string.EqualsStringFilterCriteria;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.string.StartOrEndStringFilterCriteria;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

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
    public void testNumberEqualsFilter() {
        queryModel.setFilterCriteria(new EqualsNumberFilterCriteria(NUMBER_FIELD, NUM_VALUE_1, true));
        assertTrue(queryModel.getObject().size() == 1);
        assertTrue(NUM_VALUE_1 == (Integer) queryModel.getObject().get(0).field(NUMBER_FIELD));
        queryModel.detach();

        queryModel.setFilterCriteria(new EqualsNumberFilterCriteria(NUMBER_FIELD, NUM_VALUE_2, true));
        assertTrue(queryModel.getObject().size() == 1);
        assertTrue("expected: " + NUM_VALUE_2 + " get: " + queryModel.getObject().get(0).field(NUMBER_FIELD),
                NUM_VALUE_2 == (Integer) queryModel.getObject().get(0).field(NUMBER_FIELD));
        queryModel.detach();

        queryModel.setFilterCriteria(new EqualsNumberFilterCriteria(NUMBER_FIELD, NUM_VALUE_3, false));
        assertTrue(queryModel.getObject().size() == DOCUMENTS_NUM - 1);
        for (ODocument document : queryModel.getObject()) {
            assertTrue((Integer) document.field(NUMBER_FIELD) != NUM_VALUE_3);
        }
    }

    @Test
    public void testRangeOfNumberFilter() {
        queryModel.setFilterCriteria(new RangeOfNumberFilterCriteria(NUMBER_FIELD, NUM_VALUE_1, NUM_VALUE_3, true));
        assertTrue(queryModel.getObject().size() == DOCUMENTS_NUM - 1);
        for (ODocument document : queryModel.getObject()) {
            Integer number = document.field(NUMBER_FIELD);
            assertTrue("Error in range filtering", number == NUM_VALUE_1 || number == NUM_VALUE_2 || number == NUM_VALUE_3);
        }
        queryModel.detach();

        queryModel.setFilterCriteria(new RangeOfNumberFilterCriteria(NUMBER_FIELD, NUM_VALUE_1, NUM_VALUE_3, false));
        assertTrue(queryModel.getObject().size() == 1);
        assertTrue((Integer) queryModel.getObject().get(0).field(NUMBER_FIELD) == NUM_VALUE_4);
    }

    @Test
    public void testNumberValuesFilter() {
        queryModel.setFilterCriteria(new ValuesOfNumberFilterCriteria(NUMBER_FIELD,
                Arrays.asList(NUM_VALUE_1, NUM_VALUE_4), true));
        assertTrue(queryModel.getObject().size() == 2);
        for (ODocument document : queryModel.getObject()) {
            Integer number = document.field(NUMBER_FIELD);
            assertTrue("Error in values join filtering",
                    number == NUM_VALUE_1 || number == NUM_VALUE_4);
        }
        queryModel.detach();

        queryModel.setFilterCriteria(new ValuesOfNumberFilterCriteria(NUMBER_FIELD, Arrays.asList(NUM_VALUE_1, NUM_VALUE_4), false));
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
        queryModel.setFilterCriteria(new EqualsStringFilterCriteria(STRING_FIELD, STR_VALUE_1, true));
        assertTrue(queryModel.getObject().size() == 1);
        assertTrue(queryModel.getObject().get(0).field(STRING_FIELD).equals(STR_VALUE_1));
        queryModel.detach();

        queryModel.setFilterCriteria(new EqualsStringFilterCriteria(STRING_FIELD, STR_VALUE_1, false));
        assertTrue(queryModel.getObject().size() == DOCUMENTS_NUM - 1);
        for (ODocument document : queryModel.getObject()) {
            String stringField = document.field(STRING_FIELD);
            assertTrue(stringField.equals(STR_VALUE_2) || stringField.equals(STR_VALUE_3)
                    || stringField.equals(STR_VALUE_4));
        }
    }

    @Test
    public void testStartOrEndStringFilter() {
        queryModel.setFilterCriteria(new StartOrEndStringFilterCriteria(STRING_FIELD, "string", true, true));
        assertTrue(queryModel.getObject().size() == 1);
        assertTrue(queryModel.getObject().get(0).field(STRING_FIELD).equals(STR_VALUE_1));
        queryModel.detach();

        queryModel.setFilterCriteria(new StartOrEndStringFilterCriteria(STRING_FIELD, "string", false, true));
        assertTrue(queryModel.getObject().size() == 0);
        queryModel.detach();

        queryModel.setFilterCriteria(new StartOrEndStringFilterCriteria(STRING_FIELD, "2", false, true));
        assertTrue(queryModel.getObject().size() == 1);
        assertTrue(queryModel.getObject().get(0).field(STRING_FIELD).equals(STR_VALUE_2));
        queryModel.detach();

        queryModel.setFilterCriteria(new StartOrEndStringFilterCriteria(STRING_FIELD, "string", true, false));
        assertTrue(queryModel.getObject().size() == DOCUMENTS_NUM - 1);
        for (ODocument document : queryModel.getObject()) {
            String stringField = document.field(STRING_FIELD);
            assertTrue(stringField.equals(STR_VALUE_2) || stringField.equals(STR_VALUE_3)
                    || stringField.equals(STR_VALUE_4));
        }
        queryModel.detach();

        queryModel.setFilterCriteria(new StartOrEndStringFilterCriteria(STRING_FIELD, "2", false, false));
        assertTrue(queryModel.getObject().size() == DOCUMENTS_NUM - 1);
        for (ODocument document : queryModel.getObject()) {
            String stringField = document.field(STRING_FIELD);
            assertTrue(stringField.equals(STR_VALUE_1) || stringField.equals(STR_VALUE_3)
                    || stringField.equals(STR_VALUE_4));
        }
    }

    @Test
    public void testContainsStringFilter() {
        queryModel.setFilterCriteria(new ContainsStringFilterCriteria(STRING_FIELD, "value", true));
        assertTrue(queryModel.getObject().size() == DOCUMENTS_NUM);
        queryModel.detach();

        queryModel.setFilterCriteria(new ContainsStringFilterCriteria(STRING_FIELD, "string", true));
        assertTrue(queryModel.getObject().size() == 1);
        assertTrue(queryModel.getObject().get(0).field(STRING_FIELD).equals(STR_VALUE_1));
        queryModel.detach();

        queryModel.setFilterCriteria(new ContainsStringFilterCriteria(STRING_FIELD, "string", false));
        assertTrue(queryModel.getObject().size() == DOCUMENTS_NUM - 1);
        for (ODocument document : queryModel.getObject()) {
            String stringField = document.field(STRING_FIELD);
            assertTrue(stringField.equals(STR_VALUE_2) || stringField.equals(STR_VALUE_3)
                    || stringField.equals(STR_VALUE_4));
        }
    }

    @Test
    public void testDateEqualsFilter() throws Exception {
        String dateFormat = "yyyy-MM-dd";
        DateFormat df = new SimpleDateFormat(dateFormat);

        queryModel.setFilterCriteria(new EqualsDateFilterCriteria(DATE_FIELD, df.parse(DATE_VALUE_1), dateFormat, true));
        assertTrue(queryModel.getObject().size() == 1);
        assertTrue(df.format((Date) queryModel.getObject().get(0).field(DATE_FIELD)).equals(DATE_VALUE_1));
        queryModel.detach();

        queryModel.setFilterCriteria(new EqualsDateFilterCriteria(DATE_FIELD, df.parse(DATE_VALUE_1), dateFormat, false));
        assertTrue(queryModel.getObject().size() == DOCUMENTS_NUM - 1);
        for (ODocument document : queryModel.getObject()) {
            Date date = document.field(DATE_FIELD);
            String dateString = df.format(date);
            assertTrue(dateString.equals(DATE_VALUE_2) || dateString.equals(DATE_VALUE_3)
                    || dateString.equals(DATE_VALUE_4));
        }
    }

    @Test
    public void testDateTimeEqualsFilter() throws Exception {
        String dateTimeFormat = "yyyy-MM-dd HH:mm:ss";
        DateFormat df = new SimpleDateFormat(dateTimeFormat);

        queryModel.setFilterCriteria(new EqualsDateFilterCriteria(DATETIME_FIELD, df.parse(DATETIME_VALUE_1), dateTimeFormat, true));
        assertTrue(queryModel.getObject().size() == 1);
        assertTrue(df.format((Date) queryModel.getObject().get(0).field(DATETIME_FIELD)).equals(DATETIME_VALUE_1));
        queryModel.detach();

        queryModel.setFilterCriteria(new EqualsDateFilterCriteria(DATETIME_FIELD, df.parse(DATETIME_VALUE_1), dateTimeFormat, false));
        assertTrue(queryModel.getObject().size() == DOCUMENTS_NUM - 1);
        for (ODocument document : queryModel.getObject()) {
            Date date = document.field(DATETIME_FIELD);
            String dateString = df.format(date);
            assertTrue(dateString.equals(DATETIME_VALUE_2) || dateString.equals(DATETIME_VALUE_3)
                    || dateString.equals(DATETIME_VALUE_4));
        }
    }

    @Test
    public void testRangeOfDateFilter() throws Exception {
        String dateFormat = "yyyy-MM-dd";
        DateFormat df = new SimpleDateFormat(dateFormat);

        queryModel.setFilterCriteria(new RangeOfDateFilterCriteria(DATE_FIELD, df.parse(DATE_VALUE_1), df.parse(DATE_VALUE_3),
                dateFormat, true));
        assertTrue(queryModel.getObject().size() == DOCUMENTS_NUM - 1);
        for (ODocument document : queryModel.getObject()) {
            Date date = document.field(DATE_FIELD);
            String dateString = df.format(date);
            assertTrue(dateString.equals(DATE_VALUE_1)
                    || dateString.equals(DATE_VALUE_2)
                    || dateString.equals(DATE_VALUE_3));
            assertFalse(dateString.equals(DATE_VALUE_4));
        }
        queryModel.detach();

        queryModel.setFilterCriteria(new RangeOfDateFilterCriteria(DATE_FIELD, df.parse(DATE_VALUE_1), df.parse(DATE_VALUE_3),
                dateFormat, false));
        assertTrue(queryModel.getObject().size() == 1);
        assertTrue(df.format((Date) queryModel.getObject().get(0).field(DATE_FIELD)).equals(DATE_VALUE_4));
    }

    @Test
    public void testRangeOfDateTimeFilter() throws Exception {
        String dateFormat = "yyyy-MM-dd HH:mm:ss";
        DateFormat df = new SimpleDateFormat(dateFormat);

        queryModel.setFilterCriteria(new RangeOfDateFilterCriteria(DATETIME_FIELD, df.parse(DATETIME_VALUE_1), df.parse(DATETIME_VALUE_3),
                dateFormat, true));
        assertTrue(queryModel.getObject().size() == DOCUMENTS_NUM - 1);
        for (ODocument document : queryModel.getObject()) {
            Date date = document.field(DATETIME_FIELD);
            String dateString = df.format(date);
            assertTrue(dateString.equals(DATETIME_VALUE_1)
                    || dateString.equals(DATETIME_VALUE_2)
                    || dateString.equals(DATETIME_VALUE_3));
            assertFalse(dateString.equals(DATETIME_VALUE_4));
        }
        queryModel.detach();

        queryModel.setFilterCriteria(new RangeOfDateFilterCriteria(DATETIME_FIELD, df.parse(DATETIME_VALUE_1), df.parse(DATETIME_VALUE_3),
                dateFormat, false));
        assertTrue(queryModel.getObject().size() == 1);
        assertTrue(df.format((Date) queryModel.getObject().get(0).field(DATETIME_FIELD)).equals(DATETIME_VALUE_4));
    }

    @Test
    public void testValuesOfDateFilter() throws Exception {
        String dateFormat = "yyyy-MM-dd";
        DateFormat df = new SimpleDateFormat(dateFormat);

        queryModel.setFilterCriteria(new ValuesOfDateFilterCriteria(DATE_FIELD, Arrays.asList(df.parse(DATE_VALUE_1), df.parse(DATE_VALUE_3)),
                dateFormat, true));
        assertTrue(queryModel.getObject().size() == 2);
        for (ODocument document : queryModel.getObject()) {
            Date date = document.field(DATE_FIELD);
            String dateString = df.format(date);
            assertTrue(dateString.equals(DATE_VALUE_1)
                    || dateString.equals(DATE_VALUE_3));
        }
        queryModel.detach();

        queryModel.setFilterCriteria(new ValuesOfDateFilterCriteria(DATE_FIELD, Arrays.asList(df.parse(DATE_VALUE_1), df.parse(DATE_VALUE_3)),
                dateFormat, false));
        assertTrue(queryModel.getObject().size() == 2);
        for (ODocument document : queryModel.getObject()) {
            Date date = document.field(DATE_FIELD);
            String dateString = df.format(date);
            assertTrue(dateString.equals(DATE_VALUE_2)
                    || dateString.equals(DATE_VALUE_4));
        }
    }

    @Test
    public void testValuesOfDateTimeFilter() throws Exception {
        String dateFormat = "yyyy-MM-dd HH:mm:ss";
        DateFormat df = new SimpleDateFormat(dateFormat);

        queryModel.setFilterCriteria(new ValuesOfDateFilterCriteria(DATETIME_FIELD, Arrays.asList(df.parse(DATETIME_VALUE_1),
                df.parse(DATETIME_VALUE_3)), dateFormat, true));
        assertTrue(queryModel.getObject().size() == 2);
        for (ODocument document : queryModel.getObject()) {
            Date date = document.field(DATETIME_FIELD);
            String dateString = df.format(date);
            assertTrue(dateString.equals(DATETIME_VALUE_1)
                    || dateString.equals(DATETIME_VALUE_3));
        }
        queryModel.detach();

        queryModel.setFilterCriteria(new ValuesOfDateFilterCriteria(DATETIME_FIELD, Arrays.asList(df.parse(DATETIME_VALUE_1),
                df.parse(DATETIME_VALUE_3)), dateFormat, false));
        assertTrue(queryModel.getObject().size() == 2);
        for (ODocument document : queryModel.getObject()) {
            Date date = document.field(DATETIME_FIELD);
            String dateString = df.format(date);
            assertTrue(dateString.equals(DATETIME_VALUE_2)
                    || dateString.equals(DATETIME_VALUE_4));
        }
    }

    @Test
    public void testEqualsLinkFilter() {
        Map<String, String> fieldValue = Maps.newHashMap();
        fieldValue.put(NUMBER_FIELD, Integer.toString(NUM_VALUE_1));
        fieldValue.put(STRING_FIELD, STR_VALUE_1);

        queryModel.setFilterCriteria(new EqualsLinkFilterCriteria(LINK_FIELD, fieldValue, true));
        assertTrue(queryModel.getObject().size() == 1);
        assertTrue(queryModel.getObject().get(0).field(STRING_FIELD).equals(STR_VALUE_1));
        queryModel.detach();

        queryModel.setFilterCriteria(new EqualsLinkFilterCriteria(LINK_FIELD, fieldValue, false));
        assertTrue(queryModel.getObject().size() == DOCUMENTS_NUM - 1);
        for (ODocument document : queryModel.getObject()) {
            String stringField = document.field(STRING_FIELD);
            assertTrue(stringField.equals(STR_VALUE_2) || stringField.equals(STR_VALUE_3)
                    || stringField.equals(STR_VALUE_4));
        }
    }
}
