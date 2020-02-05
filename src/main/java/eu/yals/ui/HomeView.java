package eu.yals.ui;

import com.github.appreciated.app.layout.annotations.Caption;
import com.github.appreciated.app.layout.annotations.Icon;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.board.Board;
import com.vaadin.flow.component.board.Row;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.yals.Endpoint;
import eu.yals.constants.App;
import eu.yals.json.StoreRequestJson;
import eu.yals.services.GitService;
import eu.yals.services.overall.OverallService;
import eu.yals.ui.css.HomeViewCss;
import eu.yals.utils.AppUtils;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
@SpringComponent
@UIScope
@Route(value = Endpoint.UI.HOME_PAGE, layout = AppView.class)
@Caption("Home")
@Icon(VaadinIcon.HOME)
@PageTitle("Link shortener for friends")
public class HomeView extends VerticalLayout {
  private static final String TAG = "[Front Page]";

  private final Board board = new Board();
  private final Row firstRow = new Row();
  private final Row mainRow = new Row();
  private final Row overallRow = new Row();
  private final Row resultRow = new Row();
  private final Row qrCodeRow = new Row();
  private HorizontalLayout footer = null;

  private final HomeViewCss homeViewCss;
  private final OverallService overallService;
  private final GitService gitService;
  private final AppUtils appUtils;

  private TextField input;
  Anchor shortLink;
  Image qrCode;

  private String latestCommit;
  private String latestTag;

  private Span linkCounter;

  Notification errorNotification;

  public HomeView(
      HomeViewCss css, OverallService overallService, GitService gitService, AppUtils appUtils) {
    this.homeViewCss = css;
    this.overallService = overallService;
    this.gitService = gitService;
    this.appUtils = appUtils;

    init();
    applyStyle();
    applyLoadState();
  }

  private void init() {
    this.setId(IDs.VIEW_ID);

    mainRow.add(emptyDiv(), mainArea(), emptyDiv());
    overallRow.add(emptyDiv(), overallArea(), emptyDiv());
    resultRow.add(emptyDiv(), resultArea(), emptyDiv());
    qrCodeRow.add(emptyDiv(), qrCodeArea(), emptyDiv());

    board.addRow(firstRow);
    board.addRow(mainRow);
    board.addRow(overallRow);
    board.addRow(resultRow);
    board.addRow(qrCodeRow);

    add(board);
    if (AppUtils.isNotMobile(VaadinSession.getCurrent())) {
      prepareGitInfoForFooter();
      if (displayFooter()) {
        footer = footer();
        add(footer);
      }
    }
  }

  private void applyStyle() {
    mainRow.setComponentSpan(mainRow.getComponentAt(1), 2);
    homeViewCss.applyRowStyle(mainRow);

    overallRow.setComponentSpan(overallRow.getComponentAt(1), 2);
    homeViewCss.applyRowStyle(overallRow);

    resultRow.setComponentSpan(resultRow.getComponentAt(1), 2);
    homeViewCss.applyRowStyle(resultRow);

    qrCodeRow.setComponentSpan(qrCodeRow.getComponentAt(1), 2);
    homeViewCss.applyRowStyle(qrCodeRow);

    board.setSizeFull();
  }

  private void applyLoadState() {
    long linksStored = overallService.numberOfStoredLinks();
    linkCounter.setText(Long.toString(linksStored));

    mainRow.setVisible(true);
    overallRow.setVisible(true);
    resultRow.setVisible(false);
    qrCodeRow.setVisible(false);
  }

  private void prepareGitInfoForFooter() {
    latestCommit = gitService.getGitInfoSource().getLatestCommitHash().trim();
    latestTag = gitService.getGitInfoSource().getLatestTag().trim();
  }

  private boolean displayFooter() {
    boolean commitPresent =
        (!latestCommit.equals(App.NO_VALUE) && StringUtils.isNotBlank(latestCommit));
    boolean tagPresent = (!latestTag.equals(App.NO_VALUE) && StringUtils.isNotBlank(latestTag));

    boolean displayCommitInfo = commitPresent && tagPresent;
    log.trace(
        "{} will I display footer: {}. Commit present: {}. Tag present: {} ",
        TAG,
        displayCommitInfo,
        commitPresent,
        tagPresent);

    return displayCommitInfo;
  }

  private Div emptyDiv() {
    Div div = new Div();
    div.setText("");
    return div;
  }

  private VerticalLayout mainArea() {
    H2 title = new H2("Yet another link shortener");
    title.setId(IDs.TITLE);
    Span subtitle = new Span("... for friends");
    subtitle.setId(IDs.SUBTITLE);
    homeViewCss.makeSubtitleItalic(subtitle);

    input = new TextField("Your very long URL here:");
    input.setId(IDs.INPUT);
    input.setPlaceholder("http://mysuperlongurlhere.tld");
    input.setWidthFull();

    Span publicAccessBanner =
        new Span("Note: all links considered as public and can be used by anyone");
    publicAccessBanner.setId(IDs.BANNER);

    Button submitButton = new Button("Shorten it!");
    submitButton.setId(IDs.SUBMIT_BUTTON);
    submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    submitButton.addClickListener(this::onSaveLink);

    VerticalLayout mainArea =
        new VerticalLayout(title, subtitle, input, publicAccessBanner, submitButton);
    mainArea.setId(IDs.MAIN_AREA);
    homeViewCss.applyMainAreaStyle(mainArea);
    return mainArea;
  }

