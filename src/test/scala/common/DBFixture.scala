package common

import infra.{GroupDao, GroupMemberDao, MemberDao}
import model.{Group, Member}
import org.joda.time.{DateTime, LocalDate}
import scalikejdbc._


/**
  * H2 Databaseへの接続、テーブルの作成
  */
object DBFixture {
  Class.forName("org.h2.Driver")
  ConnectionPool.singleton("jdbc:h2:mem:scalikejdbc","user","pass")
  GlobalSettings.loggingSQLAndTime = LoggingSQLAndTimeSettings(
    enabled = true,
    singleLineMode = true,
    logLevel = 'debug,
    warningEnabled = true,
    warningThresholdMillis = 1000L,
    warningLogLevel = 'WARN
  )
  DB autoCommit { implicit session =>
    SQL("""
    create table `member` (
      id bigint primary key auto_increment,
      name varchar(30) not null,
      created_at timestamp not null
    );

    create table `group` (
      id bigint primary key auto_increment,
      name varchar(30) not null,
      created_at timestamp not null
    );

    create table group_member (
      member_id bigint,
      group_id bigint,
      created_at timestamp not null,
      primary key(member_id, group_id),
      foreign key(member_id) references `member`(id),
      foreign key(group_id) references `group`(id)
    );
    """).execute.apply()
    val g1 = GroupDao.insert(Group("Backlog team"))
    val g2 = GroupDao.insert(Group("Cacoo team"))
    val m1 = MemberDao.insert(Member("Alice"))
    val m2 = MemberDao.insert(Member("Bob"))
    GroupMemberDao.insert(g1.id, m1.id)
    GroupMemberDao.insert(g1.id, m2.id)
    GroupMemberDao.insert(g2.id, m2.id)
  }
}








