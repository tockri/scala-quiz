package question.chapter1

import common.{DBFixture, FutureSupport}
import infra.GroupMemberDao
import entity._
import org.scalatest.{DiagrammedAssertions, FlatSpec}
import scalikejdbc.{DB, DBSession}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

/**
  * DBのトランザクションを使ったコードに関する問題
  */
abstract class Q3_DB_Repository extends FlatSpec with DiagrammedAssertions with FutureSupport {
  /**
    * memberのレポジトリクラスを実装しましょう
    */
  trait MemberRepository {
    /**
      * Memberを検索してFutureで返します。
      * infra.MemberDaoを利用してください。
      * 見つからない場合はNoneを返します。
      */
    def find(id:MemberId)(implicit session:DBSession, context:ExecutionContext):Future[Option[Member]]
    /**
      * MemberをDB上で更新して返します。
      * idが0の場合は新しいMemberをDBに登録し、新しいidを持ったインスタンスを返します。
      */
    def save(m:Member)(implicit session:DBSession, context:ExecutionContext):Future[Member]
  }

  /**
    * groupのレポジトリクラスを実装しましょう
    */
  trait GroupRepository {
    /**
      * Groupを検索してFutureで返します。
      * infra.GroupDaoを利用してください。
      * 見つからない場合はNoneを返します。
      */
    def find(id:GroupId)(implicit session:DBSession, context:ExecutionContext):Future[Option[Group]]
    /**
      * Groupを検索してFutureで返します。
      * SQLのJOINは使わず、MemberDao, GroupDao, GroupMemberDaoを利用してください。
      * 見つからない場合はNoneを返します。
      */
    def findWithMembers(id:GroupId)(implicit session:DBSession, context:ExecutionContext):Future[Option[GroupWithMembers]]
    /**
      * GroupをDB上で更新して返します。
      * idが0の場合は新しいGroupをDBに登録し、新しいidを持ったインスタンスを返します。
      */
    def save(g:Group)(implicit session:DBSession, context:ExecutionContext):Future[Group]
  }

  /**
    * groupに関して内部でトランザクションを持つ処理を行うサービスクラスを実装しましょう
    */
  trait GroupApplicationService {
    /**
      * メンバーを指定して新しいグループを作成し、作成したグループを返します。
      * membersの要素のMemberはすでに登録済みでなければなりません。
      * （登録済みでないMemberを指定した場合IllegalArgumentExceptionを投げる）
      * 内部でトランザクションを持つため、DB.futureLocalTx を使用して実装します。
      */
    def createNewGroup(g:GroupWithMembers)(implicit context:ExecutionContext): Future[GroupWithMembers]
    /**
      * Groupを保存しつつ参加者を一括で編集します。
      * 既存のレコードを全削除して全追加するのではなく、余分なレコードだけ削除して、足りないレコードだけ
      * 追加するように実装してください。
      * 内部でトランザクションを持つため、DB.futureLocalTx を使用して実装します。
      */
    def updateGroupWithMembers(g:GroupWithMembers)(implicit context:ExecutionContext): Future[GroupWithMembers]
  }

  /**
    * MemberRepositoryの実装インスタンスを返します
    */
  def getMemberRepository():MemberRepository

  /**
    * GroupRespotiroryの実装インスタンスを返します
    */
  def getGroupRepository():GroupRepository

  /**
    * GroupApplicationServiceの実装インスタンスを返します
    */
  def getGroupApplicationService():GroupApplicationService

  DBFixture

  import scala.concurrent.ExecutionContext.Implicits.global
  val mr = getMemberRepository()
  val gr = getGroupRepository()

  "GroupRepository.find" should "find a group" in {
    DB readOnly { implicit session =>
      val f = gr.find(GroupId(1))
      f.onComplete{t =>
        assert(t.isSuccess)
      }
      val result = await(f)
      assert(result.isDefined)
      result.map {g =>
        assert(g.id.value == 1)
        assert(g.name == "Backlog team")
      }
    }
  }
  it should "results none when not found" in {
    DB readOnly {implicit session =>
      val f = gr.find(GroupId(99))
      f.onComplete{t =>
        assert(t.isSuccess)
      }
      val result = await(f)
      assert(result.isEmpty)
    }
  }

  "GroupRepository.findWithMembers" should "find a group with its members" in {
    DB.readOnly{implicit session =>
      val f = gr.findWithMembers(GroupId(1))
      f.onComplete{t =>
        assert(t.isSuccess)
      }
      val result = await(f)
      assert(result.isDefined)
      result.map {g =>
        assert(g.id.value == 1)
        assert(g.name == "Backlog team")
        assert(g.members.length == 2)
        assert(g.members(0).name == "Alice")
        assert(g.members(1).name == "Bob")
      }

    }
  }

