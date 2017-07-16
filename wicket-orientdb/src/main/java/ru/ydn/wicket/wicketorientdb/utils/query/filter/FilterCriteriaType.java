package ru.ydn.wicket.wicketorientdb.utils.query.filter;

import org.apache.wicket.util.io.IClusterable;

/**
 * Enum which represents types of {@link IFilterCriteria}
 */
public enum FilterCriteriaType implements IClusterable {
    EQUALS("equalsFilter"),
    LIST("listFilter"),
    RANGE("rangeFilter"),
    CONTAINS_TEXT("containsTextFilter"),
    STRING_START("stringStartFilter"),
    STRING_END("stringEndFilter"),
    LINK("linkFilter"),
    LINKLIST("linkListFilter"),
    LINKSET("linkSetFilter"),
    LINKMAP("linkMapFilter"),
    COLLECTION("collection");

    private final String name;

    FilterCriteriaType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
