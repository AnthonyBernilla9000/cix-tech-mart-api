package org.istrfa.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Image;
import com.tenpisoft.n2w.MoneyConverters;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.util.Streams;
import org.istrfa.dto.PersonDTO;
import org.istrfa.models.PersonEntity;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Slf4j
public class Util {

    public static String getFullNamePersonDTO(PersonDTO dto) {
        if (Objects.isNull(dto)) return "";
        return String.join(" ",
                Optional.ofNullable(dto.getName()).orElse(""),
                Optional.ofNullable(dto.getFirstname()).orElse(""),
                Optional.ofNullable(dto.getLastname()).orElse("")
        ).trim();
    }

    public static String getFullNamePersonEntity(PersonEntity entity) {
        if (Objects.isNull(entity)) return "";
        return String.join(" ",
                Optional.ofNullable(entity.getName()).orElse(""),
                Optional.ofNullable(entity.getFirstname()).orElse(""),
                Optional.ofNullable(entity.getLastname()).orElse("")
        ).trim();
    }

    public static MultipartFile convertXmlToMultipart(String xmlContent, String namefile) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(xmlContent.getBytes(StandardCharsets.UTF_8));
        return convertByteArrayOutputStreamToMultipart(outputStream,namefile,".xml");
    }



    public static MultipartFile convertByteArrayOutputStreamToMultipart(ByteArrayOutputStream byteArrayOutputStream, String filename,String extension) {
        // Convertir el ByteArrayOutputStream a byte[]
        byte[] pdfBytes = byteArrayOutputStream.toByteArray();

        // Crear un FileItem para representar el archivo
        FileItem fileItem = new DiskFileItem(filename, "application/pdf", false, filename + extension, pdfBytes.length, null);
        try {
            Streams.copy(new ByteArrayResource(pdfBytes).getInputStream(), fileItem.getOutputStream(), true);
        } catch (IOException e) {
            log.error("Ocurrio un error al convertir el archivo: ", e);
        }

        // Crear MultipartFile desde el FileItem
        return new CommonsMultipartFile(fileItem);  // Retorna el MultipartFile generado
    }

    public static String convertDoubleToSoles(Double valor) {
        if (Objects.isNull(valor)) return "";
        // Conversión de valor monetario
        MoneyConverters moneyConverter = MoneyConverters.SPANISH_BANKING_MONEY_VALUE;
        return moneyConverter.asWords(BigDecimal.valueOf(valor)).toUpperCase();
    }


    public static Image genereteCodeQR(String text, int width, int height) throws WriterException, IOException, BadElementException {
        // Configurar el generador de QR
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 0); // Aquí configuramos el margen a 0 para minimizar el espacio blanco

        // Generar la matriz de píxeles del QR con el tamaño ajustado
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hints);

        // Convertir la matriz de QR a BufferedImage
        BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

        // Recortar la imagen para eliminar el borde blanco
        int x = bufferedImage.getMinX();
        int y = bufferedImage.getMinY();
        int w = bufferedImage.getWidth();
        int h = bufferedImage.getHeight();

        // Recortamos aún más el borde blanco, ajustando el área a una imagen más pequeña
        int margin = 4; // Establece un margen adicional para recortar el borde blanco
        BufferedImage croppedImage = bufferedImage.getSubimage(x + margin, y + margin, w - 2 * margin, h - 2 * margin);

        // Convertir la imagen recortada a un byte array
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(croppedImage, "png", byteArrayOutputStream); // Escribe la imagen recortada
        return Image.getInstance(byteArrayOutputStream.toByteArray()); // Convierte el ByteArrayOutputStream en Image de iText
    }

    public static String formatLocalDateTime(LocalDateTime dateTime, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return dateTime.format(formatter);
    }

    public static String completeWithZero(int number, int length) {
        return String.format("%0" + length + "d", number);
    }





}
