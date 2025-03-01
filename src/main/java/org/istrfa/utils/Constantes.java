package org.istrfa.utils;

import java.util.UUID;

public class Constantes {

    public static final String TOKEN_API_PERU = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6ImFudG9ueWJlcm5pbGxhMkBnbWFpbC5jb20ifQ.fqcsYC53b6WBdLgf3wIO6kN6hH4ZxCo3A8YCg-jgIFQ";

    public static final String EMAIL_USER_ACCOUNT = "cixtechmart@gmail.com";
    public static final String EMAIL_PASSWORD_APP = "rqbgvftitymudtrn";

    public static final Double IGV = 18.0;


    //Prefijo 1
    public static final UUID TIPO_DOCUMENT_DNI = UUID.fromString("d7161395-0a50-48a0-a8b5-f303313ef981");
    public static final UUID TIPO_DOCUMENT_RUC = UUID.fromString("9c678009-ac15-4944-ad14-fd8d60114275");

    //Prefijo 2 (Estados del trabajador)
    public static final UUID ESTADO_TRABAJADOR_ACTIVO = UUID.fromString("d011b41e-79a0-4c91-85de-4b6c19abfafb");
    public static final UUID ESTADO_TRABAJADOR_INACTIVO = UUID.fromString("c325d086-7b80-4cad-befa-bfaed4f68cc0");

    //Prefijo 4 (Estados del cliente)
    public static final UUID ESTADO_CLIENTE_ACTIVO = UUID.fromString("bc353a3e-f975-45a7-91cd-ad6f74e80ac5");
    public static final UUID ESTADO_CLIENTE_INACTIVO = UUID.fromString("43da6e29-158b-4c12-aaee-bb49a63024d3");
    // Prefijo 5 (Estados de las ordenes)
    public static final UUID ESTADO_ORDEN_GENERADO = UUID.fromString("57eca91c-ba22-4e0c-864e-3bd2f476f8cc");
    public static final UUID ESTADO_ORDEN_PREPARANDO_ENVIO = UUID.fromString("91b0c3af-e133-4ea4-9128-771ee336345f");
    public static final UUID ESTADO_ORDEN_EN_TRANSITO = UUID.fromString("466c94c7-09d2-4a60-9d54-f6151a2f586a");
    public static final UUID ESTADO_ORDEN_ENTREGADO= UUID.fromString("ab2e325c-0fd1-4279-a17f-d7eb66ca9cf0");

    public static final UUID ESTADO_ORDEN_CANCELADO = UUID.fromString("294bda4b-d042-4714-a244-33869c44173c");
    public static final UUID ESTADO_ORDEN_DEVUELTO = UUID.fromString("90689b03-6f47-4acb-8e79-5146d931b646");


    //Prefijo 6(Estados del producto)
    public static final UUID ESTADO_PRODUCTO_BORRADOR = UUID.fromString("21ef9af9-1ea0-4f9a-a181-1422f4680ec1");
    public static final UUID ESTADO_PRODUCTO_PUBLICADO = UUID.fromString("be987653-344a-4c01-a37a-d7f6956f5bb2");

    //Prefijo 7(Estados de las ofertas)
    public static final UUID ESTADO_OFERTA_ACTIVO = UUID.fromString("95530eed-f5e0-4fb2-9c9b-22fdd0a8720b");
    public static final UUID ESTADO_OFERTA_INACTIVO = UUID.fromString("417d7663-9298-4e12-be2f-cc2b18b13116");


    //Prefijo 8(Estados de pagos)
    public static final UUID ESTADO_POR_PAGAR = UUID.fromString("71b16791-a4f5-4a6d-a4e0-12f0b66961c9");
    public static final UUID ESTADO_PAGADO = UUID.fromString("319bb655-9580-41fe-aaa1-960fe2db32c1");
    public static final UUID ESTADO_REEMBOLSADO = UUID.fromString("794a5607-5f71-48ef-a4d9-2f2a2a48656e");

    //Prefijo 10(Estados de reembolso)
    public static final UUID ESTADO_REEMBOLSO_ENVIADO = UUID.fromString("6337563e-37fb-48c9-9af6-c2acb8520a11");
    public static final UUID ESTADO_REEMBOLSO_EVALUADO = UUID.fromString("2b2f8494-53d0-4fad-850f-347e2d11ee6f");
    //Prefijo 11(Resultado de reembolso)
    public static final UUID RESULTADO_REEMBOLSO_APROBADO = UUID.fromString("4086b7e9-657c-4652-83e4-93e62af6171d");
    public static final UUID RESULTADO_REEMBOLSO_DESAPROBADO = UUID.fromString("a6038acf-4178-45de-bfe0-3137b0306d28");


    //Respuesta para peticiones
    public static final Integer HTTP_STATUS_CORRECT=1;
    public static final Integer HTTP_STATUS_VALIDATE=0;


    /*************** Variables para facturacion */

    private Constantes() {
    }

    public static final String EXTENSION_ZIP = ".zip";


    /**
     * The constant DELIMITER.
     */
    public static final String DELIMITER = "/";


    /**
     * The constant VOUCHER_TYPE_CODE_FACT.
     */
//----------------  CODIGO TIPO DE COMPROBANTE SUNAT ------------//
    public static final String VOUCHER_TYPE_CODE_FACT = "01";
    /**
     * The constant VOUCHER_TYPE_CODE_BOLETA.
     */
    public static final String VOUCHER_TYPE_CODE_BOLETA = "03";
    /**
     * The constant VOUCHER_TYPE_CODE_CREDIT_NOTE.
     */
    public static final String VOUCHER_TYPE_CODE_CREDIT_NOTE = "07";



    /**
     * The constant VARIABLE_USER_NAME.
     */
//VARIABLES DE CLIENT SUNAT
    public static final String VARIABLE_USER_NAME = "Username";
    /**
     * The constant VARIABLE_USER_PASS.
     */
    public static final String VARIABLE_USER_PASS = "Password";
    /**
     * The constant VARIABLE_SECURITY.
     */
    public static final String VARIABLE_SECURITY = "Security";
    /**
     * The constant VARIABLE_USER_NAME_TOKEN.
     */
    public static final String VARIABLE_USER_NAME_TOKEN = "UsernameToken";
    /**
     * The constant WSSE.
     */
    public static final String WSSE = "wsse";
    /**
     * The constant VARIABLE_SOAP_ENV.
     */
    public static final String VARIABLE_SOAP_ENV = "soapenv";


    // TIPO DE DUCOMENTO DE INDENTIDAD - CÓDIGOS DE SUNAT - FATURACIÓN ELECTRONICA
    public static final String DOCUMENT_TYPE_SUNAT_DNI = "1";
    public static final String DOCUMENT_TYPE_SUNAT_RUC = "6";
    public static final String DOCUMENT_TYPE_SUNAT_PASAPORTE = "7";

    public static final String SERIE_BOLETA = "B001";
    public static final String NAME_TRAMITE = "COMPRA DE PRODUCTOS";


    //********** Si se estan utilizando
    public static final String pass_certificate_billing="1ngyTAL2023";



}
