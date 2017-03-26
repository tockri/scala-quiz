package infra

import model.{Group, GroupId}
import org.joda.time.DateTime
import scalikejdbc._

object GroupDao {
  def entity(rs:WrappedResultSet):Group =
    new Group(
      id = GroupId(rs.long("id")),
      name = rs.string("name"),
      createdAt = rs.jodaDateTime("created_at"))

  def insert(group: Group)(implicit session:DBSession):Group = {
    require(!group.id.defined)
    val now = DateTime.now()
    // sql interpolationの書き方しないとSyntax errorになる…
    val id = sql"""
          INSERT INTO `group` (name, created_at)
          VALUES (${group.name}, $now)
        """.updateAndReturnGeneratedKey().apply()
    new Group(GroupId(id), group.name, now)
  }

  def update(group: Group)(implicit session: DBSession):Unit = {
    require(group.id.defined)
    // sql interpolationの書き方しないとSyntax errorになる…
    sql"""
          UPDATE `group`
          SET name = ${group.name}
          WHERE id = ${group.id.value}
      """.update().apply()
  }

  def delete(group:Group)(implicit session:DBSession):Unit = {
    require(group.id.defined)
    // sql interpolationの書き方しないとSyntax errorになる…
    sql"""
          DELETE FROM `group`
          WHERE id = ${group.id.value}
      """.update().apply()
  }

  def all()(implicit session: DBSession):List[Group] = {
    sql"""
          SELECT id, name, created_at FROM `group`
      """.map(rs => entity(rs)).list().apply()
  }

  def findById(id:GroupId)(implicit session:DBSession):Option[Group] = {
    sql"""
          SELECT id, name, created_at FROM `group`
          WHERE id = ${id.value}
      """.map(rs => entity(rs)).single().apply()
  }

}
