package eu.yals.test;

/**
 * Test Application constants, pretty same as {@link eu.yals.constants.App}, but only stuff used in testing scope
 *
 * @since 2.5
 */
public class TestApp {
    public static class Properties {
        public static final String TEST_URL = "testUrl";
        public static final String SERVER_PORT = "port";
        public static final String TEST_RUN_MODE = "test.runMode";
    }

    public static class Selenide {
        public static final String BROWSER = "selenide.browser";
        public static final String TIMEOUT = "selenide.timeout";
        public static final String REPORT_DIR = "yals.selenide.reportdir";
    }

    public enum RunMode {
        CONTAINER,
        LOCAL
    }
}