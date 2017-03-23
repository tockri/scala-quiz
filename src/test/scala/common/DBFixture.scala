package common

import org.joda.time.{DateTime, LocalDate}
import scalikejdbc._


/**
  * H2 Databaseへの接続
  */
object DBFixture {
  Class.forName("org.h2.Driver")
  ConnectionPool.singleton("jdbc:h2:mem:scalikejdbc","user","pass")

  DB autoCommit { implicit session =>
    SQL("""
    create table member (
      id bigint primary key auto_increment,
      name varchar(30) not null,
      description varchar(1000),
      birthday date,
      department_id bigint not null,
      created_at timestamp not null
    );

    create table department (
      id bigint primary key auto_increment,
      name varchar(30) not null,
      created_at timestamp not null
    );
    """).execute.apply()
    val d1 = Department.insert("first department")
    val d2 = Department.insert("second dept")
    Member.insert("Alice", Some("Alice in wonderland"), Some(new LocalDate("1980-01-01")), d1.id)
    Member.insert("Bob", Some("Kinda person"), None, d2.id)
  }
}

case class Department(id:Long,
                      name: String,
                      createdAt: DateTime)

object Department extends SQLSyntaxSupport[Department] {
  private lazy val d = Department.syntax("d")

  def apply(rs:WrappedResultSet):Department = autoConstruct(rs, d)

  def insert(name:String)(implicit session:DBSession):Department = {
    val now = DateTime.now()
    val id = withSQL {
      val c = column
      QueryDSL.insert.into(Department).namedValues(
        c.name -> name,
        c.createdAt -> now
      )
    }.updateAndReturnGeneratedKey().apply()
    new Department(id, name, now)
  }

  def update(department: Department)(implicit session: DBSession):Unit = {
    withSQL {
      val c = column
      QueryDSL.update(Department).set(
        c.name -> department.name
      ).where.eq(c.id, department.id)
    }.update().apply()
  }

  def all()(implicit session: DBSession):List[Department] = {
    withSQL {
      QueryDSL.select.from(Department as d)
    }.map(rs => Department(rs)).list().apply()
  }

  def find(id:Long)(implicit session:DBSession):Option[Department] = {
    withSQL {
      QueryDSL.select.from(Department as d).where.eq(d.id, id)
    }.map(rs => Department(rs)).single().apply()
  }

}

case class Member(id:Long,
                  name:String,
                  description:Option[String],
                  birthday:Option[LocalDate],
                  departmentId:Long,
                  createdAt:DateTime)

object Member extends SQLSyntaxSupport[Member] {

  private lazy val m = Member.syntax("m")

  def apply(rs: WrappedResultSet):Member = autoConstruct(rs, m)


  def insert(name:String, description:Option[String], birthday:Option[LocalDate], departmentId:Long)(implicit session:DBSession):Member = {
    val now = DateTime.now()
    val id = withSQL {
      val c = column
      QueryDSL.insert.into(Member).namedValues(
        c.name -> name,
        c.description -> description,
        c.birthday -> birthday,
        c.departmentId -> departmentId,
        c.createdAt -> now
      )
    }.updateAndReturnGeneratedKey().apply()
    Member(id, name, description, birthday, departmentId, now)
  }

  def update(member:Member)(implicit session:DBSession):Unit = {
    withSQL {
      val c = column
      QueryDSL.update(Member).set(
        c.name -> member.name,
        c.description -> member.description,
        c.birthday -> member.birthday,
        c.departmentId -> member.departmentId
      ).where.eq(c.id, member.id)
    }.update.apply()
  }

  def all()(implicit session:DBSession):List[Member] = {
    withSQL {
      QueryDSL.select.from(Member as m)
    }.map(rs => Member(rs)).list().apply()
  }

  def find(id:Long)(implicit session:DBSession):Option[Member] = {
    withSQL {
      QueryDSL.select.from(Member as m).where.eq(m.id, id)
    }.map(rs => Member(rs)).single().apply()
  }

}

