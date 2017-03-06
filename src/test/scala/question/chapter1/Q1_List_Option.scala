package question.chapter1

import org.scalatest.{FlatSpec, MustMatchers}

/**
  * ListとOptionの練習
  *
  */
abstract class Q1_List_Option extends FlatSpec with MustMatchers {

  /**
    * Listを反転させるreverseを、List#reverseを使わず、foldLeftを使って作りましょう
    */
  def reverse[A](list:List[A]): List[A]

  "reverse" should "reverse elements" in {
    assert(reverse(List(1, 2, 3, 4)) == List(4, 3, 2, 1))
    assert(reverse(List()) == List())
    assert(reverse(List(1)) == List(1))
  }


  /**
    * 要素のOptionのうち、Someのものだけを返すメソッドを作りましょう
    */
  def removeOption[A](optList:List[Option[A]]): List[A]

  /**
    * removeOptionのテスト
    */
  "removeOption" should "convert List[Option[A]] to List[A]" in {
    assert(removeOption(List(Some("a"), Some("b"), Some("c"))) == List("a", "b", "c"))
    assert(removeOption(List(Some(1), None, Some(2), Some(3), None)) == List(1, 2, 3))
    assert(removeOption(List()) == List())
    assert(removeOption(List(None)) == List())
  }

  /**
    * 要素のOptionのうち、Someである要素数を返すメソッドを作りましょう
    */
  def countSome[A](optList:List[Option[A]]):Int

  "countSome" should "count Some(_)" in {
    assert(countSome(List(Some("a"), Some("b"), None, None, Some("c"))) == 3)
    countSome(List()) mustBe 0
    countSome(List(Some(1))) mustBe 1
    countSome(List(None, None, None)) mustBe 0
  }


  /**
    * 要素のOptionのうち、Someである全ての要素を足し合わせるメソッドを作りましょう
    */
  def sumSome(optList:List[Option[Int]]):Int

  /**
    * addAllのテスト
    */
  "addAll" should "add all List[Option[A]]" in {
    assert(sumSome(List(Some(1), Some(2), None, Some(3))) == 6)
    assert(sumSome(List()) == 0)
    assert(sumSome(List(None, None, Some(1))) == 1)
  }


}


