package ru.ydn.wicket.wicketorientdb.utils.query.filter;

import com.google.common.collect.Lists;
import org.apache.wicket.util.lang.Args;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Abstract class for IFilterCriteria
 */
public abstract class AbstractFilterCriteria implements IFilterCriteria {

    private String field;
    private final boolean join;
    private List<IFilterCriteria> children;

    public AbstractFilterCriteria(String field, boolean join) {
        this.field = field;
        this.join = join;
    }

    @Override
    public String apply() {
        StringBuilder sb = new StringBuilder();
        if (!join) sb.append("NOT(");
        sb.append(apply(getField()));
        if (!join) sb.append(")");
        if (children != null && !children.isEmpty()) {
            for (IFilterCriteria child : children) {
                sb.append(" AND ");
                sb.append(child.apply());
            }
        }
        return sb.toString();
    }


    protected abstract String apply(String field);

    @Override
    public String getField() {
        return field;
    }

    @Override
    public void setField(String field) {
        this.field = field;
    }

    @Override
    public void addChild(IFilterCriteria filterCriteria) {
        Args.notNull(filterCriteria, "filterCriteria");
        if (children == null) children = Lists.newArrayList();
        children.add(filterCriteria);
    }

    @Override
    public void clearChildren() {
        if (children != null) children.clear();
    }

    @Override
    public List<IFilterCriteria> getChildren() {
        return children != null ? Collections.unmodifiableList(children) : new ArrayList<IFilterCriteria>();
    }
}
