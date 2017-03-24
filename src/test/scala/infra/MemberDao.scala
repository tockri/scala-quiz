package infra

import model.Member
import org.joda.time.{DateTime, LocalDate}
import scalikejdbc._

/**
  *
  */
object MemberDao extends SQLSyntaxSupport[Member] {

  override def tableName: String = "member"

  private lazy val s = syntax("m")

  def entity(rs: WrappedResultSet):Member = autoConstruct(rs, s)

  def insert(member:Member)(implicit session:DBSession):Member = {
    val now = DateTime.now()
    val id = withSQL {
      val c = column
      QueryDSL.insert.into(this).namedValues(
        c.name -> member.name,
        c.description -> member.description,
        c.birthday -> member.birthday,
        c.departmentId -> member.departmentId,
        c.createdAt -> now
      )
    }.updateAndReturnGeneratedKey().apply()
    member.copy(id = id, createdAt = now)
  }

  def update(member:Member)(implicit session:DBSession):Unit = {
    withSQL {
      val c = column
      QueryDSL.update(this).set(
        c.name -> member.name,
        c.description -> member.description,
        c.birthday -> member.birthday,
        c.departmentId -> member.departmentId
      ).where.eq(c.id, member.id)
    }.update.apply()
  }

  def all()(implicit session:DBSession):List[Member] = {
    withSQL {
      QueryDSL.select.from(this as s)
    }.map(rs => entity(rs)).list().apply()
  }

  def find(id:Long)(implicit session:DBSession):Option[Member] = {
    withSQL {
      QueryDSL.select.from(this as s).where.eq(s.id, id)
    }.map(rs => entity(rs)).single().apply()
  }

}