package question.chapter1

import common.DBFixture
import infra.MemberDao
import org.scalatest.{DiagrammedAssertions, FlatSpec}
import scalikejdbc.DB

/**
  * scalikejdbcを使ったコードに関する問題
  */
class Q3_DB extends FlatSpec with DiagrammedAssertions {

  "select" should "select rows" in {
    DBFixture
    DB readOnly { implicit session =>
      val members = MemberDao.all ()
      println (members)
      val m = MemberDao.find (1)
      println (m)
    }
  }
}
