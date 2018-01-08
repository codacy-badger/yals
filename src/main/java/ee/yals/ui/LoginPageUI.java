package ee.yals.ui;

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
@Theme("valo")
@SpringUI(path = Endpoint.LOGIN_FORM_V)
public class LoginPageUI extends UI {

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        VerticalLayout content = new VerticalLayout();
        setContent(content);

        content.addComponent(new Label("Terve"));

        content.addComponent(new Button("Push Me", clickEvent -> Notification.show("Pushed!")));
    }
}
