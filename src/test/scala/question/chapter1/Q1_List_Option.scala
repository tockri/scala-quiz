package question.chapter1

import org.scalatest.{DiagrammedAssertions, FlatSpec}

/**
  * ListとOptionの練習
  * 問題
  */
abstract class Q1_List_Option extends FlatSpec with DiagrammedAssertions {

  /**
    * Listから0番目および偶数番目の要素を取りだして返しましょう
    */
  def pickBy2[A](list: List[A]): List[A]

  "pickBy2" should "pick elements in head and even index" in {
    assert(pickBy2(List(1, 2, 3, 4)) == List(1, 3))
    assert(pickBy2(List(1, 2, 3, 4, 5)) == List(1, 3, 5))
    assert(pickBy2(List()) == List())
    assert(pickBy2(List(1)) == List(1))
  }

  /**
    * Listを反転させるreverseを、List#reverseを使わず、foldLeftを使って作りましょう
    */
  def reverse[A](list: List[A]): List[A]

  "reverse" should "reverse elements" in {
    assert(reverse(List(1, 2, 3, 4)) == List(4, 3, 2, 1))
    assert(reverse(List()) == List())
    assert(reverse(List(1)) == List(1))
  }

  /**
    * 2つのListの要素同士をTupleにしたListを返しましょう
    */
  def cross[A, B](aList: List[A], bList: List[B]): List[(A, B)]

  "cross" should "combine 2 lists" in {
    val a = List(10, 8, 9, 5)
    val b = List("foo", "bar", "baz", "dab")
    assert(cross(a, b) == List((10, "foo"), (8, "bar"), (9, "baz"), (5, "dab")))

    val c = List(20, 30, 40, 0)
    val d = List("Alice", "Bob", "Cris")
    try {
      cross(c, d)
      fail("should fail")
    } catch {
      case ex: Exception => println(ex)
    }
  }

  /**
    * Listのidx番目にelemを挿入したListを返しましょう
    * idxがListの長さ以上の場合、末尾に追加します。
    * idxが負の場合、末尾から数えた場所に追加します。
    * idxが-(Listの長さ)以下の場合、先頭に追加します。
    */
  def insertAt[A](elem: A, idx: Int, list: List[A]): List[A]

  "insertAt" should "insert elem at index" in {
    val l = List('a, 'b, 'c, 'd)
    assert(insertAt('f, 0, l) == List('f, 'a, 'b, 'c, 'd))
    assert(insertAt('f, 1, l) == List('a, 'f, 'b, 'c, 'd))
    assert(insertAt('f, 4, l) == List('a, 'b, 'c, 'd, 'f))
    assert(insertAt('f, 5, l) == List('a, 'b, 'c, 'd, 'f))
    assert(insertAt('f, -1, l) == List('a, 'b, 'c, 'd, 'f))
    assert(insertAt('f, -3, l) == List('a, 'b, 'f, 'c, 'd))
    assert(insertAt('f, -5, l) == List('f, 'a, 'b, 'c, 'd))
    assert(insertAt('f, -6, l) == List('f, 'a, 'b, 'c, 'd))
  }


  /**
    * 要素のOptionのうち、Someのものだけを返すメソッドを作りましょう
    */
  def removeOption[A](optList: List[Option[A]]): List[A]

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
  def countSome[A](optList: List[Option[A]]): Int

  "countSome" should "count Some(_)" in {
    assert(countSome(List(Some("a"), Some("b"), None, None, Some("c"))) == 3)
    assert(countSome(List()) == 0)
    assert(countSome(List(Some(1))) == 1)
    assert(countSome(List(None, None, None)) == 0)
  }


  /**
    * 要素のOptionのうち、Someである全ての要素を足し合わせるメソッドを作りましょう
    */
  def sumSome(optList: List[Option[Int]]): Int

  /**
    * addAllのテスト
    */
  "addAll" should "add all List[Option[A]]" in {
    assert(sumSome(List(Some(1), Some(2), None, Some(3))) == 6)
    assert(sumSome(List()) == 0)
    assert(sumSome(List(None, None, Some(1))) == 1)
  }


}


