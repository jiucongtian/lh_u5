package com.longhuapuxin.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.longhuapuxin.common.DensityUtil;
import com.longhuapuxin.common.Logger;
import com.longhuapuxin.handler.CommentContent;
import com.longhuapuxin.handler.CommentZan;
import com.longhuapuxin.u5.R;
import com.longhuapuxin.view.NewsScrollerComment;

public class NewsAdapter extends BaseAdapter implements OnClickListener {
	private LinkedList<String> myList;
	private LayoutInflater infalte;
	private List<Integer> msgImgId;
	private HashMap<Integer, ImageView> msgComment;
	private HashMap<Integer, ImageView> msgZan;
	private HashMap<Integer, NewsScrollerComment> msgCommentZanParent;
	private HashMap<Integer, Boolean> msgCommentZanParentIsHide;
	Context context;
	List<CommentZan> listZan;
	List<CommentContent> listContent;

	public NewsAdapter(LinkedList<String> myList, Context context,
			List<CommentContent> listContent) {
		this.context = context;
		this.myList = myList;
		this.infalte = LayoutInflater.from(context);
		this.listContent = listContent;
		initViews();
	}

	private void initViews() {
		msgComment = new HashMap<Integer, ImageView>();
		msgZan = new HashMap<Integer, ImageView>();
		msgCommentZanParent = new HashMap<Integer, NewsScrollerComment>();
		msgCommentZanParentIsHide = new HashMap<Integer, Boolean>();
		msgImgId = new ArrayList<Integer>();
	}

	@Override
	public int getCount() {
		return myList.size();
	}

	@Override
	public Object getItem(int position) {
		return myList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = infalte.inflate(R.layout.news_item, null);
			holder = new ViewHolder();
			holder.space_portrait = (ImageView) convertView
					.findViewById(R.id.space_portrait);
			holder.space_name = (TextView) convertView
					.findViewById(R.id.space_name);
			holder.space_comment = (TextView) convertView
					.findViewById(R.id.space_comment);
			holder.space_time = (TextView) convertView
					.findViewById(R.id.space_time);
			holder.msg_img = (ImageView) convertView.findViewById(R.id.msg_img);
			holder.msg_comment = (ImageView) convertView
					.findViewById(R.id.msg_comment);
			holder.msg_zan = (ImageView) convertView.findViewById(R.id.msg_zan);
			holder.msg_comment_zan_parent = (NewsScrollerComment) convertView
					.findViewById(R.id.msg_comment_zan_parent);
			holder.space_comment_content_comment = (LinearLayout) convertView
					.findViewById(R.id.space_comment_content_comment);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.msg_img.setId(position);
		if (!msgImgId.contains(position)) {
			msgImgId.add(position);
		}
		holder.msg_img.setOnClickListener(this);
		holder.msg_comment_zan_parent.scrollTo(
				DensityUtil.dip2px(context, -71), 0);
		msgComment.put(position, holder.msg_comment);
		msgZan.put(position, holder.msg_zan);
		msgCommentZanParent.put(position, holder.msg_comment_zan_parent);
		msgCommentZanParentIsHide.put(position, true);
		// 加载图片资源
		// todo
		// 加载评论内容
		holder.space_comment_content_comment.removeAllViews();
		for (int i = 0; i < 2; i++) {
			View space_comment_content_comment_view = infalte.inflate(
					R.layout.news_item_comment, null);
			TextView name = (TextView) space_comment_content_comment_view
					.findViewById(R.id.space_comment_content__msg_name);
			name.setText(listContent.get(0).getCommentConentName());
			TextView comment = (TextView) space_comment_content_comment_view
					.findViewById(R.id.space_comment_content__msg_content);
			comment.setText(listContent.get(0).getCommentConentComment());
			holder.space_comment_content_comment
					.addView(space_comment_content_comment_view);
		}
		return convertView;
	}

	class ViewHolder {
		ImageView space_portrait;
		TextView space_name;
		TextView space_comment;
		TextView space_time;
		ImageView msg_img;
		ImageView msg_comment;
		ImageView msg_zan;
		NewsScrollerComment msg_comment_zan_parent;
		LinearLayout space_comment_content_comment;
	}

	public void refreshData(LinkedList<String> data) {
		this.myList = data;
		notifyDataSetChanged();

	}

	@Override
	public void onClick(View v) {
		for (Integer position : msgImgId) {
			if (v.getId() == position) {
//				Log.d("", "----position" + position);
				Logger.info("----position" + position);
				if (msgCommentZanParentIsHide.get(position)) {
//					Log.d("",
//							"----booleab"
//									+ msgCommentZanParentIsHide.get(position));
					Logger.info("----booleab"
							+ msgCommentZanParentIsHide.get(position));
					msgCommentZanParent.get(position).smoothScrollTo(0, 0);
					msgCommentZanParentIsHide.put(position, false);
				} else {
//					Log.d("",
//							"----booleab"
//									+ msgCommentZanParentIsHide.get(position));
					Logger.info("----booleab"
							+ msgCommentZanParentIsHide.get(position));
					msgCommentZanParent.get(position).smoothScrollTo(
							DensityUtil.dip2px(context, -71), 0);
					msgCommentZanParentIsHide.put(position, true);
				}
			}
		}
	}
}
