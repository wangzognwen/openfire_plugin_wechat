package com.wangzhe.plugins.message;

import java.io.File;

import com.wangzhe.handler.MarkReadIQHandler;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.component.InternalComponentManager;
import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.jivesoftware.openfire.interceptor.InterceptorManager;
import org.jivesoftware.openfire.user.PresenceEventDispatcher;
import org.xmpp.component.ComponentException;

public class MessagePlugin implements Plugin{
	private MessageInterceptor messageInterCeptor
		= new MessageInterceptor();
	private CustomPresenceListener customPresenceListener = new CustomPresenceListener();
	private MarkReadIQHandler markReadIQHandler = new MarkReadIQHandler();

	@Override
	public void destroyPlugin() {
		InterceptorManager.getInstance().removeInterceptor(messageInterCeptor);
		PresenceEventDispatcher.removeListener(customPresenceListener);
		XMPPServer.getInstance().getIQRouter().removeHandler(markReadIQHandler);
	}

	@Override
	public void initializePlugin(PluginManager arg0, File arg1) {
		InterceptorManager.getInstance().addInterceptor(messageInterCeptor);
        PresenceEventDispatcher.addListener(customPresenceListener);
        XMPPServer.getInstance().getIQRouter().addHandler(markReadIQHandler);
	}

}
