package locators.ios;

import java.util.HashMap;
import java.util.Map;

public class IosDatePickerLocators {

  public static Map<String, String> createLibraryButton() {
    Map<String, String> xpathToButton = new HashMap<>();
    xpathToButton.put("show year picker", "//XCUIElementTypeButton[@name=\"Show year picker\"]");
    xpathToButton.put("hide year picker", "//XCUIElementTypeButton[@name=\"Hide year picker\"]");
    xpathToButton.put("ok", "//XCUIElementTypeButton[@name=\"OK\"]");
    return xpathToButton;
  }

  public static Map<String, String> createLibraryElement() {
    Map<String, String> xpathToElement = new HashMap<>();
    xpathToElement.put("year picker wheel",
        "//XCUIElementTypeOther[@name=\"Preview\"]//XCUIElementTypePicker/XCUIElementTypePickerWheel[2]");
    xpathToElement.put("month picker wheel",
        "//XCUIElementTypeOther[@name=\"Preview\"]//XCUIElementTypePicker/XCUIElementTypePickerWheel[1]");
    xpathToElement.put("day picker",
        "//XCUIElementTypeOther[@name=\"Preview\"]//XCUIElementTypeCollectionView");
    return xpathToElement;
  }
}
