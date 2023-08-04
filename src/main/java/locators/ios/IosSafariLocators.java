package locators.ios;

import java.util.HashMap;
import java.util.Map;

public class IosSafariLocators {

  public static Map<String, String> createLabelLibrary() {
    Map<String, String> xpathToLabel = new HashMap<>();
    xpathToLabel.put("settingsMenuFirstElement", "");

    return xpathToLabel;
  }

  public static Map<String, String> createLabelLibraryButton() {
    Map<String, String> xpathToButton = new HashMap<>();
    xpathToButton.put("cdp login", "//XCUIElementTypeButton[@label='Anmelden']");
    xpathToButton.put("newcrm cdp sign in", "//XCUIElementTypeButton[@name='Log In']");
    xpathToButton.put("user menu", "//*[@name='Login' or @name='Group 2']");
    xpathToButton.put("test sky link",
        "//XCUIElementTypeStaticText[contains(@label,'kostenlos testen')]");
    xpathToButton.put("forgot pin", "//XCUIElementTypeStaticText[contains(@label,'zurücksetzen')]");
    xpathToButton.put("reset pin now", "//XCUIElementTypeButton[@name='Sky PIN zurücksetzen']");
    xpathToButton.put("my account", "//XCUIElementTypeStaticText[@name='Mein Konto verwalten']");
    xpathToButton.put("close",
        "//XCUIElementTypeOther[contains(@name,'Sky')]/XCUIElementTypeOther[2]");
    xpathToButton.put("change pin", "//XCUIElementTypeStaticText[@name='Sky PIN ändern']");
    xpathToButton.put("save new pin",
        "//XCUIElementTypeButton[contains(@name,'nderungen speichern') or contains(@label,'Speichern')]");
    xpathToButton.put("logout", "//XCUIElementTypeButton[@name='Logout']");
    xpathToButton.put("customer number lookup",
        "//XCUIElementTypeStaticText[contains(@name,'meine Kundennummer')]");
    xpathToButton.put("customer number lookup close",
        "//XCUIElementTypeOther[contains(@name,'Sky')]/XCUIElementTypeOther[1]");
    xpathToButton.put("not now", "//XCUIElementTypeButton[@name=\"Not Now\"]");
    return xpathToButton;
  }

