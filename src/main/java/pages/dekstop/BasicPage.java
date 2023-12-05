package pages.dekstop;

import static config.FilesUtils.readDocFile;
import static config.FilesUtils.readTextFile;

import config.BasePage;
import config.DriverUtil;
import config.TestDataLoader;
import io.appium.java_client.ios.IOSDriver;
import io.cucumber.core.exception.CucumberException;
import io.cucumber.datatable.DataTable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import locators.BasicPageLocators;
import modal.Directions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import pages.ios.IosBasicPage;
import steps.Hook;

public class BasicPage extends BasePage {

  public BasicPage(RemoteWebDriver driver) {
    super(driver);
  }

  private static final Logger LOG = LogManager.getLogger(BasicPage.class);
  private final Map<String, String> xpathToPage = BasicPageLocators.createLibraryPage();
  private final Map<String, String> xpathToElement = BasicPageLocators.createLibraryElement();

  private IosBasicPage iosBasicPage;

  public void openURL(String url) {
    String extractedUrl = loadUrl(url);
    if (!DriverUtil.threadLocalActiveBrowsers.get().get("current").getCurrentUrl()
        .equals(extractedUrl)) {
      switch (url) {
        case "@TD:FEHomepage":
        case "@TD:FEProduct_SkyQ":
        case "@TD:FEProduct_SkyGlass":
        case "@TD:OnPrem_WebChatUrl":
        case "@TD:FEHelp_Center":
        case "@TD:FE_OnBoardingHub":
        case "@TD:FEProduct_BundesligaPromotion":
        case "@TD:CreatePasswordForNewAccountUrl":
        case "@TD:ConfirmEmailAddressURL":
        case "@TD:FE_DELoginPage":
        case "@TD:FE_DECheckoutOrderPage":
        case "@TD:ResetPasswordUrl":
        case "@TD:ResetSkyPinUrl":
        case "@TD:ResetMpPinUrl":
        case "@TD:FEProduct_SkyQ_Cinema_Bundle_noDisney":
        case "@TD:FEProduct_MGM":
        case "@TD:FEProduct_Trial_Umbrella_ENT_noGlass_12M_90percent_DE_SportBundle":
        case "@TD:FEProduct_Test_Umbrella_UTV_12M_FictionBundle":
        case "@TD:FEProduct_Trial_Umbrella_UTV_noGlass_12M_90percent_DE_Standard2022":
        case "@TD:FE_SkyKidsPaket":
        case "@TD:FE_SkyCinemaPacket":
        case "@TD:FE_SkyBundesligaPacket":
        case "@TD:FE_SkySportPaket":
        case "@TD:FE_DAZNPaket":
        case "@TD:FE_EntertainmentPaket":
        case "@TD:FEProduct_S_Umbrella_UTV_12M_HardBundle_oG_DE_NSO_July2023":
        case "@TD:FEProduct_S_Umbrella_ENT_Sport_12M_HardBundle_oG_DE_NSO_July2023":
        case "@TD:FE_LoginToMeinSky":
          threadLocalDriverBasePage.get().get(loadUrl("@TD:FENewBaseURL"));
          handleAuthorizationPopupInSafariOfUrl("@TD:FENewBaseURL");
          break;
        case "@TD:FE_AustrianLoginPage":
        case "@TD:FE_AustriaHelpCenter":
          DriverUtil.threadLocalActiveBrowsers.get().get("current")
              .get(loadUrl("@TD:AT_FEBaseURL"));
          handleAuthorizationPopupInSafariOfUrl("@TD:AT_FEBaseURL");
          break;
        default:
          LOG.info("The page does not need credentials to be accessible. Continue...");
      }
      DriverUtil.threadLocalActiveBrowsers.get().get("current").get(extractedUrl);
      handleAuthorizationPopupInSafariOfUrl(url);
    } else {
      LOG.info("This url {} was already loaded, so no need to open it again!", extractedUrl);
    }
    if (url.contains("facebook")) {
      cookiePopupFB();
    } else if (url.contains("salesforce") || url.contains("partnerportal")) {
      LOG.info("Skip checking cookies/privacy as the URL is Salesforce / Partner Portal");
    } else {
      cookiePopup();
    }
    if (Hook.platform.equals("desktop")) {
      DriverUtil.threadLocalActiveBrowsers.get().get("current").manage().window().maximize();
    }
    if (Hook.testedEnv.equalsIgnoreCase("prod") && url.contains("facebook")) {
      clickWebElement("//a[@role='button' and contains(.,'Log In')]", "button to login mask");
    }
  }

