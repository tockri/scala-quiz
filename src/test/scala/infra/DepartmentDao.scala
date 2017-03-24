package infra

import model.Department
import org.joda.time.DateTime
import scalikejdbc._

object DepartmentDao extends SQLSyntaxSupport[Department] {

  override def tableName: String = "department"

  private lazy val s = syntax("d")

  def entity(rs:WrappedResultSet):Department = autoConstruct(rs, s)

  def insert(department: Department)(implicit session:DBSession):Department = {
    val now = DateTime.now()
    val id = withSQL {
      val c = column
      QueryDSL.insert.into(this).namedValues(
        c.name -> department.name,
        c.createdAt -> now
      )
    }.updateAndReturnGeneratedKey().apply()
    department.copy(id = id, createdAt = now)
  }

  def update(department: Department)(implicit session: DBSession):Unit = {
    withSQL {
      val c = column
      QueryDSL.update(this).set(
        c.name -> department.name
      ).where.eq(c.id, department.id)
    }.update().apply()
  }

  def all()(implicit session: DBSession):List[Department] = {
    withSQL {
      QueryDSL.select.from(this as s)
    }.map(rs => entity(rs)).list().apply()
  }

  def find(id:Long)(implicit session:DBSession):Option[Department] = {
    withSQL {
      QueryDSL.select.from(this as s).where.eq(s.id, id)
    }.map(rs => entity(rs)).single().apply()
  }

}
