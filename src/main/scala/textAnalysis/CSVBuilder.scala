import scala.io.Source
import java.io.PrintWriter
import java.io.File

object CSVBuilder {

  /**
   * List of all the vote text files paths for a legislature
   *
   * @param leg legislature
   * @return all the vote text files paths for a legislature
   */
  def allScrutinTextPaths(leg: Int): List[String] = {
    def findTxtFiles(root: File): List[String] =
      if (root.isFile)
        if (root.getName endsWith "txt")
          List(root.getCanonicalPath)
        else
          List()
      else
        (root.listFiles).toList flatMap findTxtFiles

      val root = new File("./scrutins/" + leg)

      findTxtFiles(root)
  }

  // Modifies a vote path to a csv path
  private def modifyPath(oldPath: String): (String, String) = {
    val fields = oldPath split "/"
    val fileName = fields.last.replace(".txt", ".csv")

    ((fields.init mkString "/").replace("scrutins", "csv"),
    fileName)
  }

  /**
   * Builds a CSV file from a vote text file
   *
   * @param path path of the vote text file
   */
  def buildCSVFileFrom(path: String, legislature: Int): Unit = {
    val text = (Source fromFile path).mkString
    val vs = new VoteSeparator(text)
    val votes = vs.votesTexts map (new VoteBuilder(_, legislature, vs.date).build)
    val csv = (votes map (_.toCSV)) mkString "\n"
    val (newPath, newFileName) = this modifyPath path
    val dirs = new File(newPath)

    if (!dirs.exists)
      dirs.mkdirs

    val pw = new PrintWriter(new File(newPath + "/" + newFileName))

    pw write csv

    pw.close
  }

  /**
   * Converts all the text files to CSV files for a legislature
   *
   * @param leg legislature
   */
  def buildAllCSVFor(leg: Int): Unit = {
    val files = this allScrutinTextPaths leg

    files foreach (file => buildCSVFileFrom(file, leg))
  }

}
