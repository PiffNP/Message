package com.ihs.demo.message_2013013306;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.ImageColumns;
import android.provider.Telephony.Mms.Sent;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.MutableShort;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.Toast;

import java.io.File;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.baidu.location.e.n;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer.InitiateMatchResult;
import com.google.android.gms.internal.nm;
import com.ihs.account.api.account.HSAccountManager;
import com.ihs.app.framework.activity.HSActivity;
import com.ihs.commons.utils.HSError;
import com.ihs.commons.utils.HSLog;
import com.ihs.message_2013013306.R;
import com.ihs.message_2013013306.managers.HSMessageChangeListener;
import com.ihs.message_2013013306.managers.HSMessageManager;
import com.ihs.message_2013013306.managers.HSMessageManager.SendMessageCallback;
import com.ihs.message_2013013306.types.ContactInfo;
import com.ihs.message_2013013306.types.HSBaseMessage;
import com.ihs.message_2013013306.types.HSImageMessage;
import com.ihs.message_2013013306.types.HSMessageType;
import com.ihs.message_2013013306.types.HSOnlineMessage;
import com.ihs.message_2013013306.types.HSTextMessage;
import com.ihs.message_2013013306.types.HSBaseMessage.HSMessageStatus;

//聊天主界面
public class ChatActivity extends HSActivity{
	private String contactId;
	private String contactName;
	private String lastMsgTime;
	
	private HSActivity mActivity = this;
	private InputMethodManager imm;
	
	private ListView mMsgListView;
	private MessageAdapter mMsgAdapter;
	
	private LinearLayout mChatInputBoxLayout;
	private Button mVoiceSelectBtn;
	private EditText mChatInputText;
	private Button mVoicePressBtn;
	private Button mExtraTypeBtn;
	private Button mSendBtn;
	
	private boolean useVoice;
	private boolean useExtra;
	
	private TableLayout mExtraLayout;
	private ImageButton mPhotoBtn;
	private ImageButton mCameraBtn;
	
	private static final int PHOTO_REQUEST_TAKEPHOTO = 1;
    private static final int PHOTO_REQUEST_GALLERY = 2;

    private Uri photoUri;
	
    private SoundPool soundPool;
    
    private Handler mHandler = new Handler();
    private HSMessageChangeListener mChangeListener;
    public static String curMID = null;
    
    private static final String TAG = "chat";
    
    //标记当前会话中的消息已读
    private void markRead(){
    	HSLog.d(TAG, "id" + contactId);
    	List<HSBaseMessage> mRawList = HSMessageManager.getInstance().queryMessages(contactId, 100, -1).getMessages();
    	List<HSBaseMessage> mReadList = new ArrayList<HSBaseMessage>();
		for(HSBaseMessage message: mRawList){
			if(message.getType() == HSMessageType.TEXT){
				if(message.getFrom().equals(contactId) && message.getStatus() != HSMessageStatus.READ){
					mReadList.add(message);
				}
			}
		}
		
		if(mReadList.size() > 0)
			HSMessageManager.getInstance().markRead(mReadList);
		refresh();
    }
    
