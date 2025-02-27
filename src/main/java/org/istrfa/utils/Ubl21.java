package org.istrfa.utils;

import org.istrfa.dto.DetalleOrdenPagoDTO;
import org.istrfa.dto.FacturacionPostDTO;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * The type Ubl 21.
 */
public class Ubl21 {
    private Ubl21() {
    }

    // DECLARACION DE ELEMENTOS REPETIDOS
    private static String extUBLExtensions = "ext:UBLExtensions";
    private static String extUBLExtesion = "ext:UBLExtension";
    private static String cbcName = "cbc:Name";
    private static String cbcId = "cbc:ID";
    private static String cbcInstructionId = "cbc:InstructionID";
    private static String cbcPaidAmount = "cbc:PaidAmount";
    private static String cbcDocumentTypeCode = "cbc:DocumentTypeCode";
    private static String cbcTaxableAmount = "cbc:TaxableAmount";
    private static String cbcTaxAmount = "cbc:TaxAmount";
    private static String cbcTaxTypeCode = "cbc:TaxTypeCode";
    private static String sacBillingPayment = "sac:BillingPayment";
    private static String cacTaxTotal = "cac:TaxTotal";
    private static String cacTaxSubTotal = "cac:TaxSubtotal";
    private static String cacTaxScheme = "cac:TaxScheme";
    private static String cacTaxCategory = "cac:TaxCategory";
    private static String cacParty = "cac:Party";
    private static String cacPartyName = "cac:PartyName";
    private static String cacPartyIdentification = "cac:PartyIdentification";

    private static String currencyId = "currencyID";


    private static final String XMLNS = "xmlns";
    private static final String XMLNS_SAC = "xmlns:sac";
    private static final String XMLNS_CAC = "xmlns:cac";
    private static final String XMLNS_CBC = "xmlns:cbc";
    private static final String XMLNS_EXT = "xmlns:ext";
    private static final String CAC_SIGNATURE = "cac:Signature";
    private static final String EXT_EXTENSION_CONTENT = "ext:ExtensionContent";
    private static final String SCHEME_ID = "schemeID";
    private static final String CAC_REGISTRATION_ADDRESS = "cac:RegistrationAddress";
    private static final String CAC_PARTY_LEGAL_ENTITY = "cac:PartyLegalEntity";
    private static final String CAC_ADDRESS_LINE = "cac:AddressLine";
    private static final String VALUE_XMLNS_SAC = "urn:sunat:names:specification:ubl:peru:schema:xsd:SunatAggregateComponents-1";
    private static final String VALUE_XMLNS_CBC = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2";
    private static final String VALUE_XMLNS_EXT = "urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2";
    private static final String VALUE_XMLNS_CAC = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2";
    private static final String CBC_LINE = "cbc:Line";
    private static final String CBC_ISSUE_DATE = "cbc:IssueDate";
    private static final String CBC_REGISTRATION_NAME = "cbc:RegistrationName";
    private static final String CAC_ACCOUNTING_SUPPLIER_PARTY = "cac:AccountingSupplierParty";
    private static final String CBC_CUSTOMER_ASSIGNED_ACCOUNT_ID = "cbc:CustomerAssignedAccountID";
    private static final String CBC_ADDTIONAL_ACCOUNT_ID = "cbc:AdditionalAccountID";

    public static Element buildRootElementAttributes(Document doc, FacturacionPostDTO dto) {
        Element rootElement = doc.createElement(dto.getVoucherTypeCode()
                .equals(Constantes.VOUCHER_TYPE_CODE_CREDIT_NOTE) ? "CreditNote" : "Invoice");
        if (dto.getVoucherTypeCode().equals(Constantes.VOUCHER_TYPE_CODE_CREDIT_NOTE)) {
            rootElement.setAttribute(XMLNS, "urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2");
            rootElement.setAttribute(XMLNS_SAC, VALUE_XMLNS_SAC);
        } else {
            rootElement.setAttribute(XMLNS, "urn:oasis:names:specification:ubl:schema:xsd:Invoice-2");
            rootElement.setAttribute("xmlns:xsd", "http://www.w3.org/2001/XMLSchema");
        }
        rootElement.setAttribute(XMLNS_CAC, VALUE_XMLNS_CAC);
        rootElement.setAttribute(XMLNS_CBC, VALUE_XMLNS_CBC);
        rootElement.setAttribute("xmlns:ccts", "urn:un:unece:uncefact:documentation:2");
        rootElement.setAttribute("xmlns:ds", "http://www.w3.org/2000/09/xmldsig#");
        rootElement.setAttribute(XMLNS_EXT, VALUE_XMLNS_EXT);
        rootElement.setAttribute("xmlns:qdt", "urn:oasis:names:specification:ubl:schema:xsd:QualifiedDatatypes-2");
        rootElement.setAttribute("xmlns:udt", "urn:un:unece:uncefact:data:specification:UnqualifiedDataTypesSchemaModule:2");
        rootElement.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");

        return rootElement;
    }

