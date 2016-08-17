package com.wangzhe.plugins.user;

import com.wangzhe.handler.IQUserInfoHandler;
import java.io.File;

import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;

public class UserInfoPlugin implements Plugin{ 
	private XMPPServer xmppServer = XMPPServer.getInstance();
	private IQUserInfoHandler userInfoHandler = new IQUserInfoHandler();

	@Override
	public void initializePlugin(PluginManager arg0, File arg1) {
		xmppServer.getIQRouter().addHandler(userInfoHandler);
	}
	
	@Override
	public void destroyPlugin() {
		xmppServer.getIQRouter().removeHandler(userInfoHandler);
	}


}
