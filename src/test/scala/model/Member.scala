package model

import org.joda.time.DateTime

/**
  * member.id
  */
class MemberId(value:Long) extends LongEntityId(value)

object MemberId {
  val Undef = new MemberId(0) {
    override val defined = false
  }

  def apply(value: Long): MemberId = new MemberId(value)
}

/**
  * member
  */
class Member(id:MemberId,
             val name:String,
             val createdAt:DateTime) extends Entity[MemberId](id) with Created

object Member {
  def apply(name:String):Member =
    new Member(MemberId.Undef, name, DateTime.now())
}