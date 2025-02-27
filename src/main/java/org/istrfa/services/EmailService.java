package org.istrfa.services;

import lombok.extern.slf4j.Slf4j;
import org.istrfa.utils.Constantes;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

@Slf4j
@Service
public class EmailService {



    public Integer send(String destinatario,String remitente, String asunto, String contenido) {
        try {

            // Creación del mensaje de correo
            //Por la configuración de smtp siempre saldra que el que lo envia es el correo configurado en este caso cix
            Message message = new MimeMessage(buildServerEmail());
            message.setFrom(new InternetAddress(remitente));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            message.setSubject(asunto);

            // Crear el cuerpo del mensaje
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(contenido);


            // Crear el contenedor multipart
            MimeMultipart multipart = new MimeMultipart();
            multipart.addBodyPart(textPart);

            // Establecer el contenido del mensaje como multipart
            message.setContent(multipart);

            // Envío del mensaje
            Transport.send(message);
            log.info("MENSAJE_ENVIADO_CORRECTAMENTE");
            return 200;
        } catch (MessagingException e) {
            log.error("Ocurrió un error al enviar el mensaje: ",e);
            return 500;  // Devuelve un código de error apropiado
        }
    }


    public Integer sendHtmlAndArchivos(String destinatario, String asunto, String contenido, List<MultipartFile> archivosAdjuntos) {
        try {
            // Creación del mensaje de correo
            //
            Message message = new MimeMessage(buildServerEmail());
            message.setFrom(new InternetAddress(Constantes.EMAIL_USER_ACCOUNT));//Remitente, en este caso siempre sera la empresa
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            message.setSubject(asunto);

            // Crear el cuerpo del mensaje
            MimeBodyPart textPart = new MimeBodyPart();
//            textPart.setText(contenido);
            textPart.setContent(contenido, "text/html; charset=utf-8");//Enviamos un html


            // Crear el contenedor multipart
            MimeMultipart multipart = new MimeMultipart();
            multipart.addBodyPart(textPart);
            // Agregar archivos adjuntos, si los hay
//            archivosAdjuntos.add(ticketService.createTicketOrder(null,null));
            if (Objects.nonNull(archivosAdjuntos)) {
                for (MultipartFile archivo : archivosAdjuntos) {
                    if (!archivo.isEmpty()) {
                        MimeBodyPart attachmentPart = new MimeBodyPart();
                        DataSource source = new ByteArrayDataSource(archivo.getBytes(), archivo.getContentType());
                        attachmentPart.setDataHandler(new DataHandler(source));
                        attachmentPart.setFileName(archivo.getOriginalFilename());
                        multipart.addBodyPart(attachmentPart);
                    }
                }
            }

            // Establecer el contenido del mensaje como multipart
            message.setContent(multipart);

            // Envío del mensaje
            Transport.send(message);
            log.info("MENSAJE_ENVIADO_CORRECTAMENTE");
            return 200;
        } catch (MessagingException | IOException e) {
            log.error("Ocurrió un error al enviar el mensaje: ",e);
            return 500;  // Devuelve un código de error apropiado
        }
    }

    public Session buildServerEmail() {
        // Configuración del servidor de correo
        String username = Constantes.EMAIL_USER_ACCOUNT;
        String password = Constantes.EMAIL_PASSWORD_APP;

        // Configuración de propiedades adicionales
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        // Creación de la sesión de correo
        return Session.getInstance(props, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }


}