  private HorizontalLayout overallArea() {
    Span overallTextStart = new Span("Yals already saved ");

    linkCounter = new Span();
    linkCounter.setId(IDs.OVERALL_LINKS_NUMBER);
    Span overallTextEnd = new Span(" links");

    Span overallText = new Span(overallTextStart, linkCounter, overallTextEnd);
    overallText.setId(IDs.OVERALL_LINKS_TEXT);

    HorizontalLayout overallArea = new HorizontalLayout(overallText);
    overallArea.setId(IDs.OVERALL_AREA);
    homeViewCss.applyOverallAreaStyle(overallArea);
    return overallArea;
  }

  private HorizontalLayout resultArea() {
    HorizontalLayout resultArea = new HorizontalLayout();
    resultArea.setId(IDs.RESULT_AREA);

    Span emptySpan = new Span();

    shortLink = new Anchor("", "");
    shortLink.setId(IDs.SHORT_LINK);
    homeViewCss.makeLinkStrong(shortLink);

    com.vaadin.flow.component.icon.Icon copyLinkImage;
    copyLinkImage = new com.vaadin.flow.component.icon.Icon(VaadinIcon.PASTE);
    copyLinkImage.setId(IDs.COPY_LINK_BUTTON);
    copyLinkImage.addClickListener(this::copyLinkToClipboard);

    homeViewCss.applyResultAreaStyle(resultArea);
    resultArea.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
    resultArea.setDefaultVerticalComponentAlignment(Alignment.CENTER);

    resultArea.add(emptySpan, shortLink, copyLinkImage);
    return resultArea;
  }

  private Div qrCodeArea() {
    Div qrCodeArea = new Div();
    qrCodeArea.setId(IDs.QR_CODE_AREA);

    qrCode = new Image();
    qrCode.setId(IDs.QR_CODE);
    qrCode.setSrc("");
    qrCode.setAlt("qrCode");

    homeViewCss.applyQrCodeAreaStyle(qrCodeArea);

    qrCodeArea.add(qrCode);
    return qrCodeArea;
  }

  private HorizontalLayout footer() {
    HorizontalLayout footer = new HorizontalLayout();
    footer.setId(IDs.FOOTER);

    Span versionStart = new Span(String.format("Version %s (based on commit ", this.latestTag));
    Anchor commit =
        new Anchor(
            String.format("%s/%s", App.Git.REPOSITORY, this.latestCommit),
            latestCommit.substring(0, Integer.min(latestCommit.length(), 7)));
    commit.setId(IDs.COMMIT_LINK);
    Span versionEnd = new Span(")");

    Span version = new Span(versionStart, commit, versionEnd);
    version.setId(IDs.VERSION);
    homeViewCss.paintVersion(version);

    homeViewCss.applyFooterStyle(footer);
    footer.setWidthFull();

    footer.add(version);

    return footer;
  }

  private Notification getErrorNotification(String text) {
    Notification notification = new Notification();
    notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
    notification.setPosition(Notification.Position.MIDDLE);

    Label label = new Label(text);
    Button closeButton = new Button("OK", event -> notification.close());

    notification.add(label, closeButton);
    return notification;
  }

  private void onSaveLink(ClickEvent<Button> buttonClickEvent) {
    cleanErrors();

    boolean isFormValid = true;
    String longUrl = input.getValue();
    log.debug("{} Got long URL: {}", TAG, longUrl);
    cleanForm();

    if (StringUtils.isBlank(longUrl)) {
      String errorMessage = "Long URL cannot be empty";
      showError(errorMessage);
      isFormValid = false;
    } else {
      try {
        longUrl = AppUtils.makeFullUri(longUrl).toString();
      } catch (RuntimeException e) {
        log.error("URL validation failed", e);
        showError("Got malformed URL or not URL at all");
        isFormValid = false;
      }
    }

    if (isFormValid) {
      cleanResults();
      sendLink(longUrl);
    }
  }

  private void copyLinkToClipboard(
      ClickEvent<com.vaadin.flow.component.icon.Icon> buttonClickEvent) {
    // TODO https://vaadin.com/directory/component/v-clipboard/samples
  }

  private void sendLink(String link) {
    final String apiRoute = Endpoint.Api.STORE_API;
    StoreRequestJson json = StoreRequestJson.create().withLink(link);
    HttpResponse<JsonNode> response =
        Unirest.post(appUtils.getAPIHostPort() + apiRoute).body(json).asJson();
    if (response.isSuccess()) {
      onSuccessStoreLink(response);
    } else {
      onFailStoreLink(response);
    }
  }

