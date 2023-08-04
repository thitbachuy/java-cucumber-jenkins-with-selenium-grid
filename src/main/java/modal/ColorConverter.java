package modal;

import java.util.HashMap;
import java.util.Map;

public class ColorConverter {

  public static String getColorCodeOf(String color) {
    Map<String, String> createColorCodeLibrary = new HashMap<>();
    createColorCodeLibrary.put("light green", "rgba(69, 198, 90, 1)");
    createColorCodeLibrary.put("dark green", "rgba(46, 132, 74, 1)");
    createColorCodeLibrary.put("green", "rgba(59, 167, 85, 1)");
    createColorCodeLibrary.put("blue", "rgba(1, 68, 134, 1)");
    createColorCodeLibrary.put("dark blue", "rgba(1, 68, 134, 1)");
    createColorCodeLibrary.put("grey", "rgba(243, 243, 243, 1)");
    createColorCodeLibrary.put("light grey", "rgba(243, 243, 243, 1)");
    createColorCodeLibrary.put("red", "rgba(186, 5, 23, 1)");
    createColorCodeLibrary.put("white", "rgba(0, 0, 0, 0)");
    return createColorCodeLibrary.get(color);
  }
}
