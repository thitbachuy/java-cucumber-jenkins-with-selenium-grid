package config;

/*
    All basic functions are stored in here like enter text, click element, select from Dropdown etc.
    If you have additional basic functions please enter in here as all Page classes are extended from BasePage
*/

import static steps.Hook.threadLocalCookieAccepted;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.cucumber.core.exception.CucumberException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import modal.Directions;
import modal.LocatorType;
import modal.TableInformation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import pages.ios.IosBasicPage;
import steps.Hook;

public class BasePage {

  private static final Logger LOG = LogManager.getLogger(BasePage.class);
  private static final int WAIT_TIMEOUT = 60;
  public static ThreadLocal<RemoteWebDriver> threadLocalDriverBasePage = new ThreadLocal<>();
  private IosBasicPage iosBasicPage;
  private final TableInformation tableInformation = new TableInformation();
  private static final Random r = new Random();

  public BasePage(RemoteWebDriver driver) {
    threadLocalDriverBasePage.set(
      DriverUtil.threadLocalActiveBrowsers.get().getOrDefault("current", driver));
  }

  public void enterText(String text, String locatorTextField, String textField) {
    text = TestDataLoader.getTestData(text);
    WebElement textFieldElem = verifyVisibilityOfElement(textField, locatorTextField);

    if (!textFieldElem.isDisplayed()) {
      throw new CucumberException("TextField " + textField + " not found!");
    }
    if ((textFieldElem.getText() != null && !textFieldElem.getText().isEmpty()) || (
      textFieldElem.getAttribute("value") != null && !textFieldElem.getAttribute("value").isEmpty())
      || textField.equals("tweet")) {
      clearTextOnTextField(textFieldElem);
    }
    if (!Hook.platform.equals("ios-webApp")) {
      clearWebField(textFieldElem);  //clears all text in textbox
    }
    textFieldElem.sendKeys(text);
    if (textField.contains("assword") || textField.contains("PW") || textField.contains("pw")
      || textField.contains("pin") || textField.contains("PIN") || textField.contains("Pin")
      || textField.contains("mp")) {
      LOG.info("\"********\" entered into TextField \"{}\" ", textField);
    } else {
      LOG.info("\"{}\" entered into TextField \"{}\" ", text, textField);
    }
    if (Hook.platform.equals("android-webApp")) {
      ((AndroidDriver) threadLocalDriverBasePage.get()).hideKeyboard();
    }
    if (Hook.platform.equals("ios-webApp")) {
      iosBasicPage =
        iosBasicPage == null ? new IosBasicPage(((IOSDriver) threadLocalDriverBasePage.get()))
          : iosBasicPage;
      iosBasicPage.closeKeyboard();
    }
  }

  private void clearTextOnTextField(WebElement textFieldElem) {
    textFieldElem.sendKeys(Keys.CONTROL + "a");
    textFieldElem.sendKeys(Keys.DELETE);
    int attempts = 0;
    do {
      if ((textFieldElem.getText() == null || textFieldElem.getText().isEmpty()) && (
        textFieldElem.getAttribute("value") == null || textFieldElem.getAttribute("value")
          .isEmpty())) {
        break;
      }
      waitFor(250).milliseconds();
      LOG.info("Trying to clear text field again");
      textFieldElem.clear();
      attempts++;
    } while (attempts < 10);
    if (attempts == 10) {
      LOG.info("Cannot clear text field after 10 times trying");
    }
  }

  public void clickWebElement(String locatorWebElement, String webElement) {
    if (locatorWebElement == null || locatorWebElement.isEmpty()) {
      throw new CucumberException("no Locator given for " + webElement);
    }
    verifyVisibilityOfElement(webElement, locatorWebElement).click();
    LOG.info("clicked Button {}", webElement);
  }

  public void clickWebElementAge(String locatorWebElement, String webElement) {
    waitForPageLoaded();
    assertAndVerifyElement(By.xpath(locatorWebElement));
    if (locatorWebElement == null || locatorWebElement.isEmpty()) {
      throw new CucumberException("no Locator given for " + webElement);
    }
    WebElement e = verifyVisibilityOfElement(webElement, locatorWebElement);
    e.click();
    LOG.info("clicked Button {}", webElement);
  }

  public void clickWebElementJS(String locatorWebElement, String webElement) {
    if (locatorWebElement == null || locatorWebElement.isEmpty()) {
      throw new CucumberException("no Locator given for " + webElement);
    }
    WebElement welToClickOn = waitForPresenceOfElementLocated(By.xpath(locatorWebElement));
    (threadLocalDriverBasePage.get()).executeScript("arguments[0].click();", welToClickOn);
    try {
      waitForPageLoaded();
    } catch (WebDriverException e) {
      waitForPageLoaded();
    }

    LOG.info("clicked Button {}", webElement);
  }

  public WebElement waitForElementToBeClickable(By by, int timeout) {
    WebDriverWait wait = new WebDriverWait(threadLocalDriverBasePage.get(),
      Duration.ofSeconds(timeout));
    return wait.until(ExpectedConditions.elementToBeClickable(by));
  }

  public WebElement waitForVisibilityOfElementLocated(By by, WebDriverWait wait) {
    return wait.until(ExpectedConditions.visibilityOfElementLocated(by));
  }

  public WebElement waitForElementToBeClickable(By by) {
    WebDriverWait wait = new WebDriverWait(threadLocalDriverBasePage.get(),
      Duration.ofSeconds(WAIT_TIMEOUT));
    return wait.until(ExpectedConditions.elementToBeClickable(by));
  }

  public WebElement waitForVisibilityOfElementLocated(By by) {
    WebDriverWait wait = new WebDriverWait(threadLocalDriverBasePage.get(),
      Duration.ofSeconds(WAIT_TIMEOUT));
    return wait.until(ExpectedConditions.visibilityOfElementLocated(by));
  }

  public Boolean waitForInvisibilityOfElementLocated(By by) {
    WebDriverWait wait = new WebDriverWait(threadLocalDriverBasePage.get(),
      Duration.ofSeconds(WAIT_TIMEOUT));
    return wait.until(ExpectedConditions.invisibilityOfElementLocated(by));
  }

  public Boolean waitForInvisibilityOfElementLocated(By by, int timeout) {
    WebDriverWait wait = new WebDriverWait(threadLocalDriverBasePage.get(),
      Duration.ofSeconds(timeout));
    return wait.until(ExpectedConditions.invisibilityOfElementLocated(by));
  }

