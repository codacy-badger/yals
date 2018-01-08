package ee.yals.ui;

import com.vaadin.annotations.StyleSheet;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import ee.yals.Endpoint;

/**
 * Page for entering username and password
 *
 * @since 3.0
 */

@Title("Login to MyYals")
@Theme("yals")
@SpringUI(path = Endpoint.LOGIN_FORM_V)
@StyleSheet("vaadin://loginPage.css")
public class LoginPageUI extends UI {

    private final GridLayout mainGrid = new GridLayout(3, 3);

    @Override
    protected void init(VaadinRequest vaadinRequest) {

        mainGrid.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        mainGrid.setSizeFull();

        for (int col = 1; col <= mainGrid.getColumns(); col++) {
            mainGrid.addComponent(new Label());
        }

        mainGrid.addComponent(new Label());
        mainGrid.addComponent(form());
        mainGrid.addComponent(new Label());

        for (int col = 1; col <= mainGrid.getColumns(); col++) {
            mainGrid.addComponent(new Label());
        }

        setContent(mainGrid);
    }

    private Component form() {
        VerticalLayout formLayout = new VerticalLayout();

        Label formTitle = new Label("Please log in");
        Label formSubTitle = new Label("to continue to My Yals");

        //input box user
        //input box password

        Label demoString = new Label("For demo access use: demo/demo");
        Button loginButton = new Button("Log in");

        formLayout.addComponentsAndExpand(formTitle, formSubTitle, demoString, loginButton);
        return formLayout;
    }
}
