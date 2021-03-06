package eu.yals.test.ui.pages.front;

import eu.yals.test.ui.UITest;
import eu.yals.test.ui.pageobjects.FrontPage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.open;

/**
 * Tests unusual usage
 *
 * @since 2.0
 */

@RunWith(SpringRunner.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class AbnormalUsageTest extends UITest {

    @Test
    public void extraArgumentsShouldBeIgnored() {
        final String EXTRA_ARGUMENT = "mineMetsa";
        final String LINK_TO_SAVE = "https://vr.fi";

        open("/?" + EXTRA_ARGUMENT);
        pasteValueInFormAndSubmitIt(LINK_TO_SAVE);

        FrontPage.ResultRow.RESULT_LINK.shouldNotBe(empty);
        FrontPage.ResultRow.RESULT_LINK.shouldNotHave(text(EXTRA_ARGUMENT));
    }

}
