package com.wangzhe.plugins.message;

import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.jasper.tagplugins.jstl.core.If;
import org.dom4j.Element;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.interceptor.PacketInterceptor;
import org.jivesoftware.openfire.interceptor.PacketRejectedException;
import org.jivesoftware.openfire.session.Session;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;

import com.wangzhe.service.MessageService;
import com.wangzhe.util.TimeUtil;

public class MessageInterceptor implements PacketInterceptor{
	private String utc = TimeZone.getDefault().
			getDisplayName(false, TimeZone.SHORT, Locale.getDefault());
	private MessageService messageService =
			MessageService.getInstance();

	@Override
	public void interceptPacket(Packet packet, Session session, boolean incoming, boolean processed) throws PacketRejectedException {
		if(!intercept(packet, session, incoming, processed)) return;
		Message message = (Message) packet;
		addTimeElement(message);
		storeMessage(message);
		replyClient(session, message);
	}
	
	private void addTimeElement(Message message){
		Element element = message.getElement();
		Element timeElement = 
				element.addElement("time", "xmpp:custom:time");
		timeElement.addAttribute("utc", utc);
		timeElement.addText(TimeUtil.format(new Date(), true));
	}
	
	private void storeMessage(Message message){
		if(message.getChildElement("x", "jabber:x:delay") == null){
			messageService.storeMessage(message);
		}
	}
	
	/**
	 * 告诉客户端这条消息已经收到了
	 * @param message
	 */
	private void replyClient(Session session, Message message){
		Message replyMessage = message.createCopy();
		replyMessage.setFrom(MessageComponent.NODE);
		replyMessage.setTo(message.getFrom());
		session.process(replyMessage);
	}
	
	private boolean intercept(Packet packet, Session session, boolean incoming, boolean processed){
		JID from = packet.getFrom();
		if(from == null){
			return false;
		}
		if(MessageComponent.NODE.equals(from.getNode())){
			return false;
		}
		if(packet instanceof Message && incoming && !processed){
			return true;
		}
		return false;
	}

}
