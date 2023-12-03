package locators.desktop;

import java.util.HashMap;
import java.util.Map;

public class SearchLocators {

  public static Map<String, String> createLibraryInput() {
    Map<String, String> xpathToInput = new HashMap<>();
    xpathToInput.put("correct search input", "//*[@name='q']");
    xpathToInput.put("incorrect search input", "//input[@id='uat']");
    return xpathToInput;
  }
}
