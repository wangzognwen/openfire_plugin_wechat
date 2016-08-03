package com.wangzhe.plugins.message;

import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.session.Session;
import org.xmpp.component.Component;
import org.xmpp.component.ComponentException;
import org.xmpp.component.ComponentManager;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;

import com.wangzhe.dao.MessageDao;
import com.wangzhe.service.MessageService;


public class MessageComponent implements Component{
	public static final String NAME = "receipt";
	
	private static final String SERVER_NAME =
			XMPPServer.getInstance().getServerInfo().getXMPPDomain();
	
	public static final String NODE =
			NAME + "." + SERVER_NAME;
	
	private MessageService messageService = MessageService.getInstance();

	@Override
	public String getDescription() {
		return null;
	}

	@Override
	public String getName() {
		return "receipt";
	}

	@Override
	public void initialize(JID arg0, ComponentManager arg1) throws ComponentException {
		
	}

	@Override
	public void processPacket(Packet packet) {
		if(packet instanceof Message){
			Message message = (Message) packet;
			messageService.markMessageAsRead(message);
		}
	}

	@Override
	public void shutdown() {
		
	}

	@Override
	public void start() {
		
	}


}
