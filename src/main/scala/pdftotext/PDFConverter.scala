import java.io.File
import java.io.PrintWriter

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.util.PDFTextStripper

/**
 * Object to convert local PDF files in local text files
 *
 * @author Quentin Baert
 */
object PDFConverter {

  // Create a local text path from a local pdf path
  private def txtFilePath(path: String): String = {
    val pathWithoutPrefix = path substring ((path indexOf "cri/") + 4)
    val pathWithoutSuffix =
      pathWithoutPrefix.substring(0, (pathWithoutPrefix lastIndexOf '/') + 1)

    "./critxt/" + pathWithoutSuffix
  }

  /**
   * Convert a PDF file to a text file from its local path
   *
   * @param path
   *          path of the PDF file
   */
  def convert(path: String): Unit = {
    val pdfFile = new File(path)

    if (!pdfFile.isDirectory && pdfFile.exists) {
      // To read the PDF document
      val doc = PDDocument.load(pdfFile)
      val textStripper = new PDFTextStripper()
      val pdfText = textStripper.getText(doc)

      doc.close

      // To write the text file
      val nameTxt = (path substring ((path lastIndexOf '/') + 1)).replace(".pdf", ".txt")
      val pathTxt = this.txtFilePath(path)
      val pathDirs = new File(pathTxt)

      if (!pathDirs.exists)
        pathDirs.mkdirs

      val pw = new PrintWriter(new File(pathTxt + nameTxt), "UTF-8")

      pw.write(pdfText)
      pw.close
    }
    else
      throw new IllegalArgumentException("File do not exists")
  }

  /**
   * List of all the local PDFs paths
   */
  val allLocalPDFsPaths: List[String] = {
    val urls = URLManager.pdfURLs

    def urlToLocalPath(url: String): String = {
      val name = url substring ((url lastIndexOf '/') + 1)

      PDFDownloader.pdfPath(url) + name
    }

    (urls map urlToLocalPath).toList
  }

  /**
   * Convert all the local PDF files to text files
   */
  def convertAll: Unit = {
    def convertOrPrint(path: String): Unit = try {
      this.convert(path)
      println("Converted : " + path)
    } catch {
      case e: Exception => println("File " + path + " not found. Conversion aborted.")
    }

    this.allLocalPDFsPaths foreach convertOrPrint
  }

}
