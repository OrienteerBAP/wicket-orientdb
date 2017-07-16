package ru.ydn.wicket.wicketorientdb.utils.query.filter;

import com.google.common.base.Strings;
import org.apache.wicket.model.IModel;

import java.util.List;

/**
 * Abstract class for IFilterCriteria
 */
public abstract class AbstractFilterCriteria implements IFilterCriteria {

    private final String name;
    private String field;
    private final IModel<Boolean> join;

    public AbstractFilterCriteria(String field, String name, IModel<Boolean> join) {
        this.field = field;
        this.join = join;
        this.name = name;
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

    protected boolean needToApplyFilter(List<String> stringList) {
        return stringList != null && !stringList.isEmpty();
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
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractFilterCriteria that = (AbstractFilterCriteria) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return field != null ? field.equals(that.field) : that.field == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (field != null ? field.hashCode() : 0);
        return result;
    }
}
