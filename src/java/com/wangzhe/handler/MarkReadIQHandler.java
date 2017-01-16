package com.wangzhe.handler;

import org.dom4j.Element;
import org.jivesoftware.openfire.IQHandlerInfo;
import org.jivesoftware.openfire.auth.UnauthorizedException;
import org.jivesoftware.openfire.handler.IQHandler;
import org.xmpp.packet.IQ;

import com.wangzhe.service.MessageService;

public class MarkReadIQHandler extends IQHandler{
	private IQHandlerInfo handlerInfo;

	public MarkReadIQHandler() {
		super("XMPP mark message read handler");
		handlerInfo = new IQHandlerInfo("read", "xmpp:custom:read");
	}
	
	private MessageService messageService = MessageService.getInstance();

	@Override
	public IQ handleIQ(IQ packet) throws UnauthorizedException {
		Element readElement = packet.getChildElement();
		String packetId = readElement.attributeValue("packetId");
		
		messageService.markMessageAsRead(packetId);
		
		IQ reply = IQ.createResultIQ(packet);
		reply.setChildElement(packet.getChildElement().createCopy());
		return reply;
	}

	@Override
	public IQHandlerInfo getInfo() {
		return handlerInfo;
	}

}