  public WebElement waitForPresenceOfElementLocated(By by) {
    WebDriverWait wait = new WebDriverWait(threadLocalDriverBasePage.get(),
      Duration.ofSeconds(WAIT_TIMEOUT));
    return wait.until(ExpectedConditions.presenceOfElementLocated(by));
  }

  public List<WebElement> waitForPresenceOfElementsLocated(By by) {
    WebDriverWait wait = new WebDriverWait(threadLocalDriverBasePage.get(), Duration.ofSeconds(60));
    return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(by));
  }

  public Boolean waitUntilElementHasAttributeContains(WebElement element, String attribute,
    String value, int waitTimeout) {
    WebDriverWait wait = new WebDriverWait(threadLocalDriverBasePage.get(),
      Duration.ofSeconds(waitTimeout));
    return wait.until(ExpectedConditions.attributeContains(element, attribute, value));
  }

  public Boolean waitUntilElementAttributeNotContains(WebElement element, String attribute,
    String value, int waitTimeout) {
    WebDriverWait wait = new WebDriverWait(threadLocalDriverBasePage.get(),
      Duration.ofSeconds(waitTimeout));
    return wait.until(
      ExpectedConditions.not(ExpectedConditions.attributeContains(element, attribute, value)));
  }

  public Boolean waitUntilElementHaveAttributeValueEquals(By locator, String attribute,
    String value, int waitTimeout) {
    WebDriverWait wait = new WebDriverWait(threadLocalDriverBasePage.get(),
      Duration.ofSeconds(waitTimeout));
    return wait.until(ExpectedConditions.attributeToBe(locator, attribute, value));
  }

  public Boolean waitUntilElementContainsTexts(By by, String value, int waitTimeout) {
    WebDriverWait wait = new WebDriverWait(threadLocalDriverBasePage.get(),
      Duration.ofSeconds(waitTimeout));
    return wait.until(ExpectedConditions.textToBePresentInElement(
      waitForVisibilityOfElementLocated(by, waitTimeout), value));
  }

  public void waitForPageLoaded() {
    WebDriverWait wait = new WebDriverWait(threadLocalDriverBasePage.get(), Duration.ofSeconds(50));
    wait.until(wd -> ((threadLocalDriverBasePage.get()).executeScript("return document.readyState")
      .equals("complete")));
    int count = 0;
    if ((boolean) (threadLocalDriverBasePage.get()).executeScript(
      "return window.jQuery != undefined")) {
      while (!(boolean) (threadLocalDriverBasePage.get()).executeScript(
        "return jQuery.active == 0")) {
        waitFor(1).seconds();
        if (count > 400) {
          break;
        }
        count++;
      }
    }
//    wait.until(ExpectedConditions.invisibilityOfElementLocated(
//      By.xpath("//*[@class='loadingIndicatorIcon']")));
  }

  public WebElement findElement(String locatorWebElement, String webElement) {
    if (locatorWebElement == null || locatorWebElement.isEmpty()) {
      throw new CucumberException("no Locator given for " + webElement);
    }
    return verifyVisibilityOfElement(webElement, locatorWebElement);
  }

  public void selectFromDropdownByVisibleTextEQUAL(String locatorWebElement, String valueToSelect) {
    if (locatorWebElement == null || locatorWebElement.isEmpty()) {
      throw new CucumberException("no Locator given for Dropdown");
    }
    Select select = new Select(waitForPresenceOfElementLocated(By.xpath(locatorWebElement)));
    select.selectByVisibleText(valueToSelect);

  }

  public void selectFromDropdownByIndex(String locatorWebElement, String index) {
    Select select = new Select(waitForPresenceOfElementLocated(By.xpath(locatorWebElement)));
    select.selectByIndex(Integer.parseInt(index));

  }

  public void selectFromDropdownByValueEQUAL(String locatorWebElement, String valueToSelect) {
    if (locatorWebElement == null || locatorWebElement.isEmpty()) {
      throw new CucumberException("no Locator given for Dropdown");
    }
    Select select = new Select(waitForVisibilityOfElementLocated(By.xpath(locatorWebElement)));
    select.selectByValue(valueToSelect);

  }

  public void selectFromListByVisibleTextEQUAL(String dropdownLocator, String valueToSelect) {
    if (dropdownLocator == null || dropdownLocator.isEmpty()) {
      throw new CucumberException("no Locator given for Dropdown");
    }
    clickOrEvaluateAndClick(dropdownLocator);
    assertAndVerifyElement(By.xpath(dropdownLocator));
    waitForSpinnerSF(1);
    waitForSpinnerCDP(1);
    WebElement element = null;
    List<WebElement> multipleElements = threadLocalDriverBasePage.get().findElements(By.xpath(
      "//*[@role='presentation' or @role='menuitem' or @role='option']//*[text()='" + valueToSelect
        + "' and not(ancestor::a[@*='tab-name'])][1] | //option[text()='" + valueToSelect + "']"));
    for (WebElement e : multipleElements) {
      boolean displayed = e.isDisplayed();
      boolean enabled = e.isEnabled();
      LOG.info("is displayed: {}", displayed);
      LOG.info("is enabled: {}", enabled);
      if (enabled && displayed) {
        element = e;
        break;
      }
    }
    if (element != null) {
      scrollTo(element);
      element.click();
    } else {
      throw new CucumberException("Cannot find option. Please recheck!");
    }
  }

  public void waitForNewTabSky(int expectedTabs, int timeout) {
    WebDriverWait wait = new WebDriverWait(threadLocalDriverBasePage.get(),
      Duration.ofSeconds(timeout));
    wait.until(ExpectedConditions.urlContains("sky"));
    int counter = 0;
    while (true) {
      try {
        Set<String> winId = threadLocalDriverBasePage.get().getWindowHandles();
        if (winId.size() >= expectedTabs) {
          return;
        }
        counter++;
        if (counter > timeout) {
          return;
        }
      } catch (Exception e) {
        LOG.error(e.getMessage());
        return;
      }
    }
  }

  public void clickButtonByText(String buttonName) {
    String buttonXpath = String.format("//*[text()='%s']", buttonName);
    clickOrEvaluateAndClick(buttonXpath);
    LOG.info("Click on a button with text \"{}\"", buttonName);
  }

  public void clickWebElementJsBy(String locatorWebElement, LocatorType locatorType) {
    if (locatorWebElement == null || locatorWebElement.isEmpty()) {
      throw new CucumberException("no Locator given for your element");
    }

    threadLocalDriverBasePage.get().executeScript("arguments[0].click();",
      waitForVisibilityOfElementLocated(By.xpath(locatorWebElement)));
    waitForPageLoaded();
  }

  public void cookiePopup() {
    String spMessageIframeLocator = "//iframe[contains(@id,'sp_message_iframe')]";
    if (Hook.platform.equals("ios-webApp")) {
      try {
        waitForVisibilityOfElementLocated(By.xpath(spMessageIframeLocator), 15);
        String acceptCookieXpath = "//*[contains(@title, 'Alle akzeptieren') or contains(@name, 'Alle akzeptieren')]";
        iosBasicPage =
          iosBasicPage == null ? new IosBasicPage((IOSDriver) threadLocalDriverBasePage.get())
            : iosBasicPage;
        iosBasicPage.switchContext("NATIVE");
        iosBasicPage.scrollToElement(acceptCookieXpath, acceptCookieXpath, "up", 500);
        iosBasicPage.tapElementByXpath(acceptCookieXpath, false);
        iosBasicPage.waitForInvisibilityOfElement(10, acceptCookieXpath);
        iosBasicPage.switchContext("webview");
      } catch (Exception ex) {
        LOG.info("Cookie popup not appear!");
      }
    } else {
      try {
        if (threadLocalCookieAccepted.get().equals(true)) {
          LOG.info("Sky CDP cookies is already accepted");
        } else {
          LOG.info("Checking for Sky CDP cookie popup");
          waitForSpinnerCDP(2);
          waitForVisibilityOfElementLocated(By.xpath(spMessageIframeLocator), 4);
          threadLocalDriverBasePage.get().switchTo()
            .frame(threadLocalDriverBasePage.get().findElement(By.xpath(spMessageIframeLocator)));
          waitForElementToBeClickable(By.xpath(
            "//button[contains(.,'OK') or (contains(.,'Alle') and not(contains(., 'Alle Preisdetails'))) or contains (.,'akzeptieren') or contains (.,'Akzeptieren')]"));
          clickOrEvaluateAndClick(
            "//button[contains(.,'OK') or (contains(.,'Alle') and not(contains(., 'Alle Preisdetails'))) or contains (.,'akzeptieren') or contains (.,'Akzeptieren')]");
          threadLocalCookieAccepted.set(true);
          LOG.info("cookies accepted");
        }
      } catch (Exception ignored) {
      }
      threadLocalDriverBasePage.get().switchTo().defaultContent();
    }
  }

  public void privacyPopup() {
    LOG.info("checking for privacy popup");
    String privacyPopUpLocator = "//button[contains(.,'Speichern') or (contains(.,'Alle') and not(contains(., 'Alle Preisdetails')))]";
    try {
      waitForVisibilityOfElementLocated(By.xpath(privacyPopUpLocator), 5);
      clickOrEvaluateAndClick(privacyPopUpLocator);
    } catch (Exception ignored) {
    }
  }

  public void cookiePopupFB() {
    LOG.info("checking for Facebook cookie popup");
    String facebookCookiesLocator = "//*[@aria-label='Accept All' or @title='Accept All' or @title='Alle akzeptieren' or @data-cookiebanner='accept_button' or @data-testid='cookie-policy-dialog-accept-button']";
    try {
      waitForVisibilityOfElementLocated(By.xpath(facebookCookiesLocator), 15).click();
      LOG.info("There is Facebook Cookie popup which has been closed");
    } catch (Exception ignored) {
      LOG.info("No Facebook Cookies popup detected. Do nothing and just continue");
    }
  }

  public void executeJs(String js) {
    threadLocalDriverBasePage.get().executeScript(js);
  }

  public void executeJs(String js, WebElement e) {
    threadLocalDriverBasePage.get().executeScript(js, e);
  }

  public void scrollTo(WebElement webElement) {
    try {
      executeJs("arguments[0].scrollIntoView(false);", webElement);
    } catch (Exception e) {
      waitFor(5).seconds();
      executeJs("arguments[0].scrollIntoView(false);", webElement);
    }
  }

  public void scrollToViaAction(WebElement webElement) {
    new Actions(threadLocalDriverBasePage.get()).moveToElement(webElement).perform();
  }

  public ScrollBuilder scrollInsideElement(String elementXpath) {
    return new ScrollBuilder(elementXpath);
  }

  public void closeTab(int tabIndex) {
    if (Hook.browser.equals("safari") && Hook.platform.equals("ios-webApp")) {
      iosBasicPage =
        iosBasicPage == null ? new IosBasicPage((IOSDriver) threadLocalDriverBasePage.get())
          : iosBasicPage;
      iosBasicPage.switchContext("native");
      iosBasicPage.closeTabAtIndex(tabIndex);
      iosBasicPage.switchContext("webview");
    } else {
      ArrayList<String> activeTabs = new ArrayList<>(
        threadLocalDriverBasePage.get().getWindowHandles());
      threadLocalDriverBasePage.get().switchTo().window(activeTabs.get(tabIndex)).close();
    }
  }

  public class ScrollBuilder {

    private final String elementXpath;
    private Directions direction;
    private String script;
    private int loopCount = 1;

    public ScrollBuilder(String elementXpath) {
      this.elementXpath = elementXpath;
    }

    public ScrollBuilder to(Directions direction) {
      this.direction = direction;
      switch (direction) {
        case TOP:
          this.script = "arguments[0].scrollTo(0, 0);";
          break;
        case BOTTOM:
          this.script = "arguments[0].scrollTo(0, arguments[0].scrollHeight);";
          break;
        default:
          LOG.info("Directions supported available are: {}, {}", Directions.TOP, Directions.BOTTOM);
          LOG.info("Other directions will be developed in need. Thanks for using!");
          throw new CucumberException(
            String.format("Direction is not supported in this version: [%s]", direction));
      }
      return this;
    }

    public ScrollBuilder withLoop(int loopCount) {
      this.loopCount = loopCount;
      return this;
    }

    public void perform() {
      Assert.assertTrue(
        this.loopCount > 0,
        String.format("Wrong number of loop: [%s]. Please pass the integer value greater than 0!",
          this.loopCount));
      for (int loop = 0; loop < this.loopCount; loop++) {
        executeJs(script, waitForVisibilityOfElementLocated(By.xpath(this.elementXpath)));
        LOG.info("User scrolls to {} the {} time(s)", this.direction, loop + 1);
        waitFor(1).seconds();
      }
    }
  }

  public void scrollToTop() {
    executeJs("window.scrollTo(0, 0);");
    waitFor(500).milliseconds();
  }

  public void scrollElementToCenter(WebElement e) {
    String scrollElementIntoMiddle =
      "var viewPortHeight = Math.max(document.documentElement.clientHeight, window.innerHeight || 0);"
        + "var elementTop = arguments[0].getBoundingClientRect().top;"
        + "window.scrollBy(0, elementTop-(viewPortHeight/2));";

    threadLocalDriverBasePage.get().executeScript(scrollElementIntoMiddle, e);
    waitFor(500).milliseconds();
  }

  public void waitForSpinnerPaypal() {
    try {
      LOG.info("checking for Loading Spinner");
      WebDriverWait wait = new WebDriverWait(threadLocalDriverBasePage.get(),
        Duration.ofSeconds(10), Duration.ofMillis(1000));
      wait.until(ExpectedConditions.invisibilityOfElementLocated(
        By.xpath("//*[contains(@class,'spinnerWithLockIcon')]")));
    } catch (Exception ignored) {
    }
  }

  public void waitForSpinnerCDP(int waitTimeForSpinner) {
    String spinnerXpath = "//*[contains(@data-testid,'spinner')]|//*[contains(@class,'is-loading')]";
    try {
      waitForVisibilityOfElementLocated(By.xpath(spinnerXpath), waitTimeForSpinner);
      LOG.info("Loading Spinner is visible. Wait until Spinner disappears...");
      waitForInvisibilityOfElementLocated(By.xpath(spinnerXpath), 300);
      LOG.info("Loading Spinner is invisible");
    } catch (Exception ignored) {
      LOG.info("Loading Spinner has not appeared");
    }
  }

  public void waitForSpinnerSF(int waitTimeForSpinner) {
    String spinnerXpath = "(//div[contains(@class,'loadingSpinner slds-spinner_container') or contains(@class,'slds-spinner_large') or contains(@class,' slds-spinner_medium') or contains(@class,'  forceInlineSpinner')])[last()]";
    try {
      waitForVisibilityOfElementLocated(By.xpath(spinnerXpath), waitTimeForSpinner);
      LOG.info("Loading Spinner is visible. Wait until Spinner disappears...");
      waitForInvisibilityOfElementLocated(By.xpath(spinnerXpath), 300);
      LOG.info("Loading Spinner is invisible");
    } catch (Exception ignored) {
      LOG.info("Loading Spinner has not appeared");
    }
  }

  public void switchFrame(String idOrName) {
    threadLocalDriverBasePage.get().switchTo().frame(idOrName);
  }

  public void currentPageHasText(String text) {
    if (text.toUpperCase().startsWith("@TD:")) {
      text = TestDataLoader.getTestData(text);
    }
    String selector = String.format("(//*[text()='%s' or contains(text(), '%s')])[1]", text, text);
    try {
      waitForVisibilityOfElementLocated(By.xpath(selector), 120);
    } catch (Exception e) {
      scrollToBottom(100);
      waitForVisibilityOfElementLocated(By.xpath(selector));
    }
    LOG.info("Current page contains an element with text \"{}\".", text);
  }

  public void currentPageHasNoText(String text) {
    String selector = getString(text);
    try {
      scrollToBottom(200);
      waitForVisibilityOfElementLocated(By.xpath(selector), 5);
      scrollToTop();
      waitForVisibilityOfElementLocated(By.xpath(selector), 5);
      throw new CucumberException(
        String.format("Current page contains an element with text \"%s\". Not present expected.",
          selector));
    } catch (NoSuchElementException | TimeoutException e) {
      LOG.info("Current page does not contain an element with text \"{}\" as expected.", selector);
    }
  }

  public void verifyPageHasContent(String text) {
    if (text.startsWith("@TD:")) {
      text = TestDataLoader.getTestData(text);
    }
    scrollToBottom(200);
    String pageContent = getTextOfElement(By.xpath("//body"));
    LOG.info("Page content is {}", pageContent);
    Assert.assertTrue(pageContent.contains(text),
      String.format("Current page does not contains text %s as expected", text));
    LOG.info("Current page contains text \"{}\".", text);
  }

  protected String getString(String text) {
    if (text.startsWith("@TD:")) {
      text = TestDataLoader.getTestData(text);
    }
    return String.format("//*[text()='%s' or contains(text(), '%s')]|//p[contains(.,'%s')]", text,
      text, text);
  }

  public void elementHasAttribute(String selector, String attribute) {
    WebElement element = waitForVisibilityOfElementLocated(By.xpath(selector));
    Assert.assertNotNull(
      String.format("Element with locator \"%s\" does not have attribute \"%s\". Please recheck!",
        selector, attribute), element.getAttribute(attribute));
  }

  public boolean elementsIsDisplayed(String selector) {
    try {
      List<WebElement> listElements = waitForVisibilityOfAllElementsLocated(By.xpath(selector), 15);
      if (!listElements.isEmpty()) {
        LOG.info("Elements \"{}\" are displayed", selector);
      }
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public void elementHasNotAttribute(String selector, String attribute) {
    WebElement element = waitForVisibilityOfElementLocated(By.xpath(selector));
    Assert.assertNull(
      String.format("Element \"%s\" still has \"%s\" attribute.", selector, attribute),
      element.getAttribute(attribute));
  }

  public void buttonIsEnabled(String buttonText) {
    String locator = String.format("//button[@aria-label='%s']", buttonText);
    elementHasNotAttribute(locator, "disabled");
  }

  public void buttonIsDisabled(String buttonText) {
    String attribute = "disabled";
    String selector = String.format("//button[@aria-label='%s']", buttonText);
    elementHasAttribute(selector, attribute);
    LOG.info("Button with text \"{}\" is disabled as expected.", buttonText);
  }

  public void evaluateXpathAndClick(String xpath) {
    WebElement scrollElement = verifyVisibilityOfElement(xpath, xpath, 30);
    scrollElementToCenter(scrollElement);
    executeJs("arguments[0].click();", scrollElement);
  }

  public void clickOrEvaluateAndClick(String selector) {
    LOG.info("Element locator is {}", selector);
    try {
      clickWebElement(selector, selector);
    } catch (Exception | AssertionError e) {
      // To avoid changing of selector this solution may help...
      LOG.info("Found exception: {}", e.getMessage());
      evaluateXpathAndClick(selector);
      LOG.info("Clicked on evaluated xpath: \"{}\".", selector);
    }
  }

  public void selectOptionByVisibleText(String option, String dropdownLocator) {
    Select select = new Select(waitForVisibilityOfElementLocated(By.xpath(dropdownLocator)));
    select.selectByVisibleText(option);
  }

  public void verifyCurrentUrl(String expectedUrl) {
    waitForPageLoaded();
    expectedUrl = TestDataLoader.getTestData(expectedUrl);
    String actual = threadLocalDriverBasePage.get().getCurrentUrl();
    Assert.assertEquals(
      String.format("Current Url: [%s] is not expected [%s]", actual, expectedUrl), expectedUrl,
      actual);
  }

  public void checkIsVisibleByXpath(String selector) {
    Assert.assertTrue(
      waitForVisibilityOfElementLocated(By.xpath(selector)).isDisplayed(),
      String.format("The element with selector \"%s\" is not visible to the user.", selector)
    );
  }

  public void clickBlankSpace() {
    clickOrEvaluateAndClick("//body");
    LOG.info("Clicking on a blank space on the page (body element).");
  }

  public void assertAndVerifyElement(By element) {
    boolean isPresent = false;
    for (int i = 0; i < 1000; i++) {
      try {
        if (threadLocalDriverBasePage.get().findElement(element) != null) {
          isPresent = true;
          break;
        }
      } catch (Exception ignored) {
      }
      waitFor(250).milliseconds();
    }
    Assert.assertTrue(isPresent);
  }

  public void setCssValueToHtmlElement(String HTMLTag, String CSSProp, String newValue) {
    String jsStringToEval = String.format("document.querySelector('%s').style.%s = '%s';", HTMLTag,
      CSSProp, newValue);
    executeJs(jsStringToEval);
  }

  public void actionOneClick(WebElement webElement) {
    Actions builder = new Actions(threadLocalDriverBasePage.get());
    builder.moveToElement(webElement).click(webElement).build().perform();
  }

  public String generateRandomWord(int wordLength) {
    StringBuilder sb = new StringBuilder(wordLength);
    for (int i = 0; i < wordLength; i++) {
      char tmp = (char) ('a' + r.nextInt('z' - 'a'));
      sb.append(tmp);
    }
    return sb.toString().replace("go", "og").replace("ci", "ic").replace("sky", "yks");
  }

  public String getTextOfElement(By element) {
    return waitForVisibilityOfElementLocated(element).getText();
  }

  public WaitBuilder waitFor(int duration) {
    return new WaitBuilder(duration);
  }

  public class WaitBuilder {

    private final int duration;

    public WaitBuilder(int duration) {
      this.duration = duration;
    }

    public void seconds() {
      try {
        TimeUnit.SECONDS.sleep(duration);
        LOG.info("Wait for {} seconds", duration);
      } catch (InterruptedException e) {
        e.printStackTrace();
        Thread.currentThread().interrupt();
      }
    }

    public void minutes() {
      try {
        TimeUnit.MINUTES.sleep(duration);
        LOG.info("Wait for {} minutes", duration);
      } catch (InterruptedException e) {
        e.printStackTrace();
        Thread.currentThread().interrupt();
      }
    }

    public void milliseconds() {
      try {
        TimeUnit.MILLISECONDS.sleep(duration);
        LOG.info("Wait for {} milliseconds", duration);
      } catch (InterruptedException e) {
        e.printStackTrace();
        Thread.currentThread().interrupt();
      }
    }

    public void days() {
      try {
        TimeUnit.DAYS.sleep(duration);
        LOG.info("Wait for {} day(s)", duration);
      } catch (InterruptedException e) {
        e.printStackTrace();
        Thread.currentThread().interrupt();
      }
    }
  }

  public void scrollToBottom() {
    executeJs(
      "window.scrollTo(0,document.body.scrollHeight || document.documentElement.scrollHeight)");
    waitFor(500).milliseconds();
  }

  //Covert price display in UI to same format with response from api
  public String getPricesInCdp(String stringWithPrice) {
    Pattern pattern;
    if (stringWithPrice.contains("mtl")) {
      pattern = Pattern.compile("€ \\d* mtl.");
    } else {
      pattern = Pattern.compile("€ \\d*");
    }
    Matcher matcher = pattern.matcher(stringWithPrice);
    String price = "";
    while (matcher.find()) {
      price = matcher.group(0);
    }
    if (price.equals("")) {
      price = stringWithPrice;
    }
    return price;
  }

  public List<String> getStringListOfElementVia(By locator, String valueType,
    String attributeOrCssValue) {
    waitForSpinnerSF(1);
    waitForPageLoaded();
    try {
      waitForVisibilityOfElementLocated(locator, 10);
    } catch (Exception e) {
      LOG.info("No element located by {}. The list will be empty.", locator);
    }
    List<WebElement> webElementList = threadLocalDriverBasePage.get().findElements(locator);
    List<String> result = new ArrayList<>();
    for (WebElement element : webElementList) {
      String value = null;
      if (valueType.equalsIgnoreCase("text")) {
        value = element.getText();
      } else if (valueType.equalsIgnoreCase("attribute")) {
        value = element.getAttribute(attributeOrCssValue);
      } else if (valueType.equalsIgnoreCase("cssValue")) {
        value = element.getCssValue(attributeOrCssValue);
      }
      if (value != null) {
        result.add(value);
      } else {
        LOG.info("Can not get value of this element");
      }
    }
    LOG.info("List of value is: {}", result);
    return result;
  }

  public WebElement waitForVisibilityOfElementLocated(By by, int timeout) {
    WebDriverWait wait = new WebDriverWait(threadLocalDriverBasePage.get(),
      Duration.ofSeconds(timeout));
    return wait.until(ExpectedConditions.visibilityOfElementLocated(by));
  }

  public WebElement waitForVisibilityOfElementLocated(By by, int timeout, int interval) {
    WebDriverWait wait = new WebDriverWait(threadLocalDriverBasePage.get(),
      Duration.ofSeconds(timeout), Duration.ofMillis(interval));
    return wait.until(ExpectedConditions.visibilityOfElementLocated(by));
  }

  public List<WebElement> waitForVisibilityOfAllElementsLocated(By by, int timeout) {
    WebDriverWait wait = new WebDriverWait(threadLocalDriverBasePage.get(),
      Duration.ofSeconds(timeout));
    return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(by));
  }

  public void scrollToBottom(int offset) {
    Long documentHeightBeforeScroll = getDocumentScrollHeight();
    Long documentHeightAfterScroll;
    int scrollHeight = 0;
    do {
      int scrollTimes = (int) (documentHeightBeforeScroll / offset);
      for (int i = 1; i <= scrollTimes; i++) {
        if (i == scrollTimes) {
          documentHeightBeforeScroll = getDocumentScrollHeight();
        }
        executeJs(String.format("window.scrollTo(0, %s);", (i * offset) + scrollHeight));
        waitFor(1).seconds();
        LOG.info("Scroll down by {}", (i * offset) + scrollHeight);
      }
      scrollHeight += scrollTimes * offset;
      documentHeightAfterScroll = getDocumentScrollHeight();
    } while (!documentHeightBeforeScroll.equals(documentHeightAfterScroll));
  }

  public Long getDocumentScrollHeight() {
    return (Long) threadLocalDriverBasePage.get()
      .executeScript("return document.documentElement.scrollHeight");
  }

  public void refresh() {
    threadLocalDriverBasePage.get().navigate().refresh();
    try {
      // wait for the alert to exist, then handle it and continue with refresh
      WebDriverWait wait = new WebDriverWait(threadLocalDriverBasePage.get(),
        Duration.ofSeconds(5));
      wait.until(ExpectedConditions.alertIsPresent());
      threadLocalDriverBasePage.get().switchTo().alert().accept();
      threadLocalDriverBasePage.get().switchTo().parentFrame();
      waitForPageLoaded();
      waitForSpinnerSF(1);
    } catch (Exception ignored) {
    }
  }

  public void clickBlankSpaceSf() {
    clickOrEvaluateAndClick("//section");
    LOG.info("Clicking on a blank space on the page (body element).");
  }

  public void clearAllTextOnField(String locator) {
    WebElement element = waitForVisibilityOfElementLocated(By.xpath(locator));
    element.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
    LOG.info("Cleared all text from selected field");
  }

  public void clearWebField(WebElement element) {
    while (element.getAttribute("value") != null && !element.getAttribute("value").equals("")) {
      element.sendKeys(Keys.CONTROL, Keys.chord("a"));
      element.sendKeys(Keys.BACK_SPACE);
    }
  }

  //Press Enter key at element
  public void pressEnterAtLocation(String locator) {
    threadLocalDriverBasePage.get().findElement(By.xpath(locator)).sendKeys(Keys.ENTER);
  }

  //Verify visibility Of element
  public WebElement verifyVisibilityOfElement(String element, String elementXpath) {
    waitForPageLoaded();
    WebElement webElement;
    try {
      webElement = waitForVisibilityOfElementLocated(By.xpath(elementXpath), 30);
      LOG.info("Current page has element {}", element);
    } catch (TimeoutException e) {
      LOG.info("Element {} not visible. Scroll to bottom and verify again", element);
      scrollToBottom(200);
      webElement = waitForVisibilityOfElementLocated(By.xpath(elementXpath), 30);
      LOG.info("Current page has element {}", element);
    } catch (Exception e) {
      throw new CucumberException("Can not find the element after " + 60 + " seconds");
    }
    scrollTo(webElement);
    return webElement;
  }

  //Verify visibility Of element with dynamic timeout
  public WebElement verifyVisibilityOfElement(String element, String elementXpath, int timeout) {
    WebElement webElement;
    try {
      webElement = waitForVisibilityOfElementLocated(By.xpath(elementXpath), timeout);
      LOG.info("Current page has element {}", element);
    } catch (TimeoutException e) {
      LOG.info("Element {} not visible. Scroll to bottom and verify again", element);
      scrollToBottom(200);
      webElement = waitForVisibilityOfElementLocated(By.xpath(elementXpath), 10);
      LOG.info("Current page has element {}", element);
    } catch (Exception e) {
      throw new CucumberException("Can not find the element after " + (timeout + 10) + "seconds");
    }
    scrollTo(webElement);
    return webElement;
  }

  //Verify invisibility Of element
  public void verifyInvisibilityOfElement(String element, String elementXpath, int timeout) {
    try {
      scrollToTop();
      waitForInvisibilityOfElementLocated(By.xpath(elementXpath), timeout);
      scrollToBottom(200);
      waitForInvisibilityOfElementLocated(By.xpath(elementXpath), timeout);
      LOG.info("Current page does not have element {} as expected", element);
    } catch (TimeoutException e) {
      throw new CucumberException("Fail due to current page has element " + element);
    }
  }

  public void zoomInOut(String percent) {
    executeJs(String.format("document.body.style.zoom = '%s'", percent));
    LOG.info("Zoomed {} ", percent);
  }

  public void defineTableInformation(String context, String tableName) {
    tableInformation.defineTable(context, tableName);
  }

  public void setIndexOfColumnsAreDisplayedInTable() {
    try {
      waitForVisibilityOfElementLocated(
        By.xpath(TestDataLoader.getTestData("@TD:listColumnLocator")), 5);
    } catch (TimeoutException e) {
      if (Boolean.parseBoolean(TestDataLoader.getTestData("@TD:isTableLocatedInsideElement"))) {
        scrollInsideElement(
          "//flexipage-record-home-scrollable-column[contains(@id,'middleColumn')]").to(
          Directions.BOTTOM).withLoop(2).perform();
      } else {
        scrollToBottom(200);
      }
    }
    int index = 0;
    List<WebElement> allTableColumns = waitForPresenceOfElementsLocated(
      By.xpath(TestDataLoader.getTestData("@TD:listColumnLocator")));
    for (WebElement element : allTableColumns) {
      index++;
      LOG.info("Index of column {} is {}", element.getText(), index);
      TestDataLoader.setTestData(element.getText() + "_columnIndex", String.valueOf(index));
    }
  }


  public int getIndexOfRowInTableHasValue(String rowValue) {
    rowValue = TestDataLoader.getTestData(rowValue);
    int index = 0;
    List<WebElement> allTableRows = waitForPresenceOfElementsLocated(
      By.xpath(TestDataLoader.getTestData("@TD:listRowLocator")));
    TestDataLoader.setTestData("isRowValuePresent", "false");
    for (WebElement element : allTableRows) {
      index++;
      if (element.getText().startsWith(rowValue)) {
        LOG.info("Index of row having value '{}' is '{}'", rowValue, index);
        TestDataLoader.setTestData("@TD:rowIndex", String.valueOf(index));
        TestDataLoader.setTestData("isRowValuePresent", "true");
        break;
      }
    }
    return index;
  }

  public String getCellValue(int columnIndex, int rowIndex, String expectedResult) {
    String result;
    String baseSelector = String.format(TestDataLoader.getTestData("@TD:cellValueLocator"),
      rowIndex, columnIndex);
    if (expectedResult.equalsIgnoreCase("checked") || expectedResult.equalsIgnoreCase(
      "unchecked")) {
      if (isCheckboxChecked(
        String.format(TestDataLoader.getTestData("@TD:cellValueLocator"), rowIndex, columnIndex),
        TestDataLoader.getTestData("@TD:checkboxTagAttribute"),
        TestDataLoader.getTestData("@TD:checkboxAttributeActiveValue"),
        TestDataLoader.getTestData("@TD:checkboxAttributeInactiveValue"))) {
        LOG.info("Cell value is: checked");
        return "checked";
      } else {
        LOG.info("Cell value is: unchecked");
        return "unchecked";
      }
    }
    if (expectedResult.equalsIgnoreCase("empty")) {
      result = waitForPresenceOfElementLocated(
        By.xpath(String.format(baseSelector, rowIndex, columnIndex))).getAttribute("innerText");
    } else {
      result = threadLocalDriverBasePage.get()
        .findElement(By.xpath(String.format(baseSelector, rowIndex, columnIndex)))
        .getAttribute("innerText");
    }
    LOG.info("Cell value is: \"{}\"", result);
    if (expectedResult.equalsIgnoreCase("empty") || expectedResult.equalsIgnoreCase("not empty")) {
      String cellValue = result.replaceAll("(?m)^\\s+$", "");
      if (cellValue.isEmpty()) {
        return "empty";
      } else {
        return "not empty";
      }
    }
    return result;
  }

  public boolean isCheckboxChecked(String locator, String tagAttribute, String activeValue,
    String inactiveValue) {
    WebElement element = verifyVisibilityOfElement(locator, locator);
    if (tagAttribute.isBlank() && activeValue.isBlank() && inactiveValue.isBlank()) {
      return element.isSelected();
    } else {
      Assert.assertNotNull("Class Attribute can not be null", tagAttribute);
      String actualAttributeValue = element.getAttribute(tagAttribute);
      if (activeValue != null && inactiveValue == null) {
        return (actualAttributeValue.equalsIgnoreCase(activeValue));
      } else if (activeValue == null && inactiveValue != null) {
        return (!actualAttributeValue.equalsIgnoreCase(inactiveValue));
      } else if (activeValue != null) {
        return actualAttributeValue.contains(activeValue) && !actualAttributeValue.contains(
          inactiveValue);
      } else {
        throw new CucumberException(
          "Active value and inactive value can not be null at the same time");
      }
    }
  }


  public ConvertTimeZoneBuilder convertDateTime(String dateTimeInString) {
    return new ConvertTimeZoneBuilder(dateTimeInString);
  }

  public static class ConvertTimeZoneBuilder {

    private final String dateTimeInString;
    private String expectedTimeZone;
    private String expectedFormat;

    public ConvertTimeZoneBuilder(String dateTimeInString) {
      this.dateTimeInString = dateTimeInString;
    }

    public ConvertTimeZoneBuilder toTimeZone(String expectedTimeZone) {
      this.expectedTimeZone = expectedTimeZone == null ? "Europe/Berlin" : expectedTimeZone;
      return this;
    }

    public ConvertTimeZoneBuilder withFormat(String expectedFormat) {
      this.expectedFormat = expectedFormat;
      return this;
    }

    public String asString() {
      return LocalDateTime.ofInstant(Instant.parse(dateTimeInString), ZoneId.of(expectedTimeZone))
        .format(DateTimeFormatter.ofPattern(expectedFormat));
    }

    public String toInstant() {
      String gmtDateTime = "";
      //Convert status as date time to instant format
      if (dateTimeInString.contains("T") && !dateTimeInString.contains("Z")) {
        LOG.info("Date time format \"{}\" is not correct. Convert to instant...", dateTimeInString);
        gmtDateTime = dateTimeInString.split("[.].*")[0] + "Z";
      } else if (dateTimeInString.contains("T") && dateTimeInString.contains("Z")) {
        LOG.info("Date time format \"{}\". Do not need to convert!", dateTimeInString);
      } else {
        LOG.info("Date time format \"{}\" is not correct. Convert to instant...", dateTimeInString);
        gmtDateTime = dateTimeInString.split("[.].*")[0] + "T00:00:00Z";
      }
      LOG.info("Instant date time format is: {}", gmtDateTime);
      return gmtDateTime;
    }

  }

  public void refreshCurrentTabInSalesforce() {
    waitForSpinnerSF(2);
    String currentTabLocator = "//li[contains(@class,'slds-is-active active')]//button[contains(@title,'Action')]";
    String refreshButton = "//a[./span[text()='Refresh Tab']]";
    clickOrEvaluateAndClick(currentTabLocator);
    clickOrEvaluateAndClick(refreshButton);
    LOG.info("Refreshed current tab");
  }

  public void goOnePageBack() {
    threadLocalDriverBasePage.get().navigate().back();
    waitForPageLoaded();
  }

  public void hoverOnElement(String locatorName, WebElement we) {
    waitForPageLoaded();
    Actions action = new Actions(threadLocalDriverBasePage.get());
    scrollElementToCenter(we);
    action.moveToElement(we).build().perform();
    waitForPageLoaded();
    LOG.info("Hovering over \"{}\" link ", locatorName);
  }

  public List<String> convertToTestDataWithNoSpecialChar(List<String> contentList) {
    List<String> newContentList = new ArrayList<>();
    for (String value : contentList) {
      getStringNoSpecialChar(newContentList, value);
    }
    return newContentList;
  }

  public void getStringNoSpecialChar(List<String> newContentList, String value) {
    if (value.startsWith("@TD") || value.startsWith("@date")) {
      String emailContent = TestDataLoader.getTestData(value);
      if (emailContent.contains("|")) {
        String[] expectedText = emailContent.split(" \\| ");
        newContentList.addAll(Arrays.asList(expectedText));
      } else {
        newContentList.add(emailContent);
      }
    } else {
      newContentList.add(value);
    }
  }

  public static String getValueMatchRegexFromString(String regex, String string) {
    String matchingValue = "";
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(string);
    while (matcher.find()) {
      matchingValue = matcher.group(0);
      if (!matchingValue.isEmpty()) {
        break;
      }
    }
    LOG.info("The value matching with the regex '{}' from string '{}' is:\n'{}'", regex, string,
      matchingValue);
    return matchingValue;
  }

  public void selectValueFromDropDownList(String valueToSelect, String list,
    String dropdownListXpath) {
    valueToSelect = TestDataLoader.getTestData(valueToSelect);
    String selector =
      "//*[@role='presentation' or @role='menuitem' or @role='option']//*[text()='" + valueToSelect
        + "']";
    clickOrEvaluateAndClick(dropdownListXpath);
    clickOrEvaluateAndClick(selector);
    LOG.info("Selected value {} --- from dropdown list -----{}", valueToSelect, list);
  }

  public void verifyInformationHasTestDataLoader(String informationType,
    List<String> informationList) {
    String keyword;
    String value;
    List<String> expectedResult = new ArrayList<>();
    for (String data : informationList) {
      String newData;
      if (data.contains("@TD:")) {
        keyword = getValueMatchRegexFromString("@TD:\\w+", data);
        value = TestDataLoader.getTestData(keyword);
        newData = data.replace(keyword, value);
        expectedResult.add(newData);
      } else {
        expectedResult.add(data);
      }
      waitForPageLoaded();
      scrollToBottom(200);
      String pageContent = getTextOfElement(By.xpath("//body[@class='desktop']"));
      LOG.info("Page content is {}", pageContent);
      expectedResult.forEach(info -> {
        Assert.assertTrue(pageContent.contains(info));
        LOG.info("This \"{}\" {} displays as expected", info, informationType);
      });
    }
  }

  public void verifyInformationNotDisplay(String informationType, List<String> informationList) {
    informationList = convertToTestDataWithNoSpecialChar(informationList);
    waitForPageLoaded();
    scrollToBottom();
    String pageContent = getTextOfElement(By.xpath("//body"));
    LOG.info("Page content is {}", pageContent);
    informationList.forEach(info -> {
      Assert.assertFalse(pageContent.contains(info));
      LOG.info("This \"{}\" {} does not displays as expected", info, informationType);
    });
  }

  public void verifyPageTitle(String pageTitle) {
    Assert.assertTrue(waitForPageTitleToContain(pageTitle, 30),
      String.format("The title [%s] is not matched with current: [%s]", pageTitle,
        threadLocalDriverBasePage.get().getTitle()));
    LOG.info("\"{}\" page title is verified", pageTitle);
  }


  public void switchToTabX(Integer tab) {
    if (Hook.browser.equals("safari") && Hook.platform.equals("ios-webApp")) {
      iosBasicPage =
        (iosBasicPage == null || DriverUtil.getDriver() != threadLocalDriverBasePage.get())
          ? (new IosBasicPage((IOSDriver) DriverUtil.getDriver())) : iosBasicPage;
      iosBasicPage.switchContext("native");
      iosBasicPage.switchToTabAtIndex(tab);
      iosBasicPage.switchContext("webview");
      ArrayList<String> tabs = new ArrayList<>(threadLocalDriverBasePage.get().getWindowHandles());
      threadLocalDriverBasePage.get().switchTo().window(tabs.get(tab - 1));
      TestDataLoader.setTestData("IosWorkingWindowIndex", String.valueOf(tab));
      LOG.info("Focused window title is: {}", threadLocalDriverBasePage.get().getTitle());
    } else {
      ArrayList<String> tabs = new ArrayList<>(threadLocalDriverBasePage.get().getWindowHandles());
      for (String singleTab : tabs) {
        String title = threadLocalDriverBasePage.get().switchTo().window(singleTab).getTitle();
        LOG.info(title);
      }
      threadLocalDriverBasePage.get().switchTo().window(tabs.get(tab));
    }
  }

  public void verifyElementIsClickable(String element, String elementXpath) {
    WebElement webElement;
    try {
      webElement = waitForElementToBeClickable(By.xpath(elementXpath), 20);
      LOG.info("The element {} is clickable", element);
    } catch (TimeoutException e) {
      LOG.info("Element {} not visible. Scroll to bottom and verify again", element);
      scrollToBottom(200);
      webElement = waitForElementToBeClickable(By.xpath(elementXpath), 10);
      LOG.info("The element {} is clickable", element);
    } catch (Exception e) {
      throw new CucumberException("Can not find the element after 30 seconds");
    }
    scrollToViaAction(webElement);
  }

  public String getStatusInGerman(String status) {
    status = status.equalsIgnoreCase("selected") ? "Ausgewählt" : "Auswählen";
    return status;
  }

  public String handleDateTimeData(String fieldName, String value) {
    List<String> fieldNeedHandleData = Arrays.asList("date of activation", "date of inactivation",
      "signature date", "created by", "last modified by", "kündigung möglich bis");
    if (fieldNeedHandleData.contains(fieldName.toLowerCase())) {
      String sfUserTimezone =
        (TestDataLoader.getTestData("@TD:TimeZoneSidKey") == null) ? "Europe/Berlin"
          : TestDataLoader.getTestData("@TD:TimeZoneSidKey");
      String gmtDateTime;
      // Get gmtDateTime by converting value
      if (fieldName.toLowerCase().contains("date") || fieldName.equalsIgnoreCase(
        "kündigung möglich bis")) {
        gmtDateTime = convertDateTime(TestDataLoader.getTestData(value)).toInstant();
      } else {
        gmtDateTime = convertDateTime(TestDataLoader.getTestData(value.split(", ")[1])).toInstant();
      }
      // Convert gtm datetime value to datetime value in SF user's timezone
      if (fieldName.equalsIgnoreCase("Date of activation") || fieldName.equalsIgnoreCase(
        "Date of inactivation")) {
        value = convertDateTime(gmtDateTime).toTimeZone(sfUserTimezone)
          .withFormat("dd.MM.yyyy HH:mm").asString();
      } else if (fieldName.equalsIgnoreCase("Signature date") || fieldName.equalsIgnoreCase(
        "kündigung möglich bis")) {
        value = convertDateTime(gmtDateTime).toTimeZone(sfUserTimezone).withFormat("dd.MM.yyyy")
          .asString();
      } else {
        value = TestDataLoader.getTestData(value.split(", ")[0]) + ", " + convertDateTime(
          gmtDateTime).toTimeZone(sfUserTimezone).withFormat("dd.MM.yyyy HH:mm").asString();
      }
    } else {
      value = TestDataLoader.getTestData(value);
    }
    return value;
  }

  public void waitForElementToBeSelected(By by, boolean isSelected, int timeout) {
    WebDriverWait wait = new WebDriverWait(threadLocalDriverBasePage.get(),
      Duration.ofSeconds(timeout));
    wait.until(ExpectedConditions.elementSelectionStateToBe(by, isSelected));
  }

  public boolean waitForPageTitleToContain(String title, int timeout) {
    WebDriverWait wait = new WebDriverWait(threadLocalDriverBasePage.get(),
      Duration.ofSeconds(timeout));
    return wait.until(ExpectedConditions.titleContains(title));
  }

  public List<WebElement> waitForNumberOfElementsToBePresent(By by, int numberOfElements,
    int timeout) {
    WebDriverWait wait = new WebDriverWait(threadLocalDriverBasePage.get(),
      Duration.ofSeconds(timeout));
    return wait.until(ExpectedConditions.numberOfElementsToBe(by, numberOfElements));
  }

  public boolean waiForTheUrlToContain(String url, int timeout) {
    WebDriverWait wait = new WebDriverWait(threadLocalDriverBasePage.get(),
      Duration.ofSeconds(timeout));
    return wait.until(ExpectedConditions.urlContains(url));
  }
}