  "GroupRepsitory.save" should "update a group" in {
    DB.localTx{ implicit session =>
      // update
      val f = for {
        om <- gr.find(GroupId(1))
        _ <- {
          val g = om.get
          val ug = new Group(g.id, "Updated name", g.createdAt)
          gr.save(ug)
        }
        ug <- gr.find(GroupId(1))
      } yield {
        ug.getOrElse(fail())
      }
      f.onComplete(t => assert(t.isSuccess))
      val m = await(f)
      assert(m.id.value == 1)
      assert(m.name == "Updated name")
    }
  }
  it should "insert a group" in {
    DB.localTx{implicit session =>
      val f = for {
        m <- gr.save(Group("new group"))
      } yield m
      f.onComplete(t => assert(t.isSuccess))
      val m = await(f)
      assert(m.id.value == 3)
      assert(m.name == "new group")
    }
  }  

  "MemberRepository.find" should "find a member" in {
    DB readOnly { implicit session =>
      val fResult = mr.find(MemberId(1))
      fResult.onComplete{t =>
        assert(t.isSuccess)
      }
      val result = Await.result(fResult, Duration.Inf)
      assert(result.isDefined)
      result.map {m =>
        assert(m.id.value == 1)
        assert(m.name == "Alice")
      }
    }
  }
  it should "results none when not found" in {
    DB readOnly {implicit session =>
      val fResult = mr.find(MemberId(99))
      fResult.onComplete{t =>
        assert(t.isSuccess)
      }
      val result = Await.result(fResult, Duration.Inf)
      assert(result.isEmpty)
    }
  }
  "MemberRepsitory.save" should "update a member" in {
    DB.localTx{ implicit session =>
      // update
      val f = for {
        om <- mr.find(MemberId(1))
        _ <- {
          val m = om.get
          val um = new Member(m.id, "Updated name", m.createdAt)
          mr.save(um)
        }
        um <- mr.find(MemberId(1))
      } yield {
        um.getOrElse(fail())
      }
      f.onComplete(t => assert(t.isSuccess))
      val m = Await.result(f, Duration.Inf)
      assert(m.id.value == 1)
      assert(m.name == "Updated name")
    }
  }
  it should "insert a member" in {
    DB.localTx{implicit session =>
      val f = for {
        m <- mr.save(Member("new member"))
      } yield m
      f.onComplete(t => assert(t.isSuccess))
      val m = await(f)
      assert(m.id.value == 3)
      assert(m.name == "new member")
    }
  }
  "GroupApplicationService.createNewGroup" should "create a new group with members" in {
    val s = getGroupApplicationService()
    val mr = getMemberRepository()
    val gr = getGroupRepository()
    val members = DB.localTx{implicit session =>
      await(Future.sequence(List(mr.find(MemberId(1)), mr.find(MemberId(2))))).flatten
    }
    val ng = await(s.createNewGroup(GroupWithMembers("new group with members", members)))
    val ng2 = DB.localTx{ implicit session =>
      await(gr.findWithMembers(ng.id)).getOrElse(fail())
    }
    assert(ng.id.value == 4)
    assert(ng.members == ng2.members)
    assert(ng.members.map(_.id.value) == List(1, 2))
    assert(ng.id == ng2.id)
    assert(ng.name == ng2.name)
  }
  "GroupRepository.updateMembers" should "update members" in {
    val mr = getMemberRepository()
    val gr = getGroupRepository()
    var gs = getGroupApplicationService()
    val sorter = (a:GroupMember, b:GroupMember) => a.memberId.value < b.memberId.value

    val gm = DB.readOnly { implicit s =>
      await(gr.findWithMembers(GroupId(4)))
    }.getOrElse(fail())
    val gms1 = DB.readOnly{implicit s =>
      GroupMemberDao.findByGroupId(GroupId(4))
    }.sortWith(sorter)
    assert(gm.members.map(_.id.value) == List(1, 2))
    val members = DB.readOnly{implicit s =>
      List(
        await(mr.find(MemberId(2))),
        await(mr.find(MemberId(3)))
      ).flatten
    }
    waitFor(Duration("1 sec"))
    val result = await(gs.updateGroupWithMembers(GroupWithMembers(gm.copy(name = "updated3"), members)))
    val check = DB.readOnly { implicit s =>
      await(gr.findWithMembers(GroupId(4)))
    }.getOrElse(fail())
    val gms2 = DB.readOnly{implicit s =>
      GroupMemberDao.findByGroupId(GroupId(4))
    }.sortWith(sorter)
    assert(result.id.value == 4)
    assert(result.members == check.members)
    assert(result.members.map(_.name) == check.members.map(_.name))
    assert(result.name == "updated3")
    assert(result.name == check.name)
    assert(result.members.map(_.id.value) == List(2, 3))

    assert(gms1(0).memberId.value == 1)
    assert(gms1(1).memberId.value == 2)
    assert(gms2(0).memberId.value == 2)
    assert(gms2(1).memberId.value == 3)
    assert(gms1(1).createdAt == gms2(0).createdAt, "updateGroupMembersでgroup_memberレコードを全削除してはいけません")
    assert(gms1(0).createdAt.isBefore(gms2(1).createdAt))
  }

}
