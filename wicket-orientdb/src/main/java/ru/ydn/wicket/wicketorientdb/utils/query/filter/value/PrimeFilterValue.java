package ru.ydn.wicket.wicketorientdb.utils.query.filter.value;

import com.google.common.collect.Lists;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.lang.Args;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.IStringConverter;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
    public List<String> toStringList() {
        List<String> result = Lists.newArrayList();
        if (model.getObject() != null) result.add(stringConverter.apply(model.getObject()));
        return Collections.unmodifiableList(result);
    }
}
