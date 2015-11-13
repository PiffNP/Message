package com.ihs.demo.message_2013013306;

import java.util.Date;

public class MsgRecord {
	public final static int MSG_TYPE_TEXT 	= 0;
	public final static int MSG_TYPE_PHOTO 	= 1;
	public final static int MSG_TYPE_FACE 	= 2;
	
	public final static int MSG_STATE_SENDING 	= 0;
	public final static int MSG_STATE_SUCCESS 	= 1;
	public final static int MSG_STATE_FAIL 		= 2;
	
	private String id;
	private Integer type;		// 0-text | 1-photo | 2-face | more type ... TODO://
	private Integer state; 		// 0-sending | 1-success | 2-fail
	private String fromMID;
	private String toMID;
	private Object content;

	private Boolean isSend;
	private Boolean readState; 
	private Date time;

	public MsgRecord(String id, Integer type, Integer state, String fromMID,
			String toMID, Object content, Boolean isSend, Date time,
			Boolean readState) {
		super();
		this.id = id;
		this.type = type;
		this.state = state;
		this.fromMID = fromMID;
		this.toMID = toMID;
		this.content = content;
		this.isSend = isSend;
		this.time = time;
		this.readState = readState;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public String getfromMID() {
		return fromMID;
	}

	public void setfromMID(String fromMID) {
		this.fromMID = fromMID;
	}

	public String gettoMID() {
		return toMID;
	}

	public void settoMID(String toMID) {
		this.toMID = toMID;
	}

	public Object getContent() {
		return content;
	}

	public void setContent(Object content) {
		this.content = content;
	}

	public Boolean getisSend() {
		return isSend;
	}

	public void setisSend(Boolean isSend) {
		this.isSend = isSend;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}
	
	public Boolean getReadState(){
		return readState;
	}
	
	public void setReadState(Boolean readState){
		this.readState = readState;
	}
}
