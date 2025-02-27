package org.istrfa.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The type Signature xml.
 */
@Slf4j
@Service
public class SignatureXML {

    private SignatureXML() {
    }

    @Value("${files.certificate-sunat}")
    private String pathCertificate;

    /**
     * The Sign reference id.
     */
    static final String SIGN_REFERENCE_ID = "SignID";


//    private  void loadKeyStore(KeyStore p12, String pathSignature, String passSignature) {
//        System.out.println("Este es el path "+pathCertificate);
////        File file = new File(pathCertificate);
////        if (!file.exists()) {
////            throw new IllegalArgumentException("Archivo .p12 no encontrado: " + pathCertificate);
////        }
////        log.info("Cargando KeyStore desde: " + file.getAbsolutePath());
//        try (FileInputStream fis = new FileInputStream(pathSignature)) {
////            p12.load(fis, passSignature.toCharArray());
//            p12.load(fis, "1ngyTAL2023".toCharArray());
//        } catch (IOException | NoSuchAlgorithmException | CertificateException g) {
//            // Manejo de excepciones
//            log.info("ERROR_LOAD_KEY_STORE->"+g.getMessage());
//        }
//    }

    private void loadKeyStore(KeyStore p12) {
        System.out.println("Este es el path: " + pathCertificate);

        // Carga el archivo desde el classpath
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(pathCertificate);
        if (inputStream == null) {
            throw new IllegalArgumentException("Archivo .p12 no encontrado en classpath: " + pathCertificate);
        }

        try (inputStream) { // Usamos directamente el InputStream desde el classpath
            log.info("Cargando KeyStore desde: " + pathCertificate);
            p12.load(inputStream, "1ngyTAL2023".toCharArray());
        } catch (IOException | NoSuchAlgorithmException | CertificateException g) {
            log.info("ERROR_LOAD_KEY_STORE->" + g.getMessage());
        }
    }


    /**
     * Sign xml map.
     *
     * @param xml           the path xml
     * @param pathSignature the path signature
     * @param passSignature the pass signature
     * @return the map
     */
    public  Map<String, String> signXML(String xml) {
        Map<String, String> xmlSignature = new HashMap<>();

        try {
            // CONVERTIR STRING A XML DOCUMENT
            Document copyDocument = MethodsXML.convertStringToXMLDocument(xml);

            KeyStore p12 = KeyStore.getInstance("pkcs12");

            loadKeyStore(p12);

            Enumeration<?> e = p12.aliases();
            String alias = (String) e.nextElement();

            KeyStore.PrivateKeyEntry keyEntry = (KeyStore.PrivateKeyEntry) p12.getEntry(alias, new KeyStore.PasswordProtection("1ngyTAL2023".toCharArray()));
            Node nodeExtensionContent = copyDocument.getElementsByTagNameNS("*", "ExtensionContent").item(0);

            XMLSignatureFactory signatureFactory = buildSignatureFactory();

            DOMSignContext signContext = new DOMSignContext(keyEntry.getPrivateKey(), copyDocument.getDocumentElement());
            signContext.setDefaultNamespacePrefix("ds");
            signContext.setParent(nodeExtensionContent);

            Reference reference = signatureFactory.newReference("",
                    signatureFactory.newDigestMethod(DigestMethod.SHA1, null),
                    Collections.singletonList(signatureFactory.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null)), null, null);

            SignedInfo signedInfo = signatureFactory.newSignedInfo(
                    signatureFactory.newCanonicalizationMethod(CanonicalizationMethod.INCLUSIVE, (C14NMethodParameterSpec) null),
                    signatureFactory.newSignatureMethod(SignatureMethod.RSA_SHA1, null), Collections.singletonList(reference));

            // Certificate
            List<X509Certificate> x509Content = new ArrayList<>();
            x509Content.add((X509Certificate) keyEntry.getCertificate());

            KeyInfoFactory keyInfoFactory = signatureFactory.getKeyInfoFactory();
            X509Data xdata = keyInfoFactory.newX509Data(x509Content);
            KeyInfo keyInfo = keyInfoFactory.newKeyInfo(Collections.singletonList(xdata));

            // Sign
            XMLSignature signature = signatureFactory.newXMLSignature(signedInfo, keyInfo);
            signature.sign(signContext);

            String referenceID = SIGN_REFERENCE_ID;

            Element elementParent = (Element) signContext.getParent();
            if (elementParent.getElementsByTagName("ds:Signature") != null) {
                Element elementSignature = (Element) elementParent.getElementsByTagName("ds:Signature").item(0);
                elementSignature.setAttribute("Id", referenceID);
            }

            //OBTENEMOS EL HASH (DIGEST VALUE)
            xmlSignature.put("digestValue", MethodsXML.getTextNodeXML(copyDocument, "ds:DigestValue"));
            xmlSignature.put("xml", MethodsXML.convertDocumentToXMLString(copyDocument));
        } catch (TransformerException ex) {
            Logger.getLogger(SignatureXML.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error firma: " + e);
        }

        return xmlSignature;
    }

    /**
     * Build signature factory xml signature factory.
     *
     * @return the xml signature factory
     */
    public static XMLSignatureFactory buildSignatureFactory() {
        XMLSignatureFactory signatureFactory;
        try {
            signatureFactory = XMLSignatureFactory.getInstance("DOM", "ApacheXMLDSig");
        } catch (NoSuchProviderException ex) {
            signatureFactory = XMLSignatureFactory.getInstance("DOM");
        }

        return signatureFactory;
    }
}