  private void handleAuthorizationPopupInSafariOfUrl(String url) {
    if (Hook.browser.equalsIgnoreCase("safari") && Hook.platform.equals("ios-webApp")) {
      String[] credentials = getSafariAuthorizationPopUpLoginCredentials(url);
      String username = credentials[0];
      String password = credentials[1];
      if (username != null) {
        try {
          IOSDriver iosDriver = ((IOSDriver) threadLocalDriverBasePage.get());
          LOG.info("Switch to context native app to handle authorization popup");
          iosBasicPage =
              (iosBasicPage == null || DriverUtil.getDriver() != threadLocalDriverBasePage.get())
                  ? (new IosBasicPage((IOSDriver) DriverUtil.getDriver())) : iosBasicPage;
          iosBasicPage.switchContext("native");
          WebDriverWait wait = new WebDriverWait(iosDriver, Duration.ofSeconds(15));
          wait.until(ExpectedConditions.visibilityOfElementLocated(
              By.xpath("//XCUIElementTypeTextField[@value='User Name']"))).sendKeys(username);
          wait.until(ExpectedConditions.visibilityOfElementLocated(
              By.xpath("//XCUIElementTypeSecureTextField[@value='Password']"))).sendKeys(password);
          wait.until(ExpectedConditions.visibilityOfElementLocated(
              By.xpath("//XCUIElementTypeButton[@name='Log In']"))).click();
          wait.until(ExpectedConditions.invisibilityOfElementLocated(
              By.xpath("//XCUIElementTypeButton[@name='Log In']")));
          LOG.info("Switch to context web view to continue the test...");
          iosBasicPage.switchContext("webview");
        } catch (Exception e) {
          throw new CucumberException(
              "Handle authorization popup is failed due to: " + e.getMessage());
        }
      } else {
        LOG.info("Do not need authorize for this url {}", url);
      }
    } else {
      LOG.info("Test browser is not safari. No need to work!");
    }
  }

  public String[] getSafariAuthorizationPopUpLoginCredentials(String url) {
    String username = null;
    String password = null;
    if (url.toLowerCase().contains("onprem")) {
      username = TestDataLoader.getTestData("@TD:onpremLoginUsername");
      password = TestDataLoader.getTestData("@TD:onpremLoginPassword");
    } else if (url.equals("@TD:FENewBaseURL") || url.equals("@TD:AT_FEBaseURL")) {
      username = TestDataLoader.getTestData("@TD:cdploginemail");
      password = TestDataLoader.getTestData("@TD:cdploginpassword");
    }
    return new String[]{username, password};
  }

  public String loadUrl(String url) {
    if (!url.startsWith("http://") || !url.startsWith("https://")) {
      url = TestDataLoader.getTestData(url);
    }
    return url;
  }

  public void openUrlNewWindow(String url) {
    if (Hook.browser.equals("safari") && Hook.platform.equals("ios-webApp")) {
      LOG.info("Due to testing in mobile safari, then need to open new tab in native context");
      iosBasicPage =
          (iosBasicPage == null || DriverUtil.getDriver() != threadLocalDriverBasePage.get())
              ? (new IosBasicPage((IOSDriver) DriverUtil.getDriver())) : iosBasicPage;
      iosBasicPage.switchContext("native");
      iosBasicPage.scrollToTop();
      iosBasicPage.tapElementByAccessibilityId("TabsButton");
      iosBasicPage.tapElementByAccessibilityId("AddTabButton");
      iosBasicPage.waitForElementByAccessibilityId(5, 500L, "TabsButton", false);
      iosBasicPage.switchContext("webview");
    } else {
      LOG.info("Focused window is {}", threadLocalDriverBasePage.get().getWindowHandle());
      threadLocalDriverBasePage.get().executeScript("window.open()");
    }
    ArrayList<String> tabs = new ArrayList<>(threadLocalDriverBasePage.get().getWindowHandles());
    LOG.info("All opened window are: {}", tabs);
    threadLocalDriverBasePage.get().switchTo().window(tabs.get(tabs.size() - 1));
    openURL(url);
    LOG.info("Switched to window: {}", tabs.get(tabs.size() - 1));
    waitForPageLoaded();
  }

