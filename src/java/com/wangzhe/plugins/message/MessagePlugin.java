package com.wangzhe.plugins.message;

import java.io.File;

import org.jivesoftware.openfire.component.InternalComponentManager;
import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.jivesoftware.openfire.interceptor.InterceptorManager;
import org.xmpp.component.ComponentException;

public class MessagePlugin implements Plugin{
	private MessageInterceptor messageInterCeptor
		= new MessageInterceptor();
	private MessageComponent messageComponent
		= new MessageComponent();

	@Override
	public void destroyPlugin() {
		InterceptorManager.getInstance().removeInterceptor(messageInterCeptor);
		InternalComponentManager.getInstance()
			.removeComponent(MessageComponent.NAME);
	}

	@Override
	public void initializePlugin(PluginManager arg0, File arg1) {
		InterceptorManager.getInstance().addInterceptor(messageInterCeptor);
		try {
			InternalComponentManager.getInstance().
				addComponent(MessageComponent.NAME, messageComponent);
		} catch (ComponentException e) {
			e.printStackTrace();
		}
	}

}
