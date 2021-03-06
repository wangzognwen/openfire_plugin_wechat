package com.wangzhe.plugins.message;

import java.util.List;

import org.jivesoftware.openfire.session.ClientSession;
import org.jivesoftware.openfire.user.PresenceEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;
import org.xmpp.packet.Presence;

import com.wangzhe.service.MessageService;

public class CustomPresenceListener implements PresenceEventListener{
	private MessageService messageService = 
			MessageService.getInstance();

	private static final Logger logger = LoggerFactory.getLogger(CustomPresenceListener.class);
	
	@Override
	public void availableSession(ClientSession session, Presence presence) {
	    logger.info("session available: " + session.toString());
		List<Message> unreadMessages = 
				messageService.getUnreadMessages(session.getAddress().getNode());
		for(Message message : unreadMessages){
			session.process(message);
		}
	}

	@Override
	public void unavailableSession(ClientSession session, Presence presence) {

	}

	@Override
	public void presenceChanged(ClientSession session, Presence presence) {

	}

	@Override
	public void subscribedToPresence(JID subscriberJID, JID authorizerJID) {

	}

	@Override
	public void unsubscribedToPresence(JID unsubscriberJID, JID recipientJID) {

	}

}