  public List<String> getTabs() {
    List<String> tabs = new ArrayList<>(threadLocalDriverBasePage.get().getWindowHandles());
    tabs.forEach(tab -> LOG.info("Active tab is: {}", tab));
    return tabs;
  }

  public void switchToTabByIndex(Integer idx) {
    waitFor(1).seconds();
    List<String> tabs = getTabs();
    threadLocalDriverBasePage.get().switchTo().window(tabs.get(idx));
//        threadLocalDriverBasePage.get().manage().window().maximize();
    LOG.info(threadLocalDriverBasePage.get().manage().window().getPosition());
  }

  public void switchToTabByIndexAndCLoseByIndex(Integer expectedHandleTab,
      Integer expectedClosedTab) {
    List<String> tabs = getTabs();
    LOG.info("{} tabs open", tabs.size());
    threadLocalDriverBasePage.get().switchTo().window(tabs.get(expectedClosedTab)).close();
    LOG.info("Closed tab {}", tabs.get(expectedClosedTab));
    threadLocalDriverBasePage.get().switchTo().window(tabs.get(expectedHandleTab));
    LOG.info("Switch to tab {}", tabs.get(expectedHandleTab));
//        threadLocalDriverBasePage.get().manage().window().maximize();
    LOG.info(threadLocalDriverBasePage.get().manage().window().getPosition());
  }

  public void switchToFirstTab() {
    ArrayList<String> tabs = new ArrayList<>(threadLocalDriverBasePage.get().getWindowHandles());
    threadLocalDriverBasePage.get().switchTo().window(tabs.get(0));
  }

  public void switchToFirstTabAndClose() {
    ArrayList<String> tabs = new ArrayList<>(threadLocalDriverBasePage.get().getWindowHandles());
    threadLocalDriverBasePage.get().switchTo().window(tabs.get(1)).close();
    threadLocalDriverBasePage.get().switchTo().window(tabs.get(0));
  }

  public void checkLandingPage(String landingPage) {
    if (landingPage.contains("dhl")) {
      //save cdp home page handle
      String skyCdpHandle = threadLocalDriverBasePage.get().getWindowHandle();
      Set<String> allOpenWindows = threadLocalDriverBasePage.get().getWindowHandles();

      //Iterate and switch to dhl page handle
      for (String window : allOpenWindows) {
        if (!window.equals(skyCdpHandle)) {
          threadLocalDriverBasePage.get().switchTo().window(window);
        }
      }
    }
//        Assert.assertTrue(findElement(xpath, "expected element on Landing Page").isDisplayed());
    waitForPageLoaded();
    waitForSpinnerCDP(1);
    waitForSpinnerSF(1);
    cookiePopup();
    verifyVisibilityOfElement(landingPage, xpathToPage.get(landingPage), 10);
    LOG.info("User is on {}", landingPage);
  }

  public void checkLandingPageWithUrlAndTitle(String landingPage, String url, String title) {
    cookiePopup();
    if (landingPage.contains("sky")) {
      waitForSpinnerCDP(1);
    }
    String xpath = xpathToPage.get(landingPage);
    Assert.assertTrue(waitForVisibilityOfElementLocated(By.xpath(xpath)).isDisplayed());
    Assert.assertTrue(threadLocalDriverBasePage.get().getCurrentUrl().contains(url));
    Assert.assertTrue(threadLocalDriverBasePage.get().getTitle().contains(title));
  }

  public static String[] splitStringIntoArray(String string) {
    return splitStringIntoArray(string, "|");
  }

  public static String[] splitStringIntoArray(String string, String separator) {
    return string.split(String.format("[\\%s]", separator));
  }

  public void switchTab(String tab) {
    tab = TestDataLoader.getTestData(tab);
    clickOrEvaluateAndClick(
        String.format("//a[@title='%s' and contains(@class,'label-action')]", tab));
  }

