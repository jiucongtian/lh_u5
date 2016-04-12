package com.longhuapuxin.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.longhuapuxin.adapter.ShopsListAdapter;
import com.longhuapuxin.adapter.SortGroupMemberAdapter;
import com.longhuapuxin.common.CharacterParser;
import com.longhuapuxin.common.GetFirstLetter;
import com.longhuapuxin.entity.ResponseGetAccount.User.CareWhichShop;
import com.longhuapuxin.u5.R;
import com.longhuapuxin.u5.Settings;
import com.longhuapuxin.u5.ShopDetailActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressLint({ "NewApi", "ValidFragment" })
public class ContactShopFragment extends Fragment {
	private List<CareWhichShop> careWhichShops;
	private ListView shopListView;
	private ShopsListAdapter shopsListAdapter;
	private CharacterParser characterParser = new CharacterParser();
	private EditText searchShop;
	private TextView titleLayoutNoShops;


	@Override
	public void onResume() {
		super.onResume();

		this.careWhichShops = Settings.instance().User.getCareWhichShops();
		sortCareWhichShops();
		shopsListAdapter = new ShopsListAdapter(getActivity(), careWhichShops);
		shopListView.setAdapter(shopsListAdapter);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		return inflater.inflate(R.layout.fragment_shop_contact, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		sortCareWhichShops();
		init();
		initEdit();
	}

	private void initEdit() {
		searchShop = (EditText) getView().findViewById(R.id.search_editor);

		searchShop.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				filterData(s.toString());
			}
		});

	}

	private void sortCareWhichShops() {
		for (CareWhichShop careWhichShop : careWhichShops) {
			char firstchar = GetFirstLetter.getSpells(careWhichShop.getName())
					.toUpperCase().charAt(0);

			careWhichShop.setFirstChar(String.valueOf(firstchar));
		}
		Collections.sort(careWhichShops);
	}

	private void init() {
		titleLayoutNoShops = (TextView) getView().findViewById(
				R.id.title_layout_no_shops);
		shopListView = (ListView) getView().findViewById(R.id.shop_contacts);
//		shopsListAdapter = new ShopsListAdapter(getActivity(), careWhichShops);
//		shopListView.setAdapter(shopsListAdapter);
		shopListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				CareWhichShop careWhichShop = careWhichShops.get(arg2);
				Intent intent = new Intent(getActivity(),
						ShopDetailActivity.class);
				intent.putExtra("ShopCode", careWhichShop.getCode());
				intent.putExtra("ShopName", careWhichShop.getName());
				startActivity(intent);
			}
		});
	}

	private void filterData(String filterStr) {
		List<CareWhichShop> filterDateList = new ArrayList<CareWhichShop>();

		if (TextUtils.isEmpty(filterStr)) {
			titleLayoutNoShops.setVisibility(View.GONE);
			filterDateList = careWhichShops;
		} else {
			filterDateList.clear();
			for (CareWhichShop sortModel : careWhichShops) {
				String name = sortModel.getName();
				if (name.indexOf(filterStr.toString()) != -1
						|| characterParser.getSelling(name).startsWith(
								filterStr.toString())) {
					filterDateList.add(sortModel);
				}
			}
		}
		shopsListAdapter.updateListView(filterDateList);
		if (filterDateList.size() == 0) {
			titleLayoutNoShops.setVisibility(View.VISIBLE);
		}
	}

	public ContactShopFragment(List<CareWhichShop> careWhichShops) {
		this.careWhichShops = careWhichShops;
	}

}
