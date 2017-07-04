package ru.ydn.wicket.wicketorientdb.utils.query.filter.link;

import ru.ydn.wicket.wicketorientdb.utils.query.filter.AbstractFilterCriteria;

import java.util.List;

/**
 * Filter for search documents with equals link collection field.
 */
public class EqualsLinkCollectionFilterCriteria extends AbstractFilterCriteria {

    private final List<String> linkList;

    public EqualsLinkCollectionFilterCriteria(String field, List<String> linkList, boolean join) {
        super(field, join);
        this.linkList = linkList;
    }

    @Override
    protected String apply() {
        StringBuilder sb = new StringBuilder();
        sb.append(getField());
        sb.append(" = [");
        for (int i = 0; i < linkList.size(); i++) {
            String orid = linkList.get(i);
            if (!orid.startsWith("#")) sb.append("#");
            sb.append(orid);
            if (i != linkList.size() - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }
}
