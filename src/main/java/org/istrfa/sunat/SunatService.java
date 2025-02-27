package org.istrfa.sunat;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.istrfa.dto.SunatResponseDTO;
import org.istrfa.utils.Constantes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Enumeration;
import java.util.Map;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * The type Sunat service.
 */
@Slf4j
@Service
public class SunatService {

    private SunatService() {
    }

    @Value("${directory.folder-xmls}")
    private String directoryXmls;



    public String sunatEncodeAndZip(Map<String, String> xmlFirmado, UUID id, String fileName) throws IOException {

        // Creamos carpeta temporal
        String directory = directoryXmls.concat("/").concat(id.toString());

        // Creamos carpeta donde se guardará el xml
        Path root = createFolder(directory);

        // Inicializamos los archivos
        File zipFile = new File(root + Constantes.DELIMITER.concat(fileName).concat(".zip"));

        File xmlFile = new File(root + Constantes.DELIMITER.concat(fileName).concat(".xml"));


        try (FileWriter fw = new FileWriter(xmlFile, StandardCharsets.UTF_8)) {
            fw.write(xmlFirmado.get("xml"));
            //fw.close()
        } catch (IOException e) {
            log.error("GUARDAR_XML_ERROR_ENCODE_SUNAT_AND_CIP->" + e.getMessage());

        }


        // Guardamos el archivo zip
        FileOutputStream fos = new FileOutputStream(zipFile);
        try (ZipOutputStream zos = new ZipOutputStream(fos, StandardCharsets.UTF_8)) {
            zos.putNextEntry(new ZipEntry(xmlFile.getName()));

            byte[] bytes = Files.readAllBytes(Paths.get(xmlFile.getPath()));
            zos.write(bytes, 0, bytes.length);
            zos.closeEntry();
            // zos.close()
        } catch (IOException e) {
            log.error("GUARDAR_ARCHIVO_ZIP_ERROR_ENCODE_SUNAT_AND_CIP->" + e.toString());
        }

        // Convertimos a base64
        byte[] encoded = Base64.getEncoder().encode(FileUtils.readFileToByteArray(zipFile));
        String encodedBase64 = new String(encoded);

        // Eliminamos archivos temporales
        Path zipPath = zipFile.toPath();
        try {
            Files.delete(zipPath);
            // El archivo se eliminó correctamente
        } catch (IOException e) {
            // Ocurrió un error al eliminar el archivo
            log.error("ELIMINAR_ARCHIVO_ERROR_ENCONDE_SUNAT_AND_CIP->" + e.getMessage());
        }
        return encodedBase64;
    }

    public static Path createFolder(String directory) {
        Path root = Paths.get(directory).toAbsolutePath();
        if (!Files.exists(root)) new File(root.toString()).mkdirs();
        return root;
    }



    public SunatResponseDTO sunatDecodeResponse(SOAPMessage response, String id) throws IOException, SAXException, ParserConfigurationException, SOAPException {

        String directory = directoryXmls.concat(Constantes.DELIMITER).concat(id).concat("/response");

        // Creamos carpeta donde se guardará el xml
        Path root = createFolder(directory);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        response.writeTo(outputStream);
        InputStream xmlInputStream = new ByteArrayInputStream(outputStream.toString().getBytes());

        // Leemos el xml de respuesta
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        // Deshabilitar el procesamiento de entidades externas
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);


        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(xmlInputStream);
        NodeList list = document.getElementsByTagName("applicationResponse");
        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element e = (Element) node;
                String responseBase64 = e.getTextContent();

