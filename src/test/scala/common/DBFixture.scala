package common

import infra.{DepartmentDao, MemberDao}
import model.{Department, Member}
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
    val d1 = DepartmentDao.insert(Department.create("first department"))
    val d2 = DepartmentDao.insert(Department.create("second dept"))
    MemberDao.insert(Member.create("Alice", Some("Alice in wonderland"), Some(new LocalDate("1980-01-01")), d1.id))
    MemberDao.insert(Member.create("Bob", Some("Kinda person"), None, d2.id))
  }
}