    //刷新消息
    private synchronized void refresh(){
    	HSLog.d(TAG, "id" + contactId);
    	List<HSBaseMessage> mRawList = HSMessageManager.getInstance().queryMessages(contactId, 100, -1).getMessages();
		List<MsgRecord> msgRecords = new ArrayList<MsgRecord>();
		for(HSBaseMessage message: mRawList){
			if(message.getType() == HSMessageType.TEXT){
				String id;
				Integer type;
				Integer state; 		// 0-sending | 1-success | 2-fail
				String fromMID;
				String toMID;
				Object content;

				Boolean isSend;
				Boolean readState;
				Date time;
				
				id = message.getMsgID();
				type = 0;
				content = ((HSTextMessage) message).getText();
				time = message.getTimestamp();

				if(message.getFrom().equals(contactId)){
					state = 1;
					fromMID = contactName;
					toMID = HSAccountManager.getInstance().getMainAccount().getMID();
					isSend = false;
					switch (message.getStatus()) {
						case READ:
							readState = true;
							break;
				
						default:
							readState = false;
							break;
					}
				}
				else{
					switch (message.getStatus()) {
						case SENDING:
							state = 0;
							break;
						
						case FAILED:
							state = 2;
							break;
						
						default:
							state = 1;
							break;
					}
					fromMID = HSAccountManager.getInstance().getMainAccount().getMID();
					toMID = contactName;
					isSend = true;
					readState = true;
				}
				msgRecords.add(0, new MsgRecord(id, type, state, fromMID, toMID, content,
						isSend, time, readState));
			}
			else if(message.getType() == HSMessageType.IMAGE){
				String id;
				Integer type;
				Integer state; 		// 0-sending | 1-success | 2-fail
				String fromMID;
				String toMID;
				Object content;

				Boolean isSend;
				Boolean readState;
				Date time;
				
				id = message.getMsgID();
				type = 1;
				content = message;
				time = message.getTimestamp();

				if(message.getFrom().equals(contactId)){
					state = 1;
					fromMID = contactName;
					toMID = HSAccountManager.getInstance().getMainAccount().getMID();
					isSend = false;
					switch (message.getStatus()) {
						case READ:
							readState = true;
							break;
				
						default:
							readState = false;
							break;
					}
				}
				else{
					switch (message.getStatus()) {
						case SENDING:
							state = 0;
							break;
						
						case FAILED:
							state = 2;
							break;
						
						default:
							state = 1;
							break;
					}
					fromMID = HSAccountManager.getInstance().getMainAccount().getMID();
					toMID = contactName;
					isSend = true;
					readState = true;
				}
				msgRecords.add(0, new MsgRecord(id, type, state, fromMID, toMID, content,
						isSend, time, readState));
			}
			else{
				//暂不处理其他类型的消息
				//msgRecords.add(0, new MsgRecord(0, 0, "Tom", "Jerry", "not implemented",
				//		true, new Date(System.currentTimeMillis()), false));
			}
		}
		
        mMsgAdapter.setData(msgRecords);
		mMsgAdapter.notifyDataSetChanged();
    }
    
    public static void init(){
	
    }
    
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Intent mIntent = getIntent();
        contactId = mIntent.getStringExtra("mid");
        curMID = contactId;

        mChangeListener = new HSMessageChangeListener(){

			@Override
			public void onMessageChanged(HSMessageChangeType changeType, List<HSBaseMessage> messages) {
				// TODO Auto-generated method stub
				mHandler.post(new Runnable() {
        			@Override
        			public void run() {
        				refresh();
        			}
        		});
			}

			@Override
			public void onTypingMessageReceived(String fromMid) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onOnlineMessageReceived(HSOnlineMessage message) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onUnreadMessageCountChanged(String mid, int newCount) {
				// TODO Auto-generated method stub
				mHandler.post(new Runnable() {
        			@Override
        			public void run() {
        				markRead();
        			}
        		});
			}

			@Override
			public void onReceivingRemoteNotification(JSONObject pushInfo) {
				// TODO Auto-generated method stub
				
			}
        	
        };
        HSMessageManager.getInstance().addListener(mChangeListener, mHandler);
        
	    soundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
	    soundPool.load(this,R.raw.message_ringtone_sent, 1);
	    
        setContentView(R.layout.activity_chat);
	    
        useVoice = false;
                
        mMsgListView = (ListView) findViewById(R.id.messageListView);
        
