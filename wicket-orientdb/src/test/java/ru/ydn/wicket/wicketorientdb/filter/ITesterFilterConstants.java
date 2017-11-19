package ru.ydn.wicket.wicketorientdb.filter;

import java.util.Arrays;
import java.util.List;

interface ITesterFilterConstants {

    public static final String TEST_CLASS_NAME      = "FilterTestOClass";
    public static final String LINK_TEST_CLASS_NAME = "LinkFilterOClass";

    public static final String STRING_FIELD               = "name";
    public static final String NUMBER_FIELD               = "number";
    public static final String DATE_FIELD                 = "date";
    public static final String DATETIME_FIELD             = "datetime";
    public static final String LINK_FIELD                 = "link";
    public static final String LINK_LIST_FIELD            = "linkList";
    public static final String LINK_SET_FIELD             = "linkSet";
    public static final String LINK_MAP_FIELD             = "linkMap";
    public static final String EMBEDDED_FIELD             = "embedded";
    public static final String EMBEDDED_LIST_FIELD        = "embeddedList";
    public static final String EMBEDDED_LIST_STRING_FIELD = "embeddedStringList";
    public static final String EMBEDDED_SET_STRING_FIELD  = "embeddedStringSet";
    public static final String EMBEDDED_SET_FIELD         = "embeddedSet";
    public static final String EMBEDDED_MAP_FIELD         = "embeddedMap";

    public static final String STR_VALUE_1 = "string value 1";
    public static final String STR_VALUE_2 = "summer value 2";
    public static final String STR_VALUE_3 = "winter value 3";
    public static final String STR_VALUE_4 = "spring value 4";

    public static final Integer NUM_VALUE_1 = 1;
    public static final Integer NUM_VALUE_2 = 2;
    public static final Integer NUM_VALUE_3 = 3;
    public static final Integer NUM_VALUE_4 = 4;

    public static final String DATE_VALUE_1 = "2017-01-01";
    public static final String DATE_VALUE_2 = "2017-02-02";
    public static final String DATE_VALUE_3 = "2017-03-03";
    public static final String DATE_VALUE_4 = "2017-04-04";

    public static final String DATETIME_VALUE_1 = "2017-01-01 01:01:01";
    public static final String DATETIME_VALUE_2 = "2017-02-02 02:02:02";
    public static final String DATETIME_VALUE_3 = "2017-03-03 03:03:03";
    public static final String DATETIME_VALUE_4 = "2017-04-04 04:04:04";

    public static final List<Integer> LIST_ORDER_1 = Arrays.asList(1, 2, 3, 4);
    public static final List<Integer> LIST_ORDER_2 = Arrays.asList(4, 3, 2, 1);
    public static final List<Integer> LIST_ORDER_3 = Arrays.asList(3, 2, 4, 1);
    public static final List<Integer> LIST_ORDER_4 = Arrays.asList(2, 4, 3, 1);

    public static final List<String> MAP_KEYS = Arrays.asList(STR_VALUE_1, STR_VALUE_2, STR_VALUE_3, STR_VALUE_4);
}
