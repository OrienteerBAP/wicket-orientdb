package ru.ydn.wicket.wicketorientdb.utils.query.filter;

import com.google.common.base.Strings;
import org.apache.wicket.model.IModel;

import java.util.Collection;

/**
 * Abstract class for IFilterCriteria
 */
public abstract class AbstractFilterCriteria implements IFilterCriteria {

    private final String field;
    private final IModel<?> model;
    private final IModel<Boolean> join;

    public AbstractFilterCriteria(String field, IModel<?> model, IModel<Boolean> join) {
        this.field = field;
        this.model = model;
        this.join = join;
    }

    @Override
    public String apply() {
        String filter = apply(getField());
        if (Strings.isNullOrEmpty(filter))
            return null;

        StringBuilder sb = new StringBuilder();
        if (!join.getObject()) sb.append("NOT(");
        sb.append(filter);
        if (!join.getObject()) sb.append(")");
        return sb.toString();
    }

    protected abstract String apply(String field);

    @Override
    public IModel<Boolean> getJoinModel() {
        return join;
    }


    @Override
    public String getField() {
        return field;
    }

    @Override
    public IModel<?> getModel() {
        return model;
    }

    @Override
    public String getName() {
        return getFilterCriteriaType().getName() + field;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean isEmpty() {
        FilterCriteriaType type = getFilterCriteriaType();
        if (!type.isModelCollection())
            return getModel().getObject() == null;
        Collection<?> collection = (Collection<?>) getModel().getObject();
        boolean isEmpty = true;
        if (collection != null && !collection.isEmpty()) {
            if (type.isIncludeModels())
                isEmpty = checkModelsInCollectionModel((Collection<IModel<?>>) collection);
            else isEmpty = checkCustomCollectionModel(collection);
        }
        return isEmpty;
    }

    private boolean checkCustomCollectionModel(Collection<?> collection) {
        for (Object object : collection) {
            if (object != null)
                return false;
        }
        return true;
    }

    private boolean checkModelsInCollectionModel(Collection<IModel<?>> collection) {
        for (IModel<?> model : collection) {
            if (model != null && model.getObject() != null)
                return false;
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractFilterCriteria that = (AbstractFilterCriteria) o;

        if (field != null ? !field.equals(that.field) : that.field != null) return false;
        return getFilterCriteriaType() == that.getFilterCriteriaType();
    }

    @Override
    public int hashCode() {
        int result = field != null ? field.hashCode() : 0;
        result = 31 * result + (getFilterCriteriaType() != null ? getFilterCriteriaType().hashCode() : 0);
        return result;
    }
}
