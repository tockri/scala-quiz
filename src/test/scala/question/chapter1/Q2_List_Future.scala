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
    val result = sequencial(func1, func2)(10)
    result.onComplete{t =>
      assert(t.isSuccess)
      t.map{msg =>
        assert(msg == "10:func1:func2")
      }
    }
    Await.result(result, Duration("10sec"))
  }

  /**
    * Futureを返す関数のListを順番に実行して、それぞれの実行結果をListで返しましょう。
    */
  def sequencial2[A, B](funcs:List[A=>Future[B]]): List[A] => Future[List[B]]

  "sequencial2" should "execute each function in a list in sequence" in {
    def func1(num:Int) = FutureUtil.messageInFuture(s"${num}:func1")

    val funcs = List(func1(_), func1(_), func1(_), func1(_))
    val result = sequencial2(funcs)(List(1, 2, 3, 4))
    result.onComplete{t =>
      assert(t.isSuccess)
      t.map{msgs =>
        assert(msgs == List("1:func1", "2:func1", "3:func1", "4:func1"))
      }
    }
    Await.result(result, Duration("10sec"))

  }

}


