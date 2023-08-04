package steps.desktop.pdf;

import config.DriverUtil;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.When;
import java.io.IOException;
import pages.pdf.PdfMainPage;

public class PdfMainSteps {
  private PdfMainPage pdfMainPage;
  public static final String CONTEXT = "pdf main page";

  public PdfMainSteps() {
    pdfMainPage = new PdfMainPage(DriverUtil.getDriver());
  }

  @When("the user sees following {string} on " + PdfMainSteps.CONTEXT)
  public void the_user_sees_text_string_on_pdf_main_page(String infoType, DataTable dataTable)
      throws IOException {

    pdfMainPage.verifyInformationOnPdfPage(infoType, dataTable.asList());
  }
}
