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
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.Map;


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

        try {
            // Obtenemos datos del emisor
            post.setSupplier(getSupplier());

            //Asignamos el ID y los datos de la factura
            post.setId(dto.getId());
            post.setBillingDetail(dto);
            post.setVoucherTypeCode(dto.getVoucherTypeCode());

            // Valores dinámicos para la clase estática
            post.setPathApacheOrgXml(pathApacheOrgXml);

            // Definimos el nombre del archivo xml
            post.setFileName(buildFileNameFactura());
            //CLAVE SOL
            post.setUserClaveSol("CIX_TECH_MART");
            post.setPassClaveSol("CIX_TECH_MART");

            // CONSTRUIR EL XML CON DATA DINÁMICA
            String xml = Ubl21.createStringXmlInvoice(post);

            // Construimos el xml y firmamos con el certificado digital (Método devuelve xml firmado y el digestValue)
            Map<String, String> xmlFirmado = signatureXML.signXML(xml);

            System.out.println("Este es el xml> "+xmlFirmado.get("xml"));

            // ENCONDE SUNAT
            String enconde = sunatService.sunatEncodeAndZip(xmlFirmado, dto.getId(), post.getFileName());

            //Codifica, zipea y envia a sunat
            SOAPMessage response = clientSunat.sendBillCPE(enconde, post);

            SOAPBody soapResponseBody = response.getSOAPBody();
            sunatResponseDTO.setCode("-11");
            if (soapResponseBody.hasFault()) {
                // Aca se lee el error de SUNAT
                sunatResponseDTO.setMessage(soapResponseBody.getFault().getFaultString());
            } else {
                sunatResponseDTO = sunatService.sunatDecodeResponse(response, dto.getId().toString());
                sunatResponseDTO.setDigestValue(xmlFirmado.get("digestValue"));
                sunatResponseDTO.setFileName(post.getFileName());
            }

        } catch (Exception e) {
            log.error("ERROR_CREATE_INVOCE_SUNAT->" , e);
            sunatResponseDTO.setCode("-1");
            // ACA DEBE CAMBIARSE EL ESTADO OSE A PENDIENTE Y NO DEBE GENERARSE UN COMPROBANTE ELECTRÓNICO
            sunatResponseDTO.setMessage("No se pudo procesar la respuesta de SUNAT, sin embargo se ha guardado en estado PENDIENTE.");
        }
        return sunatResponseDTO;
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