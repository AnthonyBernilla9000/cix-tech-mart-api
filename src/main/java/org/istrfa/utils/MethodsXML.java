package org.istrfa.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * The type Methods xml.
 */
public class MethodsXML {

    private MethodsXML(){}

    /**
     * Convert document to xml string string.
     *
     * @param document the document
     * @return the string
     * @throws TransformerException the transformer exception
     */
    public static String convertDocumentToXMLString(Document document) throws TransformerException {

        TransformerFactory transformerFactory = TransformerFactory.newInstance();

        // Habilitar el "Procesamiento Seguro"
        transformerFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(document);
        StringWriter stringWriter = new StringWriter();
        StreamResult result = new StreamResult(stringWriter);
        transformer.transform(source, result);
        return stringWriter.toString();
    }

    /**
     * Convert string to xml document document.
     *
     * @param xmlString the xml string
     * @return the document
     * @throws Exception the exception
     */
    public static Document convertStringToXMLDocument(String xmlString) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);

        factory.setNamespaceAware(true); // Habilitar soporte para espacios de nombres
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource inputSource = new InputSource(new StringReader(xmlString));
        return builder.parse(inputSource);
    }

    /**
     * Gets text node xml.
     *
     * @param document the document
     * @param tagName  the tag name
     * @return the text node xml
     */
    public static String getTextNodeXML(Document document, String tagName) {
        NodeList nodeList = document.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            Node node = nodeList.item(0);
            return node.getTextContent();
        }
        return null;
    }

}
