package org.lili.forfun.shark.util;

import lombok.extern.slf4j.Slf4j;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.util.Arrays;
import java.util.Properties;


@Slf4j
public final class MailUtil {

    private MailUtil() {

    }

    /**
     * 发件人电子邮箱
     */
    private static final String FROM = ConfigUtil.getString("from");

    /**
     * 密码
     */
    private static final String PASSWORD = ConfigUtil.getString("password");

    /**
     * 地址。
     */
    private static final String ALIDM_SMTP_HOST = "smtp-inc.lili.com";

    /**
     * 端口.
     */
    private static final String ALIDM_SMTP_PORT = "25";

    public static void sendMail(String to, String subject, String content) {
        sendMail(to, subject, content, null);
    }

    public static void sendMail(String to, String subject, File[] files) {
        send(to, FROM, subject, "您好！请接收您的测试报告！", files);
    }

    public static void sendMail(String to, String subject, String content, File[] files) {
        send(to, FROM, subject, content, files);
    }

    private static void send(String to, String from, String subject, String content, File[] files) {

        // 配置发送邮件的环境属性
        final Properties props = new Properties();
        // 表示SMTP发送邮件，需要进行身份验证
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", ALIDM_SMTP_HOST);
        props.put("mail.smtp.port", ALIDM_SMTP_PORT);

        // 如果使用ssl，则去掉使用25端口的配置，进行如下配置,
        // props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        // props.put("mail.smtp.socketFactory.port", "465");
        // props.put("mail.smtp.port", "465");

        // 发件人的账号
        props.put("mail.user", from);
        // 访问SMTP服务时需要提供的密码
        props.put("mail.password", PASSWORD);
        // 构建授权信息，用于进行SMTP进行身份验证
        Authenticator authenticator = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                // 用户名、密码
                String userName = props.getProperty("mail.user");
                String password = props.getProperty("mail.password");
                return new PasswordAuthentication(userName, password);
            }
        };

        // 使用环境属性和授权信息，创建邮件会话
        Session mailSession = Session.getInstance(props, authenticator);

        // 创建邮件消息
        MimeMessage message = new MimeMessage(mailSession);
        try {
            // 设置发件人
            message.setFrom(new InternetAddress(from));
            // 设置收件人
            message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(to));
            // 设置邮件标题
            message.setSubject(subject);

            //混合的组合关系
            final MimeMultipart msgMultipart = new MimeMultipart("mixed");
            message.setContent(msgMultipart);

            if (files != null) {
                Arrays.stream(files).forEach(file -> {
                    DataSource ds1 = new FileDataSource(file);
                    //数据处理器
                    DataHandler dh1 = new DataHandler(ds1);
                    MimeBodyPart attch1 = new MimeBodyPart();
                    try {
                        //设置第一个附件的数据
                        attch1.setDataHandler(dh1);
                        //设置第一个附件的文件名
                        attch1.setFileName(file.getName());
                        msgMultipart.addBodyPart(attch1);
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                });
            }

            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(content, "text/html;charset=utf-8");
            msgMultipart.addBodyPart(htmlPart);

            // 发送邮件
            Transport.send(message);
            log.info("Email send successful， from:{}  to:{} ", from, to);
        } catch (MessagingException e) {
            e.printStackTrace();
            log.error("Email send error， from:{}  to:{} ", from, to);
        }
    }
}
