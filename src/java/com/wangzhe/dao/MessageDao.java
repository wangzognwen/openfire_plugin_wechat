package com.wangzhe.dao;

import com.wangzhe.bean.MessageEntity;
import com.wangzhe.util.TimeUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.database.DbConnectionManager;
import org.jivesoftware.util.LocaleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	private static final String QUERY_UNREAD_MESSGES =
            "select * from ofMessage where receiver = ? and flag = 0";
	
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
            pstmt.executeUpdate();
        }
        catch (Exception e) {
            LOGGER.error(LocaleUtils.getLocalizedString("admin.error"), e);
        }
        finally {
            DbConnectionManager.closeConnection(pstmt, con);
        }
	}

	public List<MessageEntity> getUnreadMessages(String userName){
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<MessageEntity> messageEntities = new ArrayList<>();
        try {
            con = DbConnectionManager.getConnection();
            pstmt = con.prepareStatement(QUERY_UNREAD_MESSGES);
            pstmt.setString(1, userName);
            rs = pstmt.executeQuery();
            while (rs.next()){
                messageEntities.add(extractResult(rs));
            }
        }
        catch (Exception e) {
            LOGGER.error(LocaleUtils.getLocalizedString("admin.error"), e);
        }
        finally {
            DbConnectionManager.closeConnection(rs, pstmt, con);
        }

        return messageEntities;
    }

    private MessageEntity extractResult(ResultSet rs) throws SQLException {
	        MessageEntity messageEntity = new MessageEntity();
	        messageEntity.setId(rs.getInt("id"));
	        messageEntity.setSender(rs.getString("sender"));
	        messageEntity.setReceiver(rs.getString("receiver"));
	        messageEntity.setBody(rs.getString("body"));
	        messageEntity.setPacketId(rs.getString("packetId"));
	        messageEntity.setType(rs.getString("type"));
	        messageEntity.setFlag(rs.getInt("flag"));
	        messageEntity.setTime(TimeUtil.format(rs.getString("time"), true));

	        return messageEntity;
    }
}