    public static String buildExoneradaORIgv(String igv) {
        return Double.parseDouble(igv) == 0 ? "EXO" : "IGV";
    }

    public static String buildCodeExoneradaORIgv(String igv) {
        return Double.parseDouble(igv) == 0 ? "9997" : "1000";
    }


    /**
     * Create string xml invoice string.
     *
     * @param dto the dto
     * @return the string
     * @throws ParserConfigurationException the parser configuration exception
     * @throws TransformerException         the transformer exception
     */
    public static String createStringXmlInvoice(FacturacionPostDTO dto) throws ParserConfigurationException, TransformerException {

        String pathApacheOrgXml = dto.getPathApacheOrgXml();

        String invoiceTypeCodePost = dto.getVoucherTypeCode();
        String supplierIdentificationPost = dto.getSupplier().getSupplierIdentification();
        String supplierNamePost = dto.getSupplier().getSupplierName();
        String supplierRegistrationNamePost = dto.getSupplier().getSupplierRegistrationName();
        String supplierAddressLinePost = dto.getSupplier().getSupplierAddressLine();
        String identificationCustomerPost = dto.getBillingDetail().getNumerodocumento();
        String registrationNameCustomerPost = dto.getBillingDetail().getNombreusuario();
        String addresLineCustomerPost = dto.getBillingDetail().getDireccion();
        List<DetalleOrdenPagoDTO> items = dto.getBillingDetail().getItems();

        String total = NumberUtils.doubleAsString(dto.getBillingDetail().getTotal());
        String subtotal = NumberUtils.doubleAsString(dto.getBillingDetail().getSubTotal());
        String igv = NumberUtils.doubleAsString(dto.getBillingDetail().getIgv());
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        docBuilderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        docBuilderFactory.setFeature(pathApacheOrgXml, true);

        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

        //****************  ROOT ELEMENT **************//
        Document doc = docBuilder.newDocument();

        Element rootElement1 = buildRootElementAttributes(doc, dto);

        doc.appendChild(rootElement1);

        // ---------------- FIRMA DIGITAL ------------------------//


        Element ublExtensionsA1 = doc.createElement(extUBLExtensions);
        Element ublExtensions2 = doc.createElement(extUBLExtesion);
        Element extensionContent = doc.createElement(EXT_EXTENSION_CONTENT);

        ublExtensions2.appendChild(extensionContent);
        ublExtensionsA1.appendChild(ublExtensions2);


        //----- 3. Numeración, conformada por serie y número correlativo ----//
        Element idA1 = doc.createElement(cbcId);
        idA1.setTextContent(buildSerieAndCorrelative(dto));

        //----- 4. Fecha de emisión ----//
        Element issueDate = doc.createElement(CBC_ISSUE_DATE);
        issueDate.setTextContent(LocalDate.now().toString());

        //----- 5. Hora de emisión ----//
        Element issueTime = doc.createElement("cbc:IssueTime");
        issueTime.setTextContent(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));

        //----- 7. Tipo de moneda ----//
        Element documentCurrencyCode = doc.createElement("cbc:DocumentCurrencyCode");
        documentCurrencyCode.setTextContent("PEN");


        //----- 10. AccountingSupplierParty  -------------------//
        // Reconstruido en buildElementAccountingSupplierParty()

        // -----------------   END AccountingSupplierParty  ----------------------//

        //----- 11. AccountingCustomerParty  -------------------//

        // Reconstruido en buildElement

        // -----------------   END AccountingCustomerParty  ----------------------//

        //----------------------   Tax total  ------------------------------//
        Element taxTotal = doc.createElement(cacTaxTotal);

