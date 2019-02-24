/*
 * Copyright (C) 2018 Guillermo Castellano
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Affero GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Affero GNU General Public License for more details.
 *
 * You should have received a copy of the Affero GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package opdcontribnotification;

import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;

import java.nio.file.*;
import java.text.MessageFormat;

import java.util.Date;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import prodoc.Attribute;
import prodoc.Condition;
import prodoc.Conditions;
import prodoc.Cursor;
import prodoc.DriverGeneric;
import prodoc.PDException;
import prodoc.ProdocFW;
import prodoc.PDFolders;
import prodoc.PDGroups;
import prodoc.PDUser;
import prodoc.Record;

/**
 * 
 * @author guillearch
 */
public class OPDContribNotification {

    static private DriverGeneric MainSession = null;
    static Session session;
    static final String CONNECTOR_NAME="PD";
    static String message;
    static String prodocUser;
    static String prodocPassword;
    static String host;
    static String port;
    static String senderEmail;
    static String senderName;
    static String senderPassword;
    static String idFoldContrib;
    static String foldTypeContrib;
    static String emailAttr;
    static String nameAttr;
    static String notifGroup;
    static String subjectContrib;
    static String bodyContrib;
    static String footerContrib;
    static String subjectNotif;
    static String bodyNotif1;
    static String bodyNotif2;
    static String footerNotif;
    static String template;

    /**
    * @param args the command line arguments
    */
    public static void main(String[] args) {
	System.setProperty("mail.mime.charset", "true");
        try {
            loadConf("conf/notification.properties");
            loginOPD(prodocUser, prodocPassword);
            session = loginMail();
            Vector<Record> Res=getListNewContrib(MainSession, idFoldContrib,
                    args);
            switch (args[0]) {
                case "0":
                    for (int i = 0; i < Res.size(); i++) {
                        sendMailContrib(Res.elementAt(i));   
                        }
                    break;
                case "1":
                    Vector<String> emailList=getAddressesNotifGroup(
                            MainSession, notifGroup);
                    if (Res != null) {
                        for (int i = 0; i < emailList.size(); i++) {
                            sendMailNotif(emailList.elementAt(i), Res);
                            }
                    }
                    break;
            }   
        } catch (Exception ex) {
            ex.printStackTrace();
            }
        finally {
            logoutOPD();
            }
        System.exit(0);
    }

    /**
     * 
     * @param Prop
     * @throws Exception 
     */
    private static void loadConf(String Prop) throws Exception {
        Properties p = new Properties();
        p.load(new FileInputStream(Prop));
        prodocUser = p.getProperty("prodocUser");
        prodocPassword = p.getProperty("prodocPassword");
        host = p.getProperty("host");
        port = p.getProperty("port");        
        senderEmail = p.getProperty("senderEmail");
        senderName = p.getProperty("senderName");
        senderPassword = p.getProperty("senderPassword");        
        idFoldContrib= p.getProperty("idFoldContrib");
        foldTypeContrib= p.getProperty("foldTypeContrib");
        emailAttr= p.getProperty("emailAttr");
        nameAttr = p.getProperty("nameAttr");
        notifGroup= p.getProperty("notifGroup");
        subjectContrib = p.getProperty("subjectContrib");
        bodyContrib = p.getProperty("bodyContrib");
        footerContrib = p.getProperty("footerContrib");
        subjectNotif = p.getProperty("subjectNotif");
        bodyNotif1 = p.getProperty("bodyNotif1");
        bodyNotif2 = p.getProperty("bodyNotif2");
        footerNotif = p.getProperty("footerNotif");
	template = p.getProperty("template");
    }
    
    /**
     * 
     * @param prodocUser
     * @param prodocPassword
     * @throws PDException 
     */
    private static void loginOPD(String prodocUser, String prodocPassword)
            throws PDException {
        ProdocFW.InitProdoc(CONNECTOR_NAME, "conf/ProdocRem.properties");
        MainSession = ProdocFW.getSession(CONNECTOR_NAME,
                prodocUser, prodocPassword);
        System.out.println("Session established.");
    }

