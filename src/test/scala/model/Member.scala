package model

import org.joda.time.{DateTime, LocalDate}

case class Member(id:Long,
                  name:String,
                  description:Option[String],
                  birthday:Option[LocalDate],
                  departmentId:Long,
                  createdAt:DateTime)

object Member {
  def create(name:String,
             description:Option[String],
             birthday:Option[LocalDate],
             departmentId:Long) = Member(0, name, description, birthday, departmentId, DateTime.now())
}