  public void pageHasElementWithText(String element, String text) {
    if (text.contains("FE_sky_email")) {
      text = TestDataLoader.getTestData(text);
    }
    String selector = String.format("//%s[text()='%s' or contains(text(), '%s')]", element, text,
        text);
    waitForVisibilityOfElementLocated(By.xpath(selector));
  }

  public void currentPageUrlContains(String urlPart) {
    waitForPageLoaded();
    String cu = threadLocalDriverBasePage.get().getCurrentUrl();
    LOG.info("Current url: {}", cu);
    if (!cu.contains(urlPart)) {
      throw new CucumberException(
          String.format("Current page's url doesn't contain <%s>", urlPart));
    }
    LOG.info("Url contains <{}> as expected.", urlPart);
  }

  public void closeCurrentPage() {
    threadLocalDriverBasePage.get().close();
  }

  //Save the key-value in test data runtime
  public void saveKeyWithValue(String key, String value) {
    if (value.contains(" ") && value.toLowerCase().contains("@td:")) {
      StringBuilder valueBuilder = new StringBuilder();
      String[] valueArray = value.split(" ");
      for (String val : valueArray) {
        valueBuilder.append(TestDataLoader.getTestData(val)).append(" ");
      }
      value = valueBuilder.toString().strip();
    }
    if ("Trensport_cancellation_period_mein_abo".equals(key)
        || "Trensport_end_of_commitment_mein_abo".equals(key)) {
      String[] dateValue = TestDataLoader.getTestData(value).split("-");
      value = String.format("%s.%s.%s", dateValue[2], dateValue[1], dateValue[0]);
    }
    TestDataLoader.setTestData(key, TestDataLoader.getTestData(value));
  }

  /**
   * Generate random email address at expected mail server Yopmail support email with max length of
   * 25 The below code generates local date time as string with length 17 So set the mail address
   * max length to 8
   */
  public void generateRandomEmailAtServer(String mailAddress, String mailServer) {
    mailServer = TestDataLoader.getTestData(mailServer);
    LocalDateTime localDateTime = LocalDateTime.now(ZoneId.of("Europe/Paris"));
    String localDateTimeString = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")
        .format(localDateTime);
    String randomEmail = String.format("%s%s%s", TestDataLoader.getTestData(mailAddress),
        localDateTimeString, mailServer);
    TestDataLoader.setTestData("randomEmail", randomEmail);
  }

  //Open new browser with user-agent to avoid access denied error
  public void openBrowserWithUserAgent(String userAgent) {
    DriverUtil.initDriverWithUserAgent(Hook.browser, TestDataLoader.getTestData(userAgent));
  }

  public void switchDriverToTab(String pageTitle) {
    String currentWindow = threadLocalDriverBasePage.get().getWindowHandle();
    for (String winHandle : threadLocalDriverBasePage.get().getWindowHandles()) {
      if (threadLocalDriverBasePage.get().switchTo().window(winHandle).getTitle()
          .equalsIgnoreCase(pageTitle)) {
        break;
      } else {
        threadLocalDriverBasePage.get().switchTo().window(currentWindow);
      }
    }
  }

  //Ensure that file existence maps with expectation
  public void ensureFileExistenceAsExpectation(String fileName, String existence) {
    switch (existence.toLowerCase()) {
      case "not existing":
        ensureFileNotExist(fileName);
        break;
      case "existing":
        ensureFileExist(fileName);
        break;
      default:
        throw new CucumberException("Existence should be \"not existing\" or \"existing\"");
    }
  }

  //Make sure file exist
  private void ensureFileExist(String fileName) {
    String filePath =
        DriverUtil.PATH_TO_DOWNLOAD_DIR + System.getProperty("file.separator") + fileName;
    File files = new File(filePath);
    boolean exist;
    int tryCount = 0;
    do {
      try {
        exist = files.isFile();
        Assert.assertTrue(exist);
      } catch (AssertionError assertionError) {
        LOG.info("File {} not exists. Try to verify again after 10 seconds...", filePath);
        tryCount++;
        waitFor(10).seconds();
        exist = files.isFile();
      }
      if (exist) {
        break;
      } else {
        LOG.info("File {} can be in downloading progress. Let's wait...", fileName);
      }
    } while (tryCount < 30);
    if (exist) {
      LOG.info("File {} exists. Download file successfully!", fileName);
    } else {
      throw new CucumberException("File " + fileName + " not exist. Fail to download!");
    }
  }

