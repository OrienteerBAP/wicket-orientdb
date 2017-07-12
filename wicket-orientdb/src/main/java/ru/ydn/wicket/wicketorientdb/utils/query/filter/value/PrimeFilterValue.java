package ru.ydn.wicket.wicketorientdb.utils.query.filter.value;

import org.apache.wicket.model.IModel;
import org.apache.wicket.util.lang.Args;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.IStringConverter;

/**
 * Implementation of {@link IFilterValue} for contains prime value
 * @param <T> type of value
 */
public class PrimeFilterValue<T> implements IFilterValue {

    private final IModel<T> model;
    private final IStringConverter<T> stringConverter;

    public PrimeFilterValue(IModel<T> model, IStringConverter<T> stringConverter) {
        Args.notNull(model, "model");
        Args.notNull(stringConverter, "stringConverter");
        this.model = model;
        this.stringConverter = stringConverter;
    }

    @Override
    public String getString() {
        T value = model.getObject();
        return value != null ? stringConverter.apply(value) : null;
    }
}
