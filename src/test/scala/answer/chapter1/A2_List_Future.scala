package answer.chapter1

import question.chapter1.Q2_List_Future

import scala.collection.mutable.MutableList
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.Try


/**
  *
  */
class A2_List_Future extends Q2_List_Future {

  /**
    * Futureを返す関数を順番に実行して結果のFutureを返しましょう。
    * func1の返り値をfunc2の引数として実行します。
    */
  override def sequential[A, B, C](func1: (A) => Future[B], func2: (B) => Future[C]) =
    a => func1(a).flatMap(func2)
  /* for文を使うとこうなります（flatMapの糖衣構文なんだけど却って長くなる）
    a =>
    for {
      b1 <- func1(a)
      c1 <- func2(b1)
    } yield c1
    */

  /**
    * Futureを返す関数のListを順番に実行して、それぞれの実行結果をListで返しましょう。
    */
  override def sequential2[A, B](funcs: List[A => Future[B]]) = { (args: List[A]) =>
    funcs.zip(args).foldLeft(Future(List[B]())) { case (fResults, (func, arg)) =>
      fResults.flatMap{results =>
        func(arg).map(r => results :+ r)
      }
    }
  }

  /**
    * Futureを返す関数を並列に実行して、それぞれの結果を返しましょう。
    */
  override def parallel[A, B, C](func1: (A) => Future[B], func2: (A) => Future[C]) = {(a1:A, a2:A) =>
    val f1 = func1(a1)
    val f2 = func2(a2)
    for {
      b <- f1
      c <- f2
    } yield (b, c)
  }

  /**
    * Futureを返す関数のListを並列に実行して、それぞれの実行結果をListで返しましょう。
    */
  override def parallel2[A, B](funcs: List[(A) => Future[B]]) = { args: List[A] =>
    val results = funcs.zip(args).map{case (func, arg) =>
      func(arg)
    }
    Future.sequence(results)
  }

}