        //----- taxAmount  -------------------//
        Element taxAmount = doc.createElement(cbcTaxAmount);
        taxAmount.setAttribute(currencyId, "PEN");
        taxAmount.setTextContent(igv);

        //-------- taxSubtotal  -------------------//
        Element taxSubtotal = doc.createElement(cacTaxSubTotal);

        System.out.println("Este es ek subtotal1 " + subtotal);
        Element taxableAmount = doc.createElement(cbcTaxableAmount);
        taxableAmount.setAttribute(currencyId, "PEN");
        taxableAmount.setTextContent(subtotal);

        Element taxAmountSubtotal = doc.createElement(cbcTaxAmount);
        taxAmountSubtotal.setAttribute(currencyId, "PEN");
        taxAmountSubtotal.setTextContent(igv);

        Element taxCategorySubtotal = doc.createElement(cacTaxCategory);
        Element taxSchemeSubtotal = doc.createElement(cacTaxScheme);
        Element idShemeSubtotal = doc.createElement(cbcId);
        idShemeSubtotal.setTextContent(buildCodeExoneradaORIgv(igv));

        Element nameShemeSubtotal = doc.createElement(cbcName);
        nameShemeSubtotal.setTextContent(buildExoneradaORIgv(igv));

        Element taxTypeCodeShemeSubtotal = doc.createElement(cbcTaxTypeCode);
        taxTypeCodeShemeSubtotal.setTextContent("VAT");

        taxSchemeSubtotal.appendChild(idShemeSubtotal);
        taxSchemeSubtotal.appendChild(nameShemeSubtotal);
        taxSchemeSubtotal.appendChild(taxTypeCodeShemeSubtotal);

        taxCategorySubtotal.appendChild(taxSchemeSubtotal);

        taxSubtotal.appendChild(taxableAmount);
        taxSubtotal.appendChild(taxAmountSubtotal);
        taxSubtotal.appendChild(taxCategorySubtotal);

        taxTotal.appendChild(taxAmount);
        taxTotal.appendChild(taxSubtotal);

        //--------------------- Legal monetary total ---------------------//
        Element legalMonetaryTotal = doc.createElement("cac:LegalMonetaryTotal");
        Element lineExtensionAmount = doc.createElement("cbc:LineExtensionAmount");
        lineExtensionAmount.setAttribute(currencyId, "PEN");
        lineExtensionAmount.setTextContent(subtotal);

        Element taxInclusiveAmount = doc.createElement("cbc:TaxInclusiveAmount");
        taxInclusiveAmount.setAttribute(currencyId, "PEN");
        taxInclusiveAmount.setTextContent(total);

        Element payableAmount = doc.createElement("cbc:PayableAmount");
        payableAmount.setAttribute(currencyId, "PEN");
        payableAmount.setTextContent(total);

        legalMonetaryTotal.appendChild(lineExtensionAmount);
        legalMonetaryTotal.appendChild(taxInclusiveAmount);
        legalMonetaryTotal.appendChild(payableAmount);


        //---------  Agregamos elementos a la raiz -------//
        rootElement1.appendChild(ublExtensionsA1);
        rootElement1.appendChild(buildUBLVersionID(doc, "2.1"));
        rootElement1.appendChild(buildCustomizationID(doc, "2.0"));
        rootElement1.appendChild(idA1);
        rootElement1.appendChild(issueDate);
        rootElement1.appendChild(issueTime);

        //----- 6. Tipo de documento:  ----//
        if (!dto.getVoucherTypeCode().equals(Constantes.VOUCHER_TYPE_CODE_CREDIT_NOTE)) {
            Element invoiceTypeCode = doc.createElement("cbc:InvoiceTypeCode");
            invoiceTypeCode.setAttribute("listID", "0101");
            invoiceTypeCode.setAttribute("listAgencyName", "PE:SUNAT");
            invoiceTypeCode.setAttribute("listSchemeURI", "urn:pe:gob:sunat:cpe:see:gem:catalogos:catalogo51");
            invoiceTypeCode.setAttribute("listURI", "urn:pe:gob:sunat:cpe:see:gem:catalogos:catalogo01");
            invoiceTypeCode.setTextContent(invoiceTypeCodePost);
            rootElement1.appendChild(invoiceTypeCode);
        }

