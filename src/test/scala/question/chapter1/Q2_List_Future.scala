package question.chapter1

import common.FutureUtil
import org.scalatest.{DiagrammedAssertions, FlatSpec}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

/**
  * ListとFutureの練習
  * 問題
  */
abstract class Q2_List_Future extends FlatSpec with DiagrammedAssertions {

  implicit val executionContext = scala.concurrent.ExecutionContext.Implicits.global

  /**
    * Futureを返す関数を順番に実行して結果のFutureを返しましょう。
    * func1の返り値をfunc2の引数として実行します。
    */
  def sequencial[A, B, C](func1:A => Future[B], func2:B => Future[C]): A => Future[C]

  "sequencial" should "execute functions in sequence" in {
    def func1(num:Int) = FutureUtil.messageInFuture(s"${num}:func1")
    def func2(str:String) = FutureUtil.messageInFuture(str + ":func2")
    val begin = System.currentTimeMillis()
    var time = 0L
    val result = sequencial(func1, func2)(10)
    result.onComplete{t =>
      val end = System.currentTimeMillis()
      time = end - begin
      assert(t.isSuccess)
    }
    val msg = Await.result(result, Duration("10sec"))
    println(s"[sequencial] time: ${time}msec.")
    assert(time >= 2000)
    assert(msg == "10:func1:func2")
  }

  /**
    * Futureを返す関数のListを順番に実行して、それぞれの実行結果をListで返しましょう。
    */
  def sequencial2[A, B](funcs:List[A=>Future[B]]): List[A] => Future[List[B]]

  "sequencial2" should "execute each function in a list in sequence" in {
    def func1(num:Int) = FutureUtil.messageInFuture(s"${num}:func1", num)
    val begin = System.currentTimeMillis()
    var time = 0L

    val funcs = List(func1(_), func1(_), func1(_), func1(_))
    val future = sequencial2(funcs)(List(1000, 1200, 1500, 2000))
    future.onComplete{t =>
      val end = System.currentTimeMillis()
      time = end - begin
      assert(t.isSuccess)
    }
    val msgs = Await.result(future, Duration("10sec"))
    println(s"[sequencial2] time: ${time}msec.")
    assert(time >= 5700)
    assert(msgs == List("1000:func1", "1200:func1", "1500:func1", "2000:func1"))
  }

  /**
    * Futureを返す関数を並列に実行して、それぞれの結果を返しましょう。
    */
  def parallel[A, B, C](func1:A => Future[B], func2:A => Future[C]): (A, A) => Future[(B, C)]

  "parallel" should "execute functions in parallel" in {
    def func1(num:Int) = FutureUtil.messageInFuture(s"${num}:func1")
    def func2(num:Int) = FutureUtil.messageInFuture(s"${num}:func2")
    val future = parallel(func1, func2)(30, 50)
    val begin = System.currentTimeMillis()
    var time = 0L
    future.onComplete{t =>
      val end = System.currentTimeMillis()
      time = end - begin
      assert(t.isSuccess)
    }
    Await.result(future, Duration("10sec")) match {
      case (b, c) =>
        assert(b == "30:func1")
        assert(c == "50:func2")
        println(s"[parallel] time: ${time}msec.")
        assert(time >= 1000)
        assert(time < 1100)
      case _ => fail("unknown result")
    }
  }

  /**
    * Futureを返す関数のListを並列に実行して、それぞれの実行結果をListで返しましょう。
    */
  def parallel2[A, B](funcs:List[A=>Future[B]]): List[A] => Future[List[B]]

  "parallel2" should "execute each function in a list in parallel" in {
    def func1(num:Int) = FutureUtil.messageInFuture(s"${num}:func1", num)
    val funcs = List(func1(_), func1(_), func1(_), func1(_))
    val begin = System.currentTimeMillis()
    var time = 0L
    val future = parallel2(funcs)(List(1000, 1200, 1500, 2000))
    future.onComplete{t =>
      val end = System.currentTimeMillis()
      time = end - begin
      assert(t.isSuccess)
    }
    val result = Await.result(future, Duration("10sec"))
    assert(result == List("1000:func1", "1200:func1", "1500:func1", "2000:func1"))
    println(s"[parallel2] time: ${time}msec.")
    assert(time >= 2000)
    assert(time < 2100)

  }



}


