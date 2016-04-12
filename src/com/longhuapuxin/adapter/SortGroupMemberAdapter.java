package com.longhuapuxin.adapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.longhuapuxin.common.ImageUrlLoader;
import com.longhuapuxin.db.bean.ChatSession;
import com.longhuapuxin.entity.ResponseGetAccount;
import com.longhuapuxin.entity.ResponseGetAccount.User.CareWhichShop;
import com.longhuapuxin.entity.ResponseGetAccount.User.CareWho;
import com.longhuapuxin.u5.R;

public class SortGroupMemberAdapter extends U5BaseAdapter<CareWho> implements
		SectionIndexer {
	private List<CareWho> list = null;
	private Context mContext;
	private List<String> idList = new ArrayList<String>();
	private boolean isSelectMode = false;
	private Set<Integer> mSelectedSet;
	private Set<Integer> mFrozeId;

	public SortGroupMemberAdapter(Context mContext, List<CareWho> list, boolean isSelectMode) {
		super(mContext, list);
		this.mContext = mContext;
		this.list = list;
		this.isSelectMode = isSelectMode;
		mSelectedSet = new HashSet<Integer>();
		notifyDataSetChangedWithImages();
	}

	/**
	 * 当ListView数据发生变化时,调用此方法来更新ListView
	 * 
	 * @param list
	 */
	public void updateListView(List<CareWho> list) {
		this.list = list;
		notifyDataSetChanged();
	}

	public int getCount() {
		return this.list.size();
	}

	public Object getItem(int position) {
		return list.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View view, ViewGroup arg2) {
		ViewHolder viewHolder = null;
		final CareWho mContent = list.get(position);
		if (view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(mContext).inflate(
					R.layout.item_person_contact, null);
			viewHolder.tvTitle = (TextView) view.findViewById(R.id.title);
			viewHolder.tvLetter = (TextView) view.findViewById(R.id.catalog);
			viewHolder.tvImg = (ImageView) view.findViewById(R.id.img);
			viewHolder.mark = (ImageView) view.findViewById(R.id.ivMark);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		// 根据position获取分类的首字母的Char ascii值
		int section = getSectionForPosition(position);

		// 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
		if (position == getPositionForSection(section)) {
			String tvLetter = mContent.getFirstChar();
			Pattern p1 = Pattern.compile("[a-zA-Z]");
			Matcher m1 = p1.matcher(tvLetter);
			Pattern p2 = Pattern.compile("[\u4e00-\u9fa5]");
			Matcher m2 = p2.matcher(tvLetter);
			if (!m1.matches() && !m2.matches()) {
				tvLetter = "#";
			}
			viewHolder.tvLetter.setVisibility(View.VISIBLE);
			viewHolder.tvLetter.setText(tvLetter);
		} else {
			viewHolder.tvLetter.setVisibility(View.GONE);
		}

		if(isSelectMode) {
			viewHolder.mark.setVisibility(View.VISIBLE);
			if(mSelectedSet.contains(position)) {
				viewHolder.mark.setImageResource(R.drawable.shop_product_select_sel);
			} else {
				viewHolder.mark.setImageResource(R.drawable.shop_product_select);
			}
		} else {
			viewHolder.mark.setVisibility(View.INVISIBLE);
		}
//		viewHolder.mark.setVisibility(View.INVISIBLE);

		viewHolder.tvTitle.setText(this.list.get(position).getNickName());
		bindImageView(viewHolder.tvImg, list.get(position).getPortrait());
		return view;

	}

	final static class ViewHolder {
		ImageView tvImg;
		TextView tvLetter;
		TextView tvTitle;
		ImageView mark;
	}

	/**
	 * 根据ListView的当前位置获取分类的首字母的Char ascii值
	 */
	public int getSectionForPosition(int position) {
		CareWho careWho = list.get(position);
		char firstLetter = careWho.getFirstChar().toUpperCase().charAt(0);
		Pattern p1 = Pattern.compile("[a-zA-Z]");
		Matcher m1 = p1.matcher(String.valueOf(firstLetter));
		Pattern p2 = Pattern.compile("[\u4e00-\u9fa5]");
		Matcher m2 = p2.matcher(String.valueOf(firstLetter));
		if (!m1.matches() && !m2.matches()) {
			firstLetter = "#".charAt(0);
		}
		return firstLetter;
	}

	/**
	 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
	 */
	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = list.get(i).getFirstChar();
			Pattern p1 = Pattern.compile("[a-zA-Z]");
			Matcher m1 = p1.matcher(sortStr);
			Pattern p2 = Pattern.compile("[\u4e00-\u9fa5]");
			Matcher m2 = p2.matcher(sortStr);
			if (!m1.matches() && !m2.matches()) {
				sortStr = "#";
			}
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * 提取英文的首字母，非英文字母用#代替。
	 * 
	 * @param str
	 * @return
	 */
	private String getAlpha(String str) {
		String sortStr = str.trim().substring(0, 1).toUpperCase();
		// 正则表达式，判断首字母是否是英文字母
		if (sortStr.matches("[A-Z]")) {
			return sortStr;
		} else {
			return "#";
		}
	}

	@Override
	public Object[] getSections() {
		return null;
	}

//	private void setIdList() {
//		for (CareWho careWho : list) {
//			idList.add(careWho.getPortrait());
//		}
//		ImageUrlLoader.fetchImageUrl(this, idList);
//	}


	@Override
	public String getImageId(CareWho item) {
		return item.getPortrait();
	}

	public void refresh() {
//		setIdList();
		this.notifyDataSetChangedWithImages();
	}

	public void markItem(int index) {
		if(mSelectedSet.contains(index)) {
			mSelectedSet.remove(index);
		} else {
			mSelectedSet.add(index);
		}
	}

	public List<Integer> getSelectedList() {
		return new ArrayList<Integer>(mSelectedSet);
	}
}