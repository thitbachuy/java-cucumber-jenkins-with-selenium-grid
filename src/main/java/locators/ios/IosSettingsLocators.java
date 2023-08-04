package locators.ios;

import java.util.HashMap;
import java.util.Map;

public class IosSettingsLocators {

  public static Map<String, String> createLibraryTextView() {
    Map<String, String> xpathToTextView = new HashMap<>();
    xpathToTextView.put("networkInternetMenu",
        "//android.widget.TextView[@text='Network and Internet' or @text='Network & internet']");
    xpathToTextView.put("autoSyncData",
        "//android.widget.TextView[@text='Automatically sync data']");

    return xpathToTextView;
  }

  public static Map<String, String> createLibraryButton() {
    Map<String, String> xpathToButton = new HashMap<>();
    xpathToButton.put("removeAccountButton",
        "//android.widget.Button[@text='Remove account' or @text='REMOVE ACCOUNT']");
    xpathToButton.put("removeAccountButtonConfirmationPopup",
        "//android.widget.Button[@resource-id='android:id/button1' and (@text='Remove account' or @text='REMOVE ACCOUNT')]");

    return xpathToButton;
  }
}
