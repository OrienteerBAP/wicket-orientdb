package ru.ydn.wicket.wicketorientdb.utils.query.filter;

import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.io.IClusterable;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Class for create {@link IStringConverter} for {@link OType}
 */
public abstract class StringConverter implements IClusterable {

    /**
     * Create {@link IStringConverter<T>} for input {@link OType}
     * @param type {@link OType}
     * @param <T> type of value
     * @return {@link IStringConverter<T>}
     */
    @SuppressWarnings("unchecked")
    public static <T> IStringConverter<T> createStringConverter(OType type) {
        IStringConverter<T> stringConverter;
        switch (type) {
            case DATE:
            case DATETIME:
                stringConverter = (IStringConverter<T>) createDateStringConverter(type);
                break;
            case LINKLIST:
            case LINKMAP:
            case LINKSET:
            case LINK:
                stringConverter = (IStringConverter<T>) createLinkStringConverter();
                break;
            default:
                stringConverter = getDefaultStringConverter();
                break;
        }
        return stringConverter;
    }

    /**
     * Create default string converter
     * @param <T> type of value
     * @return {@link IStringConverter}
     */
    private static <T> IStringConverter<T> getDefaultStringConverter() {
        return new IStringConverter<T>() {
            @Override
            public String apply(T t) {
                return t != null ? t.toString() : null;
            }
        };
    }

    /**
     * Create string converter for {@link Date}
     * @param type {@link OType}
     * @return {@link IStringConverter<Date>}
     */
    private static IStringConverter<Date> createDateStringConverter(final OType type) {
        return new IStringConverter<Date>() {
            private final DateFormat format = new SimpleDateFormat(getDateFormat(type));
            @Override
            public String apply(Date date) {
                return date != null ? format.format(date) : null;
            }
        };
    }


    private static IStringConverter<ODocument> createLinkStringConverter() {
        return new IStringConverter<ODocument>() {
            @Override
            public String apply(ODocument input) {
                return input != null ? input.getIdentity().toString() : null;
            }
        };
    }

    /**
     * Get date format
     * @param type {@link OType}
     * @return date format
     */
    @SuppressWarnings("unchecked")
    private static String getDateFormat(final OType type) {
        return new DBClosure<String>() {
            @Override
            protected String execute(ODatabaseDocument db) {
                String format = null;
                if (type == OType.DATE) {
                    format = (String) db.get(ODatabase.ATTRIBUTES.DATEFORMAT);
                } else if (type == OType.DATETIME){
                    format = (String) db.get(ODatabase.ATTRIBUTES.DATETIMEFORMAT);
                }
                return format;
            }
        }.execute();
    }
}
