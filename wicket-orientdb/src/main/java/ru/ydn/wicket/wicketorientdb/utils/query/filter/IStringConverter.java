package ru.ydn.wicket.wicketorientdb.utils.query.filter;

import com.google.common.base.Function;
import org.apache.wicket.util.io.IClusterable;

/**
 * Interface to convert some value to {@link String}
 * @param <T> type of some value
 */
public interface IStringConverter<T> extends Function<T, String>, IClusterable {
}
