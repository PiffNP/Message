package com.ihs.message_2013013306.types;

import com.ihs.message_2013013306.managers.ContactDBManager;

import android.content.ContentValues;
import android.database.Cursor;

public class ContactInfo {
	public String mMid;
	public String mName;
	public String mAvatar;
	public String mLastMsgTxt;
	public String mLastMsgTime;
	public String mLastMsgExtra;
	public int mLastSelItem;
	public int mUnreadNum;
	
	public ContactInfo(String mid, String name, String avatar, String lastMsgTxt,
						String lastMsgTime, String lastMsgExtra, int lastSelItem,
						int unreadNum) {
		mMid = mid;
		mName =name;
		mAvatar = avatar;
		mLastMsgTxt = lastMsgTxt;
		mLastMsgTime = lastMsgTime;
		mLastMsgExtra = lastMsgExtra;
		mLastSelItem = lastSelItem;
		mUnreadNum = unreadNum;
	}
	
	public ContactInfo(Cursor cursor) {
		this.mMid = cursor.getString(cursor.getColumnIndex(ContactDBManager.CONTACT_COLUMN_MID));
		this.mName = cursor.getString(cursor.getColumnIndex(ContactDBManager.CONTACT_COLUMN_NAME));
		this.mAvatar = cursor.getString(cursor.getColumnIndex(ContactDBManager.CONTACT_COLUMN_AVATAR));
		this.mLastMsgTxt = cursor.getString(cursor.getColumnIndex(ContactDBManager.CONTACT_COLUMN_LAST_MSG_TXT));
		this.mLastMsgTime = cursor.getString(cursor.getColumnIndex(ContactDBManager.CONTACT_COLUMN_LAST_MSG_TIME));
		this.mLastMsgExtra = cursor.getString(cursor.getColumnIndex(ContactDBManager.CONTACT_COLUMN_LAST_MSG_EXTRA));
        this.mLastSelItem = cursor.getInt(cursor.getColumnIndex(ContactDBManager.CONTACT_COLUMN_LAST_SEL_ITEM));
        this.mUnreadNum = cursor.getInt(cursor.getColumnIndex(ContactDBManager.CONTACT_COLUMN_UNREAD_NUM));
	}
	
	public ContentValues getDBInfo() {
		ContentValues cv = new ContentValues();
		cv.put(ContactDBManager.CONTACT_COLUMN_MID, mMid);
		cv.put(ContactDBManager.CONTACT_COLUMN_NAME, mName);
	    cv.put(ContactDBManager.CONTACT_COLUMN_AVATAR, mAvatar);
	    cv.put(ContactDBManager.CONTACT_COLUMN_LAST_MSG_TXT, mLastMsgTxt);
	    cv.put(ContactDBManager.CONTACT_COLUMN_LAST_MSG_TIME, mLastMsgTime);
	    cv.put(ContactDBManager.CONTACT_COLUMN_LAST_MSG_EXTRA, mLastMsgExtra);
	    cv.put(ContactDBManager.CONTACT_COLUMN_LAST_SEL_ITEM, mLastSelItem);
	    cv.put(ContactDBManager.CONTACT_COLUMN_UNREAD_NUM, mUnreadNum);
	        
	    return cv;
	}
}
