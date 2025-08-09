package com.beyond.meongnyang.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SendEmailService {
    private final JavaMailSender mailSender;

    public void sendTemporaryPassword(String emailTO, String tempPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(emailTO);
        message.setSubject("[멍멍냥냥] 임시 비밀번호 안내");
        message.setText("요청하신 임시 비밀번호는 다음과 같습니다.\n\n"
                + tempPassword + "\n\n로그인 후 꼭 비밀번호를 변경해주세요.");
        mailSender.send(message);
    }

    public void sendVerificationCode(String emailTO , String code) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(emailTO);
        message.setSubject("[멍멍냥냥] 이메일 인증코드 안내");
        message.setText("인증코드: " + code + "\n5분 안에 입력해주세요.");
        mailSender.send(message);
    }

}
