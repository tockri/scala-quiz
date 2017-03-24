package model

import org.joda.time.DateTime

case class Department(id:Long,
                      name: String,
                      createdAt: DateTime)

object Department {
  def create(name:String) = Department(0, name, DateTime.now())
}