package ru.ydn.wicket.wicketorientdb.utils.query.filter.number;

public interface INumberFilter {
    public String apply(String field);
    public void setJoin(boolean join);
}
