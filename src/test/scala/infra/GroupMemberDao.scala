package infra

import model.{GroupId, GroupMember, MemberId}
import scalikejdbc._

/**
  * groupとmemberの関連テーブルDAO
  */
object GroupMemberDao  extends SQLSyntaxSupport[GroupMember] {
  override val tableName = "group_member"

  private lazy val s = syntax("gm")

  private lazy val c = column

  def entity(rs: WrappedResultSet):GroupMember =
    GroupMember(GroupId(rs.long(c.groupId)), MemberId(rs.long(c.memberId)))

  def insert(groupId:GroupId, memberId:MemberId)(implicit session:DBSession):GroupMember = {
    require(groupId.defined && memberId.defined)
    withSQL {
      val c = column
      QueryDSL.insert.into(this).namedValues(
        c.memberId -> memberId.value,
        c.groupId -> groupId.value
      )
    }.update().apply()
    new GroupMember(groupId, memberId)
  }

  def delete(gm:GroupMember)(implicit session:DBSession):Unit = {
    withSQL {
      QueryDSL.delete.from(this)
        .where.eq(s.groupId, gm.groupId.value)
        .and.eq(s.memberId, gm.memberId.value)
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


}
