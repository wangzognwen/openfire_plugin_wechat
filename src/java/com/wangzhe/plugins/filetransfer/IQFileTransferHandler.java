package com.wangzhe.plugins.filetransfer;

import org.dom4j.Element;
import org.jivesoftware.openfire.IQHandlerInfo;
import org.jivesoftware.openfire.SessionManager;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.auth.UnauthorizedException;
import org.jivesoftware.openfire.handler.IQHandler;
import org.jivesoftware.openfire.session.LocalClientSession;
import org.xmpp.packet.IQ;

public class IQFileTransferHandler extends IQHandler{
	private IQHandlerInfo info;

	public IQFileTransferHandler() {
		super("xmpp file transfer handler");
		info = new IQHandlerInfo("transfer", "xmpp:custom:transfer");
	}

	@Override
	public IQHandlerInfo getInfo() {
		return info;
	}

	@Override
	public IQ handleIQ(IQ packet) throws UnauthorizedException {
		if(packet.getType().equals(IQ.Type.get) ||
				packet.getType().equals(IQ.Type.get)){
			Element transferElement = packet.getChildElement();
			String sid = transferElement.attributeValue("id");
			String mimeType = transferElement.attributeValue("mime-type");
			
			Element fileElement = transferElement.element("file");
			String fileName = fileElement.attributeValue("name");
			String fileSizeAttr = fileElement.attributeValue("size");
			long fileSize = 0;
			try {
				fileSize = Long.parseLong(fileSizeAttr);
			} catch (Exception e) {}
			
			String initiator = packet.getFrom().toString();
			String target = packet.getTo().toString();
			FileTransfer fileTransfer = 
					new FileTransfer(initiator, target, sid, fileName, fileSize, mimeType);
			
			LocalClientSession session = 
					(LocalClientSession) SessionManager.getInstance().getSession(packet.getFrom());
			
		}
		
		return null;
	}

}
