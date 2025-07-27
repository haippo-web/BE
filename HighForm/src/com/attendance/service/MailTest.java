package com.attendance.service;

import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.time.LocalDate;
import java.util.Properties;

/*		[					]
 * 		[	최산하   담당   	]
 * 		[					]
 */

public class MailTest {
    public static void sendTestMail() {
        String username = "sana2d2v@gmail.com";
        String password = "mdez ynqs eqrf nqxl";
        String to = "qowhxk@naver.com";

        AttendanceCodeService service = AttendanceCodeService.getInstance();
        LocalDate today = LocalDate.now();
        String code = service.getCodeByDate(today);

        if (code == null) {
            // 코드가 없으면 새로 발급
            code = String.format("%04d", (int)(Math.random() * 10000));
            service.storeCodeInRedis(today, code);
            System.out.println("[MailTest] 오늘 코드가 없어 새로 생성: " + code);
        } else {
            System.out.println("[MailTest] 오늘 코드가 이미 존재함: " + code);
        }

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(to)
            );
            message.setSubject("[출석 테스트] 오늘의 출석 코드입니다");
            message.setText("이 메일은 출석 스케줄러의 테스트 메시지입니다.\n\n" +
                            "오늘의 출석 코드는 [" + code + "] 입니다.");

            Transport.send(message);
            System.out.println("✅ 테스트 메일 전송 완료: " + code);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
