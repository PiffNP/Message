/*
 * Copyright (c) 2014, 青岛司通科技有限公司 All rights reserved.
 * File Name：OtherAdapter.java
 * Version：V1.0
 * Author：zhaokaiqiang
 * Date：2015-1-4
 */

package com.socks.zlistview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.google.android.gms.internal.hh;
import com.ihs.demo.message_2013013306.DemoApplication;
import com.ihs.demo.message_2013013306.MsgRecord;
import com.ihs.message_2013013306.R;
import com.ihs.message_2013013306.managers.HSMessageManager;
import com.ihs.message_2013013306.types.ContactInfo;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.socks.zlistview.adapter.BaseSwipeAdapter;
import com.socks.zlistview.enums.DragEdge;
import com.socks.zlistview.enums.ShowMode;
import com.socks.zlistview.listener.SimpleSwipeListener;
import com.socks.zlistview.widget.ZSwipeItem;

public class ListViewAdapter extends BaseSwipeAdapter {

	protected static final String TAG = "ListViewAdapter";

	private Activity context;
	private List<ContactInfo> data = null;
	private DisplayImageOptions options;

	public ListViewAdapter(Activity context, List<ContactInfo> list) {
		super();
		this.context = context;
		this.data = list;
		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.chat_avatar_default_icon).showImageForEmptyUri(R.drawable.chat_avatar_default_icon)
                .showImageOnFail(R.drawable.chat_avatar_default_icon).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();
	}

	@Override
	public int getCount() {
		return data != null ? data.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getSwipeLayoutResourceId(int position) {
		return R.id.swipe_item;
	}

	@Override
	public View generateView(int position, ViewGroup parent) {
		return context.getLayoutInflater().inflate(R.layout.item_listview,
				parent, false);
	}

	@Override
	public void fillValues(final int position, View convertView) {
		
		final ZSwipeItem swipeItem = (ZSwipeItem) convertView
				.findViewById(R.id.swipe_item);
		LinearLayout mDelBtn = (LinearLayout) convertView.findViewById(R.id.contactDelButton);
		
		TextView name = (TextView) convertView.findViewById(R.id.name);
		TextView lastMsg = (TextView) convertView.findViewById(R.id.lastmsg);
		TextView lastTime = (TextView) convertView.findViewById(R.id.time);
		TextView unreadMark = (TextView) convertView.findViewById(R.id.unreadMark);
		ImageView avatar = (ImageView) convertView.findViewById(R.id.contactAvatar);
		
        ImageLoader.getInstance().displayImage("content://com.android.contacts/contacts/" + ListViewAdapter.this.getData().get(position).mMid, avatar, options);
		
		name.setText(data.get(position).mName);
		lastMsg.setText(data.get(position).mLastMsgExtra + data.get(position).mLastMsgTxt);
		lastTime.setText(displayTime(data.get(position).mLastMsgTime));
		if(data.get(position).mUnreadNum != 0){
			int count = data.get(position).mUnreadNum;
			unreadMark.setVisibility(View.VISIBLE);
			unreadMark.setText("" + (count > 99? "99+": count));
		}
		else{
			unreadMark.setVisibility(View.GONE);
			unreadMark.setText("");
		}

		swipeItem.setShowMode(ShowMode.PullOut);
		swipeItem.setDragEdge(DragEdge.Right);
		
		swipeItem.addSwipeListener(new SimpleSwipeListener() {
			@Override
			public void onOpen(ZSwipeItem layout) {
				Log.d(TAG, "打开:" + position);
			}

			@Override
			public void onClose(ZSwipeItem layout) {
				Log.d(TAG, "关闭:" + position);
			}

			@Override
			public void onStartOpen(ZSwipeItem layout) {
				Log.d(TAG, "准备打开:" + position);
			}

			@Override
			public void onStartClose(ZSwipeItem layout) {
				Log.d(TAG, "准备关闭:" + position);
			}

			@Override
			public void onHandRelease(ZSwipeItem layout, float xvel, float yvel) {
				Log.d(TAG, "手势释放");
			}

			@Override
			public void onUpdate(ZSwipeItem layout, int leftOffset,
					int topOffset) {
				Log.d(TAG, "位置更新");
			}
		});

		mDelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				swipeItem.close();
				List<ContactInfo> mContactInfo = new ArrayList<ContactInfo>();
				HSMessageManager.getInstance().deleteMessages(ListViewAdapter.this.getData().get(position).mMid);
				mContactInfo.add(ListViewAdapter.this.getData().get(position));
				DemoApplication.mContactDBManager.deletContacts(mContactInfo);
				ListViewAdapter.this.getData().remove(position);
				notifyDataSetChanged();
			}
		});
	}
	
	public List<ContactInfo> getData() {
		return data;
	}

	public void setData(List<ContactInfo> data) {
		this.data = data;
	}
	
	static class ViewHolder {
		public ImageView mContactAvatar;
		public TextView mContactName;
		public TextView mLastMsgTxt;
		public TextView mLastMsgTime;
	}
	
	private String displayTime(String record){
		if(record.equals(""))
			return "";
		String[] recordSplit = record.split("-");
		//Warning: pay attention to the following line!
		Date recordDate = new Date(Integer.parseInt(recordSplit[0]) - 1900,
							Integer.parseInt(recordSplit[1]) - 1,
							Integer.parseInt(recordSplit[2]),
							Integer.parseInt(recordSplit[3]),
							Integer.parseInt(recordSplit[4]));
		Date curDate =  new Date(System.currentTimeMillis());
		String[] curSplit = DateFormat.format("yyyy-MM-dd-HH-mm-ss", curDate.getTime())
								.toString().split("-");
		if(recordSplit[2].equals(curSplit[2])){
			return recordSplit[3] + ":" + recordSplit[4];
		}
		else{
			double recordTime = recordDate.getTime();
			double curTime = curDate.getTime();
			double delta = curDate.getHours() + curDate.getMinutes() / 60.0 
							+ curDate.getSeconds() / 3600.0;
			if(curTime < recordTime){
				return recordSplit[0] + "/" + recordSplit[1] + "/" + recordSplit[2];
			}
			else if((curTime - recordTime) / (1000.0 * 60.0 * 60.0) - delta < 24){
				return "昨天";
			}
			else if((curTime - recordTime) / (1000.0 * 60.0 * 60.0) - delta < 168){
				String[] day = {"天", "一", "二", "三", "四", "五", "六"};
				Calendar mCalendar = Calendar.getInstance();
				mCalendar.setTime(recordDate);
				return "星期" + day[mCalendar.get(Calendar.DAY_OF_WEEK) - 1];
			}
			else{
				return recordSplit[0] + "/" + recordSplit[1] + "/" + recordSplit[2];
			}
		}
	}
}
