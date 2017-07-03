package ru.ydn.wicket.wicketorientdb.filter;

import com.google.common.collect.Lists;

import java.util.List;

interface ITesterFilterConstants {

    public static final String TEST_CLASS_NAME = "FilterTestOClass";

    public static final String STRING_FIELD = "name";
    public static final String NUMBER_FIELD = "number";

    public static final String STR_VALUE_1 = "string value 1";
    public static final String STR_VALUE_2 = "summer value 2";
    public static final String STR_VALUE_3 = "winter value 3";
    public static final String STR_VALUE_4 = "spring value 4";

    public static final int NUM_VALUE_1 = 1;
    public static final int NUM_VALUE_2 = 2;
    public static final int NUM_VALUE_3 = 3;
    public static final int NUM_VALUE_4 = 4;

    public static final int DOCUMENTS_NUM = 4;

    public static final List<Integer> range = Lists.newArrayList(NUM_VALUE_2, NUM_VALUE_4);

}
