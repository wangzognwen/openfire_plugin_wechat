package com.wangzhe.plugins.message;

import com.wangzhe.service.MessageService;
import com.wangzhe.util.MessageUtil;
import com.wangzhe.util.TimeUtil;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.dom4j.Element;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.interceptor.PacketInterceptor;
import org.jivesoftware.openfire.interceptor.PacketRejectedException;
import org.jivesoftware.openfire.session.Session;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;

public class MessageInterceptor implements PacketInterceptor{
	private MessageService messageService =
			MessageService.getInstance();

    public static final String NAME = "receipt";

    private static final String SERVER_NAME =
            XMPPServer.getInstance().getServerInfo().getXMPPDomain();

    public static final String NODE =
            NAME + "." + SERVER_NAME;

	@Override
	public void interceptPacket(Packet packet, Session session, boolean incoming, boolean processed) throws PacketRejectedException {
		if(!intercept(packet, session, incoming, processed)) return;
		Message message = (Message) packet;
		MessageUtil.addTimeElement(message, new Date());
		storeMessage(message);
		replyClient(session, message);
	}
	

	
	private void storeMessage(Message message){
        messageService.storeMessage(message);
	}
	
	/**
	 * 告诉客户端这条消息已经收到了
	 * @param message
	 */
	private void replyClient(Session session, Message message){
		Message replyMessage = message.createCopy();
		replyMessage.setFrom(NODE);
		replyMessage.setTo(message.getFrom());
		replyMessage.setType(Message.Type.normal);
		session.deliverRawText(replyMessage.toXML());
	}
	
	private boolean intercept(Packet packet, Session session, boolean incoming, boolean processed){
		JID from = packet.getFrom();
		if(from == null){
			return false;
		}
		if(NODE.equals(from.getNode())){
			return false;
		}
		if(packet instanceof Message && incoming && !processed){
		    Message message = (Message) packet;
		    //不是离线消息
            if(message.getChildElement("x", "jabber:x:delay") == null){
                return true;
            }
			return false;
		}
		return false;
	}

}
