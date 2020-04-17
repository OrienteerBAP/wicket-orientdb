package ru.ydn.wicket.wicketorientdb.utils.query.filter;

import com.google.common.base.Strings;
import org.apache.wicket.model.IModel;

import java.util.Collection;

/**
 * Abstract class for IFilterCriteria
 */
public abstract class AbstractFilterCriteria implements IFilterCriteria {

	private static final long serialVersionUID = 1L;
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
        String result = null;
        String filter = !isEmpty() ? apply(getField()) : null;
        if (!Strings.isNullOrEmpty(filter)) {
            StringBuilder sb = new StringBuilder();
            if (!join.getObject()) sb.append("NOT(");
            sb.append(filter);
            if (!join.getObject()) sb.append(")");
            result = sb.toString();
        }
        return result;
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
        boolean isEmpty = true;
        FilterCriteriaType type = getFilterCriteriaType();
        if (type.isCollection()) {
            Collection<?> collection = (Collection<?>) getModel().getObject();
            if (collection != null && !collection.isEmpty()) {
                isEmpty = isCollectionEmpty(collection);
            }
        } else {
            isEmpty = getModel().getObject() == null;
        }
        return isEmpty;
    }

    private boolean isCollectionEmpty(Collection<?> collection) {
        for (Object object : collection) {
            if (object != null)
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
