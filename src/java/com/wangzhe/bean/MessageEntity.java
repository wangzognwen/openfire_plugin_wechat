package com.wangzhe.bean;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.dom4j.Element;
import org.xmpp.packet.Message;
import org.xmpp.packet.Message.Type;

import com.wangzhe.util.TimeUtil;

/**
 * The Class MessageEntity.
 */
@XmlRootElement(name = "message")
public class MessageEntity {
	private Integer id;
	private String sender;
	private String receiver;
	private String type;
	private String packetId;
	private String body;
	private Date time;
	private String extra;
	private Integer flag;

	
	public MessageEntity() {
	}

	public MessageEntity(Integer id, String sender, String receiver, String type, 
			String packetId, String body, Date time, String extra, Integer flag) {
		this.id = id;
		this.sender = sender;
		this.receiver = receiver;
		this.type = type;
		this.packetId = packetId;
		this.body = body;
		this.time = time;
		this.extra = extra;
		this.flag = flag;
	}

	public MessageEntity(Message message){
		sender = message.getFrom().getNode();
		receiver = message.getTo().getNode();
		body = message.getBody();
		type = getTypeByMessageType(message.getType());
		packetId = message.getID();
		Element timeElement = 
				message.getChildElement("time", "xmpp:custom:time");
		if(timeElement != null){
			time = TimeUtil.format(timeElement.getText(), true);
		}
		Element extraElement =
				message.getChildElement("extra", null);
		if(extraElement != null){
			extra = extraElement.asXML();
		}
		flag = 0;
	}
	
	private String getTypeByMessageType(Type type){
		switch (type) {
	        case chat:
	           return "c";
	        case groupchat:
	           return "g";
	        case headline:
	           return "h";
	        case error:
	           return "e";
	        default:
	           return "n";
		}
	}

	public Integer getId() {
		return id;
	}


	public void setId(Integer id) {
		this.id = id;
	}


	public String getSender() {
		return sender;
	}


	public void setSender(String sender) {
		this.sender = sender;
	}


	public String getReceiver() {
		return receiver;
	}


	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public String getBody() {
		return body;
	}


	public void setBody(String body) {
		this.body = body;
	}


	public Date getTime() {
		return time;
	}


	public void setTime(Date time) {
		this.time = time;
	}


	public String getExtra() {
		return extra;
	}


	public void setExtra(String extra) {
		this.extra = extra;
	}

	public Integer getFlag() {
		return flag;
	}

	public void setFlag(Integer flag) {
		this.flag = flag;
	}

	public String getPacketId() {
		return packetId;
	}

	public void setPacketId(String packetId) {
		this.packetId = packetId;
	}

	
	
}
