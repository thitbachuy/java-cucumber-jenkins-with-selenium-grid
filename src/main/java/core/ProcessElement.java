package core;

import org.openqa.selenium.By;

public class ProcessElement {

  String locator;

  /**
   * Wait until element is visible on browser using configuration timeout For setting timeout,
   * please type in command line with argument browsertimeout e.g.: -Dbrowsertimeout=30
   *
   * @return {@link ProcessElement}
   * @author lybth
   */
  public By getLocation(String locator) {
    return By.xpath(locator);
  }
}
