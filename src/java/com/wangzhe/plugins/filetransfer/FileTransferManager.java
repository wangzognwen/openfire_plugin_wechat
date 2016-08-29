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
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jivesoftware.openfire.SessionManager;
import org.jivesoftware.openfire.session.ClientSession;
import org.jivesoftware.util.JiveGlobals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.JID;

import com.wangzhe.util.FileUtil;

public class FileTransferManager{
	private ServerSocket serverSocket;
	private int port;
	private Map<String, FileTransfer> connectionMap;
	private ExecutorService executor = Executors.newCachedThreadPool();
	private static final Logger LOGGER = LoggerFactory.getLogger(FileTransferManager.class);
	
	private static final int ACTION_READ = 1;
	private static final int ACTION_WRITE = 2;

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
									LOGGER.error("Checked IOException: " + e.getMessage());
									try {
										if(!socket.isClosed()){
											byte[] response = "fail".getBytes();
											socket.getOutputStream().write(response);
										}
										socket.close();
									} catch (IOException e1) {
										e1.printStackTrace();
									}
								}
							}
						});
						
					}
				} catch (IOException e) {
					LOGGER.error("Checked servercocket IOException: " + e.getMessage());
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
        
        int action = in.read();
        if(action == ACTION_READ){
        	outputFileToClient(connection, in, out);
        }else if(action == ACTION_WRITE){
			saveFile(connection, in, out);
		}else {
			throw new IOException("not supported action: " + action);
		}
	}
	
	private void outputFileToClient(Socket connection, InputStream in, OutputStream out) throws IOException{
		int fileNameLength = in.read();
		byte[] fileNameData = new byte[fileNameLength];
		in.read(fileNameData);
		String fileName = new String(fileNameData);
		String filePath = JiveGlobals.getProperty("file.transfer.path", 
				"D:\\img");
		connection.shutdownInput();
		
		InputStream fileIn = FileUtil.getFileInputStream(filePath + "/" + fileName);
		if(fileIn == null){
			throw new IOException("can not find file: " + fileName);
		}
		byte[] resonse = "success".getBytes();
        out.write(resonse);
        
        int fileSize = fileIn.available();
  
        byte[] fileSizeData = Arrays.copyOf(String.valueOf(fileSize).getBytes(), 4);
        out.write(fileSizeData);
		FileUtil.copy(fileIn, out);
		
		fileIn.close();
		out.flush();
        connection.shutdownOutput();
	}
	
	
	private void saveFile(Socket connection, InputStream in, OutputStream out) throws IOException{
		 
        byte[] fromData = new byte[32];
        in.read(fromData);
       
        JID fromJid = new JID(new String(fromData).trim(), true);
       
        Collection<ClientSession> sessions = SessionManager.getInstance().getSessions(fromJid.getNode());
        if(sessions == null || sessions.isEmpty()){
        	throw new IOException("from is not invalid");
        }

        byte[] toData = new byte[32];
        in.read(toData);
        JID toJid = new JID(new String(toData).trim(), true);
   
       /* try {
			User toUser = UserManager.getInstance().getUser(toJid.getNode());
		} catch (UserNotFoundException e) {
			throw new IOException("to is invalid");
		}*/
        
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
   
        String filePath = JiveGlobals.getProperty("file.transfer.path", 
				"D:\\img");

		boolean save = FileUtil.saveFile(in, filePath + "/" + fileName);
        connection.shutdownInput();
        if(!save){
        	throw new IOException("save file " + fileName + " failed!");
        }
    
        byte[] resonse = "success".getBytes();
        out.write(resonse);
        out.flush();
        out.close();
		
	}
	
	public void destroy() {
		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
