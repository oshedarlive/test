/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package radhey.mail;
import javax.mail.*;
import java.util.*;
import java.io.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 *
 * @author hoshi
 */
public class MailSender2 {
    private static String defaultSmtpServer;

    public static String getDefaultSmtpServer() {
        return defaultSmtpServer;
    }

    public static void setDefaultSmtpServer(String aDefaultSmtpServer) {
        defaultSmtpServer = aDefaultSmtpServer;
    }

    private InternetAddress replyAddress[]=new InternetAddress[1]; //using array of 1 element because setReplyTo requires and Array
    private List <String> recepientAddresses;
    private InternetAddress[] fromAddress=new InternetAddress[1]; //using array of 1 element because setReplyTo requires and Array
    private InternetAddress ccAddress;
    private InternetAddress bccAddress;
    private boolean requireSSL = false;
    private String server;
    private String protocol="smtp";
    private boolean authenticate;
    private String subject;
    private String htmlBody=null;
    private String textBody=null;
    private List<File> attachments;
    private List<BodyPart> attachments2;
    private String username;
    private String password;
    private boolean stayConnected=false;
    private Transport transport;
    private Session session;
    private static InternetAddress internetAddress; //inetAdress object used by validateEmail()
    private int port;
    private static int defaultPort=-1;
    //private

    public MailSender2() {
      server=defaultSmtpServer;
      port=defaultPort;
    }

    public MailSender2(String smtpServer) {
      server=smtpServer;
      port=defaultPort;
    }

    public MailSender2(String smtpServer, int port) {
      server=smtpServer;
      this.port=port;
    }

    public MailSender2(String smtpServer, int port,boolean ssl) {
      server=smtpServer;
      this.port=port;
      this.requireSSL=ssl;
    }

    public static void setDefaultPort(int port){
        defaultPort=port;
    }

    private static void validateEmail(String strEmail) throws AddressException {
        if(internetAddress==null)
            internetAddress=new InternetAddress();
        internetAddress.setAddress(strEmail);
        internetAddress.validate();
    }

    public static boolean isValidEmail(String strEmail) { //will throw null pointer Exception if null is passed
        if(internetAddress==null)
            internetAddress=new InternetAddress();
        strEmail=strEmail.trim();//will throw null pointer Exception if strEmail is null
        internetAddress.setAddress(strEmail);
        try{
            internetAddress.validate();
        }catch(AddressException ex){return false;}
        return true;
    }

    public String getReplyAddress() {
        return replyAddress[0]!=null?replyAddress[0].getAddress():null;
    }


    public void setReplyAddress(String replyAddress) throws AddressException {
        if(replyAddress == null)
           this.replyAddress[0] = null;
        else {
            replyAddress=replyAddress.trim();
            this.replyAddress[0]=new InternetAddress(replyAddress,true);
        }
    }

    public List<String> getRecepientAddresses() {
        return recepientAddresses;
    }

    public void addRecepientAddress(String emailId) throws AddressException {
        if(emailId == null )
            throw new NullPointerException("recepient address cannot be null");
        emailId=emailId.trim();
        validateEmail(emailId);
        if(recepientAddresses == null)
            recepientAddresses = new LinkedList<String>();
        recepientAddresses.add(emailId);
    }

    public void clearRecepientAddresses(){
        if(recepientAddresses!=null)
            recepientAddresses.clear();
    }

    public String getFromAddress() {
        return fromAddress[0]!=null?fromAddress[0].toString():null;
    }

    public void setFromAddress(String emailId) throws AddressException {
        if(emailId == null )
            throw new NullPointerException("from address cannot be null");
        emailId=emailId.trim();
        this.fromAddress[0]=new InternetAddress(emailId, true);
        if(session!=null)
            closeSession();
    }

    public void setFromAddress(String name, String emailId) throws AddressException {
        if(emailId == null )
            throw new NullPointerException("from address cannot be null");
        emailId=emailId.trim();
        this.fromAddress[0]=new InternetAddress(emailId, true);
        try{this.fromAddress[0].setPersonal(name);}catch(Exception ex){}
        if(session!=null)
            closeSession();
    }


