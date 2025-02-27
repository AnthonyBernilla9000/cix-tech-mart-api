package org.istrfa.sunat;

import org.istrfa.dto.FacturacionPostDTO;
import org.istrfa.utils.Constantes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.xml.soap.*;

/* Estructura XML SOAP
      -Envelope
       -Header
        -Security
         -UsernameToken
          -Username
          -Password
      -Body
   */
@Service
public class ClientSunat {
    private ClientSunat() {
    }

    @Value("${sunat.xmlns-ser}")
    private String xmlsnSer;

    @Value("${sunat.xmlns-wss}")
    private String xmlnsWss;

    @Value("${sunat.service-web-factura}")
    private String serviceWebFactura;

    private static final String SOAP_ENV = "SOAP-ENV";
    private static final String SOAP_ENV_VALUE = "http://schemas.xmlsoap.org/soap/envelope/";


    public SOAPEnvelope buildSOAPEnvelope(SOAPPart soapPart, String userSol, String passSol) throws SOAPException {
        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.setPrefix(Constantes.VARIABLE_SOAP_ENV);
        envelope.addNamespaceDeclaration("ser", xmlsnSer);
        envelope.addNamespaceDeclaration("wsse", xmlnsWss);

        // CONSTRUIR HEADERS DENTRO DE ENVELOPE
        buildSOAPHeader(envelope, userSol, passSol);

        return envelope;
    }

    public void buildSOAPHeader(SOAPEnvelope envelope, String userSol, String passSol) throws SOAPException {
        SOAPHeader header = envelope.getHeader();
        header.setPrefix(Constantes.VARIABLE_SOAP_ENV);

        SOAPElement security = header.addChildElement(Constantes.VARIABLE_SECURITY, "wsse");
        SOAPElement usernameToken = security.addChildElement(Constantes.VARIABLE_USER_NAME_TOKEN, "wsse");

        // USUARIO CLAVE SOL
        usernameToken.addChildElement(Constantes.VARIABLE_USER_NAME, "wsse")
                .addTextNode(userSol);

        //CONTRASEÑA CLAVE SOL
        usernameToken.addChildElement(Constantes.VARIABLE_USER_PASS, "wsse")
                .addTextNode(passSol);

    }


    public SOAPMessage soapConnectionCall(SOAPMessage soapMessage) throws SOAPException {

        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection soapConnection = soapConnectionFactory.createConnection();

        SOAPMessage soapResponse = soapConnection.call(soapMessage, serviceWebFactura);
        soapConnection.close();

        return soapResponse;
    }



    public SOAPMessage sendBillCPE(String base64, FacturacionPostDTO post) throws SOAPException {


        // CREAR INSTANCIA DE SOAP Y MENSAJE
        SOAPMessage soapMessage = MessageFactory.newInstance().createMessage();

        // SOAP PARTE "ENVELOPE"
        SOAPEnvelope envelope = buildSOAPEnvelope(soapMessage.getSOAPPart(), post.getUserClaveSol(), post.getPassClaveSol());

        // SOAP Body
        SOAPBody soapBody = envelope.getBody();

        soapBody.setPrefix(Constantes.VARIABLE_SOAP_ENV);

        SOAPElement sendBill = soapBody.addChildElement("sendBill", "ser");
        sendBill.addChildElement("fileName").addTextNode(post.getFileName().concat(Constantes.EXTENSION_ZIP));
        sendBill.addChildElement("contentFile").addTextNode(base64);

        soapMessage.saveChanges();

        // RETORNO RESPUESTA
        return soapConnectionCall(soapMessage);
    }


}
