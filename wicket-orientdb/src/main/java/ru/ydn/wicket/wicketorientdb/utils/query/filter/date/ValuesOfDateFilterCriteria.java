package ru.ydn.wicket.wicketorientdb.utils.query.filter.date;

import ru.ydn.wicket.wicketorientdb.utils.query.filter.AbstractFilterCriteria;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * SELECT FROM class WHERE date IN ('date1', 'date2', ..., 'dateN')
 */
public class ValuesOfDateFilterCriteria extends AbstractFilterCriteria {

    private final List<Date> values;
    private final String dateFormat;

    public ValuesOfDateFilterCriteria(String field, List<Date> values, String dateFormat, boolean join) {
        super(field, join);
        this.values = values;
        this.dateFormat = dateFormat;
    }

    @Override
    protected String apply(String field) {
        StringBuilder sb = new StringBuilder((dateFormat.length() + 3) * values.size());
        sb.append(field);
        sb.append(" IN [");
        DateFormat df = new SimpleDateFormat(dateFormat);
        for (int i = 0; i < values.size(); i++) {
            sb.append("'");
            sb.append(df.format(values.get(i)));
            sb.append("'");
            if (i != values.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }
}
