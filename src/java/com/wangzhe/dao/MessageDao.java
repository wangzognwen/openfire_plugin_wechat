package com.wangzhe.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Time;

import org.jivesoftware.database.DbConnectionManager;
import org.jivesoftware.util.LocaleUtils;
import org.jivesoftware.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wangzhe.bean.MessageEntity;
import com.wangzhe.util.TimeUtil;

public class MessageDao {
	private static final MessageDao mInstance =
			new MessageDao();
	private static final Logger LOGGER =
			LoggerFactory.getLogger(MessageDao.class);
	
	private static final String INSERT_MESSAGE =
			"INSERT INTO ofMessage(sender, receiver, type, packetId, body, time, extra) VALUES "
			+ "(?, ?, ?, ?, ?, ?, ?);";
	private static final String UPDATE_MESSAGE_FLAG =
			"update ofMessage set flag = ? where packetId = ?";
	
	public static MessageDao getInstance(){
		return mInstance;
	}
	
	private MessageDao(){
		
	}
	
	public boolean addMessage(MessageEntity messageEntity){
		boolean added = false;
		Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = DbConnectionManager.getConnection();
            pstmt = con.prepareStatement(INSERT_MESSAGE);
            pstmt.setString(1, messageEntity.getSender());
            pstmt.setString(2, messageEntity.getReceiver());
            pstmt.setString(3, messageEntity.getType());
            pstmt.setString(5, messageEntity.getBody());
            pstmt.setString(4, messageEntity.getPacketId());
            pstmt.setString(6, TimeUtil.format(messageEntity.getTime(), true));
            pstmt.setString(7, messageEntity.getExtra());
            pstmt.executeUpdate();
            added = true;
        }
        catch (Exception e) {
            LOGGER.error(LocaleUtils.getLocalizedString("admin.error"), e);
        }
        finally {
            DbConnectionManager.closeConnection(pstmt, con);
        }
        return added;
	}
	
	public void updateMessageFlag(String packetId, int flag){
		Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = DbConnectionManager.getConnection();
            pstmt = con.prepareStatement(UPDATE_MESSAGE_FLAG);
            pstmt.setInt(1, flag);
            pstmt.setString(2, packetId);
        }
        catch (Exception e) {
            LOGGER.error(LocaleUtils.getLocalizedString("admin.error"), e);
        }
        finally {
            DbConnectionManager.closeConnection(pstmt, con);
        }
	}
}
