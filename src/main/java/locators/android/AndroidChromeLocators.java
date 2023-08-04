package locators.android;

import java.util.HashMap;
import java.util.Map;

public class AndroidChromeLocators {

  public static Map<String, String> createLabelLibrary() {
    Map<String, String> xpathToLabel = new HashMap<>();
    xpathToLabel.put("settingsMenuFirstElement", "//android.widget.TextView[@text='Basics']");
    xpathToLabel.put("sky logo",
        "//android.view.View[@resource-id='root']//android.view.View[contains(@content-desc,'sky')]");
    xpathToLabel.put("email input",
        "//android.view.View[@resource-id='root']//android.widget.EditText[@resource-id='email']");
    xpathToLabel.put("password input",
        "(//android.view.View[@resource-id='root']//android.widget.EditText)[2]");
    xpathToLabel.put("login button",
        "//android.view.View[@resource-id='root']//android.widget.Button[@text='Login']");
    xpathToLabel.put("reset pin link",
        "//android.view.View[@resource-id='root']//android.view.View[contains(@content-desc,'zurücksetzen')]");
    xpathToLabel.put("get customer number",
        "//android.view.View[@resource-id='root']//android.view.View[contains(@content-desc,'Jetzt anfordern')]");
    xpathToLabel.put("customer number lookup",
        "//android.view.View[@resource-id='root']//android.widget.TextView[contains(@text,'meine Kundennummer')]");

    return xpathToLabel;
  }

  public static Map<String, String> createLabelLibraryButton() {
    Map<String, String> xpathToButton = new HashMap<>();
    xpathToButton.put("cdp login", "//android.widget.Button[contains(@text,'Login')]");
    xpathToButton.put("newcrm cdp sign in",
        "//android.widget.Button[@resource-id='android:id/button1']");
    xpathToButton.put("guestIcon", "//div[@data-test-id='header-guest']");
    xpathToButton.put("cdpLoginButton", "//button[@type='submit' and @aria-label='Login']");
    xpathToButton.put("menuToggle", "//span[@data-test-id='toggle-menu-link']");
    xpathToButton.put("meinKonto", "//a[text()='Mein Konto']");
    xpathToButton.put("parentalPinSettings", "//div[text()='Altersfreigabe Einstellungen']");
    xpathToButton.put("parentalControlSave", "//span[text()='Speichern']");
    xpathToButton.put("user menu", "//android.widget.TextView[@resource-id='profile-icon']");
    xpathToButton.put("my account", "//android.view.View[@content-desc='Mein Konto verwalten']");
    xpathToButton.put("close", "//android.view.View[@resource-id='user-menu']");
    xpathToButton.put("change pin", "//android.view.View[@content-desc='Sky PIN ändern']");
    xpathToButton.put("save new pin",
        "//android.view.View[@resource-id='root']//android.widget.Button[contains(@text,'peichern')]");
    xpathToButton.put("logout", "//android.widget.Button[@text='Logout']");
    xpathToButton.put("forgot pin", "//android.view.View[@content-desc='Jetzt zurücksetzen']");
    xpathToButton.put("reset pin now", "//android.widget.Button[contains(@text,'Sky PIN')]");
    xpathToButton.put("unlock account",
        "//android.view.View[@content-desc='Jetzt Sky PIN zurück setzen']");
    xpathToButton.put("cdp register", "//android.widget.Button[contains(@text,'Anmelden')]");

    return xpathToButton;
  }

