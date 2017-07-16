package ru.ydn.wicket.wicketorientdb.utils.query.filter.value;

import com.google.common.base.Strings;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.lang.Args;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.IStringConverter;

import java.util.Collection;

/**
 * Implementation of {@link IFilterValue} for contains {@link Collection<T> } value
 * @param <T> type of value
 */
public class CollectionFilterValue<T> implements IFilterValue {

    private final IModel<Collection<T>> collectionModel;
    private final IStringConverter<T> stringConverter;
    private final boolean rid;


    public CollectionFilterValue(IModel<Collection<T>> collectionModel, IStringConverter<T> stringConverter) {
        this(collectionModel, stringConverter, false);
    }

    public CollectionFilterValue(IModel<Collection<T>> collectionModel, IStringConverter<T> stringConverter, boolean rid) {
        Args.notNull(collectionModel, "collectionModel");
        Args.notNull(stringConverter, "stringConverter");
        this.collectionModel = collectionModel;
        this.stringConverter = stringConverter;
        this.rid = rid;
    }

    @Override
    @SuppressWarnings("unchecked")
    public String getString() {
        StringBuilder sb = new StringBuilder();
        int counter = 0;
        if (collectionModel.getObject() != null && !collectionModel.getObject().isEmpty()) {
            for (T value : collectionModel.getObject()) {
                String valueInString;
                if (value instanceof IModel) {
                    IModel<T> model = (IModel<T>) value;
                    valueInString = stringConverter.apply(model.getObject());
                } else valueInString = stringConverter.apply(value);

                if (!Strings.isNullOrEmpty(valueInString)) {
                    if (counter > 0)
                        sb.append(VALUE_SEPARATOR);
                    if (!rid) sb.append("'");
                    sb.append(valueInString);
                    if (!rid) sb.append("'");
                    counter++;
                }
            }
        }
        return sb.toString();
    }

}