        mChatInputBoxLayout = (LinearLayout) findViewById(R.id.chatInputBox);
        mVoiceSelectBtn = (Button) findViewById(R.id.voiceButton);
        mChatInputText = (EditText) findViewById(R.id.messageEditText);
        mVoicePressBtn = (Button) findViewById(R.id.speakButton);
        mExtraTypeBtn = (Button) findViewById(R.id.extraTypeButton);
        mSendBtn = (Button) findViewById(R.id.sendButton);
        mExtraLayout = (TableLayout)findViewById(R.id.extraLayout);
        mPhotoBtn = (ImageButton) findViewById(R.id.imageButton);
        mCameraBtn = (ImageButton) findViewById(R.id.cameraButton);
        imm = (InputMethodManager)mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        
        mVoiceSelectBtn.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(mChatInputText.getWindowToken(), 0);
            	if(useVoice){
            		mVoiceSelectBtn.setBackgroundResource(R.drawable.chatting_setmode_msg_btn);
            		mVoicePressBtn.setVisibility(View.GONE);
            		mSendBtn.setVisibility(View.VISIBLE);
            		mChatInputText.setVisibility(View.VISIBLE);
            	}
            	else{
            		mVoiceSelectBtn.setBackgroundResource(R.drawable.chatting_setmode_voice_btn);
            		mVoicePressBtn.setVisibility(View.VISIBLE);
            		mChatInputText.setVisibility(View.GONE);
            		mSendBtn.setVisibility(View.GONE);
            	}
            	useVoice = !useVoice;
            }
        });
        mSendBtn.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
            	if(useVoice)
            		return;
            	else{
            		MsgRecord message = new MsgRecord("", 0, 0, HSAccountManager.getInstance().getMainAccount().getMID(), contactName, mChatInputText.getText().toString(),
            										true, new Date(System.currentTimeMillis()), false);
            		mChatInputText.setText("");
            		sendMsg(message);
            	}
            }
        });
        mExtraTypeBtn.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(mChatInputText.getWindowToken(), 0);
            	if(useExtra){
                	mExtraLayout.setVisibility(View.VISIBLE);
            	}
            	else{
            		mExtraLayout.setVisibility(View.GONE);
            	}
            	useExtra = !useExtra;
            }
        });
        mChatInputText.addTextChangedListener(new TextWatcher(){             
            @Override 
            public void afterTextChanged(Editable s) {  
                if(s.length() != 0)
                	mSendBtn.setVisibility(View.VISIBLE);
                else
                	mSendBtn.setVisibility(View.GONE);
            }

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub		
			}  
        });
		mChatInputText.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus){
                    imm.hideSoftInputFromWindow(mChatInputText.getWindowToken(), 0);
				}
			}			
		});
			
		mMsgListView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				imm.hideSoftInputFromWindow(mChatInputText.getWindowToken(), 0);
				return false;
			}
		});
				
		mPhotoBtn.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
        		startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
            }
        });
		mCameraBtn.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
            	File tempFile = createPhotoFile();
            	if(tempFile == null)
            		return;
            	Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri = Uri.fromFile(tempFile));
                startActivityForResult(intent, PHOTO_REQUEST_TAKEPHOTO);
            }
        });
		
        mMsgAdapter = new MessageAdapter(this, null);
		markRead();        
		mMsgListView.setAdapter(mMsgAdapter);
		mMsgAdapter.notifyDataSetChanged();
		
		//载入数据库中状态到当前聊天界面
        ContactInfo mContactInfo = DemoApplication.mContactDBManager.getReadableContact(contactId);
        if(mContactInfo != null){
        	contactName = mContactInfo.mName;
        	lastMsgTime = mContactInfo.mLastMsgTime;
        	//TODO avatar
        	if(!mContactInfo.mLastMsgExtra.equals(""))
        		mChatInputText.setText(mContactInfo.mLastMsgTxt);
        	int lastSelItem = mContactInfo.mLastSelItem;
        	mMsgListView.setSelection(lastSelItem != ListView.INVALID_POSITION? lastSelItem: mMsgAdapter.getCount() - 1);
        }
        else{
        	contactName = mIntent.getStringExtra("name");
        	mMsgListView.setSelection(mMsgAdapter.getCount() - 1);
        }

	}
	
	@Override
    protected void onDestroy() {
		String lastMsgTime;
		String lastMsgExtra;
		String lastMsgTxt;
		
		curMID = null;
		markRead();
		
		int lastSelItem = mMsgListView.getSelectedItemPosition();
		if(mChatInputText.getText().toString().equals("")){
			lastMsgTime = "";
			lastMsgExtra = "";
			if(mMsgAdapter.getData().size() == 0){
				lastMsgTxt = "";
				if(!DemoApplication.mContactDBManager.checkContact(contactId)){
					super.onDestroy();
					return;
				}
			}
			else{
				MsgRecord lastMsg = mMsgAdapter.getData().get(mMsgAdapter.getData().size() - 1);
				lastMsgTime = DateFormat.format("yyyy-MM-dd-HH-mm-ss", lastMsg.getTime()).toString();
				int lastMsgType = lastMsg.getType();
				switch (lastMsgType) {
					case 0:
						lastMsgTxt = (String)lastMsg.getContent();
						break;
					case 1:
						lastMsgTxt = "[图片]";
						break;
					default:
						lastMsgTxt = "";
				}
			}
		}
		else{
			lastMsgTime = DateFormat.format("yyyy-MM-dd-HH-mm-ss", new Date(System.currentTimeMillis()).getTime()).toString();
			lastMsgExtra = "[草稿]";
			lastMsgTxt = mChatInputText.getText().toString();
		}
		
		//存储当前聊天界面状态
		ContactInfo mContactInfo = new ContactInfo(contactId, contactName, "", lastMsgTxt, lastMsgTime, 
									lastMsgExtra, lastSelItem, 0);
		DemoApplication.mContactDBManager.insertContacts(mContactInfo);
		HSMessageManager.getInstance().removeListener(mChangeListener);
        super.onDestroy();
	}
	
	@Override
	protected void onStart() {
		DemoApplication.nm.cancelAll();
		super.onStart();
	};
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
		HSImageMessage msg;
        super.onActivityResult(requestCode, resultCode, data);
		if(resultCode != Activity.RESULT_OK)
			return;
        switch (requestCode) {
        	case PHOTO_REQUEST_TAKEPHOTO:
        		if(photoUri == null)
        			break;
        		msg = new HSImageMessage(contactId, new File(photoUri.getPath()).getAbsolutePath());
        		sendMsg(new MsgRecord("", 1, 0, HSAccountManager.getInstance().getMainAccount().getMID(), contactName, msg,
						true, new Date(System.currentTimeMillis()), false));
        		break;

        	case PHOTO_REQUEST_GALLERY:
        		if(data == null || data.getData() == null)
        			break;
        		msg = new HSImageMessage(contactId, getRealFilePath(mActivity, data.getData()));
        		sendMsg(new MsgRecord("", 1, 0, HSAccountManager.getInstance().getMainAccount().getMID(), contactName, msg,
						true, new Date(System.currentTimeMillis()), false));
        		break;
        }
    }
	
	private void sendMsg(MsgRecord message){
		//imm.hideSoftInputFromWindow(mChatInputText.getWindowToken(), 0);
	    soundPool.play(1, 1, 1, 0, 0, 1);
		mMsgAdapter.getData().add(message);
		mMsgListView.setSelection(mMsgListView.getBottom());
		mExtraLayout.setVisibility(View.GONE);
		mMsgAdapter.notifyDataSetChanged();
		
		if(message.getType() == 0){
			HSMessageManager.getInstance().send(new HSTextMessage(contactId, (String)message.getContent()), new SendMessageCallback() {

				@Override
				public void onMessageSentFinished(HSBaseMessage message, boolean success, HSError error) {
					mHandler.post(new Runnable() {
	        			@Override
	        			public void run() {
	        				refresh();
	        			}
	        		});
				}
			}, new Handler());
		}
		else if(message.getType() == 1){
			HSMessageManager.getInstance().send((HSImageMessage)(message.getContent()), new SendMessageCallback() {

				@Override
				public void onMessageSentFinished(HSBaseMessage message, boolean success, HSError error) {
					mHandler.post(new Runnable() {
	        			@Override
	        			public void run() {
	        				refresh();
	        			}
	        		});
				}
			}, new Handler());
		}
	}
	
	private File createPhotoFile(){
		String fileDirPath = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() 
	            				+ "/iMessage/Camera";  
	    String filename = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss").format(new Date(System.currentTimeMillis()))
	    					+ ".jpg";
	    String filePath = fileDirPath + "/" + filename;
	    File dir = new File(fileDirPath); 
		File imageFile = new File(filePath); 
	    boolean hasSDCard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	    boolean fileState = true;
	    try {
    		if(!hasSDCard){
	    		Toast.makeText(this, "SD Card not found!", Toast.LENGTH_LONG).show();
                fileState = false;
	    	}    		
	    	else{
	    		if ((!dir.exists() && !dir.mkdirs()) || !imageFile.createNewFile()) {
		    		Toast.makeText(this, "Cannot create file!", Toast.LENGTH_LONG).show();
	                fileState = false;
	            }  
	        }
	    }
	    catch (Exception e) {  
	    	e.printStackTrace();
	    }
		return (fileState? imageFile: null);
	}
	
	public static String getRealFilePath(final Context context, final Uri uri) {
	    if ( null == uri ) return null;
	    final String scheme = uri.getScheme();
	    String data = null;
	    if ( scheme == null )
	        data = uri.getPath();
	    else if (ContentResolver.SCHEME_FILE.equals( scheme ) ) {
	        data = uri.getPath();
	    } else if (ContentResolver.SCHEME_CONTENT.equals( scheme ) ) {
	        Cursor cursor = context.getContentResolver().query( uri, new String[] { ImageColumns.DATA }, null, null, null );
	        if ( null != cursor ) {
	            if ( cursor.moveToFirst() ) {
	                int index = cursor.getColumnIndex( ImageColumns.DATA );
	                if ( index > -1 ) {
	                    data = cursor.getString( index );
	                }
	            }
	            cursor.close();
	        }
	    }
	    return data;
	}
}
