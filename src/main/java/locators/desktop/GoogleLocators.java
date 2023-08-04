package locators.desktop;

import java.util.HashMap;
import java.util.Map;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class GoogleLocators {

  public static Map<String, String> createLibraryInput() {
    Map<String, String> xpathToInput = new HashMap<>();
    xpathToInput.put("correct search input", "//*[@name='q']");
    xpathToInput.put("incorrect search input", "//input[@id='uat']");
    return xpathToInput;
  }
}
