package locators.desktop.chromeextension;

import java.util.HashMap;
import java.util.Map;

public class ChromeAPIExtensionLocators {

  public static Map<String, String> createLibraryTextField() {
    Map<String, String> xpathToTextField = new HashMap<>();
    xpathToTextField.put("apiURL",
        "//*[@data-test-id='request-configuration-0-url-expression']/input[@class='mdc-text-field__input ']");
    xpathToTextField.put("responsePayload", "//*[@class='ace_content']");
    xpathToTextField.put("nothing here", "//*[text()='Ohhh... nothing here, but me!']");
    ;

    return xpathToTextField;
  }

  public static Map<String, String> createLibraryButton() {
    Map<String, String> xpathToButton = new HashMap<>();
    xpathToButton.put("add", "//button[@title='Create a new configuration']");
    xpathToButton.put("run", "//button[@title='Extension Paused']");

    return xpathToButton;
  }

  public static Map<String, String> createLibraryLink() {
    Map<String, String> xpathToLink = new HashMap<>();
    xpathToLink.put("log in", "//*[@href='/user/sign_in']");

    return xpathToLink;
  }

  public static Map<String, String> createLibraryDropdown() {
    Map<String, String> xpathToDropdown = new HashMap<>();
    xpathToDropdown.put("HTTP Method", "//*[@data-test-id='request-configuration-0-http-methods']");
    xpathToDropdown.put("Response Status", "//*[@data-test-id='request-configuration-0-status']");
    xpathToDropdown.put("503-Service Unavailable", "//li[contains(.,'503 - Service Unavailable')]");

    return xpathToDropdown;
  }

  public static Map<String, String> createLibraryMessages() {
    Map<String, String> xpathToMessage = new HashMap<>();
    xpathToMessage.put("log in", "//*[@href='/user/sign_in']");

    return xpathToMessage;
  }
}
