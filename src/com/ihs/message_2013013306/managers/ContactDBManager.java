package com.ihs.message_2013013306.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.text.style.StrikethroughSpan;

import com.ihs.app.framework.HSApplication;
import com.ihs.commons.utils.HSLog;
import com.ihs.message_2013013306.types.ContactInfo;

public class ContactDBManager extends SQLiteOpenHelper {
    public static final String CONTACT_COLUMN_MID = "contact_c_mid";
    public static final String CONTACT_COLUMN_NAME = "contact_c_name";
    public static final String CONTACT_COLUMN_AVATAR = "contact_c_avatar";
    public static final String CONTACT_COLUMN_LAST_MSG_TXT = "contact_c_last_msg_txt";
    public static final String CONTACT_COLUMN_LAST_MSG_TIME = "contact_c_last_msg_time";
    public static final String CONTACT_COLUMN_LAST_MSG_EXTRA = "contact_c_last_msg_extra";
    public static final String CONTACT_COLUMN_LAST_SEL_ITEM = "contact_c_last_sel_item";
    public static final String CONTACT_COLUMN_UNREAD_NUM = "contact_c_unread_num";
    
    private static final String TYPE_TEXT = "text";
    private static final String TYPE_LONG = "long";
    private static final String TYPE_INTEGER = "integer";

    private static String DATABASE_NAME = "ContactDB";
    static int DATABASE_VERION = 1;

    private final static String TAG = MessageDBManager.class.getName();
    private String mMid;
    private HashMap<String, String> mContactColumns;

    public ContactDBManager(String mid) {
        super(HSApplication.getContext(), DATABASE_NAME, null, DATABASE_VERION);
        mMid = mid;
        {
            mContactColumns = new HashMap<String, String>();
            mContactColumns.put(CONTACT_COLUMN_MID, TYPE_TEXT);
            mContactColumns.put(CONTACT_COLUMN_NAME, TYPE_TEXT);
            mContactColumns.put(CONTACT_COLUMN_AVATAR, TYPE_TEXT);
            mContactColumns.put(CONTACT_COLUMN_LAST_MSG_TXT, TYPE_TEXT);
            mContactColumns.put(CONTACT_COLUMN_LAST_MSG_TIME, TYPE_TEXT);
            mContactColumns.put(CONTACT_COLUMN_LAST_MSG_EXTRA, TYPE_TEXT);
            mContactColumns.put(CONTACT_COLUMN_LAST_SEL_ITEM, TYPE_INTEGER);
            mContactColumns.put(CONTACT_COLUMN_UNREAD_NUM, TYPE_INTEGER);
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void doCreateTables() {
        if (TextUtils.isEmpty(this.mMid))
            return;
        SQLiteDatabase db = getWritableDatabase();
        String msgTableName = getContactTableName();
        StringBuffer sqlSentence = new StringBuffer("CREATE TABLE IF NOT EXISTS ");
        sqlSentence.append(msgTableName);
        sqlSentence.append(" ( ");
        int sum = mContactColumns.size();
        int count = 0;
        for (String key : mContactColumns.keySet()) {
            count++;
            String type = mContactColumns.get(key);
            sqlSentence.append(key);
            sqlSentence.append(" ");
            sqlSentence.append(type);
            if (count < sum) {
                sqlSentence.append(", ");
            }
        }
        sqlSentence.append(");");
        db.execSQL(sqlSentence.toString());
    }

    private String getContactTableName() {
        return "Contacts_" + this.mMid;
    }

    public String getMid() {
        return mMid;
    }

    public void insertContacts(ContactInfo contact) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
        	HSLog.d(TAG, "insert contact with content " + contact);
            String contactID = contact.mMid;
            boolean isContactExisted = isContactExisted(contactID, db);
            if (!isContactExisted) {
            	long r = db.insert(getContactTableName(), null, contact.getDBInfo());
                HSLog.d(TAG, "insert contact result: " + r);
            }
            else{
            	updateContact(contact);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }
    
    public void insertContacts(List<ContactInfo> contacts) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            for (ContactInfo contact : contacts) {
                HSLog.d(TAG, "insert contact with content " + contact);
                String contactID = contact.mMid;
                boolean isContactExisted = isContactExisted(contactID, db);
                if (!isContactExisted) {
                    long r = db.insert(getContactTableName(), null, contact.getDBInfo());
                    HSLog.d(TAG, "insert contact result: " + r);
                }
                else{
                	updateContact(contact);
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }


    public void updateContact(ContactInfo contact) {
        this.getWritableDatabase().update(getContactTableName(), contact.getDBInfo(), "contact_c_mid = ?", new String[] { contact.mMid });
    }

    private boolean isContactExisted(String contactID, SQLiteDatabase db) {
        Cursor c = db.rawQuery("select * from " + getContactTableName() + " where contact_c_mid = ?", new String[] { contactID });
        HSLog.e(TAG, "isContactExisted: " + c);
        boolean existed = false;
        try {
            if (null != c && c.moveToNext()) {
                existed = true;
            }
        } catch (Exception e) {
        } finally {
            if (null != c)
                c.close();
        }
        return existed;
    }

    public boolean checkContact(String contactID) {
        return isContactExisted(contactID, getReadableDatabase());
    }
    
    public List<ContactInfo> queryContacts(String conditions, String[] selectionArgs) {
        SQLiteDatabase db = getReadableDatabase();
        List<ContactInfo> contacts = new ArrayList<ContactInfo>();
        Cursor cursor = db.rawQuery("select * from " + getContactTableName() + " " + conditions, selectionArgs);
        while (cursor != null && cursor.moveToNext()) {
            ContactInfo contact = new ContactInfo(cursor);
            contacts.add(contact);
        }
        if (null != cursor) {
            cursor.close();
        }
        return contacts;
    }
    
    public List<ContactInfo> getAllContacts() {
        SQLiteDatabase db = getReadableDatabase();
    	List<ContactInfo> contacts = new ArrayList<ContactInfo>();
        Cursor c = db.rawQuery("select * from " + getContactTableName() 
        			+ " order by contact_c_last_msg_time DESC", null);
        while(c != null && c.moveToNext()) {
            contacts.add(new ContactInfo(c));
        }
        if (c != null)
            c.close();
        return contacts;
    }

    public void deleteAllContacts() {
        this.getWritableDatabase().execSQL("delete from " + getContactTableName());
    }

    private ContactInfo getContact(String contactId, SQLiteDatabase database) {
        Cursor c = database.rawQuery("select * from " + getContactTableName() + " where contact_c_mid = ?", new String[] { contactId });
        ContactInfo contact = null;
        if (c != null && c.moveToNext()) {
            contact = new ContactInfo(c);
        }
        if (c != null)
            c.close();
        return contact;
    }
    
    public ContactInfo getReadableContact(String contactId) {
        return getContact(contactId, getReadableDatabase());
    }

    public void deletContacts(List<ContactInfo> contacts) {
        SQLiteDatabase db = getWritableDatabase();
        String table = getContactTableName();
        db.beginTransaction();
        for (ContactInfo contact : contacts) {
            String contactID = contact.mMid;
            db.delete(table, "contact_c_mid = ?", new String[] { contactID });
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }
}
