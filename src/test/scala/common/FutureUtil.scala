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
  def messageInFuture(message:String, sleepMs:Int = 1000): Future[String] = {
    Future {
      Thread.sleep(sleepMs)
      message
    }
  }
}