  public static Map<String, String> createLabelLibraryTextfield() {
    Map<String, String> xpathToTextfield = new HashMap<>();
    xpathToTextfield.put("cdp login email", "//XCUIElementTypeTextField[1]");
    xpathToTextfield.put("cdp login password", "//XCUIElementTypeSecureTextField[1]");
    xpathToTextfield.put("cdp login pin1", "//XCUIElementTypeSecureTextField[1]");
    xpathToTextfield.put("cdp login pin2", "//XCUIElementTypeSecureTextField[2]");
    xpathToTextfield.put("cdp login pin3", "//XCUIElementTypeSecureTextField[3]");
    xpathToTextfield.put("cdp login pin4", "//XCUIElementTypeSecureTextField[4]");
    xpathToTextfield.put("cdp old pin1", "//XCUIElementTypeSecureTextField[1]");
    xpathToTextfield.put("cdp old pin2", "//XCUIElementTypeSecureTextField[2]");
    xpathToTextfield.put("cdp old pin3", "//XCUIElementTypeSecureTextField[3]");
    xpathToTextfield.put("cdp old pin4", "//XCUIElementTypeSecureTextField[4]");
    xpathToTextfield.put("cdp new pin1", "//XCUIElementTypeSecureTextField[5]");
    xpathToTextfield.put("cdp new pin2", "//XCUIElementTypeSecureTextField[6]");
    xpathToTextfield.put("cdp new pin3", "//XCUIElementTypeSecureTextField[7]");
    xpathToTextfield.put("cdp new pin4", "//XCUIElementTypeSecureTextField[8]");
    xpathToTextfield.put("cdp confirm pin1", "//XCUIElementTypeSecureTextField[9]");
    xpathToTextfield.put("cdp confirm pin2", "//XCUIElementTypeSecureTextField[10]");
    xpathToTextfield.put("cdp confirm pin3", "//XCUIElementTypeSecureTextField[11]");
    xpathToTextfield.put("cdp confirm pin4", "//XCUIElementTypeSecureTextField[12]");
    xpathToTextfield.put("set new pin1", "//XCUIElementTypeSecureTextField[1]");
    xpathToTextfield.put("set new pin2", "//XCUIElementTypeSecureTextField[2]");
    xpathToTextfield.put("set new pin3", "//XCUIElementTypeSecureTextField[3]");
    xpathToTextfield.put("set new pin4", "//XCUIElementTypeSecureTextField[4]");
    xpathToTextfield.put("confirm set new pin1", "//XCUIElementTypeSecureTextField[5]");
    xpathToTextfield.put("confirm set new pin2", "//XCUIElementTypeSecureTextField[6]");
    xpathToTextfield.put("confirm set new pin3", "//XCUIElementTypeSecureTextField[7]");
    xpathToTextfield.put("confirm set new pin4", "//XCUIElementTypeSecureTextField[8]");
    xpathToTextfield.put("newcrm cdp user",
        "//XCUIElementTypeOther//XCUIElementTypeTextField[@value='Username']");
    xpathToTextfield.put("newcrm cdp password",
        "//XCUIElementTypeOther//XCUIElementTypeSecureTextField[@value='Password']");
    xpathToTextfield.put("login error message",
        "//XCUIElementTypeStaticText[contains(@value,'Daten leider nicht in unserem System finden') or contains(@value,'eingegebene Sky PIN')]");
    xpathToTextfield.put("login success", "//XCUIElementTypeOther[@name='Du bist nun eingeloggt']");
    xpathToTextfield.put("email error message",
        "//XCUIElementTypeStaticText[contains(@value,'mindestens 10 Ziffern oder eine gültige E-Mail-Adresse')]");
    xpathToTextfield.put("reset pin email",
        "//XCUIElementTypeStaticText[@name='E-mail']//following::XCUIElementTypeTextField");
    xpathToTextfield.put("password textfield", "//XCUIElementTypeSecureTextField[1]");

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
    xpathToTUrl.put("cdp login page url", "uat.id.skycdp.de");

    return xpathToTUrl;
  }

  public static Map<String, String> createLabelLibraryTitle() {
    Map<String, String> xpathToTitle = new HashMap<>();
    xpathToTitle.put("sky product overview title", "Live Sport");
    xpathToTitle.put("sky contract title", "Vertrag - Sky");
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
    xpathToTitle.put("cdp login page title", "Sky");

    return xpathToTitle;
  }

  public static Map<String, String> createLabelLibraryElement() {
    Map<String, String> xpathToElement = new HashMap<>();
    xpathToElement.put("sky logo", "(//XCUIElementTypeLink[@label='Sky'])[1]");
    xpathToElement.put("email input",
        "//XCUIElementTypeStaticText[contains(@name,'E-Mail')]//following::XCUIElementTypeTextField");
    xpathToElement.put("password input",
        "//XCUIElementTypeStaticText[@name='Passwort']//following::XCUIElementTypeSecureTextField[1]");
    xpathToElement.put("login button", "//XCUIElementTypeButton[@label='Anmelden']");
    xpathToElement.put("reset pin link",
        "//XCUIElementTypeLink[contains(@name,'Jetzt') and contains(@name,'cksetzen')]");
    xpathToElement.put("get customer number", "//XCUIElementTypeLink[@name='Jetzt anfordern']");
    xpathToElement.put("customer number lookup",
        "//XCUIElementTypeStaticText[contains(@name,'meine Kundennummer')]");
    xpathToElement.put("customer number lookup popup",
        "//XCUIElementTypeStaticText[contains(@value,'Wo finde ich meine Kundennummer')]");

    return xpathToElement;
  }
}
