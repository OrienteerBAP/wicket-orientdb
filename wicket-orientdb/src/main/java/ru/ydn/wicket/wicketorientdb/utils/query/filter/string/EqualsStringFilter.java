package ru.ydn.wicket.wicketorientdb.utils.query.filter.string;

import ru.ydn.wicket.wicketorientdb.utils.query.filter.AbstractFilter;

/**
 * SELECT FROM class WHERE stringField = 'myString'
 */
public class EqualsStringFilter extends AbstractFilter {

    private final String value;

    public EqualsStringFilter(String field, String value, boolean equals) {
        super(field, equals);
        this.value = value;
    }

    @Override
    public String apply(String field) {
        StringBuilder sb = new StringBuilder();
        sb.append(field);
        sb.append(" = '");
        sb.append(value).append("'");
        return sb.toString();
    }
}
