package model

/**
  * groupとmemberの関連付け
  */
case class GroupMember(groupId:GroupId, memberId:MemberId) {
  require(groupId.defined && memberId.defined)
}


