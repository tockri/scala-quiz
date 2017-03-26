package model

import org.joda.time.DateTime

/**
  * group.id
  */
class GroupId(value:Long) extends LongEntityId(value)

object GroupId {
  val Undef = new GroupId(0){
    override val defined: Boolean = false
  }

  def apply(value: Long): GroupId = new GroupId(value)
}

/**
  * group
  */
class Group(id:GroupId,
            val name: String,
            val createdAt: DateTime) extends Entity[GroupId](id) with Created

object Group {
  def apply(name:String):Group = new Group(GroupId.Undef, name, DateTime.now())
}

/**
  * Memberを集約したGroup
  */
class GroupWithMembers(id:GroupId,
                       name:String,
                       val members:List[Member],
                       createdAt:DateTime)
  extends Group(id, name, createdAt)

object GroupWithMembers {
  def apply(name:String, members:List[Member]):GroupWithMembers =
    new GroupWithMembers(GroupId.Undef, name, members, DateTime.now())

  def apply(g:Group, members:List[Member]):GroupWithMembers =
    new GroupWithMembers(g.id, g.name, members, g.createdAt)
}
