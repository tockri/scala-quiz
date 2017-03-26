package common

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

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

  def waitFor(duration:Duration)(implicit executionContext: ExecutionContext):Unit =
    await(Future(Thread.sleep(duration.toMillis)))

  /**
    * 単純にAwait.resultするだけ
    */
  def await[A](f:Future[A], duration:Duration = Duration.Inf)(implicit executionContext: ExecutionContext):A =
    Await.result(f, Duration.Inf)
}
