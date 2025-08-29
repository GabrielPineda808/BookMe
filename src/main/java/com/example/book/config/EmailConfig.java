package com.example.book.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class EmailConfig {
    @Value("${spring.mail.username}")
    private String emailUsername;
    @Value(("${spring.mail.password}"))
    private String password;

    @Bean
    public JavaMailSender javaMailSender(){
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl(); // Spring’s mail sender impl

        mailSender.setHost("smtp.gmail.com"); // Gmail SMTP host
        mailSender.setPort(587);              // STARTTLS port
        mailSender.setUsername(emailUsername); // your Gmail address (or alias)
        mailSender.setPassword(password);      // your app password (see notes below)

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");   // use SMTP
        props.put("mail.smtp.auth", "true");            // require auth (username+password or OAuth)
        props.put("mail.smtp.starttls.enable", "true"); // upgrade to TLS on port 587
        props.put("mail.debug", "true");                // verbose logs (dev only)


        return mailSender;
    }
}
