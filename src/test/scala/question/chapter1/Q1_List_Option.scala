package question.chapter1

import org.scalatest.{FlatSpec, MustMatchers}

/**
  * ListとOptionの練習
  *
  */
class Q1_List_Option extends FlatSpec with MustMatchers {
  /**
    * 要素のOptionのうち、Someのものだけを返すメソッドを作りましょう
    */
  def removeOption[A](optList:List[Option[A]]): List[A] = ???

  /**
    * removeOptionのテスト
    */
  it should "convert List[Option[A]] to List[A]" in {
    assert(removeOption(List(Some("a"), Some("b"), Some("c"))) == List("a", "b", "c"))
    assert(removeOption(List(Some(1), None, Some(2), Some(3), None)) == List(1, 2, 3))
    assert(removeOption(List()) == List())
    assert(removeOption(List(None)) == List())
  }


  /**
    * 要素のOptionのうち、Someである全ての要素を足し合わせるメソッドを作りましょう
    */
  def addAll(optList:List[Option[Int]]):Int = ???

  /**
    * addAllのテスト
    */
  it should "add all List[Option[A]]" in {
    assert(addAll(List(Some(1), Some(2), None, Some(3))) == 6)
    assert(addAll(List()) == 0)
    assert(addAll(List(None, None, Some(1))) == 1)
  }
}


