package com.proaula.aula.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class MailConfiguration {

    @Bean
    @ConditionalOnMissingBean(JavaMailSender.class)
    public JavaMailSender javaMailSender() {
        // Provee un JavaMailSender incluso si no hay propiedades SMTP configuradas.
        // Si no se configura spring.mail.host, el envío de correo fallará en tiempo de uso,
        // pero la aplicación podrá iniciarse sin excepción de arranque.
        return new JavaMailSenderImpl();
    }
}
