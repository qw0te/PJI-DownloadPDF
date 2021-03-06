import sys.process._
import java.net.URL
import java.io.File

/**
 * Object to download a PDF from the french National Assembly website :
 * http://archives.assemblee-nationale.fr/X/cri
 * (with X the legislature number between 1 and 11)
 *
 * @author Quentin Baert
 */
object PDFDownloader {

  // List of the URLs sorted and grouped by 100
  private val groupedURLs =
    ((URLManager.pdfURLs sortWith (_ < _)) grouped 100).toList

  /**
   * Gives the local path of the PDF from its URL
   *
   * @param url
   *          url to convert in local path
   * @return local path of the PDF from its URL
   */
  def pdfPath(url: String): String = {
    val parts: List[String] = (url split '/').toList
    val legislature = parts(3)
    val part = parts(5)
    val years = part.substring(0, part lastIndexOf '-')
    val kind = part.substring((part lastIndexOf '-') + 1, part.length)

    "./cri/" + legislature + "/" + years + "/" + kind + "/"
  }

  /**
   * Downloads a PDF from the given URL
   *
   * @param url
   *          URL of the PDF
   */
  def downloadPDF(url: String): Unit = {
    val name = url substring ((url lastIndexOf '/') + 1)
    val path = this.pdfPath(url)
    val pathDirs = new File(path)
    val pdfURL = new URL(url)

    if (pdfURL.openConnection.getContentLength != -1) {
      if (!pathDirs.exists)
        pathDirs.mkdirs

      pdfURL #> new File(path + name) !!
    }
  }

  // Download a PDF aor print a error trace
  private def downloadOrPrint(pdfURL: String): Unit = try {
    this downloadPDF pdfURL
    println("Downloaded : " + pdfURL)
  } catch {
    case e: Exception => println("File " + pdfURL + " not found. Download aborted.")
  }

  /**
   * Dowloads all the PDFs
   */
  def downloadAll: Unit = URLManager.pdfURLs foreach downloadOrPrint

  /**
   * Downloads all the PDF of a given legislature
   *
   * @param leg
   *    number of the legislature
   */
  def downloadLeg(leg: Int): Unit = {
    val legNum = "/" + leg + "/"
    val urls = URLManager.pdfURLs filter (_ contains legNum)

    urls foreach downloadOrPrint
   }

  /**
   * Downloads the nth group of 100 PDFs
   *
   * @param n
   *          number of the group of PDF to download
   */
  def downloadGroupNb(n: Int): Unit =
    this.groupedURLs(n) foreach downloadOrPrint

}