    public String getBccAddress() {
        return bccAddress!=null?bccAddress.getAddress():null;
    }

    public void setBccAddress(String bccAddress) throws AddressException {
        if(bccAddress == null)
            throw new NullPointerException("bcc address cannot be null");
        bccAddress=bccAddress.trim();
        this.bccAddress=new InternetAddress(bccAddress, true);
    }

    public String getCcAddress() {
        return ccAddress!=null?ccAddress.getAddress():null;
    }

    public void setCcAddress(String ccAddress) throws AddressException {
        if(ccAddress == null)
            throw new NullPointerException("cc address cannot be null");
        ccAddress=ccAddress.trim();
         this.ccAddress=new InternetAddress(ccAddress, true);
    }

    public boolean isRequireSSL() {
        return requireSSL;
    }

    public void setRequireSSL(boolean requireSSL) {
        if(this.requireSSL!=requireSSL)
            if(session!=null) closeSession();
        this.requireSSL = requireSSL;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String mailServer) {
        if(mailServer==null)
            throw new NullPointerException("Server cannot be null");
        this.server=mailServer;
        if(session!=null)
            closeSession();
    }

    public boolean isAuthenticate() {
        return authenticate;
    }

    public void setAuthenticate(boolean authenticate) {
        if(this.authenticate != authenticate)
            if(transport!=null) closeTransport();
        this.authenticate = authenticate;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        if(subject!=null)
            this.subject = subject.trim();
        else
            this.subject=null;

    }

    public String getHtmlBody() {
        return htmlBody;
    }

    public void setHtmlBody(String htmlBody) {
        if(htmlBody==null || htmlBody.length()<1)
            this.htmlBody = null;
        else
            this.htmlBody=htmlBody;
    }

    public List<File> getAttachments() {
        return attachments;
    }

    public void addAttachment(File file) throws FileNotFoundException {
       if(file != null && file.isFile() )
              if(file.exists()){
                  if(attachments == null)
                      attachments = new ArrayList<File>(5);
                    attachments.add(file);
              }
              else
                throw new FileNotFoundException("File not exists.");
          else
              throw new IllegalArgumentException("File name can not be empty.");
    }

    public void addAttachment(String file) throws FileNotFoundException {
          if(file != null)
              addAttachment(new File(file));
          else
              throw new IllegalArgumentException("File name can not be empty.");
    }

    public void addAttachment(String body,String fileName) throws MessagingException {
          if(body==null || fileName==null)
              throw new IllegalArgumentException("File name can not be empty.");
          BodyPart bodyPart=new MimeBodyPart();
          bodyPart.setText(body);
          bodyPart.setFileName(fileName);
          if(attachments2==null)
              attachments2=new ArrayList<BodyPart>(3);
          attachments2.add(bodyPart);
    }

    public void clearAttachments(){
        if(attachments!=null)
            attachments.clear();
        if(attachments2!=null)
            attachments2.clear();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        if(username!=null)
            this.username = username.trim();
        else
            this.username=null;
        if(transport!=null) closeTransport();

    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if(password!=null)
            this.password = password.trim();
        else
            this.password=null;
        if(transport!=null) closeTransport();
    }

