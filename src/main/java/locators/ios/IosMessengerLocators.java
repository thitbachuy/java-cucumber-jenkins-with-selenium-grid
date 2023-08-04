package locators.ios;

import java.util.HashMap;
import java.util.Map;

public class IosMessengerLocators {

  public static Map<String, String> createLibraryButton() {
    Map<String, String> xpathToButton = new HashMap<>();
    xpathToButton.put("send button", "//XCUIElementTypeButton[@name=\"composer-send-button\"]");
    return xpathToButton;
  }

  public static Map<String, String> createLibraryTextField() {
    Map<String, String> xpathToTextField = new HashMap<>();
    xpathToTextField.put("message text field",
        "//XCUIElementTypeTextView[@name=\"composer-input-field\"]");
    return xpathToTextField;
  }

  public static Map<String, String> createLibraryElement() {
    Map<String, String> xpathToElement = new HashMap<>();
    xpathToElement.put("last received message",
        "(//XCUIElementTypeStaticText[contains(@name,\"You\")])[last()]/ancestor::*[@name=\"message-row-cell\"]/following-sibling::*[@name=\"message-row-cell\"]//XCUIElementTypeStaticText");
    return xpathToElement;
  }
}
