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
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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

//用于照片全屏显示
public class PhotoActivity extends HSActivity{
	private ImageView mImageView;
	
	@Override
	protected void onStart() {
		DemoApplication.nm.cancelAll();
		super.onStart();
	};
    
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Intent mIntent = getIntent();
        String path = mIntent.getStringExtra("Path");
        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        requestWindowFeature(Window.FEATURE_NO_TITLE); 
        
        setContentView(R.layout.activity_photo);
        mImageView = (ImageView) findViewById(R.id.photoImageView);
        mImageView.setImageURI(Uri.fromFile(new File(path)));
        
        mImageView.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
            	finish();
            }
        });//*/
	}
}
