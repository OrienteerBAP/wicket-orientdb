package ru.ydn.wicket.wicketorientdb.utils.query.filter;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.lang.Args;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.value.IFilterValue;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.value.ListFilterValue;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.value.PrimeFilterValue;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Default implementation of {@link IFilterCriteriaManager}
 */
public class FilterCriteriaManager implements IFilterCriteriaManager {

    private final IModel<OProperty> propertyModel;
    private final Map<FilterCriteriaType, IFilterCriteria> filterCriterias;
    private boolean and;

    public FilterCriteriaManager(IModel<OProperty> propertyModel) {
        Args.notNull(propertyModel, "propertyModel");
        Args.notNull(propertyModel.getObject(), "propertyModel.getObject()");
        this.propertyModel = propertyModel;
        filterCriterias = Maps.newHashMap();
        and = true;
    }

    @Override
    public String apply() {
        if (filterCriterias.isEmpty())
            return null;

        StringBuilder sb = new StringBuilder();
        String logicalOperator = and ? " AND " : " OR ";
        int counter = 0;
        for (FilterCriteriaType key : filterCriterias.keySet()) {
            IFilterCriteria filterCriteria = filterCriterias.get(key);
            if (filterCriteria == null)
                continue;
            String filter = filterCriteria.apply();
            if (!Strings.isNullOrEmpty(filter)) {
                if (counter > 0)
                    sb.append(logicalOperator);
                sb.append(filter);
                counter++;
            }
        }
        return sb.toString();
    }

    @Override
    public void setAnd(boolean and) {
        this.and = and;
    }

    @Override
    public <T> IFilterCriteria createEqualsFilterCriteria(IModel<T> model, IModel<Boolean> join) {
        OProperty property = propertyModel.getObject();
        IStringConverter<T> stringConverter = StringConverter.getStringConverter(property.getType());
        IFilterValue value = new PrimeFilterValue<>(model, stringConverter);

        return new EqualsFilterCriteria(property.getName(), value, join);
    }

    @Override
    public <T> IFilterCriteria createListFilterCriteria(List<IModel<T>> models, IModel<Boolean> join) {
        OProperty property = propertyModel.getObject();
        IStringConverter<T> stringConverter = StringConverter.getStringConverter(property.getType());
        ListFilterValue<?> value = new ListFilterValue<>(models, stringConverter);

        return new ListFilterCriteria(property.getName(), value, join);
    }

    @Override
    public <T> IFilterCriteria createRangeFilterCriteria(List<IModel<T>> models, IModel<Boolean> join) {
        Args.isTrue(models.size() == 2, "models.size() == 2");
        OProperty property = propertyModel.getObject();
        IStringConverter<T> stringConverter = StringConverter.getStringConverter(property.getType());
        ListFilterValue<?> value = new ListFilterValue<>(models, stringConverter);
        return new RangeFilterCriteria(property.getName(), value, join);
    }

    @Override
    public IFilterCriteria createStartOrEndStringFilterCriteria(IModel<String> model, boolean start, IModel<Boolean> join) {
        OProperty property = propertyModel.getObject();
        Args.isTrue(property.getType() == OType.STRING, "property.getType() == OType.STRING");
        IStringConverter<String> stringConverter = StringConverter.getStringConverter(property.getType());
        IFilterValue value = new PrimeFilterValue<>(model, stringConverter);
        return new StartOrEndStringFilterCriteria(property.getName(), value, start, join);
    }

    @Override
    public void setFilterCriteria(FilterCriteriaType type, IFilterCriteria filterCriteria) {
        filterCriterias.put(type, filterCriteria);
    }

    @Override
    public IFilterCriteria getFilterCriteria(FilterCriteriaType type) {
        return filterCriterias.get(type);
    }

    @Override
    public Map<FilterCriteriaType, IFilterCriteria> getFilterCriterias() {
        return Collections.unmodifiableMap(filterCriterias);
    }

    @Override
    public void clearFilterCriterias() {
        filterCriterias.clear();
    }

    @Override
    public boolean isFilterApply() {
        for (IFilterCriteria criteria : filterCriterias.values()) {
            String filter = criteria.apply();
            if (!Strings.isNullOrEmpty(filter)) {
                return true;
            }
        }
        return false;
    }
}