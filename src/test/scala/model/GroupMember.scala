package model

import org.joda.time.DateTime

/**
  * groupとmemberの関連付け
  */
case class GroupMember(groupId:GroupId, memberId:MemberId, createdAt:DateTime) extends Created {
  require(groupId.defined && memberId.defined)
}


