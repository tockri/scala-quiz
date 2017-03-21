package answer.chapter1

import question.chapter1.Q2_List_Future

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}


/**
  *
  */
class A2_List_Future extends Q2_List_Future {

  override def sequencial[A, B, C](func1: (A) => Future[B], func2: (B) => Future[C]) =
    a => func1(a).flatMap(func2)
  /* for文を使うとこうなります（flatMapの糖衣構文なんだけど却って長くなる）
    a =>
    for {
      b1 <- func1(a)
      c1 <- func2(b1)
    } yield c1
    */


  override def sequencial2[A, B](funcs: List[(A) => Future[B]]) = {args:List[A] =>
    if (funcs.length > args.length) {
      throw new IllegalArgumentException("funcs.length > args.length.")
    }
    val argsMap = args.zipWithIndex
    Future(funcs.zipWithIndex.map{case (func, idx) =>
      Await.result(func(argsMap(idx)._1), Duration.Inf)
    })
  }

  override def parallel[A, B, C](func1: (A) => Future[B], func2: (A) => Future[C]) = {(a1:A, a2:A) =>
    val f1 = func1(a1)
    val f2 = func2(a2)
    for {
      b <- f1
      c <- f2
    } yield (b, c)
  }

  override def parallel2[A, B](funcs: List[(A) => Future[B]]) = { args: List[A] =>
    if (funcs.length > args.length) {
      throw new IllegalArgumentException("funcs.length > args.length.")
    }
    val argsMap = args.zipWithIndex
    val results = funcs.zipWithIndex.map { case (f, idx) =>
      f(argsMap(idx)._1)
    }
    Future(results.map(f => Await.result(f, Duration.Inf)))
  }

}