    public void send() throws Exception{
        if( recepientAddresses == null || recepientAddresses.isEmpty())
            throw new Exception("Recepient address can not be empty.");
        if(subject == null || subject.length() == 0 )
            throw new Exception("Subject can not be empty.");
        if(textBody==null && htmlBody==null)
            throw new Exception("Body can not be empty. Either set textBody or htmlBody");
        if( fromAddress[0] == null)
            throw new Exception("From address can not be empty.");

        if(session==null){
            if(server == null || server.length() == 0 )
                throw new Exception("Server can not be empty.");
             Properties prop = new Properties();
             prop.put("mail.from", fromAddress[0].toString());
             prop.put("mail.transport.protocol", "" + protocol);
             prop.put("mail.host","" + server);
             prop.put("mail." + protocol + ".starttls.enable", "" + requireSSL);
             if(port>=0)
                 prop.put("mail." + protocol + ".port", "" + port);
             session=Session.getInstance(prop);
         }
         if(transport==null){
             transport=session.getTransport();
         }

        MimeMultipart rootContent = null; //for alternative with attachments
        MimeMultipart content = null;
        if(textBody!=null && htmlBody!=null)
            content=new MimeMultipart("alternative");
        else
            rootContent=content=new MimeMultipart();

        MimeBodyPart bodyPart = null;
        if(textBody!=null){
            bodyPart=new MimeBodyPart();
            bodyPart.setText(textBody);
            content.addBodyPart(bodyPart);
        }
        if(htmlBody!=null){
            bodyPart=new MimeBodyPart();
            bodyPart.setContent(htmlBody,"text/html");
            content.addBodyPart(bodyPart);
        }

        if( attachments != null && !attachments.isEmpty()){
            if(rootContent==null){
                rootContent=new MimeMultipart();
                bodyPart=new MimeBodyPart();
                bodyPart.setContent(content);
                rootContent.addBodyPart(bodyPart);
                content=rootContent;
            }
            for(int ctr=0; ctr < attachments.size();ctr++){
               bodyPart=new MimeBodyPart();
               bodyPart.attachFile(attachments.get(ctr));
               content.addBodyPart(bodyPart);
            }
        }

        if( attachments2 != null && !attachments2.isEmpty()){
            if(rootContent==null){
                rootContent=new MimeMultipart();
                bodyPart=new MimeBodyPart();
                bodyPart.setContent(content);
                rootContent.addBodyPart(bodyPart);
                content=rootContent;
            }
            for(int ctr=0; ctr < attachments2.size();ctr++){
               content.addBodyPart(attachments2.get(ctr));
            }
        }

         MimeMessage msg = new MimeMessage(session);
         int noOfRecepients=recepientAddresses.size();
         for(int ctr= 0; ctr < noOfRecepients;ctr++){
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(recepientAddresses.get(ctr)));
         }
         if(ccAddress!=null)
            msg.setRecipient(Message.RecipientType.CC,ccAddress);
         if(bccAddress!=null)
            msg.setRecipient(Message.RecipientType.BCC,bccAddress);

         if(replyAddress[0]==null)
             msg.setReplyTo(fromAddress);
         else
            msg.setReplyTo(replyAddress);
         msg.setSubject(subject);
         msg.setFrom(fromAddress[0]);

