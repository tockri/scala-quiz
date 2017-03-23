package question.chapter1

import common.{DBFixture, Member}
import org.scalatest.{DiagrammedAssertions, FlatSpec}
import scalikejdbc.DB

/**
  * scalikejdbcを使ったコードに関する問題
  */
class Q3_DB extends FlatSpec with DiagrammedAssertions {

  "select" should "select rows" in {
    DBFixture
    DB readOnly { implicit session =>
      val members = Member.all ()
      println (members)
      val m = Member.find (1)
      println (m)
    }
  }
}
