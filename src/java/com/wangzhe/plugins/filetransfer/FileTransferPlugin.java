package com.wangzhe.plugins.filetransfer;

import java.io.File;

import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;

public class FileTransferPlugin implements Plugin{
	private IQFileTransferHandler fileTransferHandler
		= new IQFileTransferHandler();

	@Override
	public void destroyPlugin() {
		XMPPServer.getInstance().getIQRouter().removeHandler(fileTransferHandler);
	}

	@Override
	public void initializePlugin(PluginManager arg0, File arg1) {
		XMPPServer.getInstance().getIQRouter().addHandler(fileTransferHandler);
	}

}
