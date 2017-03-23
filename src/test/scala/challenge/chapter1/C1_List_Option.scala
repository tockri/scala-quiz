package challenge.chapter1

import question.chapter1.Q1_List_Option

/**
  *
  */
class C1_List_Option extends Q1_List_Option {

  /**
    * Listを反転させるreverseを、List#reverseを使わず、foldLeftを使って作りましょう
    */
  override def reverse[A](list: List[A]) = ???

  /**
    * 2つのListを結合したListを返しましょう
    */
  override def cross[A, B](aList:List[A], bList:List[B]): List[(A, B)] = ???

  /**
    * Listのidx番目にelemを挿入したListを返しましょう
    * idxがListの長さ以上の場合、末尾に追加します。
    * idxが負の場合、末尾から数えた場所に追加します。
    * idxが-(Listの長さ)以下の場合、先頭に追加します。
    */
  override def insertAt[A](elem: A, idx: Int, list: List[A]) = ???

  /**
    * 要素のOptionのうち、Someのものだけを返すメソッドを作りましょう
    */
  override def removeOption[A](optList:List[Option[A]]): List[A] = ???

  /**
    * 要素のOptionのうち、Someである要素数を返すメソッドを作りましょう
    */
  override def countSome[A](optList: List[Option[A]]) = ???

  /**
    * 要素のOptionのうち、Someである全ての要素を足し合わせるメソッドを作りましょう
    */
  override def sumSome(optList:List[Option[Int]]):Int = ???

}
