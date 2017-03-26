package entity

import org.joda.time.DateTime

/**
  * エンティティを一意に識別するためのvalue object
  */
abstract class EntityId[T](val value:T) {
  val defined = true

  override def equals(obj: scala.Any): Boolean = {
    (obj.getClass == getClass) &&
      (obj.asInstanceOf[EntityId[_]].value == value)
  }

  override def hashCode(): Int = value.hashCode()
}

abstract class LongEntityId(value:Long) extends EntityId[Long](value) {
  require((defined && value > 0) || (!defined && value == 0))
}

/**
  * modelクラスの基底クラス
  */
abstract class Entity[ID<:EntityId[_]](val id:ID) {
  override def equals(obj: scala.Any): Boolean = {
    (obj.getClass == getClass) &&
      (obj.asInstanceOf[Entity[_]].id == id)
  }

  override def hashCode(): Int = id.hashCode()
}

trait Created {
  val createdAt:DateTime
}