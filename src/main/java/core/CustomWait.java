package core;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;

public class CustomWait {

  /**
   * An expectation for checking WebElement with given locator has attribute contains specific
   * value
   *
   * @param driver
   * @param locator
   * @param attribute
   * @param value
   * @return <code>true</code> if element's attribute contains value
   */
  public static ExpectedCondition<Boolean> waitUntilElementAttributeValueContains(WebDriver driver,
      By locator, String attribute, String value) {
    return new ExpectedCondition<Boolean>() {
      private String message;
      private String returnVal;

      @Override
      public Boolean apply(WebDriver driver) {
        for (int i = 0; i <= 2; i++) {
          try {
            returnVal = driver.findElement(locator).getAttribute(attribute);
            if (returnVal.contains(value)) {
              return true;
            }
          } catch (StaleElementReferenceException staleExcept) {
            if (i == 2) {
              throw staleExcept;
            }
          }
        }
        return false;
      }

      @Override
      public String toString() {
        message = attribute + " contains value: " + value;
        message += "\nLocator: " + locator;
        message += "\nGot: " + returnVal;
        return message;
      }
    };
  }

  /**
   * An expectation for checking WebElement with given locator has attribute has specific value
   *
   * @param driver
   * @param locator
   * @param attribute
   * @param value
   * @return <code>true</code> if element's attribute contains value
   */
  public static ExpectedCondition<Boolean> waitUntilElementAttributeValue(WebDriver driver,
      By locator, String attribute, String value) {
    return new ExpectedCondition<Boolean>() {
      @Override
      public Boolean apply(WebDriver driver) {
        for (int i = 0; i <= 2; i++) {
          try {
            if (driver.findElement(locator).getAttribute(attribute).equals(value)) {
              return true;
            }
          } catch (StaleElementReferenceException staleExcept) {
            if (i == 2) {
              throw staleExcept;
            }
          }
        }
        return false;
      }

      @Override
      public String toString() {
        return attribute + " value is: " + value;
      }
    };
  }

  /**
   * An expectation for checking new window is opened
   * <p>
   * only apply for case 1 main and 1 new opened window
   *
   * @param driver
   * @return <code>true</code> if there are at least 2 windows
   */
  public static ExpectedCondition<Boolean> waitUntilNewWindowIsOpened(WebDriver driver) {
    return new ExpectedCondition<Boolean>() {
      @Override
      public Boolean apply(WebDriver driver) {
        if (driver.getWindowHandles().size() == 1) {
          return false;
        }
        try {
          Thread.sleep(500);
        } catch (InterruptedException e) {
          // Do nothing
        }
        return true;
      }

      @Override
      public String toString() {
        return "New windows is opened";
      }
    };
  }

  /**
   * An expectation for checking new window is closed
   *
   * @param driver
   * @return <code>true</code> if there are at least 2 windows
   */
  public static ExpectedCondition<Boolean> waitUntilNewWindowIsCLosed(WebDriver driver) {
    return new ExpectedCondition<Boolean>() {
      @Override
      public Boolean apply(WebDriver driver) {
        if (driver.getWindowHandles().size() == 1) {
          return true;
        }
        try {
          Thread.sleep(500);
        } catch (InterruptedException e) {
          // Do nothing
        }
        return false;
      }

      @Override
      public String toString() {
        return "New windows is closed";
      }
    };
  }

  /**
   * An expectation for checking number of elements present on web page
   *
   * @param driver
   * @return <code>true</code> if number of elements as expected
   */
  public static ExpectedCondition<Boolean> waitUntilNumberOfElement(WebDriver driver, By locator,
      int numOfElements) {
    return new ExpectedCondition<Boolean>() {
      @Override
      public Boolean apply(WebDriver driver) {
        if (driver.findElements(locator).size() != numOfElements) {
          return false;
        }
        try {
          Thread.sleep(500);
        } catch (InterruptedException e) {
          // Do nothing
        }
        return true;
      }

      @Override
      public String toString() {
        return "Number of elements is: " + numOfElements + " with locator: " + locator.toString();
      }
    };
  }
}
