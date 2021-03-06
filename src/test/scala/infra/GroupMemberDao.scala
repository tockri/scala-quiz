package infra

import entity.{GroupId, GroupMember, Member, MemberId}
import org.joda.time.DateTime
import scalikejdbc._

/**
  * groupとmemberの関連テーブルDAO
  */
object GroupMemberDao extends SQLSyntaxSupport[GroupMember] {
  override val tableName = "group_member"

  private lazy val s = syntax("gm")

  private lazy val c = column

  def entity(rs: WrappedResultSet):GroupMember =
    GroupMember(
      GroupId(rs.long(c.groupId)),
      MemberId(rs.long(c.memberId)),
      rs.jodaDateTime(c.createdAt)
    )

  def insert(groupId:GroupId, memberId:MemberId)(implicit session:DBSession):GroupMember = {
    require(groupId.defined && memberId.defined)
    val now = DateTime.now()
    withSQL {
      val c = column
      QueryDSL.insert.into(this).namedValues(
        c.memberId -> memberId.value,
        c.groupId -> groupId.value,
        c.createdAt -> now
      )
    }.update().apply()
    new GroupMember(groupId, memberId, now)
  }

  def deleteMembers(groupId:GroupId, memberIds:List[MemberId])(implicit session: DBSession):Unit = {
    if (memberIds.nonEmpty) {
      withSQL {
        QueryDSL.delete.from(this as s)
          .where.eq(s.groupId, groupId.value)
          .and.in(s.memberId, memberIds.map(_.value))
      }.update().apply()
    }
  }

  def findByGroupId(groupId:GroupId)(implicit session:DBSession):List[GroupMember] = {
    withSQL {
      QueryDSL.select.from(this as s)
        .where.eq(s.groupId, groupId.value)
    }.map(rs => entity(rs)).list().apply()
  }

  /**
    * groupに所属しているmemberの配列を返す
    */
  def membersInGroup(groupId:GroupId)(implicit session:DBSession):List[Member] = {
    withSQL {
      val ms = MemberDao.s
      QueryDSL.select(ms.*).from(this as s)
        .innerJoin(MemberDao as ms)
          .on(ms.id, s.memberId)
        .where.eq(s.groupId, groupId.value)
    }.map(rs => MemberDao.entity(rs)).list.apply()
  }
}
