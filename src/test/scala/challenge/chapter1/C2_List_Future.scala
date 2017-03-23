package challenge.chapter1

import question.chapter1.Q2_List_Future

import scala.concurrent.Future


/**
  *
  */
class C2_List_Future extends Q2_List_Future {

  /**
    * Futureを返す関数を順番に実行して結果のFutureを返しましょう。
    * func1の返り値をfunc2の引数として実行します。
    */
  override def sequential[A, B, C](func1: (A) => Future[B], func2: (B) => Future[C]) = ???

  /**
    * Futureを返す関数のListを順番に実行して、それぞれの実行結果をListで返しましょう。
    */
  override def sequential2[A, B](funcs: List[A => Future[B]]) = ???

  /**
    * Futureを返す関数を並列に実行して、それぞれの結果を返しましょう。
    */
  override def parallel[A, B, C](func1: (A) => Future[B], func2: (A) => Future[C]) = ???

  /**
    * Futureを返す関数のListを並列に実行して、それぞれの実行結果をListで返しましょう。
    */
  override def parallel2[A, B](funcs: List[(A) => Future[B]]) = ???
}