                // Decodificamos y escribimos el .zip de respuesta
                File file = new File(root + "/application.zip");
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    byte[] decoder = Base64.getDecoder().decode(responseBase64);
                    fos.write(decoder);
                }

                try (ZipFile vanilla = new ZipFile(file)) {
                    return processFile(vanilla, directory);
                } catch (Exception u) {
                    log.info(u.getMessage());
                }

            }
        }
        throw new IllegalArgumentException("No se pudo procesar la respuesta de SUNAT");
    }

    public SunatResponseDTO processFile(ZipFile zipFile, String directory) throws IOException, ParserConfigurationException, SAXException {

        int thresholdEntries = 10000; //UMBRAL ENTRADAS

        int thresholdSize = 1000000000; //Tamaño del umbral

        double thresholdRatio = 10;
        int totalSizeArchive = 0;
        int totalEntryArchive = 0;

        Enumeration<? extends ZipEntry> entries = zipFile.entries();


        // Obtenemos y escribimos los archivos dentro del zip
        while (entries.hasMoreElements()) {
            ZipEntry ze = entries.nextElement();
            InputStream in = new BufferedInputStream(zipFile.getInputStream(ze));
            OutputStream out = new BufferedOutputStream(new FileOutputStream("./output_onlyfortesting.txt"));

            totalEntryArchive++;

            int nBytes = -1;
            byte[] buffer = new byte[2048];
            int totalSizeEntry = 0;

            while ((nBytes = in.read(buffer)) > 0) { // Compliant
                out.write(buffer, 0, nBytes);
                totalSizeEntry += nBytes;
                totalSizeArchive += nBytes;

                double compressionRatio = (double) totalSizeEntry / ze.getCompressedSize();
                if (compressionRatio > thresholdRatio) {
                    // ratio between compressed and uncompressed data is highly suspicious, looks like a Zip Bomb Attack
                    String message = "La relación entre datos comprimidos y sin comprimir es muy sospechosa, parece un ataque Zip Bomb.";
                    log.warn(message);
                    break;
                }
            }

            if (totalSizeArchive > thresholdSize) {
                // Validación recomendada por Security Review - SONARQUBE
                String message = "El tamaño de los datos sin comprimir es demasiado para la capacidad de recursos de la aplicación";
                log.error(message);
                throw new IllegalArgumentException(message);
            }

            if (totalEntryArchive > thresholdEntries) {
                // Validación recomendada por Security Review - SONARQUBE
                String message = "Demasiadas entradas en este archivo pueden provocar el agotamiento de los inodos del sistema";
                log.error(message);
                throw new IllegalArgumentException(message);
            }


            if (!ze.isDirectory()) {
                File fileIntoZip = procesarZipEntry(zipFile, ze, directory);

                // Leemos el xml obtenido del zip #####################
                DocumentBuilderFactory factory1 = DocumentBuilderFactory.newInstance();
                // Deshabilitar el procesamiento de entidades externas
                factory1.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
                factory1.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);


                DocumentBuilder builder1 = factory1.newDocumentBuilder();
                Document document1 = builder1.parse(fileIntoZip);

                // Encontramos la etiqueta de código y respuesta
                return builSunatReponseDTO(document1);
            }
        }

        throw new IllegalArgumentException("No se pudo procesar el ZIP SUNAT");
    }

    public static File procesarZipEntry(ZipFile zipFile, ZipEntry entry, String directory) throws IOException {


        // Verificar la ruta del archivo para prevenir Path Traversal
        String canonicalDirPath = new File(directory).getCanonicalPath();
        File fileIntoZip = new File(directory + Constantes.DELIMITER + entry);

        String canonicalFilePath = fileIntoZip.getCanonicalPath();

        if (!canonicalFilePath.startsWith(canonicalDirPath)) {
            throw new IOException("Entrada zip apunta a una ubicación fuera del directorio de destino: " + entry.getName());
        }

        InputStream stream = zipFile.getInputStream(entry);

        try (FileWriter fw1 = new FileWriter(fileIntoZip, StandardCharsets.UTF_8)) {
            fw1.write(new String(stream.readAllBytes(), StandardCharsets.UTF_8));
        } catch (IOException p) {
            // Manejo de excepciones
            log.error("ERROR_PROCESAR_ZIP_ENTRY->{}", p.toString());
        }

        return fileIntoZip;
    }

    public static SunatResponseDTO builSunatReponseDTO(Document document1) {
        SunatResponseDTO responseSunat = new SunatResponseDTO();

        NodeList listCode = document1.getElementsByTagName("cbc:ResponseCode");
        for (int j = 0; j < listCode.getLength(); j++) {
            Node node1 = listCode.item(j);
            if (node1.getNodeType() == Node.ELEMENT_NODE) {
                Element e1 = (Element) node1;
                responseSunat.setCode(e1.getTextContent());
            }
        }

        NodeList listDescription = document1.getElementsByTagName("cbc:Description");
        for (int j = 0; j < listDescription.getLength(); j++) {
            Node node1 = listDescription.item(j);
            if (node1.getNodeType() == Node.ELEMENT_NODE) {
                Element e1 = (Element) node1;
                responseSunat.setMessage(e1.getTextContent());
            }
        }
        return responseSunat;
    }

}