  //Make sure file not exist. If it exists =>Delete
  private void ensureFileNotExist(String fileName) {
    String filePath =
        DriverUtil.PATH_TO_DOWNLOAD_DIR + System.getProperty("file.separator") + fileName;
    try {
      boolean result = Files.deleteIfExists(Paths.get(filePath));
      if (result) {
        LOG.info("File {} exists. Delete file successfully!", fileName);
      } else {
        LOG.info("File {} not exists. Continue!", fileName);
      }
    } catch (IOException e) {
      LOG.info("Invalid permissions.");
    }
  }

  //Open new browser with same type from -Dbrowser and assign to an alias to handle
  public void openNewBrowserWithAlias(String browserAlias) {
    String testBrowser = Hook.browser;
    DriverUtil.initializeBrowserWithSessionAlias(testBrowser, browserAlias);
  }

  public void verifyContentOfTextFile(String fileName, DataTable dataTable) {
    String filePath =
        DriverUtil.PATH_TO_DOWNLOAD_DIR + System.getProperty("file.separator") + fileName;
    String actualTexts = null;
    if (fileName.contains(".txt")) {
      actualTexts = readTextFile(filePath);
    } else if (fileName.contains(".docx")) {
      actualTexts = readDocFile(filePath);
    }
    if (actualTexts == null) {
      throw new CucumberException("Did not found any file containing the extension: " + fileName);
    }
    List<String> expectedTexts = dataTable.asList(String.class);
    expectedTexts = convertToTestDataWithNoSpecialChar(expectedTexts);
    for (String expectedText : expectedTexts) {
      Assert.assertTrue(actualTexts.contains(expectedText),
          "The actual texts:\n" + actualTexts + "\ndo not contain the expected texts: "
              + expectedText);
      LOG.info("The following expected texts are present in the file: {}", expectedText);
    }
  }

  /**
   * Logic: - Get column index via column name and save to a map (to avoid repeat
   * getIndexOfColumnNameInTable for every row) - Verify table data + Define unique column based on
   * tableName and context + Get base row in which data needs to be verified + Verify cell value
   * based on column index and row index
   *
   * @param tableName = {name of table in every context}
   * @param context   = {text context}
   * @param data      {table information needs to be verified}
   */
  public void verifyInformationInTable(String action, String tableName, String context,
      List<Map<String, String>> data) {
    // Switch to Iframe
    if (context.contains("salesforce reports page")) {
      waitForPageLoaded();
      waitForSpinnerSF(5);
      scrollInsideElement("//div[contains(@class,'widget-container_scroll-container')]").to(
          Directions.TOP).withLoop(5).perform();
    }
    defineTableInformation(context, tableName);

    // Map all columns onto corresponding index
    setIndexOfColumnsAreDisplayedInTable();
    String uniqueCol = TestDataLoader.getTestData("@TD:uniqueColumn");
    int baseColumnIndex = Integer.parseInt(
        TestDataLoader.getTestData("@TD:" + uniqueCol + "_columnIndex"));
    LOG.info("baseColumnIndex: {}", baseColumnIndex);
    for (Map<String, String> itemRow : data) {
      // define a base row base on unique column name and row value
      int baseRowIndex = getIndexOfRowInTableHasValue(itemRow.get(uniqueCol));
      LOG.info("baseRowIndex: {}", baseRowIndex);
      if (action.equals("does not see")) {
        Assert.assertNotEquals(baseRowIndex, 0, itemRow.get(uniqueCol) + " is still displayed");
        LOG.info("column: {} is not displayed", itemRow.get(uniqueCol));
      } else {
        for (Map.Entry<String, String> column : itemRow.entrySet()) {
          String columnName = column.getKey();
          int columnIndexItem = Integer.parseInt(
              TestDataLoader.getTestData("@TD:" + columnName + "_columnIndex"));
          if (tableName.equals("Webchat Transfer Queue Information") && getCellValue(1,
              baseRowIndex, itemRow.get(columnName)).equals("")) {
            columnIndexItem--;
          }
          //getCellValue() currently support expected result as: text, checkbox(checked or unchecked),empty, not empty
          String actualValue = getCellValue(columnIndexItem, baseRowIndex, itemRow.get(columnName));
          String expectedValue = TestDataLoader.getTestData(
              setTableExpectedValue(itemRow, columnName, tableName));
          if (columnName.equalsIgnoreCase("Incident ID")) {
            expectedValue = expectedValue.substring(0, 15);
            Assert.assertTrue(actualValue.contains(expectedValue),
                String.format("Expectation value is: %s, but found actual: %s", expectedValue,
                    actualValue));
          } else {
            Assert.assertEquals(actualValue, expectedValue);
          }
          LOG.info("The table {} of context {} with column index {} and row index {} has value {}",
              tableName, context, TestDataLoader.getTestData(columnName + "_columnIndex"),
              baseRowIndex, expectedValue);
        }
      }
    }
    threadLocalDriverBasePage.get().switchTo().defaultContent();
  }

