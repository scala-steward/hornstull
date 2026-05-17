package hornstull

import ox.*
import scala.concurrent.duration.*

object Main:
  def computation1: Int =
    sleep(2.seconds); 1
  def computation2: String =
    sleep(1.second); "2"
  val result1: (Int, String) = par(computation1, computation2)
