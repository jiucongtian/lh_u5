package com.longhuapuxin.handler;

public class CommentContent {
	public CommentContent(String commentConentName, String commentConentComment) {
		this.commentConentName = commentConentName;
		this.commentConentComment = commentConentComment;
	}

	private String commentConentName;

	public String getCommentConentName() {
		return commentConentName;
	}

	public void setCommentConentName(String commentConentName) {
		this.commentConentName = commentConentName;
	}

	public String getCommentConentComment() {
		return commentConentComment;
	}

	public void setCommentConentComment(String commentConentComment) {
		this.commentConentComment = commentConentComment;
	}

	private String commentConentComment;

}
