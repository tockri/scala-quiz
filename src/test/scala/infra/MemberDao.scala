package infra

import entity.{Member, MemberId}
import org.joda.time.{DateTime, LocalDate}
import scalikejdbc._

/**
  *
  */
object MemberDao extends SQLSyntaxSupport[Member] {

  override def tableName: String = "member"

  private lazy val s = syntax("m")

  def entity(rs: WrappedResultSet):Member =
    new Member(
      new MemberId(rs.long(column.id)),
      rs.string(column.name),
      rs.jodaDateTime(column.createdAt)
    )

  def insert(member:Member)(implicit session:DBSession):Member = {
    require(!member.id.defined)
    val now = DateTime.now()
    val id = withSQL {
      val c = column
      QueryDSL.insert.into(this).namedValues(
        c.name -> member.name,
        c.createdAt -> now
      )
    }.updateAndReturnGeneratedKey().apply()
    new Member(new MemberId(id), member.name, now)
  }

  def update(member:Member)(implicit session:DBSession):Unit = {
    require(member.id.defined)
    withSQL {
      val c = column
      QueryDSL.update(this).set(
        c.name -> member.name
      ).where.eq(c.id, member.id.value)
    }.update.apply()
  }

  def delete(member:Member)(implicit session:DBSession):Unit = {
    require(member.id.defined)
    withSQL {
      QueryDSL.delete.from(this)
        .where.eq(s.id, member.id.value)
    }.update().apply()
  }

  def all()(implicit session:DBSession):List[Member] = {
    withSQL {
      QueryDSL.select.from(this as s)
    }.map(rs => entity(rs)).list().apply()
  }

  def findById(id:MemberId)(implicit session:DBSession):Option[Member] = {
    withSQL {
      QueryDSL.select.from(this as s).where.eq(s.id, id.value)
    }.map(rs => entity(rs)).single().apply()
  }

  def findByIdList(ids:List[MemberId])(implicit session:DBSession):List[Member] = {
    withSQL {
      QueryDSL.select.from(this as s).where.in(s.id, ids.map(_.value))
    }.map(rs => entity(rs)).list.apply()
  }

}