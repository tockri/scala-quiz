package common

import scala.concurrent.{ExecutionContext, Future}

/**
  *
  */
trait FutureSupport {
  /**
    * get message in future
    */
  def messageInFuture(message:String, sleepMs:Int = 1000)(implicit executionContext: ExecutionContext): Future[String] = {
    Future {
      Thread.sleep(sleepMs)
      message
    }
  }
}
