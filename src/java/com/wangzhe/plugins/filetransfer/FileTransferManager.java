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
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jivesoftware.openfire.SessionManager;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.container.BasicModule;
import org.jivesoftware.openfire.session.ClientSession;
import org.jivesoftware.openfire.session.Session;
import org.jivesoftware.openfire.user.User;
import org.jivesoftware.openfire.user.UserManager;
import org.jivesoftware.openfire.user.UserNotFoundException;
import org.jivesoftware.util.JiveGlobals;
import org.jivesoftware.util.PropertyEventListener;
import org.jivesoftware.util.cache.Cache;
import org.jivesoftware.util.cache.CacheFactory;
import org.xmpp.packet.JID;

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
            throw new IOException("Only version 5 supported");
        }
        
        byte[] fromData = new byte[32];
        in.read(fromData);
       
        JID fromJid = new JID(new String(fromData), true);
       
        Collection<ClientSession> sessions = SessionManager.getInstance().getSessions(fromJid.getNode());
        if(sessions == null || sessions.isEmpty()){
        	throw new IOException("from is not invalid");
        }

        byte[] toData = new byte[32];
        in.read(toData);
        JID toJid = new JID(new String(toData), true);
   
        try {
			User toUser = UserManager.getInstance().getUser(toJid.getNode());
		} catch (UserNotFoundException e) {
			throw new IOException("to is invalid");
		}
        
        byte[] mimeTypeData = new byte[16];
        in.read(mimeTypeData);
        String mimeType = new String(mimeTypeData);
        int fileNamelength = in.read();
        byte[] fileNameData = new byte[fileNamelength];
        in.read(fileNameData);
        String fileName = new String(fileNameData);
        byte[] fileSizeData = new byte[4];
        in.read(fileSizeData);
        int fileSize = Integer.parseInt(new String(fileSizeData));
   
        saveFile(in, fileName);
        connection.shutdownInput();
    
        byte[] resonse = "success".getBytes();
        out.write(resonse);
        out.flush();
        out.close();
	}
	
	private void saveFile(InputStream in, String fileName){
		String filePath = JiveGlobals.getProperty("file.transfer.path", 
				"D:\\img");
		File file = new File(filePath, fileName);
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
