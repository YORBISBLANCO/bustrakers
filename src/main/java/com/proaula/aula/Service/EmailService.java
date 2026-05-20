package com.proaula.aula.Service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendSimpleMessage(String to, String subject, String text) {
        if (to == null || to.isBlank()) return;
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
        } catch (Exception e) {
            // No detener el flujo si falla el envío; registrar puede ayudar en debugging
            System.err.println("Error enviando correo a " + to + ": " + e.getMessage());
        }
    }

    public void sendLoginNotification(String to, String username) {
        String subject = "Inicio de sesión en Bustraker";
        String text = "Hola " + (username != null ? username : "") + ",\n\nSe registró un inicio de sesión en tu cuenta.\n\nSi no fuiste tú, por favor revisa la seguridad de tu cuenta.";
        sendSimpleMessage(to, subject, text);
    }
}
