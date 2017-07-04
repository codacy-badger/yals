package ee.yals.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * App-wide constants
 *
 * @author Alexander Muravya (alexander.muravya@kuehne-nagel.com)
 * @since 1.0
 */
public class AppUtils {
    public static final String LOCATION_HEADER_NAME = "Location";
    public static final String TEST_HEADER_NAME = "Tezt";

    private AppUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static final Gson GSON = new GsonBuilder().serializeNulls().create();
}
