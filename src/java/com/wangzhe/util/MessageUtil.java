package com.wangzhe.util;

import org.dom4j.Element;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.util.XMPPDateTimeFormat;
import org.xmpp.packet.Message;

import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by wangzhe on 2017/1/24.
 */
public class MessageUtil {
    private static final String utc = TimeZone.getDefault().
            getDisplayName(false, TimeZone.SHORT, Locale.getDefault());


    public static void addTimeElement(Message message, Date date){
        Element element = message.getElement();
        Element timeElement =
                element.addElement("time", "xmpp:custom:time");
        timeElement.addAttribute("utc", utc);
        timeElement.addText(TimeUtil.format(date, true));
    }

    public static void addDelayElement(Message message, Date creationDate){
        Element delay = message.addChildElement("delay", "urn:xmpp:delay");
        delay.addAttribute("from", XMPPServer.getInstance().getServerInfo().getXMPPDomain());
        delay.addAttribute("stamp", XMPPDateTimeFormat.format(creationDate));
    }
}
