package com.wangzhe.plugins.filetransfer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jivesoftware.openfire.container.BasicModule;
import org.jivesoftware.util.cache.Cache;
import org.jivesoftware.util.cache.CacheFactory;

public class FileTransferManager extends BasicModule{
	private ServerSocket serverSocket;
	private static final int PORT = 7700;
	private Cache<String, FileTransfer> connectionMap;
	private ExecutorService executor = Executors.newCachedThreadPool();

	public FileTransferManager() {
		super("xmpp custom file transfer");
		connectionMap = CacheFactory.createCache("custom file transfer");
	}
	
	@Override
	public void start() throws IllegalStateException {
		super.start();
		try {
			serverSocket = new ServerSocket(PORT);
			while (!serverSocket.isClosed()) {
				final Socket socket = serverSocket.accept();
				executor.submit(new Runnable() {
					@Override
					public void run() {
						try {
							processIncomingSocket(socket);
						} catch (IOException e) {
							try {
								socket.close();
							} catch (IOException e1) {
								e1.printStackTrace();
							}
						}
					}
				});
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void processIncomingSocket(Socket connection) throws IOException{
		OutputStream out = new DataOutputStream(connection.getOutputStream());
        InputStream in = new DataInputStream(connection.getInputStream());
        
		 // first byte is version should be 5
        int b = in.read();
        if (b != 5) {
            throw new IOException("Only SOCKS5 supported");
        }
	}
	
	@Override
	public void destroy() {
		super.destroy();
		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
