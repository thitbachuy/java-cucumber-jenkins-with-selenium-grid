package pages.ios;

import io.appium.java_client.ios.IOSDriver;
import io.cucumber.core.exception.CucumberException;
import locators.ios.IosDatePickerLocators;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;
import java.util.Map;

public class IosDateTimePicker extends IosBasicPage {

  public IosDateTimePicker(IOSDriver driver) {
    super(driver);
  }

  private static final Logger LOG = LogManager.getLogger(IosDateTimePicker.class);
  private final Map<String, String> xpathToButton = IosDatePickerLocators.createLibraryButton();
  private final Map<String, String> xpathToElement = IosDatePickerLocators.createLibraryElement();

  public void pickDateFromCalendar(String month, String day, String year) {
    //Wait for date time picker appears
    waitForElementByXpath(5, 500L, xpathToButton.get("show year picker"), false);
    //Select desired year
    selectYearInCalendar(year);
    //Select desired month
    selectMonthInCalendar(month);
    //Select day and save
    selectDay(day);
    tapElementByXpath(xpathToButton.get("ok"), false);
    waitForInvisibilityOfElement(5, xpathToElement.get("day picker"));
  }

  private void selectDay(String day) {
    openShowDayPicker();
    String dayXpath = String.format(
        "//XCUIElementTypeStaticText[@name=\"%s\"]/ancestor::XCUIElementTypeButton",
        Integer.parseInt(day));
    tapElementByXpath(dayXpath, false);
    String dayIsSelectedXpath = dayXpath + "/XCUIElementTypeOther/XCUIElementTypeOther";
    waitForPresenceOfElementByXpath(5, dayIsSelectedXpath);
  }

  /**
   * if month - selectedMonth < 0 =>swipe down (direction = down, order = previous) if month -
   * selectedMonth > 0 =>swipe down (direction = up, order = down) if monthDifference == 0 =>End if
   * 10 <= monthDifference < 20 =>swipe with offset 10 (velocity = 400) if 3 <= monthDifference < 10
   * =>selectPickerWheelValue with offset 3 (offset = 0.3)) if monthDifference < 3
   * =>selectPickerWheelValue with offset yearDifference (offset = yearDifference * 0.1)
   *
   * @param month = expected month
   */
  private void selectMonthInCalendar(String month) {
    int expectedMonth = Integer.parseInt(month);
    openShowYearPicker();
    WebElement wheelElement = waitForElementByXpath(5, 500L,
        xpathToElement.get("month picker wheel"), false);
    //Convert month in text to number
    DateTimeFormatter parser = DateTimeFormatter.ofPattern("MMMM").withLocale(Locale.ENGLISH);
    TemporalAccessor accessor = parser.parse(wheelElement.getAttribute("value"));
    int selectedMonth = accessor.get(ChronoField.MONTH_OF_YEAR);
    int monthDifference = expectedMonth - selectedMonth;
    String direction, order;
    int velocity, retry = 0;
    double offset;
    if (monthDifference == 0) {
      return;
    }
    if (monthDifference < 0) {
      direction = "down";
      order = "previous";
    } else {
      direction = "up";
      order = "next";
    }
    while (monthDifference != 0 && retry < 30) {
      if (Math.abs(monthDifference) >= 10 && Math.abs(monthDifference) < 20) {
        velocity = 400;
        swipeOnElementToDirection(wheelElement, direction, velocity);
      } else if (Math.abs(monthDifference) >= 3 && Math.abs(monthDifference) < 10) {
        offset = 0.3;
        selectPickerWheelOfElement(wheelElement, order, offset);
      } else if (Math.abs(monthDifference) < 3) {
        offset = 0.1 * Math.abs(monthDifference);
        selectPickerWheelOfElement(wheelElement, order, offset);
      }
      accessor = parser.parse(
          waitForElementByXpath(5, 500L, xpathToElement.get("month picker wheel"),
              false).getAttribute("value"));
      selectedMonth = accessor.get(ChronoField.MONTH_OF_YEAR);
      monthDifference = expectedMonth - selectedMonth;
      retry++;
    }
    if (retry == 30) {
      throw new CucumberException("Can not select expected month: " + month);
    }
    LOG.info("Month is selected as expectation: {}", month);
  }

  /**
   * if year - selectedYear < 0 =>swipe down (direction = down, order = previous) if year -
   * selectedYear > 0 =>swipe down (direction = up, order = down) if yearDifference == 0 =>End if
   * yearDifference > 20 =>swipe with offset 20 (velocity = 1200) if 10 <= yearDifference < 20
   * =>swipe with offset 10 (velocity = 400) if 3 <= yearDifference < 10 =>selectPickerWheelValue
   * with offset 3 (offset = 0.3)) if yearDifference < 3 =>selectPickerWheelValue with offset
   * yearDifference (offset = yearDifference * 0.1)
   *
   * @param year = expected year
   */
  private void selectYearInCalendar(String year) {
    int expectedYear = Integer.parseInt(year);
    openShowYearPicker();
    WebElement wheelElement = waitForElementByXpath(5, 500L,
        xpathToElement.get("year picker wheel"), false);
    int selectedYear = Integer.parseInt(wheelElement.getAttribute("value"));
    int yearDifference = expectedYear - selectedYear;
    String direction, order;
    int velocity, retry = 0;
    double offset;
    if (yearDifference == 0) {
      return;
    }
    if (yearDifference < 0) {
      direction = "down";
      order = "previous";
    } else {
      direction = "up";
      order = "next";
    }
    while (yearDifference != 0 && retry < 30) {
      if (Math.abs(yearDifference) >= 20) {
        velocity = 1100;
        swipeOnElementToDirection(wheelElement, direction, velocity);
      } else if (Math.abs(yearDifference) >= 10 && Math.abs(yearDifference) < 20) {
        velocity = 400;
        swipeOnElementToDirection(wheelElement, direction, velocity);
      } else if (Math.abs(yearDifference) >= 3 && Math.abs(yearDifference) < 10) {
        offset = 0.3;
        selectPickerWheelOfElement(wheelElement, order, offset);
      } else if (Math.abs(yearDifference) < 3) {
        offset = 0.1 * Math.abs(yearDifference);
        selectPickerWheelOfElement(wheelElement, order, offset);
      }
      selectedYear = Integer.parseInt(
          waitForElementByXpath(5, 500L, xpathToElement.get("year picker wheel"),
              false).getAttribute("value"));
      yearDifference = expectedYear - selectedYear;
      retry++;
    }
    if (retry == 30) {
      throw new CucumberException("Can not select expected year: " + year);
    }
    LOG.info("Year is selected as expectation: {}", year);
  }

  private void openShowDayPicker() {
    try {
      waitForElementByXpath(1, 500L, xpathToElement.get("day picker"), false);
      LOG.info("Show day picker already opened!");
    } catch (TimeoutException e) {
      LOG.info("Show day picker not opened. Opening it...");
      tapElementByXpath(xpathToButton.get("hide year picker"), false);
    }
  }

  private void openShowYearPicker() {
    try {
      waitForElementByXpath(1, 500L, xpathToButton.get("hide year picker"), false);
      LOG.info("Show year picker already opened!");
    } catch (TimeoutException e) {
      LOG.info("Show year picker not opened. Opening it...");
      tapElementByXpath(xpathToButton.get("show year picker"), false);
    }
  }
}
