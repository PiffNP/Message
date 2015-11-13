package com.ihs.demo.message_2013013306;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.ihs.message_2013013306.R;
import com.ihs.message_2013013306.managers.HSMessageChangeListener;
import com.ihs.message_2013013306.managers.HSMessageManager;
import com.ihs.message_2013013306.managers.HSMessageChangeListener.HSMessageChangeType;
import com.ihs.message_2013013306.types.ContactInfo;
import com.ihs.message_2013013306.types.HSBaseMessage;
import com.ihs.message_2013013306.types.HSOnlineMessage;
import com.socks.zlistview.ListViewAdapter;
import com.socks.zlistview.widget.ZListView;
import com.socks.zlistview.widget.ZListView.IXListViewListener;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Toast;

public class MessagesFragment extends Fragment {
	private ZListView listView;
	private Handler handler = new Handler();
	
	private List<ContactInfo> contactRecords = null;
	private ListViewAdapter mListViewAdapter;
    private HSMessageChangeListener mChangeListener;
	
	private synchronized void refresh(){
		if(DemoApplication.mContactDBManager != null){
        	mListViewAdapter.setData(DemoApplication.mContactDBManager.getAllContacts());
        	mListViewAdapter.notifyDataSetChanged();
		}
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mChangeListener = new HSMessageChangeListener(){

			@Override
			public void onMessageChanged(HSMessageChangeType changeType, List<HSBaseMessage> messages) {
				// TODO Auto-generated method stub
				handler.post(new Runnable() {
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
				handler.post(new Runnable() {
        			@Override
        			public void run() {
        				refresh();
        			}
        		});
			}

			@Override
			public void onReceivingRemoteNotification(JSONObject pushInfo) {
				// TODO Auto-generated method stub
				
			}
        	
        };
        HSMessageManager.getInstance().addListener(mChangeListener, handler);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	
    	View view = inflater.inflate(R.layout.fragment_messages, container, false);
        listView = (ZListView) view.findViewById(R.id.listview);
        listView.setXListViewListener(new IXListViewListener() {
        	@Override
        	public void onRefresh() {
        		handler.post(new Runnable() {
        			@Override
        			public void run() {
        				refresh();
        			}
        		});
        		handler.postDelayed(new Runnable() {
        			@Override
        			
        			public void run() {
        				listView.stopRefresh();
        			}
        		}, 1000);
        	}
        	@Override
        	public void onLoadMore() {
        		handler.post(new Runnable() {
        			@Override
        			public void run() {
        				refresh();
        			}
        		});
				handler.postDelayed(new Runnable() {
        			@Override
        			public void run() {
        				listView.stopLoadMore();
        			}
        		}, 1000);
        	}
        });
        
        listView.setPullLoadEnable(true);
        listView.setOnItemClickListener(new OnItemClickListener() {
        	@Override
        	public void onItemClick(AdapterView<?> parent, View view,
        								int position, long id) {
        		String mid = contactRecords.get(position - 1).mMid;
                String name = contactRecords.get(position - 1).mName;
                Intent intent = new Intent(MessagesFragment.this.getActivity(), ChatActivity.class);
                intent.putExtra("mid", mid);
                intent.putExtra("name", name);
                startActivity(intent);
        	}
        });
        /*
        listView.setOnItemLongClickListener(new OnItemLongClickListener() {
        	@Override
        	public boolean onItemLongClick(AdapterView<?> parent, View view,
        									int position, long id) {
        		Toast.makeText(MessagesFragment.this.getActivity(),
        						"onItemLongClick=" + position, Toast.LENGTH_SHORT).show();
        		return true;
        	}
        })//*/;
        if(DemoApplication.mContactDBManager != null){
        	contactRecords = DemoApplication.mContactDBManager.getAllContacts();
        }
        else{
        	contactRecords = new ArrayList<ContactInfo>();
        }
        mListViewAdapter = new ListViewAdapter(MessagesFragment.this.getActivity(),
        						contactRecords);
    	listView.setAdapter(mListViewAdapter);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
    
    @Override
    public void onResume(){
    	super.onResume();
    	handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				refresh();
			}
		}, 1000);
    }
}