         msg.setContent(content);
         if(!transport.isConnected()){
             if(authenticate){
                if(username==null || username.length()<1)
                    throw new Exception("User name can not be empty.");
                if(password==null || password.length()<1)
                    throw new Exception("password can not be empty.");
                transport.connect(username, password);
             }
             else
                 transport.connect();
             //System.out.println("transport made connection");
         }
         msg.saveChanges();
         transport.sendMessage(msg, msg.getAllRecipients());
         if(!isStayConnected()){
             transport.close();
         }
    }

    public void closeSession(){
        if(transport!=null && transport.isConnected())
            try{transport.close();}catch(Exception ex){}
        transport=null;
        session=null;
    }
    private void closeTransport(){
        if(transport!=null && transport.isConnected())
            try{transport.close();}catch(Exception ex){}
        transport=null;
    }
    /**
     * @return the protocol
     */
    public String getProtocol() {
        return protocol;
    }

    /**
     * @param protocol the protocol to set
     */
    public void setProtocol(String protocol) {
        if(protocol!=null)
            protocol=protocol.trim();
        if(protocol==null || protocol.length()<1)
            this.protocol="smtp";
        else
            this.protocol = protocol;
        if(session!=null)
            closeSession();
    }

    /**
     * @return the textBody
     */
    public String getTextBody() {
        return textBody;
    }

    /**
     * @param textBody the textBody to set
     */
    public void setTextBody(String textBody) {
        if(textBody==null || textBody.length()<1)
            this.textBody = null;
        else
            this.textBody = textBody;
    }

    /**
     * @return the stayConnected
     */
    public boolean isStayConnected() {
        return stayConnected;
    }

    /**
     * @param stayConnected the stayConnected to set
     */
    public void setStayConnected(boolean stayConnected) {
        this.stayConnected = stayConnected;
        if(stayConnected==false){
            if(transport!=null)
                closeTransport();
        }
    }

    @Override
    protected void finalize() throws Throwable{
        closeSession();
        super.finalize();
    }

    /**
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * @param port the port to set
     */
    public void setPort(int port) {
        this.port = port;
        if(session!=null) closeSession();
    }

    public static void main(String[] args) throws Exception{
        //usage
            MailSender2 mailSender=new radhey.mail.MailSender2();
            mailSender.setServer("sending server name or ip (i.e.  the smtp server)");
            mailSender.setProtocol("smtp");//not required default is smtp
            //mailSender.setPort(25); //not required for default smtp port
            mailSender.setRequireSSL(false);//not required default is false
            mailSender.setAuthenticate(true);//user name and password will be authenticated
            mailSender.setFromAddress("from address");
            mailSender.setUsername("login name");
            mailSender.setPassword("password");
            mailSender.setSubject("subject");
            mailSender.addRecepientAddress("to address");
            mailSender.setTextBody("mail body");
            mailSender.send();


        //System.out.println(isValidEmail(null));
        /*File file=new File("..");
        File[] files=file.listFiles();
        for(File f:files){
            if(f.isFile())
                System.out.println(f.getAbsolutePath());
        }
        if(1==1) return;*/
        /*
        setDefaultSmtpServer("smtp.gmail.com");
        MailSender2 mailSender=new MailSender2();
        mailSender.setPort(-1);
        mailSender.setAuthenticate(true);
        mailSender.setFromAddress("Hoshedar Irani", "freewebservices4u.care@gmail.com");
        mailSender.setUsername("freewebservices4u.care@gmail.com");
        mailSender.setPassword("#bolradhey#");
        mailSender.setSubject("this is a test13");
        //mailSender.addAttachment("this is test","test.log");
        mailSender.setStayConnected(true);
        mailSender.requireSSL=true;
        String[] userEmails={"oshedar@radheySoft.com"};//,"oshedar@hotmail.com"};//,"oshedar@yahoo.com"};//"naveen.bhardwaj87@gmail.com","sindhu.kumari87@gmail.com",
        mailSender.setCcAddress("oshedar@gmail.com");
        mailSender.setBccAddress("oshedar@hotmail.com");
        int ctr;
        for(ctr=0;ctr<userEmails.length;ctr++){
            mailSender.clearRecepientAddresses();
            mailSender.addRecepientAddress(userEmails[ctr]);
            mailSender.setHtmlBody("<font color='red' size=6>ha ha ha</font>");
            mailSender.setTextBody("hahaha");
            mailSender.send();
            System.out.println("mail sent successfully");
        }
        mailSender.closeSession();
        */
    }
}

/* sending multiple messages in 1 session
message.saveChanges(); // implicit with send()
Transport transport = session.getTransport("smtp");
transport.connect(host, username, password);
transport.sendMessage(message, message.getAllRecipients());
transport.close();
 */

/*
You have to build the message using a MimeMultipart object as content.
If you have attachments you have to set the first part of the message with a MimeMultipart object .

To build a multipart/alternative object with a plain content and an html one follow this code:

String plain_text = ... ;
String html_text  = ... ;
MimeMultipart content = new MimeMultipart("alternative");
MimeBodyPart text = new MimeBodyPart();
MimeBodyPart html = new MimeBodyPart();
text.setText(plain_text);
html.setContent(html_text, "text/html");
content.addBodyPart(text);
content.addBodyPart(html);

To parse a message with a content multipart/alternative you have to use the MimeMultipart class and then choose from the parts inside the one you prefer according to its content type.
 */
