package com.ihs.demo.message_2013013306;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.ihs.message_2013013306.R;
import com.ihs.message_2013013306.managers.HSMessageManager;
import com.ihs.message_2013013306.types.HSBaseMessage;
import com.ihs.message_2013013306.types.HSBaseMessage.HSMessageMediaStatus;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
//import com.nostra13.universalimageloader.core.DisplayImageOptions;
//import com.nostra13.universalimageloader.core.ImageLoader;
import com.ihs.message_2013013306.types.HSImageMessage;

public class MessageAdapter extends BaseAdapter {
	
	private Context context;
	private List<MsgRecord> data = null;
	private ImageView lastDeleteImageView = null, lastCancelImageView = null;
	private DisplayImageOptions options;
	
	public MessageAdapter(Context context, List<MsgRecord> list) {
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
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemViewType(int position) {
		return this.data.get(position).getisSend() ? 1 : 0;
	}
	
	@Override
	public int getViewTypeCount() {
		return 2;
	}
	
	@SuppressLint("InflateParams")
	public View getView(int position, View convertView, ViewGroup parent) {

		final MsgRecord message = data.get(position);
		final int mPosition = position;
		boolean isSend = message.getisSend();

		final ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			if (isSend) {
				convertView = LayoutInflater.from(context).inflate(R.layout.msg_item_right, null);
			} else {
				convertView = LayoutInflater.from(context).inflate(R.layout.msg_item_left, null);
			}
			viewHolder.sendDateTextView = (TextView) convertView.findViewById(R.id.sendDateTextView);
			viewHolder.sendTimeTextView = (TextView) convertView.findViewById(R.id.sendTimeTextView);
			viewHolder.userAvatarImageView = (ImageView) convertView.findViewById(R.id.userAvatarImageView);
			viewHolder.userNameTextView = (TextView) convertView.findViewById(R.id.userNameTextView);
			viewHolder.textTextView = (TextView) convertView.findViewById(R.id.textTextView);
			viewHolder.photoImageView = (ImageView) convertView.findViewById(R.id.photoImageView);
			viewHolder.faceImageView = (ImageView) convertView.findViewById(R.id.faceImageView);
			viewHolder.failImageView = (ImageView) convertView.findViewById(R.id.failImageView);
			viewHolder.sendingProgressBar = (ProgressBar) convertView.findViewById(R.id.sendingProgressBar);
			viewHolder.deleteImageView = (ImageView) convertView.findViewById(R.id.deleteBarImageView);
			viewHolder.cancelImageView = (ImageView) convertView.findViewById(R.id.cancelBarImageView);
			
			viewHolder.isSend = isSend;
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		try {
			String dateString = DateFormat.format("yyyy-MM-dd h:mmaa", message.getTime()).toString();
			String [] t = dateString.split(" ");
			viewHolder.sendDateTextView.setText(dateString);
			
			if(position == 0){
				viewHolder.sendDateTextView.setVisibility(View.VISIBLE);
			}
			else{	
				MsgRecord pmsg = data.get(position - 1);
				if(t[0].equals(DateFormat.format("yyyy-MM-dd", message.getTime()).toString()))
					viewHolder.sendDateTextView.setText(t[1]);
				if(isShortInterval(pmsg.getTime(), message.getTime())){
					viewHolder.sendDateTextView.setVisibility(View.GONE);
				}else{
					viewHolder.sendDateTextView.setVisibility(View.VISIBLE);
				}
				
			}
			
		} catch (Exception e) {
		}
		
        ImageLoader.getInstance().displayImage("content://com.android.contacts/contacts/" + message.getfromMID(), viewHolder.userAvatarImageView, options);
		
		viewHolder.userNameTextView.setText(message.getfromMID());
		viewHolder.deleteImageView.setVisibility(View.GONE);
		viewHolder.cancelImageView.setVisibility(View.GONE);
		
		switch (message.getType()) {
		case 0://text
			viewHolder.textTextView.setText((String)message.getContent());
			viewHolder.textTextView.setVisibility(View.VISIBLE);
			viewHolder.photoImageView.setVisibility(View.GONE);
			viewHolder.faceImageView.setVisibility(View.GONE);
			if(message.getisSend()){
				//LayoutParams sendTimeTextViewLayoutParams = (LayoutParams) viewHolder.sendTimeTextView.getLayoutParams();
				//sendTimeTextViewLayoutParams.addRule(RelativeLayout.LEFT_OF, R.id.textTextView);
				//viewHolder.sendTimeTextView.setLayoutParams(sendTimeTextViewLayoutParams);
				//if(data.get(mPosition).getReadState() == false)
				//	viewHolder.sendTimeTextView.setText("Unread");
				//else
				//	viewHolder.sendTimeTextView.setText("Read");
				viewHolder.sendTimeTextView.setVisibility(View.GONE);

				
				LayoutParams layoutParams = (LayoutParams) viewHolder.failImageView.getLayoutParams();
				layoutParams.addRule(RelativeLayout.LEFT_OF, R.id.textTextView);
				if(message.getState() == 2){
					viewHolder.failImageView.setVisibility(View.VISIBLE);
					viewHolder.failImageView.setLayoutParams(layoutParams);
				}else{
					viewHolder.failImageView.setVisibility(View.GONE);
				}
				
				if(message.getState() == 0){
					viewHolder.sendingProgressBar.setVisibility(View.VISIBLE);
					viewHolder.sendingProgressBar.setLayoutParams(layoutParams);
				}else{
					viewHolder.sendingProgressBar.setVisibility(View.GONE);
				}
				
			}else{
				viewHolder.failImageView.setVisibility(View.GONE);
				viewHolder.sendingProgressBar.setVisibility(View.GONE);
				
				//LayoutParams sendTimeTextViewLayoutParams = (LayoutParams) viewHolder.sendTimeTextView.getLayoutParams();
				//sendTimeTextViewLayoutParams.addRule(RelativeLayout.RIGHT_OF, R.id.textTextView);
				//viewHolder.sendTimeTextView.setLayoutParams(sendTimeTextViewLayoutParams);
				viewHolder.sendTimeTextView.setVisibility(View.GONE);
			}
			
			break;
		case 1://photo
			viewHolder.textTextView.setVisibility(View.GONE);
			viewHolder.photoImageView.setVisibility(View.VISIBLE);
			viewHolder.faceImageView.setVisibility(View.GONE);
			
			HSImageMessage msg = (HSImageMessage)(message.getContent());
			viewHolder.sendTimeTextView.setVisibility(View.GONE);

			if(message.getisSend() ){
				viewHolder.photoImageView.setImageURI(Uri.parse(new File(msg.getNormalImageFilePath()).toURI().toString()));
				
				LayoutParams layoutParams = (LayoutParams) viewHolder.failImageView.getLayoutParams();
				layoutParams.addRule(RelativeLayout.LEFT_OF, R.id.photoImageView);
				if(message.getState() == 2){
					viewHolder.failImageView.setVisibility(View.VISIBLE);
					viewHolder.failImageView.setLayoutParams(layoutParams);
				}else{
					viewHolder.failImageView.setVisibility(View.GONE);
				}
				
				if(message.getState() == 0){
					viewHolder.sendingProgressBar.setVisibility(View.VISIBLE);
					viewHolder.sendingProgressBar.setLayoutParams(layoutParams);
				}else{
					viewHolder.sendingProgressBar.setVisibility(View.GONE);
				}	
			}
			else{
				if(msg.getNormalImageMediaStatus() == HSMessageMediaStatus.DOWNLOADED)
					viewHolder.photoImageView.setImageURI(Uri.parse(new File(msg.getNormalImageFilePath()).toURI().toString()));				
				else if(msg.getThumbnailMediaStatus() == HSMessageMediaStatus.DOWNLOADED)
					viewHolder.photoImageView.setImageURI(Uri.parse(new File(msg.getThumbnailFilePath()).toURI().toString()));
				else {
					viewHolder.photoImageView.setImageResource(R.drawable.chat_message_default_image_send);
				}
								
				LayoutParams layoutParams = (LayoutParams) viewHolder.failImageView.getLayoutParams();
				layoutParams.addRule(RelativeLayout.RIGHT_OF, R.id.photoImageView);
				if(msg.getThumbnailMediaStatus() == HSMessageMediaStatus.FAILED){
					viewHolder.failImageView.setVisibility(View.VISIBLE);
					viewHolder.failImageView.setLayoutParams(layoutParams);
				}else{
					viewHolder.failImageView.setVisibility(View.GONE);
				}
				
				if(msg.getThumbnailMediaStatus() == HSMessageMediaStatus.DOWNLOADING){
					viewHolder.sendingProgressBar.setVisibility(View.VISIBLE);
					viewHolder.sendingProgressBar.setLayoutParams(layoutParams);
				}else{
					viewHolder.sendingProgressBar.setVisibility(View.GONE);
				}
				
				if(msg.getNormalImageMediaStatus() == HSMessageMediaStatus.TO_DOWNLOAD)
					msg.downloadNormalImage();
				if(msg.getThumbnailMediaStatus() == HSMessageMediaStatus.TO_DOWNLOAD)
					msg.download();
			}
			
			break;
			
		default:
			viewHolder.textTextView.setText((String)message.getContent());
			viewHolder.photoImageView.setVisibility(View.GONE);
			viewHolder.faceImageView.setVisibility(View.GONE);
			break;
		}
		
		viewHolder.textTextView.setOnLongClickListener(new View.OnLongClickListener(){ 
			@Override
			public boolean onLongClick(View v) {
				if(lastDeleteImageView != null)
					lastDeleteImageView.setVisibility(View.GONE);
				viewHolder.deleteImageView.setVisibility(View.VISIBLE);
				lastDeleteImageView = viewHolder.deleteImageView;
				if(lastCancelImageView != null)
					lastCancelImageView.setVisibility(View.GONE);
				viewHolder.cancelImageView.setVisibility(View.VISIBLE);
				lastCancelImageView = viewHolder.cancelImageView;
				return true;
			}
		});
		viewHolder.deleteImageView.setOnClickListener(new View.OnClickListener(){ 
			@Override
			public void onClick(View v) {
				String id = message.getId();
				if(HSMessageManager.getInstance().queryMessage(id) != null){
					List<HSBaseMessage> mList = new ArrayList<HSBaseMessage>();
					mList.add(HSMessageManager.getInstance().queryMessage(id));
					HSMessageManager.getInstance().deleteMessages(mList);
				}
				MessageAdapter.this.getData().remove(mPosition);
				MessageAdapter.this.notifyDataSetChanged();
				lastDeleteImageView.setVisibility(View.GONE);
				lastDeleteImageView = null;
				lastCancelImageView.setVisibility(View.GONE);
				lastCancelImageView = null;
				viewHolder.deleteImageView.setVisibility(View.GONE);
				viewHolder.cancelImageView.setVisibility(View.GONE);
	
			}
		});
		viewHolder.cancelImageView.setOnClickListener(new View.OnClickListener(){ 
			@Override
			public void onClick(View v) {
				lastDeleteImageView.setVisibility(View.GONE);
				lastDeleteImageView = null;
				lastCancelImageView.setVisibility(View.GONE);
				lastCancelImageView = null;
				viewHolder.deleteImageView.setVisibility(View.GONE);
				viewHolder.cancelImageView.setVisibility(View.GONE);
			}
		});

		viewHolder.photoImageView.setOnLongClickListener(new View.OnLongClickListener(){ 
			@Override
			public boolean onLongClick(View v) {
				if(lastDeleteImageView != null)
					lastDeleteImageView.setVisibility(View.GONE);
				viewHolder.deleteImageView.setVisibility(View.VISIBLE);
				lastDeleteImageView = viewHolder.deleteImageView;
				if(lastCancelImageView != null)
					lastCancelImageView.setVisibility(View.GONE);
				viewHolder.cancelImageView.setVisibility(View.VISIBLE);
				lastCancelImageView = viewHolder.cancelImageView;
				return true;
			}
		});
		
		viewHolder.photoImageView.setOnClickListener(new View.OnClickListener(){ 
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MessageAdapter.this.context, PhotoActivity.class);
                intent.putExtra("Path", ((HSImageMessage) message.getContent()).getNormalImageFilePath());
                MessageAdapter.this.context.startActivity(intent);
			}
		});
		
//		viewHolder.textTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
		
		return convertView;
	}


	public List<MsgRecord> getData() {
		return data;
	}

	public void setData(List<MsgRecord> data) {
		this.data = data;
	}


	public static boolean isShortInterval(Date date1, Date date2) {
		double diffMinute = (date2.getTime() - date1.getTime()) / (1000.0 * 60);
		String dateString1 = DateFormat.format("MM", date1.getTime()).toString();
		String dateString2 = DateFormat.format("MM", date2.getTime()).toString();
        if (Math.abs(diffMinute) < 5 && dateString1.charAt(0) == dateString2.charAt(0)) {
			return true;
		}
		return false;
	}


	static class ViewHolder {
		public ImageView userAvatarImageView;
		public TextView sendDateTextView;
		public TextView userNameTextView;
		
		public TextView textTextView;
		public ImageView photoImageView;
		public ImageView faceImageView;
		
		public ImageView failImageView;
		public TextView sendTimeTextView;
		public ProgressBar sendingProgressBar;
		
		public ImageView deleteImageView;
		public ImageView cancelImageView;
		
		public boolean isSend = true;
	}


}