  public static Map<String, String> createLabelLibraryTextfield() {
    Map<String, String> xpathToTextfield = new HashMap<>();
    xpathToTextfield.put("cdp login email", "//android.widget.EditText[@resource-id='email']");
    xpathToTextfield.put("cdp login pin1", "(//android.widget.EditText)[2]");
    xpathToTextfield.put("cdp login pin2", "(//android.widget.EditText)[3]");
    xpathToTextfield.put("cdp login pin3", "(//android.widget.EditText)[4]");
    xpathToTextfield.put("cdp login pin4", "(//android.widget.EditText)[5]");
    xpathToTextfield.put("cdp old pin1", "(//android.widget.EditText)[1]");
    xpathToTextfield.put("cdp old pin2", "(//android.widget.EditText)[2]");
    xpathToTextfield.put("cdp old pin3", "(//android.widget.EditText)[3]");
    xpathToTextfield.put("cdp old pin4", "(//android.widget.EditText)[4]");
    xpathToTextfield.put("cdp new pin1", "(//android.widget.EditText)[5]");
    xpathToTextfield.put("cdp new pin2", "(//android.widget.EditText)[6]");
    xpathToTextfield.put("cdp new pin3", "(//android.widget.EditText)[7]");
    xpathToTextfield.put("cdp new pin4", "(//android.widget.EditText)[8]");
    xpathToTextfield.put("cdp confirm pin1", "(//android.widget.EditText)[9]");
    xpathToTextfield.put("cdp confirm pin2", "(//android.widget.EditText)[10]");
    xpathToTextfield.put("cdp confirm pin3", "(//android.widget.EditText)[11]");
    xpathToTextfield.put("cdp confirm pin4", "(//android.widget.EditText)[12]");
    xpathToTextfield.put("newcrm cdp user",
        "//android.widget.EditText[@resource-id='com.android.chrome:id/username']");
    xpathToTextfield.put("newcrm cdp password",
        "//android.widget.EditText[@resource-id='com.android.chrome:id/password']");
    xpathToTextfield.put("login error message", "//android.widget.TextView");
    xpathToTextfield.put("login success",
        "(//android.view.View[contains(@text,'Login')]//preceding::android.view.View)[last()]");
    xpathToTextfield.put("email error message",
        "//android.view.View[@resource-id='email-error-message']//android.widget.TextView");
    xpathToTextfield.put("cdpLoginPageUsernameTextbox", "//input[@id='email']");
    xpathToTextfield.put("cdpLoginPinDigit0", "//input[@data-test-id='default-pin-0']");
    xpathToTextfield.put("cdpLoginPinDigit1", "//input[@data-test-id='default-pin-1']");
    xpathToTextfield.put("cdpLoginPinDigit2", "//input[@data-test-id='default-pin-2']");
    xpathToTextfield.put("cdpLoginPinDigit3", "//input[@data-test-id='default-pin-3']");
    xpathToTextfield.put("password textfield",
        "//android.widget.EditText[@resource-id='email']//following::android.widget.EditText[1]");
    xpathToTextfield.put("reset pin email", "//android.widget.EditText[@resource-id='email']");
    xpathToTextfield.put("set new pin1", "(//android.widget.EditText)[1]");
    xpathToTextfield.put("set new pin2", "(//android.widget.EditText)[2]");
    xpathToTextfield.put("set new pin3", "(//android.widget.EditText)[3]");
    xpathToTextfield.put("set new pin4", "(//android.widget.EditText)[4]");
    xpathToTextfield.put("confirm set new pin1", "(//android.widget.EditText)[5]");
    xpathToTextfield.put("confirm set new pin2", "(//android.widget.EditText)[6]");
    xpathToTextfield.put("confirm set new pin3", "(//android.widget.EditText)[7]");
    xpathToTextfield.put("confirm set new pin4", "(//android.widget.EditText)[8]");

    return xpathToTextfield;
  }

  public static Map<String, String> createLabelLibraryUrl() {
    Map<String, String> xpathToTUrl = new HashMap<>();
    xpathToTUrl.put("sky product overview url", "sky.de");
    xpathToTUrl.put("sky contract url", "sky.de/hilfecenter/vertrag");
    xpathToTUrl.put("sky program url", "sky.de/hilfecenter/programm");
    xpathToTUrl.put("sky configuration url", "sky.de/hilfecenter/meine-einstellungen");
    xpathToTUrl.put("sky billing url", "sky.de/hilfecenter/abrechnung");
    xpathToTUrl.put("sky apps url", "sky.de/hilfecenter/sky-apps");
    xpathToTUrl.put("sky device url", "sky.de/hilfecenter/sky-geraete");
    xpathToTUrl.put("sky shipping url", "sky.de/hilfecenter/versand-retoure");
    xpathToTUrl.put("ticket configuration url",
        "skyticket.sky.de/service/faq/mein-account-und-bezahlung/tickets-verwalten");
    xpathToTUrl.put("ticket data url", "skyticket.sky.de/service/faq/mein-account-und-bezahlung");
    xpathToTUrl.put("ticket billing url",
        "skyticket.sky.de/service/faq/mein-account-und-bezahlung");
    xpathToTUrl.put("ticket functions url", "skyticket.sky.de/service/faq/inhalte-und-funktionen");
    xpathToTUrl.put("ticket program url", "skyticket.sky.de/watch/home");
    xpathToTUrl.put("ticket tv stick url", "skyticket.sky.de/service/faq/sky-ticket-tv-stick");
    xpathToTUrl.put("cdp login page url", "");

    return xpathToTUrl;
  }

