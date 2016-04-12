package com.longhuapuxin.slidingmenu;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

public class SlidingMenuActivity extends Activity {
	private SlidingMenu slidingMenu;
	private FragmentManager mFragmentManager;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		slidingMenu = new SlidingMenu(this);
		setContentView(slidingMenu);
		mFragmentManager = getFragmentManager();
	}

	public SlidingMenu getSlidingMenu() {
		return slidingMenu;
	}

	/**
	 * 初始化：添加菜单Fragment和主Fragment
	 * 
	 * @param menuFragment
	 * @param mainFragment
	 */
	@SuppressLint("NewApi")
	public void initFragments(Fragment menuFragment, Fragment mainFragment) {
		FragmentTransaction transaction = mFragmentManager.beginTransaction();
		transaction.add(SlidingMenu.MENU_CONTAINER_ID, menuFragment,
				getFragmentTag(menuFragment));
		transaction.add(SlidingMenu.MAIN_CONTAINER_ID, mainFragment,
				getFragmentTag(mainFragment));
		transaction.commit();
		mCurrentFragment = mainFragment;
	}

	/**
	 * 获取Fragment的类名
	 * 
	 * @param fragment
	 * @return
	 */
	private String getFragmentTag(Fragment fragment) {
		return fragment.getClass().getName();
	}

	/**
	 * 打开菜单
	 */
	protected void openMenu() {
		slidingMenu.openMenu();
	}

	/**
	 * 关闭菜单
	 */
	protected void closeMenu() {
		slidingMenu.closeMenu();
	}

	/**
	 * 切换Fragment
	 * 
	 * @param c
	 *            ： Fragment的class
	 */
	@SuppressLint("NewApi")
	public void switchFragment(Class<?> c) {
		closeMenu();
		FragmentTransaction transaction = mFragmentManager.beginTransaction();
		Fragment fragment = mFragmentManager.findFragmentByTag(c.getName());
		if (fragment == mCurrentFragment) {
			return;
		}
		if (fragment == null) {
			try {
				Fragment fragmentNew = (Fragment) c.newInstance();
				transaction.add(SlidingMenu.MAIN_CONTAINER_ID, fragmentNew,
						getFragmentTag(fragmentNew));
				transaction.hide(mCurrentFragment);
				mCurrentFragment = fragmentNew;
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		} else {
			transaction.hide(mCurrentFragment);
			transaction.show(fragment);
			mCurrentFragment = fragment;
		}
		transaction.commit();
	}

	private Fragment mCurrentFragment;

	public void setIntercept(boolean intercept) {
		slidingMenu.setIntercept(intercept);
	}

	/**
	 * 获取当前Fragment对象
	 * 
	 * @return
	 */
	public Fragment getCurFragment() {
		return mCurrentFragment;
	}

	/**
	 * 判断菜单是否打开
	 * 
	 * @return
	 */
	public boolean menuIsOpen() {
		return slidingMenu.menuIsOpen();
	}

	private List<View> getAllChildViews() {

		View view = getWindow().getDecorView();

		return getAllChildViews(view);

	}

	private List<View> getAllChildViews(View view) {

		List<View> allchildren = new ArrayList<View>();

		if (view instanceof ViewGroup) {

			ViewGroup vp = (ViewGroup) view;

			for (int i = 0; i < vp.getChildCount(); i++) {

				View viewchild = vp.getChildAt(i);

				allchildren.add(viewchild);

				allchildren.addAll(getAllChildViews(viewchild));

			}

		}

		return allchildren;

	}

}
