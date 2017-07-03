package ru.ydn.wicket.wicketorientdb.filter;

import com.google.common.collect.Lists;

import java.util.List;

interface ITesterFilterConstants {

    public static final String TEST_CLASS_NAME = "FilterTestOClass";

    public static final String STRING_FIELD   = "name";
    public static final String NUMBER_FIELD   = "number";
    public static final String DATE_FIELD     = "date";
    public static final String DATETIME_FIELD = "datetime";

    public static final int DOCUMENTS_NUM = 4;

    public static final String STR_VALUE_1 = "string value 1";
    public static final String STR_VALUE_2 = "summer value 2";
    public static final String STR_VALUE_3 = "winter value 3";
    public static final String STR_VALUE_4 = "spring value 4";

    public static final int NUM_VALUE_1 = 1;
    public static final int NUM_VALUE_2 = 2;
    public static final int NUM_VALUE_3 = 3;
    public static final int NUM_VALUE_4 = 4;

    public static final String DATE_VALUE_1 = "2017-01-01";
    public static final String DATE_VALUE_2 = "2017-02-02";
    public static final String DATE_VALUE_3 = "2017-03-03";
    public static final String DATE_VALUE_4 = "2017-04-04";

    public static final String DATETIME_VALUE_1 = "2017-01-01 01:01:01";
    public static final String DATETIME_VALUE_2 = "2017-02-02 02:02:02";
    public static final String DATETIME_VALUE_3 = "2017-03-03 03:03:03";
    public static final String DATETIME_VALUE_4 = "2017-04-04 04:04:04";

    public static final List<Integer> range = Lists.newArrayList(NUM_VALUE_2, NUM_VALUE_4);

}
