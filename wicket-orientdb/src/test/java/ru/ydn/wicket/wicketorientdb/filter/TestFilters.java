package ru.ydn.wicket.wicketorientdb.filter;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import ru.ydn.wicket.wicketorientdb.model.OQueryModel;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.IFilterCriteria;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.date.EqualsDateFilterCriteria;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.date.RangeOfDateFilterCriteria;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.date.ValuesOfDateFilterCriteria;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.embedded.ContainsKeyInEmbMapFilterCriteria;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.embedded.EqualsEmbCollectionFilterCriteria;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.embedded.EqualsEmbeddedFilterCriteria;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.embedded.EqualsValueInEmbMapFilterCriteria;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.link.ContainsLinkMapFilterCriteria;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.link.EqualsLinkCollectionFilterCriteria;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.link.EqualsLinkFilterCriteria;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.link.ContainsLinkFilterCriteria;
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
import java.util.List;
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
    public void testNumberEqualsFilterCriteria() {
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
    public void testRangeOfNumberFilterCriteria() {
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
    public void testNumberValuesFilterCriteria() {
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
    public void testStringEqualsFilterCriteria() {
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
    public void testStartOrEndStringFilterCriteria() {
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
    public void testContainsStringFilterCriteria() {
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
    public void testDateEqualsFilterCriteria() throws Exception {
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
    public void testDateTimeEqualsFilterCriteria() throws Exception {
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
    public void testRangeOfDateFilterCriteria() throws Exception {
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
    public void testRangeOfDateTimeFilterCriteria() throws Exception {
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
    public void testValuesOfDateFilterCriteria() throws Exception {
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
    public void testValuesOfDateTimeFilterCriteria() throws Exception {
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
    public void testEqualsLinkFilterCriteria() {
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

    @Test
    public void testEqualsORIDFilterCriteria() {
        Map<String, String> fieldValue = Maps.newHashMap();
        fieldValue.put(NUMBER_FIELD, Integer.toString(NUM_VALUE_1));
        fieldValue.put(STRING_FIELD, STR_VALUE_1);

        queryModel.setFilterCriteria(new EqualsLinkFilterCriteria(LINK_FIELD, fieldValue, true));
        assertTrue(queryModel.getObject().size() == 1);
        ODocument document = queryModel.getObject().get(0).field(LINK_FIELD);
        String orid = document.getIdentity().toString();
        queryModel.detach();

        queryModel.setFilterCriteria(new EqualsLinkFilterCriteria(LINK_FIELD, orid, true));
        assertTrue(queryModel.getObject().size() == 1);
        assertTrue(queryModel.getObject().get(0).field(LINK_FIELD).equals(document));
        queryModel.detach();

        queryModel.setFilterCriteria(new EqualsLinkFilterCriteria(LINK_FIELD, orid, false));
        assertTrue(queryModel.getObject().size() == DOCUMENTS_NUM - 1);
        for (ODocument doc : queryModel.getObject()) {
            assertTrue(!doc.field(LINK_FIELD).equals(document));
        }
    }

    @Test
    public void testValuesOfLinkFilterCriteria() {
        Map<String, List<String>> fieldsAndValues = Maps.newHashMap();
        fieldsAndValues.put(STRING_FIELD, Arrays.asList(STR_VALUE_1, STR_VALUE_2));
        fieldsAndValues.put(NUMBER_FIELD, Arrays.asList(Integer.toString(NUM_VALUE_1),
                Integer.toString(NUM_VALUE_2)));

        queryModel.setFilterCriteria(new ContainsLinkFilterCriteria(LINK_FIELD, fieldsAndValues, true));
        assertTrue(queryModel.getObject().size() == 2);
        for (ODocument document : queryModel.getObject()) {
            String stringField = document.field(STRING_FIELD);
            Integer numberField = document.field(NUMBER_FIELD);
            assertTrue(stringField.equals(STR_VALUE_1) || stringField.equals(STR_VALUE_2));
            assertTrue(numberField == NUM_VALUE_1 || numberField == NUM_VALUE_2);
        }
        queryModel.detach();

        queryModel.setFilterCriteria(new ContainsLinkFilterCriteria(LINK_FIELD, fieldsAndValues, false));
        assertTrue(queryModel.getObject().size() == 2);
        for (ODocument document : queryModel.getObject()) {
            String stringField = document.field(STRING_FIELD);
            Integer numberField = document.field(NUMBER_FIELD);
            assertTrue(stringField.equals(STR_VALUE_3) || stringField.equals(STR_VALUE_4));
            assertTrue(numberField == NUM_VALUE_3 || numberField == NUM_VALUE_4);
        }
    }

    @Test
    public void testValuesOfOridLinkFilterCriteria() {
        Map<String, List<String>> fieldsAndValues = Maps.newHashMap();
        fieldsAndValues.put(STRING_FIELD, Arrays.asList(STR_VALUE_1, STR_VALUE_2));
        fieldsAndValues.put(NUMBER_FIELD, Arrays.asList(Integer.toString(NUM_VALUE_1),
                Integer.toString(NUM_VALUE_2)));

        queryModel.setFilterCriteria(new ContainsLinkFilterCriteria(LINK_FIELD, fieldsAndValues, true));
        assertTrue(queryModel.getObject().size() == 2);
        List<String> orids = Lists.newArrayList();
        List<ODocument> checkedDocs = Lists.newArrayList();
        for (ODocument document : queryModel.getObject()) {
            ODocument linkDocument = document.field(LINK_FIELD);
            orids.add(linkDocument.getIdentity().toString());
            checkedDocs.add(document);
        }
        queryModel.detach();

        queryModel.setFilterCriteria(new ContainsLinkFilterCriteria(LINK_FIELD, orids, true));
        assertTrue(queryModel.getObject().size() == 2);
        for (int i = 0; i < queryModel.getObject().size(); i++) {
            assertTrue(checkedDocs.get(i).equals(queryModel.getObject().get(i)));
        }

        queryModel.setFilterCriteria(new ContainsLinkFilterCriteria(LINK_FIELD, fieldsAndValues, true));
        assertTrue(queryModel.getObject().size() == 2);
        orids.clear();
        checkedDocs.clear();
        for (ODocument document : queryModel.getObject()) {
            ODocument linkDocument = document.field(LINK_FIELD);
            orids.add(linkDocument.getIdentity().toString());
            checkedDocs.add(document);
        }
        queryModel.detach();

        queryModel.setFilterCriteria(new ContainsLinkFilterCriteria(LINK_FIELD, orids, false));
        assertTrue(queryModel.getObject().size() == 2);
        for (int i = 0; i < queryModel.getObject().size(); i++) {
            assertTrue(!checkedDocs.get(i).equals(queryModel.getObject().get(i)));
        }
    }

    @Test
    public void testLinkListFilterCriteria() {
        queryModel.setFilterCriteria(new ValuesOfNumberFilterCriteria(NUMBER_FIELD,
                Arrays.asList(NUM_VALUE_1), true));
        assertTrue(queryModel.getObject().size() == 1);
        ODocument document = queryModel.getObject().get(0);
        List<String> orids = Lists.newArrayList();
        List<ODocument> links = document.field(LINK_LIST_FIELD);
        for (ODocument link : links) {
            orids.add(link.getIdentity().toString());
        }
        queryModel.detach();

        queryModel.setFilterCriteria(new EqualsLinkCollectionFilterCriteria(LINK_LIST_FIELD, orids, true));
        assertTrue(queryModel.getObject().size() == 1);
        assertTrue(document.equals(queryModel.getObject().get(0)));
        queryModel.detach();

        queryModel.setFilterCriteria(new EqualsLinkCollectionFilterCriteria(LINK_LIST_FIELD, orids, false));
        assertTrue(queryModel.getObject().size() == DOCUMENTS_NUM - 1);
        for (ODocument doc : queryModel.getObject()) {
            assertFalse(document.equals(doc));
        }
    }

    @Test
    public void testContainsLinkMapFilterCriteria() {
        queryModel.setFilterCriteria(new ContainsLinkMapFilterCriteria(LINK_MAP_FIELD, MAP_KEYS, null, false, true));
        assertTrue(queryModel.getObject().size() == 4);
        queryModel.detach();

        queryModel.setFilterCriteria(new ContainsLinkMapFilterCriteria(LINK_MAP_FIELD, MAP_KEYS, null, true, true));
        assertTrue(queryModel.getObject().size() == 0);
    }

    @Test
    public void testEqualsEmbeddedFilterCriteria() {
        Map<String, String> fieldAndValue = Maps.newHashMap();
        fieldAndValue.put(STRING_FIELD, STR_VALUE_1);
        fieldAndValue.put(NUMBER_FIELD, Integer.toString(NUM_VALUE_1));

        queryModel.setFilterCriteria(new EqualsEmbeddedFilterCriteria(EMBEDDED_FIELD, fieldAndValue, true));
        assertTrue(queryModel.getObject().size() == 1);
        assertTrue(queryModel.getObject().get(0).field(STRING_FIELD).equals(STR_VALUE_1));
        queryModel.detach();

        queryModel.setFilterCriteria(new EqualsEmbeddedFilterCriteria(EMBEDDED_FIELD, fieldAndValue, false));
        assertTrue(queryModel.getObject().size() == DOCUMENTS_NUM - 1);
        for (ODocument document : queryModel.getObject()) {
            assertFalse(document.field(STRING_FIELD).equals(STR_VALUE_1));
        }
    }

    @Test
    public void testEqualsEmbeddedCollectionFilterCriteria() {
        Map<Integer, IFilterCriteria> map = Maps.newHashMap();
        List<Map<Integer, IFilterCriteria>> filter = Lists.newArrayList();
        map.put(0, new EqualsNumberFilterCriteria(NUMBER_FIELD, NUM_VALUE_1, true));
        filter.add(map);
        map.clear();
        map.put(0, new EqualsStringFilterCriteria(STRING_FIELD, STR_VALUE_1, true));
        filter.add(map);

        queryModel.setFilterCriteria(new EqualsEmbCollectionFilterCriteria(EMBEDDED_LIST_FIELD, filter, true, true));
        assertTrue(queryModel.getObject().size() == 1);
        assertTrue(queryModel.getObject().get(0).field(STRING_FIELD).equals(STR_VALUE_1));
        queryModel.detach();

        queryModel.setFilterCriteria(new EqualsEmbCollectionFilterCriteria(EMBEDDED_LIST_FIELD, filter, true, false));
        assertTrue(queryModel.getObject().size() == DOCUMENTS_NUM - 1);
        for (ODocument document : queryModel.getObject()) {
            assertFalse(document.field(STRING_FIELD).equals(STR_VALUE_1));
        }
        queryModel.detach();
    }

    @Test
    public void testEqualsValueInEmbeddedMapFilterCriteria() {
        String key = MAP_KEYS.get(0);
        Map<String, String> fieldAndValue = Maps.newHashMap();
        fieldAndValue.put(STRING_FIELD, STR_VALUE_1);
        fieldAndValue.put(NUMBER_FIELD, Integer.toString(NUM_VALUE_1));

        queryModel.setFilterCriteria(new EqualsValueInEmbMapFilterCriteria(EMBEDDED_MAP_FIELD, key, fieldAndValue,
                true, true));
        assertTrue(queryModel.getObject().size() == 1);
        assertTrue(queryModel.getObject().get(0).field(STRING_FIELD).equals(STR_VALUE_1));
        queryModel.detach();

        queryModel.setFilterCriteria(new EqualsValueInEmbMapFilterCriteria(EMBEDDED_MAP_FIELD, key, fieldAndValue,
                true, false));
        assertTrue(queryModel.getObject().size() == DOCUMENTS_NUM - 1);
        for (ODocument document : queryModel.getObject()) {
            assertFalse(document.field(STRING_FIELD).equals(STR_VALUE_1));
        }
    }

    @Test
    public void testContainsKeyInEmbeddedMapFilterCriteria() {
        List<String> keys = Arrays.asList(MAP_KEYS.get(0), MAP_KEYS.get(1));

        queryModel.setFilterCriteria(new ContainsKeyInEmbMapFilterCriteria(EMBEDDED_MAP_FIELD, keys, false, true));
        assertTrue(queryModel.getObject().size() == 2);
        for (ODocument document : queryModel.getObject()) {
            String field = document.field(STRING_FIELD);
            assertTrue(field.equals(STR_VALUE_1) || field.equals(STR_VALUE_2));
        }
        queryModel.detach();

        queryModel.setFilterCriteria(new ContainsKeyInEmbMapFilterCriteria(EMBEDDED_MAP_FIELD, keys, false, false));
        assertTrue(queryModel.getObject().size() == 2);
        for (ODocument document : queryModel.getObject()) {
            String field = document.field(STRING_FIELD);
            assertFalse(field.equals(STR_VALUE_1) || field.equals(STR_VALUE_2));
        }
        queryModel.detach();

        queryModel.setFilterCriteria(new ContainsKeyInEmbMapFilterCriteria(EMBEDDED_MAP_FIELD, keys, true, true));
        assertTrue(queryModel.getObject().size() == 0);
    }
}