  public String setTableExpectedValue(Map<String, String> itemRow, String columnName,
      String tableName) {
    String expectedValue = itemRow.get(columnName);
    if (expectedValue.contains("@TD:")) {
      expectedValue = TestDataLoader.getTestData(itemRow.get(columnName));
    }

    //convert date value for hw return items table with hour and minute to verify
    if (columnName.contains("Created Date") && tableName.contains("HW Return")) {
      String sfUserTimezone =
          (TestDataLoader.getTestData("@TD:TimeZoneSidKey") == null) ? "Europe/Berlin"
              : TestDataLoader.getTestData("@TD:TimeZoneSidKey");
      String gmtDateTime = convertDateTime(TestDataLoader.getTestData(expectedValue)).toInstant();
      expectedValue = convertDateTime(gmtDateTime).toTimeZone(sfUserTimezone)
          .withFormat("dd.MM.yyyy HH:mm").asString();
    }
    if (columnName.contains("Initiated Source Country")) {
      switch (expectedValue) {
        case "AT":
          expectedValue = "Austria";
          break;
        case "DE":
          expectedValue = "Germany";
          break;
        default:
          throw new CucumberException(
              "Expected value for \"Initiated Source Country\" column should be AT or DE");
      }
    }
    if (columnName.contains("Chat Transcript ID")) {
      expectedValue = expectedValue.substring(0, expectedValue.length() - 3);
    }
    if (columnName.equals("PAYBACK Points")) {
      DecimalFormat myFormatter = new DecimalFormat("###");
      expectedValue = myFormatter.format(Float.valueOf(expectedValue)).concat(",00");
    }
    return expectedValue;
  }

  public void subStringAndSaveInDataRunTime(String string, int startIndex, int endIndex,
      String keyToSave) {
    String stringToHandle = TestDataLoader.getTestData(string);
    TestDataLoader.setTestData(keyToSave, stringToHandle.substring(startIndex, endIndex));
  }


  public void executeJsAndSaveValue(String command) {
    String jsCommand = String.format("return %s", command);
    Object output = threadLocalDriverBasePage.get().executeScript(jsCommand);
    String valueToSave;
    String keyToSave;
    if (command.equals("adobeDataLayer")) {
      @SuppressWarnings("unchecked")
      List<Map<String, String>> returnedArray = (List<Map<String, String>>) output;
      valueToSave = String.valueOf(returnedArray.size());
      keyToSave = "adobeDataLayerResultSize";
    } else {
      throw new CucumberException(
          "Invalid command. The valid commands should be one of the following: {'adobeDataLayer'}");
    }
    LOG.info("The output value for command '{}' is '{}' and being saved as key '{}'", command,
        output, keyToSave);
    TestDataLoader.setTestData(keyToSave, valueToSave);
  }

  public void compareTwoStrings(String value1, String value2, String comparison) {
    value1 = TestDataLoader.getTestData(value1);
    value2 = TestDataLoader.getTestData(value2);
    LOG.info("Comparing the'{}' to '{}' '{}'", value1, comparison, value2);
    if (comparison.equals("equals")) {
      Assert.assertEquals(value1, value2);
    } else if (comparison.equals("contains")) {
      Assert.assertTrue(value1.contains(value2));
    } else {
      throw new CucumberException(
          "Invalid comparison. The valid comparison should be one of the following: {'equals', 'contains'}");
    }
  }
}
