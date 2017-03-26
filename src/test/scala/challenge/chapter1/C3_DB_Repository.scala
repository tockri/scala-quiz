package challenge.chapter1

import entity._
import question.chapter1.Q3_DB_Repository
import scalikejdbc.DBSession

import scala.concurrent.{ExecutionContext, Future}

/**
  *
  */
class C3_DB_Repository extends Q3_DB_Repository {
  /**
    * memberのレポジトリクラスを実装しましょう
    */
  object MemberRepositoryImpl extends MemberRepository {
    /**
      * Memberを検索してFutureで返します。
      * infra.MemberDaoを利用してください。
      * 見つからない場合はNoneを返します。
      */
    def find(id:MemberId)(implicit session:DBSession, context:ExecutionContext):Future[Option[Member]] = ???
    /**
      * MemberをDB上で更新して返します。
      * idが0の場合は新しいMemberをDBに登録し、新しいidを持ったインスタンスを返します。
      */
    def save(m:Member)(implicit session:DBSession, context:ExecutionContext):Future[Member] = ???
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
    def find(id:GroupId)(implicit session:DBSession, context:ExecutionContext):Future[Option[Group]] = ???

    /**
      * Groupを検索してFutureで返します。
      * SQLのJOINは使わず、MemberDao, GroupDao, GroupMemberDaoを利用してください。
      * 見つからない場合はNoneを返します。
      */
    def findWithMembers(id:GroupId)(implicit session:DBSession, context:ExecutionContext):Future[Option[GroupWithMembers]] = ???
    /**
      * GroupをDB上で更新して返します。
      * idが0の場合は新しいGroupをDBに登録し、新しいidを持ったインスタンスを返します。
      */
    def save(g:Group)(implicit session:DBSession, context:ExecutionContext):Future[Group] = ???
  }

  /**
    * groupに関して内部でトランザクションを持つ処理を行うサービスクラスを実装しましょう
    */
  object GroupApplicationServiceImpl extends GroupApplicationService {
    /**
      * メンバーを指定して新しいグループを作成し、作成したグループを返します。
      * membersの要素のMemberはすでに登録済みでなければなりません。
      * （登録済みでないMemberを指定した場合IllegalArgumentExceptionを投げる）
      * 内部でトランザクションを持つため、DB.futureLocalTx を使用して実装し、
      * DAOを直接使わずRepositoryを使うようにしてください。
      */
    def createNewGroup(g:GroupWithMembers)(implicit context:ExecutionContext): Future[GroupWithMembers] = ???
    /**
      * Groupを保存しつつ参加者を一括で編集します。
      * 既存のレコードを全削除して全追加するのではなく、余分なレコードだけ削除して、足りないレコードだけ
      * 追加するように実装してください。
      * 内部でトランザクションを持つため、DB.futureLocalTx を使用して実装します。
      */
    def updateGroupWithMembers(g:GroupWithMembers)(implicit context:ExecutionContext):Future[GroupWithMembers] = ???
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
