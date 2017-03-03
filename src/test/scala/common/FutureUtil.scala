package common

import scala.concurrent.Future

/**
  *
  */
object FutureUtil {
  import scala.concurrent.ExecutionContext.Implicits.global
  /**
    * get message in future
    */
  def messageInFuture(message:String): Future[String] = {
    Future {
      Thread.sleep(1000)
      s"message in future : ${message}"
    }
  }
}
