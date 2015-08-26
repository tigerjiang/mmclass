package com.multimedia.room;

public interface IUpdateGroupMembersListenner {
	public void joinGroup(String newGroup, String member);

	public void leaveGroup(String member);

	public void joinMeGroup(MediaMessage message);

}