  private void onSuccessStoreLink(HttpResponse<JsonNode> response) {
    cleanErrors();
    cleanForm();
    if (response.getStatus() == 201) {
      JsonNode json = response.getBody();
      String ident = json.getObject().getString("ident");
      if (StringUtils.isNotBlank(ident)) {
        shortLink.setText(appUtils.getServerUrl() + "/" + ident);
        shortLink.setHref(ident);
        resultRow.setVisible(true);
        updateCounter();
        generateQRCode(ident);
      } else {
        showError("Internal error. Got malformed reply from server");
      }
    }
  }

  private void onFailStoreLink(HttpResponse<JsonNode> response) {
    JsonNode json = response.getBody();
    log.error("{} Failed to store link. Reply: {}", TAG, json);
    String message = json.getObject().getString("message");
    showError(message);
  }

  private void updateCounter() {
    int currentNumber = Integer.parseInt(linkCounter.getText());
    linkCounter.setText(String.valueOf(currentNumber + 1));
  }

  private int calculateQRCodeSize() {
    int[] browserWidthInfo = new int[1];
    if (getUI().isPresent()) {
      int[] finalBrowserWidthInfo = browserWidthInfo;
      getUI()
          .get()
          .getPage()
          .retrieveExtendedClientDetails(
              details -> finalBrowserWidthInfo[0] = details.getScreenWidth());
    } else {
      browserWidthInfo = new int[] {0};
    }
    int browserWidth = browserWidthInfo[0];

    int defaultQRBlockSize = 371;
    int defaultQRCodeSize = 350;
    float qrBlockRatio = 0.943f; // 350/371

    int size;
    if (browserWidth > defaultQRBlockSize) {
      size = defaultQRCodeSize;
    } else {
      size = Math.round(browserWidth * qrBlockRatio);
    }
    return size;
  }

  private void generateQRCode(String ident) {
    int size = calculateQRCodeSize();
    String qrCodeGeneratorRoute;
    if (size == 0) {
      qrCodeGeneratorRoute =
          String.format("%s/%s/%s", appUtils.getAPIHostPort(), Endpoint.Api.QR_CODE_API, ident);
    } else {
      qrCodeGeneratorRoute =
          String.format(
              "%s/%s/%s/%d", appUtils.getAPIHostPort(), Endpoint.Api.QR_CODE_API, ident, size);
    }

    HttpResponse<JsonNode> response = Unirest.get(qrCodeGeneratorRoute).asJson();
    if (response.isSuccess()) {
      onSuccessGenerateQRCode(response);
    } else {
      onFailGenerateQRCode(response);
    }
  }

  private void onSuccessGenerateQRCode(HttpResponse<JsonNode> response) {
    if (response.getStatus() == 200) {
      String qrCode = response.getBody().getObject().getString("qrCode");
      if (StringUtils.isNotBlank(qrCode)) {
        this.qrCode.setSrc(qrCode);
        qrCodeRow.setVisible(true);
        if (footer != null) footer.setVisible(false);
      } else {
        showError("Internal error. Got malformed reply from QR generator");
      }
    }
  }

  private void onFailGenerateQRCode(HttpResponse<JsonNode> response) {
    showError("Internal error. Got malformed reply from QR generator");
    this.qrCode.setSrc("");
    qrCodeRow.setVisible(false);

    if (response.getBody() != null) {
      log.debug("{} QR Code Reply JSON: {}", TAG, response.getBody());
    }
  }

  private void showError(String errorMessage) {
    String errorMsg = "<b>Hups!</b> Validation failed: <br>" +
            errorMessage;
    errorNotification = getErrorNotification(errorMsg);
    errorNotification.open();
  }

  private void cleanForm() {
    input.setValue("");
  }

  private void cleanErrors() {
    if (errorNotification != null && errorNotification.isOpened()) {
      errorNotification.close();
    }
  }

  private void cleanResults() {
    shortLink.setHref("");
    shortLink.setText("");
    resultRow.setVisible(false);

    qrCode.setSrc("");
    qrCodeRow.setVisible(false);
  }

  public static class IDs {
    public static final String VIEW_ID = "homeView";
    public static final String MAIN_AREA = "mainArea";
    public static final String TITLE = "siteTitle";
    public static final String SUBTITLE = "subTitle";
    public static final String INPUT = "longUrlInput";
    public static final String BANNER = "publicAccessBanner";
    public static final String SUBMIT_BUTTON = "submitButton";

    public static final String OVERALL_AREA = "overallArea";
    public static final String OVERALL_LINKS_TEXT = "overallLinksText";
    public static final String OVERALL_LINKS_NUMBER = "overallLinksNum";

    public static final String RESULT_AREA = "resultArea";

    public static final String SHORT_LINK = "shortLink";
    public static final String COPY_LINK_BUTTON = "copyLink";

    public static final String QR_CODE_AREA = "qrCodeArea";
    public static final String QR_CODE = "qrCode";

    public static final String FOOTER = "footer";
    public static final String VERSION = "version";
    public static final String COMMIT_LINK = "commitLink";
  }
}
