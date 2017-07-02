package ru.ydn.wicket.wicketorientdb.utils.query.filter.number;

/**
 * Interface for generating SQL for number fields
 */
public interface INumberFilter {
    public String apply(String field);
    public void setJoin(boolean join);
}
