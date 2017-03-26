package question.chapter1

import common.FutureSupport
import org.scalatest.{DiagrammedAssertions, FlatSpec}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

/**
  * ListとFutureの練習
  * 問題
  */
abstract class Q2_List_Future extends FlatSpec with DiagrammedAssertions with FutureSupport {

  implicit val executionContext = scala.concurrent.ExecutionContext.Implicits.global

  /**
    * Futureを返す関数を順番に実行して結果のFutureを返しましょう。
    * func1の返り値をfunc2の引数として実行します。
    */
  def sequential[A, B, C](func1:A => Future[B], func2:B => Future[C]): A => Future[C]

  "sequential" should "execute functions in sequence" in {
    def func1(num:Int) = messageInFuture(s"${num}:func1")
    def func2(str:String) = messageInFuture(str + ":func2")
    val begin = System.currentTimeMillis()
    val result = sequential(func1, func2)(10)
    result.onComplete{t =>
      assert(t.isSuccess)
    }
    val msg = await(result)
    val time = System.currentTimeMillis() - begin
    println(s"[sequential] time: ${time}ms.")
    assert(time >= 2000)
    assert(time < 2100)
    assert(msg == "10:func1:func2")
  }

  /**
    * Futureを返す関数のListを順番に実行して、それぞれの実行結果をListで返しましょう。
    */
  def sequential2[A, B](funcs:List[A=>Future[B]]): List[A] => Future[List[B]]

  "sequential2" should "execute each function in a list in sequence" in {
    def func1(num:Int) = messageInFuture(s"${num}:func1", num)
    val begin = System.currentTimeMillis()
    val funcs = List(func1(_), func1(_), func1(_), func1(_))
    val future = sequential2(funcs)(List(1000, 2000, 1500, 1200))
    future.onComplete{t =>
      assert(t.isSuccess)
    }
    val msgs = await(future)
    val time = System.currentTimeMillis() - begin
    println(s"[sequential2] time: ${time}ms.")
    assert(time >= 5700)
    assert(time < 5800)
    assert(msgs == List("1000:func1", "2000:func1", "1500:func1", "1200:func1"))
  }

  /**
    * Futureを返す関数を並列に実行して、それぞれの結果を返しましょう。
    */
  def parallel[A, B, C](func1:A => Future[B], func2:A => Future[C]): (A, A) => Future[(B, C)]

  "parallel" should "execute functions in parallel" in {
    def func1(num:Int) = messageInFuture(s"${num}:func1")
    def func2(num:Int) = messageInFuture(s"${num}:func2")
    val future = parallel(func1, func2)(30, 50)
    val begin = System.currentTimeMillis()
    future.onComplete{t =>
      assert(t.isSuccess)
    }
    val (b, c) = await(future)
    val time = System.currentTimeMillis() - begin
    assert(b == "30:func1")
    assert(c == "50:func2")
    println(s"[parallel] time: ${time}ms.")
    assert(time >= 1000)
    assert(time < 1100)
  }

  /**
    * Futureを返す関数のListを並列に実行して、それぞれの実行結果をListで返しましょう。
    */
  def parallel2[A, B](funcs:List[A=>Future[B]]): List[A] => Future[List[B]]

  "parallel2" should "execute each function in a list in parallel" in {
    def func1(num:Int) = messageInFuture(s"${num}:func1", num)
    val funcs = List(func1(_), func1(_), func1(_), func1(_))
    val begin = System.currentTimeMillis()
    val future = parallel2(funcs)(List(1000, 2000, 1500, 1200))
    future.onComplete{t =>
      assert(t.isSuccess)
    }
    val result = await(future)
    val time = System.currentTimeMillis() - begin
    assert(result == List("1000:func1", "2000:func1", "1500:func1", "1200:func1"))
    println(s"[parallel2] time: ${time}ms.")
    assert(time >= 2000)
    assert(time < 2100)
  }



}


