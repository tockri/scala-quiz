package infra

import model.{GroupId, GroupMember, Member, MemberId}
import org.joda.time.DateTime
import scalikejdbc._

/**
  * groupとmemberの関連テーブルDAO
  */
object GroupMemberDao  extends SQLSyntaxSupport[GroupMember] {
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

  def delete(groupId:GroupId, memberId:MemberId)(implicit session:DBSession):Unit = {
    withSQL {
      QueryDSL.delete.from(this as s)
        .where.eq(s.groupId, groupId.value)
        .and.eq(s.memberId, memberId.value)
    }.update().apply()
  }

  def findByMemberId(memberId:MemberId)(implicit session:DBSession):List[GroupMember] = {
    withSQL {
      QueryDSL.select.from(this as s)
        .where.eq(s.memberId, memberId.value)
    }.map(rs => entity(rs)).list().apply()
  }

  def findByGroupId(groupId:GroupId)(implicit session:DBSession):List[GroupMember] = {
    withSQL {
      QueryDSL.select.from(this as s)
        .where.eq(s.groupId, groupId.value)
    }.map(rs => entity(rs)).list().apply()
  }

  def findById(groupId:GroupId, memberId:MemberId)(implicit session:DBSession):Option[GroupMember] = {
    withSQL {
      QueryDSL.select.from(this as s)
        .where.eq(s.groupId, groupId.value)
        .and.eq(s.memberId, memberId.value)
    }.map(rs => entity(rs)).single().apply()
  }

  /**
    * groupに所属しているmemberの配列を返す
    */
  def membersInGroup(groupId:GroupId)(implicit session:DBSession):List[Member] = {
    sql"""
          SELECT m.* FROM group_member gm
          INNER JOIN member m ON m.id = gm.member_id
          WHERE gm.group_id = ${groupId.value}
      """.map(rs => MemberDao.entity(rs)).list.apply()
  }
}
