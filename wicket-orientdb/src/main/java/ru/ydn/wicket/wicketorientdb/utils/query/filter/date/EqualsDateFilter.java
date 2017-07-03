package ru.ydn.wicket.wicketorientdb.utils.query.filter.date;

import ru.ydn.wicket.wicketorientdb.utils.query.filter.AbstractFilter;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * SELECT FROM class WHERE dateField = 'yyyy-MM-dd'
 * or
 * SELECT FROM class WHERE datetimeField = 'yyyy-MM-dd HH:mm:ss'
 */
public class EqualsDateFilter extends AbstractFilter {

    private final Date date;
    private final String dateFormat;

    public EqualsDateFilter(String field, Date date, String dateFormat, boolean join) {
        super(field, join);
        this.date = date;
        this.dateFormat = dateFormat;
    }

    @Override
    protected String apply(String field) {
        StringBuilder sb = new StringBuilder();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        sb.append(field);
        sb.append(" = '");
        sb.append(simpleDateFormat.format(date));
        sb.append("'");
        return sb.toString();
    }
}
