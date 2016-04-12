package com.longhuapuxin.u5;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.longhuapuxin.adapter.NewsAdapter;
import com.longhuapuxin.handler.CommentContent;
import com.longhuapuxin.view.NewsRefreshListView;
import com.longhuapuxin.view.NewsRefreshListView.IOnRefreshListener;

public class NewsActivity extends Activity implements IOnRefreshListener {
	private NewsRefreshListView newsRefreshListView;
	private LinkedList<String> news;
	private RefreshDataAsynTask mRefreshAsynTask;
	private NewsAdapter newsAdapter;
	private View NewsHeader;
	private List<CommentContent> listContent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_news);
		initHeader();
		initNews();
		initListContent();
		initNewsRefreshListView();
	}

	private void initListContent() {
		listContent = new ArrayList<CommentContent>();
		listContent.add(new CommentContent("reckless", "love justin"));
	}

	private void initHeader() {
		NewsHeader = LayoutInflater.from(getApplicationContext()).inflate(
				R.layout.news_header, null);
	}

	private void initNews() {
		news = new LinkedList<String>();
		news.addFirst("6");
		news.addFirst("5");
		news.addFirst("4");
		news.addFirst("3");
		news.addFirst("2");
		news.addFirst("1");

	}

	private void initNewsRefreshListView() {
		newsRefreshListView = (NewsRefreshListView) findViewById(R.id.news_listview);
		newsRefreshListView.setOnRefreshListener(this);
		newsRefreshListView.addHeaderView(NewsHeader);
		newsAdapter = new NewsAdapter(news, getApplicationContext(),
				listContent);
		newsRefreshListView.setAdapter(newsAdapter);

	}

	@Override
	public void OnRefresh() {
		mRefreshAsynTask = new RefreshDataAsynTask();
		mRefreshAsynTask.execute();
	}

	class RefreshDataAsynTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... arg0) {

			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			news.addFirst("fitst");
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			newsAdapter.refreshData(news);
			newsRefreshListView.onRefreshComplete();
		}

	}
}