  public static Map<String, String> createLabelLibraryTitle() {
    Map<String, String> xpathToTitle = new HashMap<>();
    xpathToTitle.put("sky product overview title", "Live Sport");
    xpathToTitle.put("sky contract title", "Vertrag - Hilfecenter | Sky");
    xpathToTitle.put("sky program title", "Programm - Sky");
    xpathToTitle.put("sky configuration title", "Meine Einstellungen - Sky");
    xpathToTitle.put("sky billing title", "Abrechnung - Sky");
    xpathToTitle.put("sky apps title", "Sky Apps - Sky");
    xpathToTitle.put("sky device title", "Sky Geräte - Sky");
    xpathToTitle.put("sky shipping title", "Versand / Retoure - Sky");
    xpathToTitle.put("ticket configuration title", "Tickets verwalten");
    xpathToTitle.put("ticket data title", "Mein Account & Bezahlung");
    xpathToTitle.put("ticket billing title", "Mein Account & Bezahlung");
    xpathToTitle.put("ticket functions title", "Inhalte & Funktionen");
    xpathToTitle.put("ticket program title", "Sky Ticket");
    xpathToTitle.put("ticket tv stick title", "Sky Ticket TV Stick");
    xpathToTitle.put("cdp login page title", "");

    return xpathToTitle;
  }

  public static Map<String, String> createLabelLibraryLandingPage() {
    Map<String, String> xpathToLandingPage = new HashMap<>();
    xpathToLandingPage.put("sky product overview", "(//android.view.View[@content-desc='Sky'])[1]");
    xpathToLandingPage.put("sky contract",
        "(//android.view.View[contains(@text,'Vertrag') or contains(@text,'contract')])[1]");
    xpathToLandingPage.put("sky program",
        "(//android.view.View[contains(@text,'Programm') or contains(@text,'program')])[1]");
    xpathToLandingPage.put("sky configuration",
        "(//android.view.View[contains(@text,'Einstellungen') or contains(@text,'settings')])[1]");
    xpathToLandingPage.put("sky billing",
        "(//android.view.View[contains(@text,'Abrechnung') or contains(@text,'Billing')])[1]");
    xpathToLandingPage.put("sky apps",
        "(//android.view.View[contains(@text,'Apps') or contains(@text,'apps')])[1]");
    xpathToLandingPage.put("sky device",
        "(//android.view.View[contains(@text,'Geräte') or contains(@text,'devices')])[1]");
    xpathToLandingPage.put("sky shipping",
        "(//android.view.View[contains(@text,'Retoure') or contains(@text,'returns')])[1]");
    xpathToLandingPage.put("ticket configuration",
        "(//android.view.View[contains(@text,'verwalten')])[1]");
    xpathToLandingPage.put("ticket data",
        "(//android.widget.ListView//android.view.View[contains(@content-desc,'Account')])[1]");
    xpathToLandingPage.put("ticket billing",
        "(//android.widget.ListView//android.view.View[contains(@content-desc,'Abrechnung')])[1]");
    xpathToLandingPage.put("ticket functions",
        "(//android.widget.ListView//android.view.View[contains(@content-desc,'Sky Ticket')])[1]");
    xpathToLandingPage.put("ticket program", "//android.view.View[@text='Highlights']");
    xpathToLandingPage.put("ticket tv stick",
        "(//android.widget.ListView//android.view.View[contains(@content-desc,'Sky Ticket TV Stick')])[1]");
    xpathToLandingPage.put("cdp login page", "");
    return xpathToLandingPage;
  }
}