        rootElement1.appendChild(documentCurrencyCode);

        /************ PARA NOTA DE CRÉDITO ***********/

        // ----- 8. Código del tipo de nota de crédito electrónica -------//


        /*************************************************************/
        // Agrego el Elemento Signature
        rootElement1.appendChild(buildElementSignature(doc, supplierIdentificationPost, supplierAddressLinePost));
        rootElement1.appendChild(buildElementAccountingSupplierParty(doc, supplierIdentificationPost, supplierAddressLinePost, supplierNamePost, supplierRegistrationNamePost)); //AccountingSupplierParty
        rootElement1.appendChild(buildElementAccountingCustomerParty(doc, identificationCustomerPost, registrationNameCustomerPost, addresLineCustomerPost));

        // SI NO ES NOTA DE CRÉDITO
        if (!dto.getVoucherTypeCode().equals(Constantes.VOUCHER_TYPE_CODE_CREDIT_NOTE)) {
            //---------------------  Payment Terms  ---------------------------//
            Element paymentTerms = doc.createElement("cac:PaymentTerms");

            //----- ID  -------------------//
            Element idPayment = doc.createElement(cbcId);
            idPayment.setTextContent("FormaPago");

            //----- ID  -------------------//
            Element paymentMeansID = doc.createElement("cbc:PaymentMeansID");
            paymentMeansID.setTextContent("Contado");

            paymentTerms.appendChild(idPayment);
            paymentTerms.appendChild(paymentMeansID);
            rootElement1.appendChild(paymentTerms);
        }

        rootElement1.appendChild(taxTotal);
        rootElement1.appendChild(legalMonetaryTotal);

