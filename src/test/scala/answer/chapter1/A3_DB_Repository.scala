package answer.chapter1

import infra.{GroupDao, GroupMemberDao, MemberDao}
import entity._
import question.chapter1.Q3_DB_Repository
import scalikejdbc.{DB, DBSession}

import scala.concurrent.{ExecutionContext, Future}

/**
  *
  */
class A3_DB_Repository extends Q3_DB_Repository {
  /**
    * memberのレポジトリクラスを実装しましょう
    */
  object MemberRepositoryImpl extends MemberRepository {
    /**
      * Memberを検索してFutureで返します。
      * infra.MemberDaoを利用してください。
      * 見つからない場合はNoneを返します。
      */
    def find(id:MemberId)(implicit session:DBSession, context:ExecutionContext):Future[Option[Member]] =
      Future(MemberDao.findById(id))
    /**
      * MemberをDB上で更新して返します。
      * idが0の場合は新しいMemberをDBに登録し、新しいidを持ったインスタンスを返します。
      */
    def save(m:Member)(implicit session:DBSession, context:ExecutionContext):Future[Member] = Future {
      if (m.id.defined) {
        MemberDao.update(m)
        m
      } else {
        MemberDao.insert(m)
      }
    }
  }

  /**
    * groupのレポジトリクラスを実装しましょう
    */
  object GroupRepositoryImpl extends GroupRepository {
    /**
      * Groupを検索してFutureで返します。
      * infra.GroupDaoを利用してください。
      * 見つからない場合はNoneを返します。
      */
    def find(id:GroupId)(implicit session:DBSession, context:ExecutionContext):Future[Option[Group]] =
      Future(GroupDao.findById(id))

    /**
      * Groupを検索してFutureで返します。
      * SQLのJOINは使わず、MemberDao, GroupDao, GroupMemberDaoを利用してください。
      * 見つからない場合はNoneを返します。
      */
    def findWithMembers(id:GroupId)(implicit session:DBSession, context:ExecutionContext):Future[Option[GroupWithMembers]] = Future {
      GroupDao.findById(id).map {g =>
        val members = GroupMemberDao.membersInGroup(g.id)
        new GroupWithMembers(g.id, g.name, members, g.createdAt)
      }
    }

    /**
      * GroupをDB上で更新して返します。
      * idが0の場合は新しいGroupをDBに登録し、新しいidを持ったインスタンスを返します。
      */
    def save(g:Group)(implicit session:DBSession, context:ExecutionContext):Future[Group] = Future {
      if (g.id.defined) {
        GroupDao.update(g)
        g
      } else {
        GroupDao.insert(g)
      }
    }
  }


  /**
    * groupに関して内部でトランザクションを持つ処理を行うサービスクラスを実装しましょう
    */
  object GroupApplicationServiceImpl extends GroupApplicationService {
    /**
      * メンバーを指定して新しいグループを作成し、作成したグループを返します。
      * membersの要素のMemberはすでに登録済みでなければなりません。
      * （登録済みでないMemberを指定した場合IllegalArgumentExceptionを投げる）
      * 内部でトランザクションを持つため、DB.futureLocalTx を使用して実装します。
      */
    def createNewGroup(g:GroupWithMembers)(implicit context:ExecutionContext): Future[GroupWithMembers] =
      DB.futureLocalTx{implicit session =>
        require(!g.id.defined)
        // memberはすでに登録済みである必要
        g.members.foreach(m => require(m.id.defined))
        for {
          ng <- GroupRepositoryImpl.save(g)
          _ <- updateGroupMemberIds(ng.id, Nil, g.members.map(_.id))
        } yield GroupWithMembers(ng, g.members)
      }

    /**
      * group_memberを必要な分だけ削除、追加する
      */
    private def updateGroupMemberIds(groupId: GroupId, before:List[MemberId], after:List[MemberId])(implicit context:ExecutionContext, session:DBSession): Future[Unit] = Future{
      after.diff(before).foreach(id => GroupMemberDao.insert(groupId, id))
      GroupMemberDao.deleteMembers(groupId, before.diff(after))
    }

    /**
      * Groupを保存しつつ参加者を一括で編集します。
      * 既存のレコードを全削除して全追加するのではなく、余分なレコードだけ削除して、足りないレコードだけ
      * 追加するように実装してください。
      * 内部でトランザクションを持つため、DB.futureLocalTx を使用して実装します。
      */
    def updateGroupWithMembers(g:GroupWithMembers)(implicit context:ExecutionContext): Future[GroupWithMembers] =
      DB.futureLocalTx{implicit session =>
        require(g.id.defined)
        // memberはすでに登録済みである必要
        g.members.foreach(m => require(m.id.defined))

        val existingIds = GroupMemberDao.findByGroupId(g.id).map(_.memberId)
        for {
          ng <- GroupRepositoryImpl.save(g)
          _ <- updateGroupMemberIds(ng.id, existingIds, g.members.map(_.id))
        } yield GroupWithMembers(ng, g.members)
      }
  }


  /**
    * MemberRepositoryの実装インスタンスを返します
    */
  def getMemberRepository():MemberRepository = MemberRepositoryImpl

  /**
    * GroupRespotiroryの実装インスタンスを返します
    */
  def getGroupRepository():GroupRepository = GroupRepositoryImpl

  /**
    * GroupApplicationServiceの実装インスタンスを返します
    */
  def getGroupApplicationService():GroupApplicationService = GroupApplicationServiceImpl

}
