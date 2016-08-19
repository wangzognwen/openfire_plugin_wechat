package com.wangzhe.plugins.filetransfer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.container.BasicModule;
import org.jivesoftware.util.JiveGlobals;
import org.jivesoftware.util.PropertyEventListener;
import org.jivesoftware.util.cache.Cache;
import org.jivesoftware.util.cache.CacheFactory;

import com.wangzhe.util.FileUtil;

public class FileTransferManager{
	private ServerSocket serverSocket;
	private int port;
	private Map<String, FileTransfer> connectionMap;
	private ExecutorService executor = Executors.newCachedThreadPool();

	public FileTransferManager() {
		connectionMap = IQFileTransferHandler.getInstance().getConnectionMap();
		port = JiveGlobals.getIntProperty("xmpp.custom.filetransfer.port", 7700);
		
	}
	

	public void start() throws IllegalStateException {
		executor.submit(new Runnable() {
			@Override
			public void run() {
				try {
					serverSocket = new ServerSocket(port);
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
		});
		
	}
	
	private void processIncomingSocket(Socket connection) throws IOException{
		OutputStream out = new DataOutputStream(connection.getOutputStream());
        InputStream in = new DataInputStream(connection.getInputStream());
        
		 // first byte is version should be 5
        int b = in.read();
        if (b != 5) {
            throw new IOException("Only SOCKS5 supported");
        }
        int digestlength = in.read();
        byte[] addr = new byte[digestlength];
        int read = in.read(addr, 0, addr.length);
        if (read != addr.length) {
            throw new IOException("Error reading provided address");
        }
        String digest = new String(addr);
        FileTransfer fileTransfer = connectionMap.get(digest);
        if(fileTransfer == null){
        	throw new IOException("forbidden transfer");
        }
   
        saveFile(in, fileTransfer);
        connection.shutdownInput();
        connectionMap.remove(digest);
        byte[] resonse = "success".getBytes();
        out.write(resonse);
        out.flush();
        out.close();
	}
	
	private void saveFile(InputStream in, FileTransfer fileTransfer){
		String filePath = JiveGlobals.getProperty("file.transfer.path", 
				"D:\\img");
		File file = new File(filePath, fileTransfer.getFileName());
		OutputStream out;
		try {
			out = new FileOutputStream(file);
			FileUtil.copy(in, out);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void destroy() {
		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
