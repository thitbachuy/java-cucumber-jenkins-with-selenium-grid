package locators.android;

import java.util.HashMap;
import java.util.Map;

public class DatePickerLocators {
    public static Map<String, String> createLibraryButton() {
        Map<String, String> xpathToButton = new HashMap<>();
        xpathToButton.put("header year", "//*[@resource-id='android:id/date_picker_header_year']");
        xpathToButton.put("previous month", "//android.widget.ImageButton[@content-desc='Previous month']");
        xpathToButton.put("next month", "//android.widget.ImageButton[@content-desc='Next month']");
        xpathToButton.put("set", "//android.widget.Button[@resource-id='android:id/button1' and @text='SET']");
        return xpathToButton;
    }

    public static Map<String, String> createLibraryElement() {
        Map<String, String> xpathToElement = new HashMap<>();
        xpathToElement.put("date picker", "//android.widget.DatePicker[@resource-id='android:id/datePicker']");
        xpathToElement.put("year picker", "//*[@resource-id='android:id/date_picker_year_picker']");
        xpathToElement.put("day picker", "//*[@resource-id='android:id/date_picker_day_picker']");
        return xpathToElement;
    }
}