    /**
     * 
     * @return session
     */
    private static Session loginMail() {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        Authenticator auth = new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        };
        Session session = Session.getInstance(properties, auth);
        return(session);
    }
    
    /**
     * 
     * @param MainSession
     * @param idFoldContrib
     * @param args
     * @return
     * @throws PDException 
     */
    private static Vector<Record> getListNewContrib(DriverGeneric MainSession,
            String idFoldContrib, String[] args) throws PDException {
        Vector<Record> Res=new Vector();
        PDFolders Fold=new PDFolders(MainSession);
        Date lastCheckDate=new Date(System.currentTimeMillis()
                - Long.valueOf(args[1]));
        Attribute Attr = Fold.getRecord().getAttr(PDFolders.fPDDATE).Copy();
        Attr.setValue(lastCheckDate);
        Condition c=new Condition(Attr, Condition.cGET);
        Conditions cs=new Conditions();
        cs.addCondition(c);
        Cursor CursorId = Fold.Search(foldTypeContrib, cs, true, true,
                idFoldContrib, null);
        Record Rec=MainSession.NextRec(CursorId);
        while (Rec!=null) {
            Res.add(Rec);
            Rec=MainSession.NextRec(CursorId);            
        }
        MainSession.CloseCursor(CursorId);
        return (Res);
    }

    /**
     * 
     * @param Rec 
     */
    private static void sendMailContrib(Record Rec) {
        try {
		String Destinatario=(String)Rec.getAttr(emailAttr).getValue();
		String contributorName=(String)Rec.getAttr(nameAttr).getValue();
		String subject=subjectContrib;
		String message;
		if (template.isEmpty()) {
			message = "<p>Hi " + contributorName + ",</p>";
			message += bodyContrib;
			message += footerContrib;	
		} else {
			message = readHTML(template);
			message = message.replaceAll("%contributor", contributorName);
		}
		sendEmail (Destinatario, subject, message);
        } catch (Exception Ex) {
            Ex.printStackTrace();
            }
    }

    /**
     * 
     * @param MainSession
     * @param notifGroup
     * @return emailList
     * @throws PDException 
     */
    private static Vector<String> getAddressesNotifGroup(
            DriverGeneric MainSession, String notifGroup) throws PDException {
        Vector<String> emailList=new Vector();
        PDGroups Grp=new PDGroups(MainSession);
        Cursor CursorId = Grp.ListUsers(notifGroup);
        Record Rec=MainSession.NextRec(CursorId);
        while (Rec!=null) {
            String UserName=(String)Rec.getAttr(PDGroups.fUSERNAME).getValue();
            PDUser U=new PDUser(MainSession);
            U.Load(UserName);
            emailList.add(U.geteMail());
            Rec=MainSession.NextRec(CursorId);
            }
        MainSession.CloseCursor(CursorId);
        return (emailList);
    }
    
    /**
     * 
     * @param elementAt
     * @param Res 
     */
    private static void sendMailNotif(String elementAt, Vector<Record> Res) {
        try {
            String Destinatario= elementAt;
            String subject = subjectNotif;
            message = bodyNotif1;
            message += "<ul>";
            for (int i = 0; i < Res.size(); i++) {
                createMesage(Res.elementAt(i));
                }
            message += "</ul>";
            message += bodyNotif2;
            message += footerNotif;
            sendEmail(Destinatario, subject, message);
        } catch (Exception Ex) {
            Ex.printStackTrace();
            }
    }
    
    private static String createMesage(Record Rec) {
        message += "<li>"
                + (String)Rec.getAttr(PDFolders.fTITLE).getValue()
                + "</li>";
        return message;
    }
    
    /**
     * 
     * @param Destinatario
     * @param subject
     * @param message
     * @throws AddressException
     * @throws MessagingException 
     */
    private static void sendEmail(String Destinatario,
            String subject, String message) throws AddressException,
            MessagingException {
        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(senderEmail, senderName));
            InternetAddress[] toAddresses = {new InternetAddress(Destinatario)};
            msg.setRecipients(Message.RecipientType.TO, toAddresses);
            msg.setSubject(subject);
            msg.setSentDate(new Date());
            msg.setContent(message, "text/html");
            Transport.send(msg);
            System.out.println("Email sent to " + Destinatario + ".");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(OPDContribNotification.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * 
     */
    private static void logoutOPD() {
        try {   
        if (MainSession!=null)  
            ProdocFW.freeSesion(CONNECTOR_NAME, MainSession);
        System.out.println("Session disconnected.");
        } catch (Exception Ex) {
            Ex.printStackTrace();
        }
    }
    /**
     * 
     * @param fileName
     * @return
     * @throws Exception 
     */
    public static String readHTML(String fileName)throws Exception { 
	String data = ""; 
	data = new String(Files.readAllBytes(Paths.get(fileName))); 
	return data;
    }

}