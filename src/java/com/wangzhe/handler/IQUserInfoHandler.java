package com.wangzhe.handler;

import java.util.Date;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.jivesoftware.openfire.IQHandlerInfo;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.auth.UnauthorizedException;
import org.jivesoftware.openfire.handler.IQAuthHandler;
import org.jivesoftware.openfire.handler.IQHandler;
import org.jivesoftware.openfire.roster.Roster;
import org.jivesoftware.openfire.roster.RosterItem;
import org.jivesoftware.openfire.user.User;
import org.jivesoftware.openfire.user.UserManager;
import org.jivesoftware.openfire.user.UserNotFoundException;
import org.jivesoftware.openfire.user.UserProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.IQ;
import org.xmpp.packet.IQ.Type;
import org.xmpp.packet.JID;

import com.wangzhe.bean.UserBean;
import com.wangzhe.constant.ResultCode;
import com.wangzhe.util.StringUtil;

public class IQUserInfoHandler extends IQHandler{
	private static final Logger Log = LoggerFactory.getLogger(IQAuthHandler.class);

	private IQHandlerInfo info;
	private XMPPServer xmppServer;
	private UserProvider userProvider;

	public IQUserInfoHandler() {
		super("XMPP user info handler");
		info = new IQHandlerInfo("userinfo", "xmpp:custom:userinfo");
	}

	@Override
	public IQHandlerInfo getInfo() {
		return info;
	}

	@Override
	public IQ handleIQ(IQ packet) throws UnauthorizedException {
		JID to = packet.getTo();
		//这个iq应该是发给服务器的
		if(to == null || to.getNode() == null){
			Type type = packet.getType();
			if(type == Type.get){
				IQ reply;
				try {
					reply = queryUserInfo(packet);
				} catch (UserNotFoundException e) {
					reply = IQ.createResultIQ(packet);
					reply.setChildElement(packet.getChildElement().createCopy());
					reply.setType(Type.error);
					Element error = reply.getChildElement().addElement("error");
					ResultCode resultCode = ResultCode.USER_NOT_FOUND;
					error.addAttribute("code", resultCode.getValue() + "");
					error.addText(resultCode.toString());
				}
				return reply;
			}else if(type == Type.set){
				IQ reply;
				try {
					reply = updateUserInfo(packet);
				} catch (UserNotFoundException e) {
					reply = IQ.createResultIQ(packet);
					reply.setChildElement(packet.getChildElement().createCopy());
					reply.setType(Type.error);
					Element error = reply.getChildElement().addElement("error");
					ResultCode resultCode = ResultCode.USER_NOT_FOUND;
					error.addAttribute("code", resultCode.getValue() + "");
					error.addText(resultCode.toString());
				}
				return reply;
			}
		}else{
			IQ response = IQ.createResultIQ(packet);
			response.setChildElement(packet.getChildElement().createCopy());
			response.setType(Type.error);
			Element error = response.getChildElement().addElement("error");
			ResultCode resultCode = ResultCode.TO_JID_WRONG;
			error.addAttribute("code", resultCode.getValue() + "");
			error.addText(resultCode.toString());
			return response;
		}
		return packet;
	}
	
	private IQ queryUserInfo(IQ packet) throws UserNotFoundException{
		Element userElement = packet.getChildElement();
		String userName = userElement.attributeValue(UserBean.USERNAME);
		//请求查询自己的userInfo
		if(StringUtil.isEmpty(userName)){
			String fromName = packet.getFrom().getNode();
			User fromUser = userProvider.loadUser(fromName);
			return getReplyIQ(packet, fromUser);
		}else { //请求别人的userInfo
			User user = userProvider.loadUser(userName);
			return getReplyIQ(packet, user);
		}
	}
	
	/**
	 * 对应客户端不想更新的属性，不应该在iq包中加上这个属性。
	 * @param packet
	 * @return
	 * @throws UserNotFoundException
	 */
	private IQ updateUserInfo(IQ packet) throws UserNotFoundException{
		Element userElement = packet.getChildElement();
		String userName = userElement.attributeValue(UserBean.USERNAME);
		if(StringUtil.isEmpty(userName)){
			throw new UserNotFoundException();
		}else { //请求别人的userInfo
			User user = userProvider.loadUser(userName);
			Map<String, String> properties = user.getProperties();
			String nickName = userElement.attributeValue(UserBean.NICKNAME);
			if(nickName != null){
				properties.put(UserBean.NICKNAME, nickName);
			}
			String email = userElement.attributeValue(UserBean.EMAIL);
			if(email != null){
				properties.put(UserBean.EMAIL, email);
			}
			String telephone = userElement.attributeValue(UserBean.TELEPHONE);
			if(telephone != null){
				properties.put(UserBean.TELEPHONE, telephone);
			}
			String headUrl = userElement.attributeValue(UserBean.HEADURL);
			if(headUrl != null){
				properties.put(UserBean.HEADURL, headUrl);
			}
			String signature = userElement.attributeValue(UserBean.SIGNATURE);
			if(signature != null){
				properties.put(UserBean.SIGNATURE, signature);
			}
			String sex = userElement.attributeValue(UserBean.SEX);
			if(sex != null){
				properties.put(UserBean.SEX, sex);
			}
			String location = userElement.attributeValue(UserBean.LOCATION);
			if(location != null){
				properties.put(UserBean.LOCATION, location);
			}
			String birthday = userElement.attributeValue(UserBean.BIRTHDAY);
			if(birthday != null){
				properties.put(UserBean.BIRTHDAY, birthday);
			}
			String type = userElement.attributeValue(UserBean.TYPE);
			if(type != null){
				properties.put(UserBean.TYPE, type);
			}
			
			Date date = new Date();
			userProvider.setModificationDate(userName, date);
			userElement.addAttribute(UserBean.MODIFICATIONDATE, 
					date.getTime() + "");
			
			broadcastUpdateToRoster(user, packet);
			IQ reply = IQ.createResultIQ(packet);
			reply.setChildElement(packet.getChildElement().createCopy());
			return reply;
		}
	}
	
	private void broadcastUpdateToRoster(User user, IQ packet){
		Roster roster = user.getRoster();
		for(RosterItem rosterItem : roster.getRosterItems()){
			if(rosterItem.getSubStatus() == RosterItem.SUB_BOTH || 
					rosterItem.getSubStatus() == RosterItem.SUB_FROM){
				IQ iq = new IQ();
				iq.setType(Type.result);
				iq.setFrom(packet.getFrom());
				iq.setTo(rosterItem.getJid());
				iq.setChildElement(packet.getChildElement().createCopy());
				xmppServer.getIQRouter().route(iq);
			}
		}
	}
	
	private IQ getReplyIQ(IQ packet, User user){
		UserBean userBean = new UserBean(user);
		IQ reply = IQ.createResultIQ(packet);
		try {
			Document document = DocumentHelper.parseText(userBean.toXML());
			reply.setChildElement(document.getRootElement());
		} catch (DocumentException e) {
			Log.error(e.getMessage());
		}
		return reply;
	}
	
	@Override
	public void initialize(XMPPServer server) {
		super.initialize(server);
		xmppServer = XMPPServer.getInstance();
		userProvider = UserManager.getUserProvider();
	}

}
