package com.wangzhe.plugins.filetransfer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.dom4j.Element;
import org.jivesoftware.openfire.IQHandlerInfo;
import org.jivesoftware.openfire.SessionManager;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.auth.UnauthorizedException;
import org.jivesoftware.openfire.filetransfer.proxy.ProxyTransfer;
import org.jivesoftware.openfire.handler.IQHandler;
import org.jivesoftware.openfire.session.LocalClientSession;
import org.jivesoftware.util.StringUtils;
import org.jivesoftware.util.cache.Cache;
import org.jivesoftware.util.cache.CacheFactory;
import org.xmpp.packet.IQ;


public class IQFileTransferHandler extends IQHandler{
	private IQHandlerInfo info;
	private String domain;
	private Map<String, FileTransfer> connectionMap;
	private static IQFileTransferHandler mInstance;

	public IQFileTransferHandler() {
		super("xmpp file transfer handler");
		info = new IQHandlerInfo("transfer", "xmpp:custom:transfer");
		domain = XMPPServer.getInstance().getServerInfo().getXMPPDomain();
		connectionMap = new ConcurrentHashMap<>();
		mInstance = this;
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
			
			String digest = StringUtils.hash(initiator + target, "SHA-1");
			connectionMap.put(digest, fileTransfer);
			transferElement.addAttribute("digest", digest);
			
			IQ replyPacket = IQ.createResultIQ(packet);
			replyPacket.setFrom(domain);
			replyPacket.setChildElement(packet.getChildElement().createCopy());
			return replyPacket;
		}
		
		return packet;
	}
	
	public static IQFileTransferHandler getInstance(){
		return mInstance;
	}

	public Map<String, FileTransfer> getConnectionMap() {
		return connectionMap;
	}
	
	

}
