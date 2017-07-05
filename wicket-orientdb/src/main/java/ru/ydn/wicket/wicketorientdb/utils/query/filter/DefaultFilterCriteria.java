package ru.ydn.wicket.wicketorientdb.utils.query.filter;

/**
 * Class witch represents no SQL filter
 */
public class DefaultFilterCriteria extends AbstractFilterCriteria {
    public DefaultFilterCriteria() {
        super(null, false);
    }

    @Override
    public String getField() {
        return null;
    }

    @Override
    protected String apply(String field) {
        throw new UnsupportedOperationException("DefaultFilterCriteria can't create filter SQL!");
    }
}
