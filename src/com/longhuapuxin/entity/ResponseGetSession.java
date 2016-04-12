package com.longhuapuxin.entity;

public class ResponseGetSession extends ResponseDad {
	private Session Session;

	public Session getSession() {
		return Session;
	}

	public void setSession(Session session) {
		Session = session;
	}

	public class Session {
		private String UserId1;
		private String UserId2;
		private String NickName1;

		public String getUserId1() {
			return UserId1;
		}

		public void setUserId1(String userId1) {
			UserId1 = userId1;
		}

		public String getUserId2() {
			return UserId2;
		}

		public void setUserId2(String userId2) {
			UserId2 = userId2;
		}

		public String getNickName1() {
			return NickName1;
		}

		public void setNickName1(String nickName1) {
			NickName1 = nickName1;
		}

		public String getNickName2() {
			return NickName2;
		}

		public void setNickName2(String nickName2) {
			NickName2 = nickName2;
		}

		public String getPortrait1() {
			return Portrait1;
		}

		public void setPortrait1(String portrait1) {
			Portrait1 = portrait1;
		}

		public String getPortrait2() {
			return Portrait2;
		}

		public void setPortrait2(String portrait2) {
			Portrait2 = portrait2;
		}

		public String getSessionId() {
			return SessionId;
		}

		public void setSessionId(String sessionId) {
			SessionId = sessionId;
		}

		private String NickName2;
		private String Portrait1;
		private String Portrait2;
		private String SessionId;
		private String Gender1;

		public String getGender1() {
			return Gender1;
		}

		public void setGender1(String gender1) {
			Gender1 = gender1;
		}

		public String getGender2() {
			return Gender2;
		}

		public void setGender2(String gender2) {
			Gender2 = gender2;
		}

		private String Gender2;
	}
}
