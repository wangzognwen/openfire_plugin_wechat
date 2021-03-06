package com.wangzhe.service;

import com.wangzhe.bean.MessageEntity;
import com.wangzhe.dao.MessageDao;

import com.wangzhe.util.MessageUtil;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.user.UserManager;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;

import java.util.ArrayList;
import java.util.List;

public class MessageService {
	private static final MessageService mInstance =
			new MessageService();
	private MessageDao messageDao = MessageDao.getInstance();

	private static final String SUFFIX = "@" + XMPPServer.getInstance().getServerInfo().getXMPPDomain();
	
	public static MessageService getInstance(){
		return mInstance;
	}
	
	private MessageService(){
		
	}
	
	public boolean storeMessage(Message message){
		if(shouldStoreMessage(message)){
			JID recipient = message.getTo();
		    String username = recipient.getNode();
		        // If the username is null (such as when an anonymous user), don't store.
		    if (username == null || !UserManager.getInstance().isRegisteredUser(recipient)) {
		       return false;
		    }else if (!XMPPServer.getInstance().getServerInfo().getXMPPDomain().equals(recipient.getDomain())) {
		        // Do not store messages sent to users of remote servers
		       return false;
		    }
		    
		    MessageEntity messageEntity = new MessageEntity(message);
		    return messageDao.addMessage(messageEntity);
		}
		
		return false;
	}
	
	public void markMessageAsRead(String packetId){
		messageDao.updateMessageFlag(packetId, 1);
	}

	public List<Message> getUnreadMessages(String userName){
	    List<MessageEntity> messageEntities = messageDao.getUnreadMessages(userName);
	    List<Message> unreadMessages = new ArrayList<>();
	    for(MessageEntity messageEntity : messageEntities){
	        Message message = new Message();
	        message.setID(messageEntity.getPacketId());
	        message.setFrom(messageEntity.getSender() + SUFFIX);
	        message.setTo(messageEntity.getReceiver() + SUFFIX);
	        message.setBody(messageEntity.getBody());
	        message.setType(Message.Type.chat);
            MessageUtil.addTimeElement(message, messageEntity.getTime());
            MessageUtil.addDelayElement(message, messageEntity.getTime());
            unreadMessages.add(message);
        }

        return unreadMessages;
    }
	
	private boolean shouldStoreMessage(Message message){
		if (message.getChildElement("no-store", "urn:xmpp:hints") != null) {
            return false;
		}
		switch (message.getType()) {
	        case chat:
	            return true;
	        case groupchat:
	        case headline:
	            // XEP-0160: "groupchat" message types SHOULD NOT be stored offline
	            // XEP-0160: "headline" message types SHOULD NOT be stored offline
	            return false;
	        case error:
	            return false;
	        default:
	            // XEP-0160: Messages with a 'type' attribute whose value is "normal" (or messages with no 'type' attribute) SHOULD be stored offline.
	            break;
		 }
		 return true;
	}
}
