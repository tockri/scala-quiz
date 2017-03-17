package answer.chapter1

import question.chapter1.Q2_List_Future

import scala.concurrent.Future

/**
  *
  */
class A2_List_Future extends Q2_List_Future {

  override def sequencial[A, B, C](func1: (A) => Future[B], func2: (B) => Future[C]) = a => func1(a).flatMap(func2)

  override def sequencial2[A, B](funcs: List[(A) => Future[B]]) = {args =>
    assert(funcs.length <= args.length)
    val argsMap = args.zipWithIndex
    val results = funcs.zipWithIndex.map{case (f, idx) =>
      f(argsMap(idx)._1)
    }
    Future.sequence(results)
  }

}
