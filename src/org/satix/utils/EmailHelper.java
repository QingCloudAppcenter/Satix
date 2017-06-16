package org.satix.utils;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeMessage.RecipientType;

import org.satix.constants.ReservedVariables;
import org.satix.properties.SystemConfigurations;


/**
 * An email delivering utility
 * 
 */
public class EmailHelper {
	private static final String CLASSNAME = EmailHelper.class.getSimpleName();
	private static final Logger logger = SatixLogger.getLogger(CLASSNAME);
	
	private String from;
	private String password;
	private String[] to;
	private String[] cc;
	private String[] bcc;
	private String subject;
	private String body;
	private String[] attachmentFilePaths;
	
	private String smtpServer;
	private Session session;
	
	public EmailHelper() {
		setFrom(SystemConfigurations.getValue(ReservedVariables.FROM.getName(), null));
		setPassword(SystemConfigurations.getValue(ReservedVariables.PASSWORD.getName(), null));		
		String to = SystemConfigurations.getValue(ReservedVariables.TO.getName(), null);
		if (to != null) {
			setTo(to.split(","));
		}
		String cc = SystemConfigurations.getValue(ReservedVariables.CC.getName(), null);
		if (cc != null) {
			setCc(cc.split(","));
		}
		String bcc = SystemConfigurations.getValue(ReservedVariables.BCC.getName(), null);
		if (bcc != null) {
			setBcc(bcc.split(","));
		}
		setSubject(SystemConfigurations.getValue(ReservedVariables.SUBJECT.getName(), null));
		
		setSmtpServer(SystemConfigurations.getValue(ReservedVariables.SMTPSERVER.getName(), null));
		
		if (getPassword() != null) {
			setSession("SSL"); //using SSL by default
		}
	}
	
	public void send() throws AddressException, MessagingException, Exception {		
		MimeMessage msg = null;
		if (getSession() != null) {
			msg = new MimeMessage(getSession());
		} else {
			Properties props = System.getProperties();
			props.put("mail.smtp.host", getSmtpServer());
			msg = new MimeMessage(Session.getDefaultInstance(props, null));
		}
	
		msg.setFrom(new InternetAddress(getFrom()));
		
		String[] to = getTo();
		InternetAddress[] addressTo = new InternetAddress[to.length];
		for (int i=0; i<to.length; i++) {
			addressTo[i] = new InternetAddress(to[i]);
		}
		msg.setRecipients(RecipientType.TO, addressTo);
		
		String[] cc = getCc();
		if (cc!=null && cc.length>0) {
			InternetAddress[] addressCC = new InternetAddress[cc.length];
			for (int i=0; i<cc.length; i++) {
				addressCC[i] = new InternetAddress(cc[i]);
			}
			msg.setRecipients(RecipientType.CC, addressCC);
		}
		
		String[] bcc = getBcc();
		if (bcc!=null && bcc.length>0) {
			InternetAddress[] addressBCC = new InternetAddress[bcc.length];
			for (int i=0; i<bcc.length; i++) {
				addressBCC[i] = new InternetAddress(bcc[i]);
			}
			msg.setRecipients(RecipientType.BCC, addressBCC);
		}			

		msg.setSubject(getSubject());
		msg.setSentDate(new java.util.Date());

		// content
		MimeMultipart multipart = new MimeMultipart("related");
		BodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setContent(getBody(), "text/html; charset=\"UTF-8\"");
		multipart.addBodyPart(messageBodyPart);
		
		// attachments
		String[] attachments = getAttachmentFilePaths();
		if (attachments!=null && attachments.length>0) {
			for (int i=0; i<attachments.length; i++) {
				FileDataSource fds = new FileDataSource(attachments[i]);
				MimeBodyPart mimeAttach = new MimeBodyPart();
				mimeAttach.setDataHandler(new DataHandler(fds));
				mimeAttach.setFileName(fds.getName());
				multipart.addBodyPart(mimeAttach);
			}
		}
		
		msg.setContent(multipart);
		Transport.send(msg);
		logger.log(Level.INFO, "Email sent out successfully.");
	}

	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String[] getTo() {
		return to;
	}
	public void setTo(String[] to) {
		this.to = to;
	}
	public String[] getCc() {
		return cc;
	}
	public void setCc(String[] cc) {
		this.cc = cc;
	}
	public String[] getBcc() {
		return bcc;
	}
	public void setBcc(String[] bcc) {
		this.bcc = bcc;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String[] getAttachmentFilePaths() {
		return attachmentFilePaths;
	}
	public void setAttachmentFilePaths(String[] attachmentFilePaths) {
		this.attachmentFilePaths = attachmentFilePaths;
	}
	
	public String getSmtpServer() {
		return smtpServer;
	}

	public void setSmtpServer(String smtpServer) {
		this.smtpServer = smtpServer;
	}
	
	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}
	
