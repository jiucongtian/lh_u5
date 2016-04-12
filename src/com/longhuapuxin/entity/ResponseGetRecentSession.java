package com.longhuapuxin.entity;

import java.util.List;

import com.longhuapuxin.view.SlideView;

public class ResponseGetRecentSession extends ResponseDad {
	private List<Sessions> Sessions;

	public List<Sessions> getSessions() {
		return Sessions;
	}

	public void setSessions(List<Sessions> sessions) {
		Sessions = sessions;
	}

	public class Sessions {
		private List<TalkMessages> Messages;

		public List<TalkMessages> getTalkMessages() {
			return Messages;
		}

		public void setTalkMessages(List<TalkMessages> talkMessages) {
			Messages = talkMessages;
		}

		public class TalkMessages {
			private String Id;
			private boolean HasRead;

			public String getId() {
				return Id;
			}

			public void setId(String id) {
				Id = id;
			}

			public boolean isHasRead() {
				return HasRead;
			}

			public void setHasRead(boolean hasRead) {
				HasRead = hasRead;
			}

			public String getSender() {
				return Sender;
			}

			public void setSender(String sender) {
				Sender = sender;
			}

			public String getSendTime() {
				return SendTime;
			}

			public void setSendTime(String sendTime) {
				SendTime = sendTime;
			}

			public String getText() {
				return Text;
			}

			public void setText(String text) {
				Text = text;
			}

			private String Sender;
			private String SendTime;
			private String Text;
		}

		// private SlideView slideView;
		//
		// public SlideView getSlideView() {
		// return slideView;
		// }
		//
		// public void setSlideView(SlideView slideView) {
		// this.slideView = slideView;
		// }

		private String SessionId;

		private String UnreadCount;

		public String getSessionId() {
			return SessionId;
		}

		public void setSessionId(String sessionId) {
			SessionId = sessionId;
		}

		public String getUnreadCount() {
			return UnreadCount;
		}

		public void setUnreadCount(String unreadCount) {
			UnreadCount = unreadCount;
		}

		public String getUserId1() {
			return UserId1;
		}

		public void setUserId1(String userId1) {
			UserId1 = userId1;
		}

		public String getNickName1() {
			return NickName1;
		}

		public void setNickName1(String nickName1) {
			NickName1 = nickName1;
		}

		public String getUserId2() {
			return UserId2;
		}

		public void setUserId2(String userId2) {
			UserId2 = userId2;
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

		private String UserId1;
		private String NickName1;
		private String UserId2;
		private String NickName2;
		private String Portrait1;
		private String Portrait2;
	}
}
