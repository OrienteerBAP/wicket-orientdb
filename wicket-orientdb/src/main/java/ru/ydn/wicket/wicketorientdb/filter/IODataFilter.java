package ru.ydn.wicket.wicketorientdb.filter;

import org.apache.wicket.model.IModel;
import org.apache.wicket.util.io.IClusterable;
import ru.ydn.wicket.wicketorientdb.model.OQueryModel;

/**
 * @author Vitaliy Gonchar
 * Interface for getting filtered documents from OrientDB
 * @param <K> type of provide object
 * @param <F> type of filtered properties String or OProperty
 */
public interface IODataFilter<K, F> extends IClusterable {
    public IODataFilter<K, F> setQueryModel(OQueryModel<K> queryModel);
    public OQueryModel<K> getQueryModel();
    public OQueryModel<K> createQueryModel();
    public IModel<?> getFilteredValueByProperty(F filteredProperty);
    public void setQueryBuilder(IQueryBuilder<K> queryBuilder);
}