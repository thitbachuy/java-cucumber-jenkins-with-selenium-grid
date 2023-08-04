package core;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class JavascriptSupport {

  public static void applyStyle(WebDriver driver, WebElement element, String style) {
    if (driver instanceof JavascriptExecutor) {
      ((JavascriptExecutor) driver)
          .executeScript("arguments[0].setAttribute('style', arguments[1]);", element, style);
    }
  }

  public static void applyStyle(WebDriver driver, WebElement element, String attribute,
      String value) {
    if (driver instanceof JavascriptExecutor) {
      ((JavascriptExecutor) driver)
          .executeScript("arguments[0].setAttribute('" + attribute + "', arguments[1]);", element,
              value);
    }
  }

  public static void clearText(WebDriver driver, WebElement element) {
    if (driver instanceof JavascriptExecutor) {
      ((JavascriptExecutor) driver).executeScript("arguments[0].setAttribute('value', '');",
          element);
    }
  }

  public static void enterText(WebDriver driver, WebElement element, String text) {
    if (driver instanceof JavascriptExecutor) {
      ((JavascriptExecutor) driver)
          .executeScript("arguments[0].setAttribute('value', arguments[1]);", element, text);
    }
  }

  public static void scrollToBottomPage(WebDriver driver) {
    if (driver instanceof JavascriptExecutor) {
      ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight)");
    }
  }

  public static void scrollToTopPage(WebDriver driver) {
    if (driver instanceof JavascriptExecutor) {
      ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 0)");
    }
  }

  public static void moveToElement(WebDriver driver, WebElement element) {
    if (driver instanceof JavascriptExecutor) {
      ((JavascriptExecutor) driver).executeScript(
          "return arguments[0].scrollIntoView(true);", element);
    }
  }

  public static void removeElement(WebDriver driver, WebElement element) {
    if (driver instanceof JavascriptExecutor) {
      ((JavascriptExecutor) driver).executeScript(
          "return arguments[0].remove();", element);
    }
  }
}
