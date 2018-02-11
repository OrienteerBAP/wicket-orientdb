package ru.ydn.wicket.wicketorientdb.utils.query.filter;

import com.google.common.collect.Maps;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.io.IClusterable;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Abstract class which which represents types of {@link IFilterCriteria}
 * For create own {@link FilterCriteriaType} use name pattern {@link FilterCriteriaType#NAME_PATTERN}
 */
public final class FilterCriteriaType implements IClusterable {
    private static final Map<String, FilterCriteriaType> REGISTRY = Maps.newHashMap();

    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9]+$");

    public static final FilterCriteriaType EQUALS                        = createAndRegister("equals", false, false);
    public static final FilterCriteriaType COLLECTION                   = createAndRegister("collection", true, false);
    public static final FilterCriteriaType RANGE                        = createAndRegister("range", true, false);
    public static final FilterCriteriaType CONTAINS_TEXT                = createAndRegister("containsText", false, false);
    public static final FilterCriteriaType LINK                         = createAndRegister("link", false, false);
    public static final FilterCriteriaType LINKLIST                     = createAndRegister("linkList", true, false);
    public static final FilterCriteriaType LINKSET                      = createAndRegister("linkSet", true, false);
    public static final FilterCriteriaType CONTAINS_KEY                 = createAndRegister("containsKey", false, true);
    public static final FilterCriteriaType CONTAINS_VALUE               = createAndRegister("containsValue", false, true);
    public static final FilterCriteriaType EMBEDDED_CONTAINS_VALUE      = createAndRegister("embeddedContainsValue", false, false);
    public static final FilterCriteriaType EMBEDDED_CONTAINS_KEY        = createAndRegister("embeddedContainsKey", false, false);
    public static final FilterCriteriaType EMBEDDED_COLLECTION          = createAndRegister("embeddedCollection", true, false);
    public static final FilterCriteriaType EMBEDDED_COLLECTION_CONTAINS = createAndRegister("embeddedCollectionContains", false, false);

    private final boolean collection;
    private final boolean map;
    private final String name;

    /**
     * @param name name of {@link FilterCriteriaType}
     * @param collection if true - {@link FilterCriteriaType} include {@link IModel<Collection<?>>}
     * @param map if true - {@link FilterCriteriaType} include {@link IModel<Map<?>>}
     */
    private FilterCriteriaType(String name, boolean collection, boolean map) {
        if (!NAME_PATTERN.matcher(name).matches())
            throw new IllegalStateException(
                    String.format("FilterCriteriaType name '%s' don't match with pattern: '%s'",
                            name, NAME_PATTERN.pattern()));
        this.name = name;
        this.collection = collection;
        this.map = map;
    }

    /**
     * @return name of current {@link FilterCriteriaType}
     */
    public String getName() {
        return name;
    }

    /**
     * @return if true - {@link IFilterCriteria} appropriate to current {@link FilterCriteriaType}
     *                   contains model with {@link Collection}
     */
    public boolean isCollection() {
        return collection;
    }

    /**
     *
     * @return d
     */
    public boolean isMap() {
        return map;
    }

    /**
     * Create {@link FilterCriteriaType}
     * @param name name of current {@link FilterCriteriaType}
     * @param collection if true - {@link FilterCriteriaType} include {@link IModel} with {@link Collection}
     * @param map if true
     * @return new {@link FilterCriteriaType}
     * @throws IllegalStateException if name don't matches with name pattern
     */
    public static FilterCriteriaType create(String name,  boolean collection, boolean map) {
        return new FilterCriteriaType(name, collection, map);
    }

    /**
     * Create and register {@link FilterCriteriaType}
     * @param name name of current {@link FilterCriteriaType}
     * @param collection if true - {@link FilterCriteriaType} include {@link IModel} with {@link Collection}
     * @param map d
     * @return new {@link FilterCriteriaType}
     * @throws IllegalStateException if {@link FilterCriteriaType} with current name if already registered
     */
    public static FilterCriteriaType createAndRegister(String name,  boolean collection, boolean map) {
        FilterCriteriaType type = create(name, collection, map);
        register(type);
        return type;
    }

    /**
     * Register {@link FilterCriteriaType}
     * @param type {@link FilterCriteriaType} for register
     * @throws IllegalStateException if {@link FilterCriteriaType} with current name if already registered
     */
    public static void register(FilterCriteriaType type) {
        if (REGISTRY.containsKey(type.getName()))
            throw new IllegalStateException(
                    String.format("FilterCriteriaType with name %s is already registered!", type.getName()));
        REGISTRY.put(type.getName(), type);
    }

    /**
     * Get {@link FilterCriteriaType} from registry by name
     * @param name name of {@link FilterCriteriaType}
     * @return {@link FilterCriteriaType} or null if {@link FilterCriteriaType} with current name not registered
     */
    public static FilterCriteriaType getByName(String name) {
        return REGISTRY.get(name);
    }

    /**
     * @return unmodifiable {@link Collection} of {@link FilterCriteriaType} which contains all registered {@link FilterCriteriaType}
     */
    public static Collection<FilterCriteriaType> getRegisteredCriteriaTypes() {
        return Collections.unmodifiableCollection(REGISTRY.values());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FilterCriteriaType type = (FilterCriteriaType) o;

        return name != null ? name.equals(type.name) : type.name == null;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "FilterCriteriaType name: " + getName();
    }
}