	public void setSession(String type) {
		if (!"TLS".equalsIgnoreCase(type) && !"SSL".equalsIgnoreCase(type)) {
			logger.log(Level.SEVERE, "You need to specify the connection type to send email, TLS or SSL");
			return;
		}
		
		Properties props = new Properties();
		props.put("mail.smtp.host", getSmtpServer());
		props.put("mail.smtp.auth", "true");
		if ("TLS".equalsIgnoreCase(type)) {			
			props.put("mail.smtp.starttls.enable", "true");			
			props.put("mail.smtp.port", "587");	 
			session = Session.getInstance(props,
					new javax.mail.Authenticator() {
						protected PasswordAuthentication getPasswordAuthentication() {
							return new PasswordAuthentication(getFrom(), getPassword());
						}
			  		});
		} else if ("SSL".equalsIgnoreCase(type)) {
			props.put("mail.smtp.socketFactory.port", "465");
			props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");			
			props.put("mail.smtp.port", "465");	 
			session = Session.getDefaultInstance(props,
					new javax.mail.Authenticator() {
						protected PasswordAuthentication getPasswordAuthentication() {
							return new PasswordAuthentication(getFrom(),getPassword());
						}
					});
		}
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public static void main(String[] args) {
		StringBuffer sb = new StringBuffer();
		sb.append("<html><head><title>SatixRunner Automated Test Report</title>");
		sb.append("<style type=\"text/css\">#main{width:800px;margin-left:400px;margin-right:300px;height:800px;background:#fff;}</style></head><body>");
		sb.append("<div id=\"main\"><br><font style=\"font-family:Arial; font-size:150%; color:#000; padding-left:100px;\">SatixRunner Running Report</font><br><br>");
		sb.append("<font style=\"font-family:Arial; font-size:100%; color:#4474BB\">System info: </font><br><font style=\"font-family:Arial; font-size:90%;\">" +
				"os.name: 'Windows XP', os.arch: 'x86', os.version: '5.1 build 2600 Service Pack 3', java.version: '1.6.0'</font><br>");
		sb.append("<font style=\"font-family:Arial; font-size:100%; color:#4474BB\">Browser info:</font><br><font style=\"font-family:Arial; font-size:90%;\">" +
				"driver.version: firefox</font><br>");
		sb.append("<br>");
		sb.append("<table border=\"1\" width=\"600\" height=\"75\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
		sb.append("<tr height=\"40\" bgcolor=\"#4474BB\" style=\"color:#fff; font-family:Arial; font-size:100%\">");
		sb.append("<th width=\"210\">Test Case Name</th><th width=\"210\">Elapsed(ms)</th><th width=\"180\">Test Status</th></tr>");
		sb.append("<tr height=\"30\" align=\"center\" style=\"font-family:Arial; font-size:80%; color:#104E8B\"><td bgcolor=\"#F5FFFA\">" +
				"Login</td><td bgcolor=\"#FAFAD2\">1515</td>");
		sb.append("<td bgcolor=\"#90EE90\">");
		sb.append("Successful</td></tr>");
		sb
				.append("<tr height=\"35\" align=\"center\" style=\"font-family:Arial; font-size:90%; color:#4D4D4D\" bgcolor=\"#F5FFFA\">" +
						"<td><b>Total Test Cases: </b>1</td><td bgcolor=\"#FAFAD2\"><b>Total Elapsed: </b>1515</td><td bgcolor=\"#90EE90\">" +
						"<b>Total Failed: </b>0</td></tr>");
		sb.append("</table><div></body></html>");
		try {
			EmailHelper eh = new EmailHelper();
			//eh.setSmtpServer("smtp.gmail.com");
			//eh.setFrom("xszhou@gmail.com");
			//eh.setPassword("xxxxxx");
			//eh.setTo(new String[] { "zxiaosi@cn.ibm.com" });
			//eh.setSubject("Test");
			eh.setBody(sb.toString());
			eh.send();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
