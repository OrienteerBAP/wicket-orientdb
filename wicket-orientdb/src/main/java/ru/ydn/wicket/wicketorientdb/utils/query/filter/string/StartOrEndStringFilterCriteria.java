package ru.ydn.wicket.wicketorientdb.utils.query.filter.string;

import ru.ydn.wicket.wicketorientdb.utils.query.filter.AbstractFilterCriteria;

/**
 * SELECT FROM class WHERE stringField LIKE '%myString'
 * or
 * SELECT FROM class WHERE stringField LIKE 'myString%'
 */
public class StartOrEndStringFilterCriteria extends AbstractFilterCriteria {

    private final String value;
    private final boolean start;

    public StartOrEndStringFilterCriteria(String field, String value, boolean start, boolean join) {
        super(field, join);
        this.value = value;
        this.start = start;
    }

    @Override
    public String apply() {
        StringBuilder sb = new StringBuilder();
        sb.append(getField());
        sb.append(" LIKE '");
        if (start) {
            sb.append(value);
            sb.append("%'");
        } else {
            sb.append("%");
            sb.append(value);
            sb.append("'");
        }
        return sb.toString();
    }
}
