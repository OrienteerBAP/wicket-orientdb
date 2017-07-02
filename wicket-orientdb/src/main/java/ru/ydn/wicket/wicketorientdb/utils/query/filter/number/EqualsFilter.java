package ru.ydn.wicket.wicketorientdb.utils.query.filter.number;

/**
 * value - value for equals
 * SELECT FROM class WHERE num = value
 */
class EqualsFilter implements INumberFilter {

    private final Integer value;
    private boolean join;

    public EqualsFilter(Integer value, boolean join) {
        this.value = value;
        this.join = join;
    }

    @Override
    public String apply(String field) {
        StringBuilder sb = new StringBuilder();
        sb.append(field);
        if (join) {
            sb.append(" = ");
            sb.append(value.toString());
        } else {
            sb.append(" <> ");
            sb.append(value.toString());
        }
        return sb.toString();
    }

    @Override
    public void setJoin(boolean join) {
        this.join = join;
    }

    @Override
    public String toString() {
        return "EqualsFilter{" +
                "value=" + value +
                ", join=" + join +
                '}';
    }
}
