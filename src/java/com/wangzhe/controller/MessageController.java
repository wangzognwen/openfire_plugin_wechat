package com.wangzhe.controller;

import javax.ws.rs.core.Response;

import org.jivesoftware.openfire.SessionManager;

import com.wangzhe.bean.MessageEntity;
import com.wangzhe.exceptions.ExceptionType;
import com.wangzhe.exceptions.ServiceException;

/**
 * The Class MessageController.
 */
public class MessageController {
	/** The Constant INSTANCE. */
	public static final MessageController INSTANCE = new MessageController();

	/**
	 * Gets the single instance of MessageController.
	 *
	 * @return single instance of MessageController
	 */
	public static MessageController getInstance() {
		return INSTANCE;
	}

}