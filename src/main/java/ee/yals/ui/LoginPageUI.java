package ee.yals.ui;

import com.vaadin.annotations.StyleSheet;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import ee.yals.Endpoint;

/**
 * Page for entering username and password
 *
 * @since 3.0
 */

@Title("Login to MyYals")
@Theme("valo")
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
        Component form = form();
        mainGrid.addComponent(form);
        mainGrid.setComponentAlignment(form, Alignment.MIDDLE_CENTER);
        mainGrid.addComponent(new Label());

        for (int col = 1; col <= mainGrid.getColumns(); col++) {
            mainGrid.addComponent(new Label());
        }

        setContent(mainGrid);
    }

    private Component form() {
        FormLayout formLayout = new FormLayout();

        Label formTitle = new Label("Please log in");
        Label formSubTitle = new Label("to continue to My Yals");

        //input box user
        TextField userField = new TextField("Name");
        userField.setIcon(VaadinIcons.USER);

        //input box password
        PasswordField passwordField = new PasswordField("Password");
        passwordField.setIcon(VaadinIcons.KEY);

        Label demoString = new Label("For demo access use: demo/demo");

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setSizeFull();

        Button loginButton = new Button("Log in");
        loginButton.setStyleName(ValoTheme.BUTTON_PRIMARY);

        buttons.addComponent(loginButton);

        formLayout.addComponents(formTitle, formSubTitle, userField, passwordField, demoString, buttons);

        // setSizeUndefined();

        return formLayout;
    }
}
