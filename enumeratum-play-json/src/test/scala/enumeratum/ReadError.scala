package enumeratum

case class ReadError(errorMessages: Iterable[String], errorArgs: Iterable[String])

object ReadError {
  def onlyMessages(errorMessages: Iterable[String]): ReadError =
    new ReadError(errorMessages, errorArgs = Iterable())
}
