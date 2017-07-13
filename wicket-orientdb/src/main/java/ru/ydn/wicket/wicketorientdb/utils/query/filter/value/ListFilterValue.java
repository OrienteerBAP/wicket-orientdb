package ru.ydn.wicket.wicketorientdb.utils.query.filter.value;

import com.google.common.base.Strings;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.lang.Args;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.IStringConverter;

import java.util.List;

/**
 * Implementation of {@link IFilterValue} for contains {@link List<T> } value
 * @param <T> type of value
 */
public class ListFilterValue<T> implements IFilterValue {

    private final List<IModel<T>> list;
    private final IStringConverter<T> stringConverter;

    public ListFilterValue(List<IModel<T>> list, IStringConverter<T> stringConverter) {
        Args.notNull(list, "list");
        Args.notNull(stringConverter, "stringConverter");
        for (IModel<T> model : list) {
            Args.notNull(model, "model");
        }
        this.list = list;
        this.stringConverter = stringConverter;
    }

    @Override
    public String getString() {
        StringBuilder sb = new StringBuilder();
        int counter = 0;
        for (IModel<T> model : list) {
            T value = model.getObject();
            if (value != null) {
                String valueInString = stringConverter.apply(value);
                if (!Strings.isNullOrEmpty(valueInString)) {
                    if (counter > 0)
                        sb.append(VALUE_SEPARATOR);
                    sb.append("'");
                    sb.append(valueInString);
                    sb.append("'");
                    counter++;
                }
            }
        }
        return sb.toString();
    }

}
