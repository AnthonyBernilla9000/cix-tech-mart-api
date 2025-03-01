package org.istrfa.sunat;

import lombok.extern.slf4j.Slf4j;
import org.istrfa.dto.FacturacionDetalleDTO;
import org.istrfa.dto.FacturacionPostDTO;
import org.istrfa.dto.SunatResponseDTO;
import org.istrfa.dto.SupplierEconomicZoneDTO;
import org.istrfa.utils.SignatureXML;
import org.istrfa.utils.Ubl21;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.TransformerException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.Base64;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


/**
 * The type Li fac manager.
 */
@Slf4j
@Service
public class LiFacManager {

    private FacturacionPostDTO post = new FacturacionPostDTO();
    private SunatResponseDTO sunatResponseDTO = new SunatResponseDTO();

    private final ClientSunat clientSunat;

    private final SunatService sunatService;

    private final SignatureXML signatureXML;

    @Value("${sunat.path-apache-org-xml}")
    private String pathApacheOrgXml;

    @Autowired
    public LiFacManager(ClientSunat clientSunat, SunatService sunatService, SignatureXML signatureXML) {
        this.clientSunat = clientSunat;
        this.sunatService = sunatService;
        this.signatureXML = signatureXML;
    }

    public SunatResponseDTO createInvoice(FacturacionDetalleDTO dto) {
        SunatResponseDTO sunatResponseDTO = new SunatResponseDTO();

        try {
            // Obtenemos datos del emisor
            post.setSupplier(getSupplier());

            // Asignamos el ID y los datos de la factura
            post.setId(dto.getId());
            post.setBillingDetail(dto);
            post.setVoucherTypeCode(dto.getVoucherTypeCode());

            // Valores dinámicos para la clase estática
            post.setPathApacheOrgXml(pathApacheOrgXml);

            // Definimos el nombre del archivo xml
            post.setFileName(buildFileNameFactura());

            // CLAVE SOL
            post.setUserClaveSol("CIX_TECH_MART");
            post.setPassClaveSol("CIX_TECH_MART");

            // CONSTRUIR EL XML CON DATA DINÁMICA
            String xml = Ubl21.createStringXmlInvoice(post);

            // Firmamos el XML y obtenemos el XML firmado y digestValue
            Map<String, String> xmlFirmado = signatureXML.signXML(xml);

            // Agregamos el XML firmado en la respuesta
            sunatResponseDTO.setXml(xmlFirmado.get("xml"));

            // ENCODE + ZIP del XML firmado en memoria (sin guardar en local)
            String encodedXml = encodeAndZipInMemory(xmlFirmado.get("xml"), post.getFileName());

            // Enviar el XML a SUNAT y obtener respuesta
            SOAPMessage response = clientSunat.sendBillCPE(encodedXml, post);

            // Procesar la respuesta SOAP de SUNAT
            SOAPBody soapResponseBody = response.getSOAPBody();
            sunatResponseDTO.setCode("-11");
            if (soapResponseBody.hasFault()) {
                // Error en la respuesta de SUNAT
                sunatResponseDTO.setMessage(soapResponseBody.getFault().getFaultString());
            } else {
                // Procesamos la respuesta exitosa de SUNAT
                sunatResponseDTO = sunatService.sunatDecodeResponse(response, dto.getId().toString());
                sunatResponseDTO.setDigestValue(xmlFirmado.get("digestValue"));
                sunatResponseDTO.setFileName(post.getFileName());
                sunatResponseDTO.setXml(xmlFirmado.get("xml"));
            }

        } catch (Exception e) {
            log.error("ERROR_CREATE_INVOICE_SUNAT->", e);
            sunatResponseDTO.setCode("-1");
            sunatResponseDTO.setMessage("Error al procesar la respuesta de SUNAT. Estado: PENDIENTE.");
        }

        return sunatResponseDTO;
    }

    private String encodeAndZipInMemory(String xmlContent, String fileName) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream, StandardCharsets.UTF_8)) {

            // Agregamos el XML al ZIP
            zipOutputStream.putNextEntry(new ZipEntry(fileName + ".xml"));
            zipOutputStream.write(xmlContent.getBytes(StandardCharsets.UTF_8));
            zipOutputStream.closeEntry();

            zipOutputStream.finish();

            // Convertimos el ZIP a Base64
            return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException("Error al crear el ZIP en memoria", e);
        }
    }


    public String buildFileNameFactura() {
        return post.getSupplier()
                .getSupplierIdentification()
                .concat("-")
                .concat(post.getVoucherTypeCode())
                .concat("-").
                concat(post.getBillingDetail().getSerie())
                .concat("-")
                .concat(post.getBillingDetail().getNumerocomprobante());
    }


    public static SupplierEconomicZoneDTO getSupplier() {
        SupplierEconomicZoneDTO supplier = new SupplierEconomicZoneDTO();
        supplier.setSupplierIdentification("10732521386"); // Cambiará por el RUC de la zona económica
        supplier.setSupplierName("CIX TECH MART");
        supplier.setSupplierRegistrationName("CIX TECH MART");
        supplier.setSupplierAddressLine("Cll. 7 Enero 225");
        return supplier;
    }
}