        //------------------------------------------- CONSTRUIR ITEMS ----------------------------------//
        for (int i = 0; i < items.size(); i++) {
            String igvItem = NumberUtils.doubleAsString(items.get(i).getIgv());
            String subTotalItem = NumberUtils.doubleAsString(items.get(i).getSubtotal());
            String precioItem = NumberUtils.doubleAsString(items.get(i).getPrecio());
            String cantidadItem = String.valueOf(items.get(i).getCantidad());
            String igvPercent = NumberUtils.doubleAsString(items.get(i).getPorcentajeigv());
            Double tasaIgv = items.get(i).getPorcentajeigv() / 100; // obtengo igv en decimal
            Double igvPrecioUnitario = items.get(i).getPrecio() * tasaIgv; // obtengo el igv del precio unitario
            String valorItem = NumberUtils.doubleAsString(igvPrecioUnitario + items.get(i).getPrecio()); // obtengo el precio unitario con igv

            String taxShemeIdItem = buildTaxSchemeIdItem(igvItem);
            String taxShemeNameItem = buildTaxSchemeNameItem(igvItem);
            String taxShemeTypeCodeItem = "VAT";


            Element invoiceLine = doc.createElement(dto.getVoucherTypeCode()
                    .equals(Constantes.VOUCHER_TYPE_CODE_CREDIT_NOTE) ? "cac:CreditNoteLine" : "cac:InvoiceLine");

            //----- ID  -------------------//
            Element id = doc.createElement(cbcId);
            id.setTextContent(String.valueOf(i + 1));

            //----- InvoicedQuantity  -------------//
            Element invoicedQuantity = doc.createElement(dto.getVoucherTypeCode()
                    .equals(Constantes.VOUCHER_TYPE_CODE_CREDIT_NOTE) ? "cbc:CreditedQuantity" : "cbc:InvoicedQuantity");
            invoicedQuantity.setAttribute("unitCode", "NIU");
            invoicedQuantity.setTextContent(cantidadItem); // cantidad de item

            Element lineExtensionAmountInvoiceLine = doc.createElement("cbc:LineExtensionAmount");
            lineExtensionAmountInvoiceLine.setAttribute(currencyId, "PEN");
            lineExtensionAmountInvoiceLine.setTextContent(subTotalItem); // sub total de item

            //----- 12.5 PricingReference  -------------//
            Element pricingReference = doc.createElement("cac:PricingReference");

            Element alternativeConditionPrice = doc.createElement("cac:AlternativeConditionPrice");

            Element priceAmountPricing = doc.createElement("cbc:PriceAmount");
            priceAmountPricing.setAttribute(currencyId, "PEN");
            priceAmountPricing.setTextContent(valorItem); //precio unitario con igv

            Element priceTypeCode = doc.createElement("cbc:PriceTypeCode");
            priceTypeCode.setTextContent("01");

            alternativeConditionPrice.appendChild(priceAmountPricing);
            alternativeConditionPrice.appendChild(priceTypeCode);

            pricingReference.appendChild(alternativeConditionPrice);

            Element item = doc.createElement("cac:Item");

            Element description = doc.createElement("cbc:Description");
            description.setTextContent(items.get(i).getNametramite()); // Nombre de trámite
            item.appendChild(description);

            Element price = doc.createElement("cac:Price");
            Element priceAmount = doc.createElement("cbc:PriceAmount");
            priceAmount.setAttribute(currencyId, "PEN");
            priceAmount.setTextContent(precioItem); //precio unitario sin igv

            price.appendChild(priceAmount);

            //----------------------   Tax total  ------------------------------//

            Element taxTotalInvoiceLine = doc.createElement(cacTaxTotal);


            //----- taxAmount  -------------------//
            Element taxAmountInvoiceLine = doc.createElement(cbcTaxAmount);
            taxAmountInvoiceLine.setAttribute(currencyId, "PEN");
            taxAmountInvoiceLine.setTextContent(igvItem);

            //-------- taxSubtotal  -------------------//
            Element taxSubtotalInvoiceLine = doc.createElement(cacTaxSubTotal);

            System.out.println("Este es ek subtotal2 " + subTotalItem);
            Element taxableAmountInvoiceLine = doc.createElement(cbcTaxableAmount);
            taxableAmountInvoiceLine.setAttribute(currencyId, "PEN");
            taxableAmountInvoiceLine.setTextContent(subTotalItem);

            Element taxAmountSubtotalInvoiceLine = doc.createElement(cbcTaxAmount);
            taxAmountSubtotalInvoiceLine.setAttribute(currencyId, "PEN");
            taxAmountSubtotalInvoiceLine.setTextContent(igvItem);

            Element taxCategorySubtotalInvoiceLine = doc.createElement(cacTaxCategory);

            Element percentSchemeSubtotalInvoiceLine = doc.createElement("cbc:Percent");
            percentSchemeSubtotalInvoiceLine.setTextContent(igvPercent);

            Element taxExemptionReasonCode = doc.createElement("cbc:TaxExemptionReasonCode");
            taxExemptionReasonCode.setTextContent(Double.parseDouble(igvItem) == 0 ? "20" : "10");

            Element taxSchemeSubtotalInvoiceLine = doc.createElement(cacTaxScheme);
            Element idShemeSubtotalInvoiceLine = doc.createElement(cbcId);
            idShemeSubtotalInvoiceLine.setTextContent(taxShemeIdItem);

            Element nameShemeSubtotalInvoiceLine = doc.createElement(cbcName);
            nameShemeSubtotalInvoiceLine.setTextContent(taxShemeNameItem);

            Element taxTypeCodeShemeSubtotalInvoiceLine = doc.createElement(cbcTaxTypeCode);
            taxTypeCodeShemeSubtotalInvoiceLine.setTextContent(taxShemeTypeCodeItem);

            taxSchemeSubtotalInvoiceLine.appendChild(idShemeSubtotalInvoiceLine);
            taxSchemeSubtotalInvoiceLine.appendChild(nameShemeSubtotalInvoiceLine);
            taxSchemeSubtotalInvoiceLine.appendChild(taxTypeCodeShemeSubtotalInvoiceLine);

            taxCategorySubtotalInvoiceLine.appendChild(percentSchemeSubtotalInvoiceLine);
            taxCategorySubtotalInvoiceLine.appendChild(taxExemptionReasonCode);
            taxCategorySubtotalInvoiceLine.appendChild(taxSchemeSubtotalInvoiceLine);

            taxSubtotalInvoiceLine.appendChild(taxableAmountInvoiceLine);
            taxSubtotalInvoiceLine.appendChild(taxAmountSubtotalInvoiceLine);
            taxSubtotalInvoiceLine.appendChild(taxCategorySubtotalInvoiceLine);

            taxTotalInvoiceLine.appendChild(taxAmountInvoiceLine);
            taxTotalInvoiceLine.appendChild(taxSubtotalInvoiceLine);

            invoiceLine.appendChild(id);
            invoiceLine.appendChild(invoicedQuantity);
            invoiceLine.appendChild(lineExtensionAmountInvoiceLine);
            invoiceLine.appendChild(pricingReference);
            invoiceLine.appendChild(taxTotalInvoiceLine);
            invoiceLine.appendChild(item);
            invoiceLine.appendChild(price);

            rootElement1.appendChild(invoiceLine);
        }

