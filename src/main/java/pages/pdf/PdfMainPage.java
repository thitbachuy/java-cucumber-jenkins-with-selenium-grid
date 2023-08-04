package pages.pdf;

import config.BasePage;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.Assert;
import org.openqa.selenium.remote.RemoteWebDriver;


public class PdfMainPage extends BasePage {

  public PdfMainPage(RemoteWebDriver driver) {
    super(driver);
  }

  private static final Logger LOG = LogManager.getLogger(PdfMainPage.class);

  /*
  Gets the url of the pdf document
  Reads the number of pages and asserts for the text
   */
  public void verifyInformationOnPdfPage(String infoType, List<String> informationList)
      throws IOException {
    String pdfPageUrl = threadLocalDriverBasePage.get().getCurrentUrl();
    URL url = new URL(pdfPageUrl);
    PDDocument pdfDocument = PDDocument.load(url.openStream());
    LOG.info("PDF contains \"{}\" pages", pdfDocument.getPages().getCount());
    String pdfText = new PDFTextStripper().getText(pdfDocument);
    LOG.info("Pdf file has content: {}", pdfText);
    informationList.forEach(info -> {
      Assert.assertTrue(pdfText.contains(info));
      LOG.info("Pdf file has {}: {}", infoType, info);
    });
    pdfDocument.close();
  }
}
