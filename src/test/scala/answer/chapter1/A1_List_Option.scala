package answer.chapter1

import question.chapter1.Q1_List_Option

/**
  *
  */
class A1_List_Option extends Q1_List_Option {

  /**
    * Listを反転させるreverseを、List#reverseを使わず、foldLeftを使って作りましょう
    */
  override def reverse[A](list: List[A]) = list.foldLeft(List[A]())((l, e) => e :: l)

  /**
    * 2つのListを結合したListを返しましょう
    */
  override def cross[A, B](aList:List[A], bList:List[B]): List[(A, B)] = aList.zip(bList)

  /**
    * 要素のOptionのうち、Someのものだけを返すメソッドを作りましょう
    */
  override def removeOption[A](optList:List[Option[A]]): List[A] = optList.flatten

  /**
    * 要素のOptionのうち、Someである要素数を返すメソッドを作りましょう
    */
  override def countSome[A](optList: List[Option[A]]) = optList.count(_.isDefined)

  /**
    * 要素のOptionのうち、Someである全ての要素を足し合わせるメソッドを作りましょう
    */
  override def sumSome(optList:List[Option[Int]]):Int = removeOption(optList).sum

}