        return MethodsXML.convertDocumentToXMLString(doc);
    }


    public static Element buildElementAccountingCustomerParty(
            Document doc, String identificationCustomerPost, String registrationNameCustomerPost,
            String addresLineCustomerPost) {

        Element accountingCustomerParty = doc.createElement("cac:AccountingCustomerParty");

        //----- 11.3 Party Customer ------------------//
        Element partyCustomer = doc.createElement(cacParty);
        //----- 10.3.1 partyIdentificationCustomer - Emisor  ------------------//
        Element partyIdentificationCustomer = doc.createElement(cacPartyIdentification);
        Element idCustomer = doc.createElement(cbcId);


        idCustomer.setAttribute(SCHEME_ID, codeDocumentType(identificationCustomerPost));
        idCustomer.setTextContent(identificationCustomerPost);
        partyIdentificationCustomer.appendChild(idCustomer);

        //----- 11.3.1 Razón social - Receptor  ------------------//
        Element partyLegalEntityCustomer = doc.createElement(CAC_PARTY_LEGAL_ENTITY);
        Element registrationNameCustomer = doc.createElement(CBC_REGISTRATION_NAME);
        registrationNameCustomer.setTextContent(registrationNameCustomerPost);

        Element registrationAddressCustomer = doc.createElement(CAC_REGISTRATION_ADDRESS);

        Element addressLineCustomer = doc.createElement(CAC_ADDRESS_LINE);

        Element lineCustomer = doc.createElement(CBC_LINE);
        lineCustomer.setTextContent(addresLineCustomerPost);

        addressLineCustomer.appendChild(lineCustomer);
        registrationAddressCustomer.appendChild(addressLineCustomer);
        partyLegalEntityCustomer.appendChild(registrationNameCustomer);
        partyLegalEntityCustomer.appendChild(registrationAddressCustomer);


        partyCustomer.appendChild(partyIdentificationCustomer);
        partyCustomer.appendChild(partyLegalEntityCustomer);

        accountingCustomerParty.appendChild(partyCustomer);

        return accountingCustomerParty;
    }

    public static Element buildElementAccountingSupplierParty(
            Document doc, String supplierIdentificationPost, String supplierAddressLinePost,
            String supplierNamePost, String supplierRegistrationNamePost) {

        Element accountingSupplierParty = doc.createElement(CAC_ACCOUNTING_SUPPLIER_PARTY);

        //----- 10.3 Party Supplier ------------------//
        Element partySupplierA1 = doc.createElement(cacParty);

        //----- 10.3.1 partyIdentificationSupplier - Emisor  ------------------//
        Element partyIdentificationSupplierA1 = doc.createElement(cacPartyIdentification);
        //----- 10.3.1 partyIdentificationSupplier - Emisor  ------------------//
        Element idSupplierA1 = doc.createElement(cbcId);
        idSupplierA1.setAttribute(SCHEME_ID, "6");
        idSupplierA1.setTextContent(supplierIdentificationPost);
        partyIdentificationSupplierA1.appendChild(idSupplierA1);

        //----- 10.3.1 Nombre comercial - Emisor  ------------------//
        Element partyNameSupplierA1 = doc.createElement(cacPartyName);
        Element nameSupplierA1 = doc.createElement(cbcName);
        nameSupplierA1.setTextContent(supplierNamePost);
        partyNameSupplierA1.appendChild(nameSupplierA1);

        //----- 10.3.1 Razón social - Emisor ------------------//
        Element partyLegalEntitySupplierA1 = doc.createElement(CAC_PARTY_LEGAL_ENTITY);
        Element registrationNameSupplierA1 = doc.createElement(CBC_REGISTRATION_NAME);
        registrationNameSupplierA1.setTextContent(supplierRegistrationNamePost);

        Element registrationAddressSupplierA1 = doc.createElement(CAC_REGISTRATION_ADDRESS);
        Element addressTypeCodeSupplierA1 = doc.createElement("cbc:AddressTypeCode");
        addressTypeCodeSupplierA1.setTextContent("00000");

        Element addressLineSupplierA1 = doc.createElement(CAC_ADDRESS_LINE);
        Element lineSupplierA1 = doc.createElement(CBC_LINE);
        lineSupplierA1.setTextContent(supplierAddressLinePost);
        addressLineSupplierA1.appendChild(lineSupplierA1);

        registrationAddressSupplierA1.appendChild(addressTypeCodeSupplierA1);
        registrationAddressSupplierA1.appendChild(addressLineSupplierA1);

        partyLegalEntitySupplierA1.appendChild(registrationNameSupplierA1);
        partyLegalEntitySupplierA1.appendChild(registrationAddressSupplierA1);

        partySupplierA1.appendChild(partyIdentificationSupplierA1);
        partySupplierA1.appendChild(partyNameSupplierA1);
        partySupplierA1.appendChild(partyLegalEntitySupplierA1);

        accountingSupplierParty.appendChild(partySupplierA1);

        return accountingSupplierParty;
    }

    public static Element buildElementSignature(Document doc, String supplierIdentificationPost, String supplierAddressLinePost) {
        //--------------- Signature ---------//
        Element signature = doc.createElement(CAC_SIGNATURE);

        Element idSignatureA1 = doc.createElement(cbcId);
        idSignatureA1.setTextContent("APISUNAT");

        Element signatoryPartyA1 = doc.createElement("cac:SignatoryParty");
        Element partyIdentificationA1 = doc.createElement(cacPartyIdentification);
        Element idPartyIdentificationA1 = doc.createElement(cbcId);
        idPartyIdentificationA1.setTextContent(supplierIdentificationPost);
        partyIdentificationA1.appendChild(idPartyIdentificationA1);

        Element partyNameA1 = doc.createElement(cacPartyName);
        Element nameA1 = doc.createElement(cbcName);
        nameA1.setTextContent(supplierAddressLinePost);
        partyNameA1.appendChild(nameA1);

        signatoryPartyA1.appendChild(partyIdentificationA1);
        signatoryPartyA1.appendChild(partyNameA1);

        Element digitalSignatureAttachmentA1 = doc.createElement("cac:DigitalSignatureAttachment");
        Element externalReferenceA1 = doc.createElement("cac:ExternalReference");
        Element uriA1 = doc.createElement("cbc:URI");
        uriA1.setTextContent("https://apisunat.com/");
        externalReferenceA1.appendChild(uriA1);
        digitalSignatureAttachmentA1.appendChild(externalReferenceA1);

        signature.appendChild(idSignatureA1);
        signature.appendChild(signatoryPartyA1);
        signature.appendChild(digitalSignatureAttachmentA1);

        return signature;
    }



    public static String buildTaxSchemeNameItem(String igvItem) {
        Double igv = Double.parseDouble(igvItem);
        if (igv.equals(0.0)) return "EXO";
        else return "IGV";
    }

    public static String buildTaxSchemeIdItem(String igvItem) {
        Double igv = Double.parseDouble(igvItem);
        if (igv.equals(0.0)) return "9997";
        else return "1000";
    }


    public static String buildSerieAndCorrelative(FacturacionPostDTO dto) {
        return dto.getBillingDetail().getSerie().concat("-").concat(dto.getBillingDetail().getNumerocomprobante());

    }

    public static String codeDocumentType(String document) {
        Integer length = document.length();
        if (length.equals(11)) return Constantes.DOCUMENT_TYPE_SUNAT_RUC;
        else if (length.equals(8)) return Constantes.DOCUMENT_TYPE_SUNAT_DNI;
        else return Constantes.DOCUMENT_TYPE_SUNAT_PASAPORTE;
    }


    //----- 1. Versión del UBL ----//
    public static Element buildUBLVersionID(Document doc, String version) {
        Element ublVersionID = doc.createElement("cbc:UBLVersionID");
        ublVersionID.setTextContent(version);
        return ublVersionID;
    }

    //----- 2. Versión de la estructura del documento ----//
    public static Element buildCustomizationID(Document doc, String version) {
        Element customizationID = doc.createElement("cbc:CustomizationID");
        customizationID.setTextContent(version);
        return customizationID;
    }


}

