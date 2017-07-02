package ru.ydn.wicket.wicketorientdb.filter;

import com.orientechnologies.orient.core.record.impl.ODocument;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import ru.ydn.wicket.wicketorientdb.model.OQueryModel;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.number.NumberFilterCriteriaCreator;

import java.util.Arrays;

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
        NumberFilterCriteriaCreator creator = new NumberFilterCriteriaCreator();
        queryModel.setFilterCriteria(creator.createEqualsFilterCriteria(NUMBER_FIELD, NUM_VALUE_1, true));
        assertTrue(queryModel.getObject().size() == 1);
        assertTrue(NUM_VALUE_1 == (Integer) queryModel.getObject().get(0).field(NUMBER_FIELD));
        queryModel.detach();

        queryModel.setFilterCriteria(creator.createEqualsFilterCriteria(NUMBER_FIELD, NUM_VALUE_2, true));
        assertTrue(queryModel.getObject().size() == 1);
        assertTrue("expected: " + NUM_VALUE_2 + " get: " + queryModel.getObject().get(0).field(NUMBER_FIELD),
                NUM_VALUE_2 == (Integer) queryModel.getObject().get(0).field(NUMBER_FIELD));
        queryModel.detach();

        queryModel.setFilterCriteria(creator.createEqualsFilterCriteria(NUMBER_FIELD, NUM_VALUE_3, false));
        assertTrue(queryModel.getObject().size() == DOCUMENTS_NUM - 1);
        for (ODocument document : queryModel.getObject()) {
            assertTrue((Integer) document.field(NUMBER_FIELD) != NUM_VALUE_3);
        }
    }

    @Test
    public void testNumberRangeFilter() {
        NumberFilterCriteriaCreator creator = new NumberFilterCriteriaCreator();
        queryModel.setFilterCriteria(creator.createRangeFilterCriteria(NUMBER_FIELD, NUM_VALUE_1, NUM_VALUE_3, true));
        assertTrue(queryModel.getObject().size() == DOCUMENTS_NUM - 1);
        for (ODocument document : queryModel.getObject()) {
            Integer number = document.field(NUMBER_FIELD);
            assertTrue("Error in range filtering", number == NUM_VALUE_1 || number == NUM_VALUE_2 || number == NUM_VALUE_3);
        }
        queryModel.detach();

        queryModel.setFilterCriteria(creator.createRangeFilterCriteria(NUMBER_FIELD, NUM_VALUE_1, NUM_VALUE_3, false));
        assertTrue(queryModel.getObject().size() == 1);
        assertTrue((Integer) queryModel.getObject().get(0).field(NUMBER_FIELD) == NUM_VALUE_4);
    }

    @Test
    public void testNumberValuesFilter() {
        NumberFilterCriteriaCreator creator = new NumberFilterCriteriaCreator();
        queryModel.setFilterCriteria(creator.createValuesFilterCriteria(NUMBER_FIELD,
                Arrays.asList(NUM_VALUE_1, NUM_VALUE_4), true));
        assertTrue(queryModel.getObject().size() == 2);
        for (ODocument document : queryModel.getObject()) {
            Integer number = document.field(NUMBER_FIELD);
            assertTrue("Error in values join filtering",
                    number == NUM_VALUE_1 || number == NUM_VALUE_4);
        }
        queryModel.detach();

        queryModel.setFilterCriteria(creator.createValuesFilterCriteria(NUMBER_FIELD,
                Arrays.asList(NUM_VALUE_1, NUM_VALUE_4), false));
        assertTrue(queryModel.getObject().size() == 2);
        for (ODocument document : queryModel.getObject()) {
            Integer number = document.field(NUMBER_FIELD);
            assertTrue("Error in values no join filtering",
                    number == NUM_VALUE_2 || number == NUM_VALUE_3);
        }
        assertTrue((Integer) queryModel.getObject().get(0).field(NUMBER_FIELD) == NUM_VALUE_2);
        assertTrue((Integer) queryModel.getObject().get(1).field(NUMBER_FIELD) == NUM_VALUE_3);
    }
}
