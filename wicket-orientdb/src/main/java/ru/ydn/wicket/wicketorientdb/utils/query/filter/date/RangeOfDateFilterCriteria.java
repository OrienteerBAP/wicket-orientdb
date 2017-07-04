package ru.ydn.wicket.wicketorientdb.utils.query.filter.date;

import ru.ydn.wicket.wicketorientdb.utils.query.filter.AbstractFilterCriteria;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * SELECT FROM class WHERE date BETWEEN 'date1' AND 'date2'
 */
public class RangeOfDateFilterCriteria extends AbstractFilterCriteria {

    private final Date start;
    private final Date end;
    private final String dateFormat;

    public RangeOfDateFilterCriteria(String field, Date start, Date end, String dateFormat, boolean join) {
        super(field, join);
        this.start = start;
        this.end = end;
        this.dateFormat = dateFormat;
    }

    @Override
    protected String apply(String field) {
        StringBuilder sb = new StringBuilder();
        sb.append(field);
        DateFormat df = new SimpleDateFormat(dateFormat);
        sb.append(" BETWEEN '");
        sb.append(df.format(start));
        sb.append("' AND '");
        sb.append(df.format(end));
        sb.append("'");
        return sb.toString();
    }
}
