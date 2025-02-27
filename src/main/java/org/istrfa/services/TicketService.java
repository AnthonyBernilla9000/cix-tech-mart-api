package org.istrfa.services;

import com.google.zxing.WriterException;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.istrfa.models.ClientEntity;
import org.istrfa.models.DetailOrderEntity;
import org.istrfa.models.OrderEntity;
import org.istrfa.models.ProductEntity;
import org.istrfa.utils.Util;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class TicketService {


    @Value("${imgs.image-logo}")
    private String imageLogoPath;


    public MultipartFile createTicketOrder(OrderEntity order, ClientEntity client, List<DetailOrderEntity> listDetails) throws DocumentException, IOException, WriterException {
        // Crear el documento PDF
        Document document = new Document();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        // Crear el escritor de PDF que escribirá en el ByteArrayOutputStream
        PdfWriter.getInstance(document, byteArrayOutputStream);

        // Abrir el documento y agregar contenido
        document.open();

        // Añadir el encabezado al documento
        document.add(creatHeader(order));  // Pasa el orden si necesitas usarlo en el encabezado

        // Añadir el cuerpo con las 3 columnas
        document.add(createClientInfoTable(order,client)); // Llamada al método que crea el cuerpo con los datos del cliente

        //Añadimos la tabla con el detalle de los productos
        document.add(createDetailsProducts(listDetails)); // Llamada al método que crea el cuerpo con los datos del cliente

        //Añadimos el texto del total de soles
        document.add(createTotalOrderText(order));

        //Añadimos el apartado final del pdf
        document.add(createTableFooter(order));


        // Cierra el documento
        document.close();

        // Devuelve el PDF como MultipartFile
        return Util.convertByteArrayOutputStreamToMultipart(byteArrayOutputStream, "BOLETA");
    }

    private PdfPTable creatHeader(OrderEntity order) throws BadElementException, IOException {
        // Crear una tabla de 3 columnas para el encabezado
        PdfPTable headerTable = new PdfPTable(3);
        headerTable.setWidthPercentage(100); // Establece el ancho de la tabla al 100%

        // Configurar las celdas para la tabla
        PdfPCell cell1 = new PdfPCell();
        cell1.setBorder(Rectangle.NO_BORDER); // Sin bordes
        // Añadir una imagen a la celda de la izquierda
        InputStream imageStream = getClass().getClassLoader().getResourceAsStream(imageLogoPath);
        if (imageStream == null) {
            throw new IllegalArgumentException("Imagen no encontrada: " + imageLogoPath);
        }

        Image image = Image.getInstance(IOUtils.toByteArray(imageStream)); // Usa IOUtils de Apache Commons

        image.scaleToFit(100, 100); // Ajusta el tamaño de la imagen
        cell1.addElement(image);
        cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);  // Alineación vertical centrada
        headerTable.addCell(cell1);

        // Crear una celda para el centro con varias líneas de texto
        PdfPCell cell2 = new PdfPCell();
        cell2.setBorder(Rectangle.NO_BORDER); // Sin bordes
        cell2.setVerticalAlignment(Element.ALIGN_MIDDLE); // Alineación vertical
        cell2.setHorizontalAlignment(Element.ALIGN_CENTER); // Alineación horizontal

        // Crear una fuente para negrita
        Font boldFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD);

        // Crear una fuente normal para el resto del texto
        Font normalFont = new Font(Font.FontFamily.HELVETICA, 11);

        // Crear los párrafos con la mezcla de negrita y texto normal
        Paragraph paragraphC21 = new Paragraph("CIX TECH MART", new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD));

        Paragraph paragraphC22 = new Paragraph();
        paragraphC22.add(new Chunk("Telf: ", boldFont)); // "Telf:" en negrita
        paragraphC22.add(new Chunk("977 514 860", normalFont)); // El número de teléfono en normal

        Paragraph paragraphC23 = new Paragraph();
        paragraphC23.add(new Chunk("Email: ", boldFont)); // "Email:" en negrita
        paragraphC23.add(new Chunk("cixtechmart@gmail.com", normalFont)); // El correo en normal

        Paragraph paragraphC24 = new Paragraph();
        paragraphC24.add(new Chunk("Web: ", boldFont)); // "Web:" en negrita
        paragraphC24.add(new Chunk("www.cixtechmart.com", normalFont)); // La web en normal

        // Centramos el texto
        paragraphC21.setAlignment(Element.ALIGN_CENTER);
        paragraphC22.setAlignment(Element.ALIGN_CENTER);
        paragraphC23.setAlignment(Element.ALIGN_CENTER);
        paragraphC24.setAlignment(Element.ALIGN_CENTER);

        // Añadir los párrafos a la celda
        cell2.addElement(paragraphC21);
        cell2.addElement(paragraphC22);
        cell2.addElement(paragraphC23);
        cell2.addElement(paragraphC24);

        headerTable.addCell(cell2);

        // Crear una celda para la derecha con un cuadro y varias líneas de texto
        PdfPCell cell3 = new PdfPCell();
        cell3.setBorder(Rectangle.BOX); // Borde alrededor del cuadro
        cell3.setPadding(10); // Añadir padding alrededor del texto
        cell3.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell3.setHorizontalAlignment(Element.ALIGN_CENTER);

        // Añadir texto dentro del cuadro
        Paragraph paragraph4 = new Paragraph("R.U.C. 20732521386", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD));
        Paragraph paragraph5 = new Paragraph("BOLETA DE VENTA ELECTRÓNICA", new Font(Font.FontFamily.HELVETICA, 15, Font.BOLD));
        Paragraph paragraph6 = new Paragraph("N° B001 - "+Util.completeWithZero(order.getNumcode(),8), new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD));

        // Centramos los textos
        paragraph4.setAlignment(Element.ALIGN_CENTER);
        paragraph5.setAlignment(Element.ALIGN_CENTER);
        paragraph6.setAlignment(Element.ALIGN_CENTER);

        cell3.addElement(paragraph4);
        cell3.addElement(paragraph5);
        cell3.addElement(paragraph6);

        headerTable.addCell(cell3);

        return headerTable;
    }

    private PdfPTable createClientInfoTable(OrderEntity order,ClientEntity client) throws DocumentException {
        // Crear una tabla con 3 columnas para contener la información del cliente
        PdfPTable clientInfoTable = new PdfPTable(3);
        clientInfoTable.setWidthPercentage(100); // Establecer el ancho de la tabla al 100%

        // Establecer los anchos de las columnas (20%, 5%, 75%)
        float[] columnWidths = {20f, 5f, 75f};
        clientInfoTable.setWidths(columnWidths);

        // Añadir un espacio antes de la tabla
        clientInfoTable.setSpacingBefore(10f);  // Espacio antes de la tabla
        clientInfoTable.setSpacingAfter(10f);   // Espacio después de la tabla

        // Crear la primera fila: CLIENTE, con los valores en la tercera columna
        addClientInfoRow(clientInfoTable, "CLIENTE", client.getFullname());
//        addClientInfoRow(clientInfoTable, "DNI", order.getClient().getPerson().getNumerodoc());
        addClientInfoRow(clientInfoTable, "DIRECCIÓN", order.getAddress());
        addClientInfoRow(clientInfoTable, "FECHA DE EMISIÓN", Util.formatLocalDateTime(order.getDatecreate(), "dd/MM/yyyy"));
//        addClientInfoRow(clientInfoTable, "FECHA DE VENCIMIENTO", "12/12/2024");

        return clientInfoTable;
    }

    //Metodo auxiliar para
    private void addClientInfoRow(PdfPTable table, String property, String value) {
        // Crear una fila para cada propiedad
        PdfPCell cell1 = new PdfPCell(new Paragraph(property, new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD)));
        cell1.setBorder(Rectangle.NO_BORDER); // Sin borde para la celda de la propiedad
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT); // Alineación izquierda
        table.addCell(cell1);

        // Segunda columna para los dos puntos ":"
        PdfPCell cell2 = new PdfPCell(new Paragraph(":"));
        cell2.setBorder(Rectangle.NO_BORDER); // Sin borde para la celda de los dos puntos
        cell2.setHorizontalAlignment(Element.ALIGN_CENTER); // Alineación centrada
        table.addCell(cell2);

        // Tercera columna con el valor de la propiedad
        PdfPCell cell3 = new PdfPCell(new Paragraph(value, new Font(Font.FontFamily.HELVETICA, 11)));
        cell3.setBorder(Rectangle.NO_BORDER); // Sin borde para la celda de la información
        cell3.setHorizontalAlignment(Element.ALIGN_LEFT); // Alineación izquierda
        table.addCell(cell3);
    }

    private PdfPTable createDetailsProducts(List<DetailOrderEntity> listDetails) throws DocumentException {

        // Crear la tabla con las columnas para cada detalle del producto
        PdfPTable detailsTable = new PdfPTable(5);
        detailsTable.setWidthPercentage(100); // Establecer el ancho de la tabla al 100%
        detailsTable.setSpacingBefore(10f);  // Espacio antes de la tabla
        detailsTable.setSpacingAfter(10f);   // Espacio después de la tabla

        // Establecer los anchos de las columnas
        float[] columnWidths = {10f, 50f, 10f, 20f, 10f}; // Anchos relativos para cada columna
        detailsTable.setWidths(columnWidths);

        // Crear el encabezado de la tabla
        PdfPCell headerCell1 = new PdfPCell(new Phrase("Código", new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD)));
        headerCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
        headerCell1.setBorder(Rectangle.BOX); // Bordes para toda la celda
        detailsTable.addCell(headerCell1);

        PdfPCell headerCell2 = new PdfPCell(new Phrase("Producto", new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD)));
        headerCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
        headerCell2.setBorder(Rectangle.BOX); // Bordes para toda la celda
        detailsTable.addCell(headerCell2);

        PdfPCell headerCell3 = new PdfPCell(new Phrase("Cantidad", new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD)));
        headerCell3.setHorizontalAlignment(Element.ALIGN_CENTER);
        headerCell3.setBorder(Rectangle.BOX); // Bordes para toda la celda
        detailsTable.addCell(headerCell3);

        PdfPCell headerCell4 = new PdfPCell(new Phrase("Precio unitario", new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD)));
        headerCell4.setHorizontalAlignment(Element.ALIGN_CENTER);
        headerCell4.setBorder(Rectangle.BOX); // Bordes para toda la celda
        detailsTable.addCell(headerCell4);

        PdfPCell headerCell5 = new PdfPCell(new Phrase("Total", new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD)));
        headerCell5.setHorizontalAlignment(Element.ALIGN_CENTER);
        headerCell5.setBorder(Rectangle.BOX); // Bordes para toda la celda
        detailsTable.addCell(headerCell5);

        // Recorrer la lista de detalles y agregar los valores a la tabla
        for (int i = 0; i < listDetails.size(); i++) {
            DetailOrderEntity detail = listDetails.get(i);

            // Formatear el índice con 3 dígitos (001, 002, ..., 010, 011, ...)
            String indexFormatted = String.format("%03d", i + 1);

            // Código
            PdfPCell cell1 = new PdfPCell(new Phrase(indexFormatted, new Font(Font.FontFamily.HELVETICA, 10)));
            cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell1.setBorder(Rectangle.LEFT | Rectangle.RIGHT); // Bordes de separación vertical
            detailsTable.addCell(cell1);

            // Producto
            String detalle=detail.getDescription();
            if(detail.getType()==1)detalle=detail.getProduct().getName();
            PdfPCell cell2 = new PdfPCell(new Phrase(detalle, new Font(Font.FontFamily.HELVETICA, 10)));
            cell2.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell2.setBorder(Rectangle.LEFT | Rectangle.RIGHT); // Bordes de separación vertical
            detailsTable.addCell(cell2);

            // Cantidad
            PdfPCell cell3 = new PdfPCell(new Phrase(String.valueOf(detail.getQuantity()), new Font(Font.FontFamily.HELVETICA, 10)));
            cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell3.setBorder(Rectangle.LEFT | Rectangle.RIGHT); // Bordes de separación vertical
            detailsTable.addCell(cell3);

            // Precio Unitario
            PdfPCell cell4 = new PdfPCell(new Phrase(String.valueOf(detail.getUnitprice()), new Font(Font.FontFamily.HELVETICA, 10)));
            cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell4.setBorder(Rectangle.LEFT | Rectangle.RIGHT); // Bordes de separación vertical
            detailsTable.addCell(cell4);

            // Total
            PdfPCell cell5 = new PdfPCell(new Phrase(String.valueOf(detail.getTotal()), new Font(Font.FontFamily.HELVETICA, 10)));
            cell5.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell5.setBorder(Rectangle.LEFT | Rectangle.RIGHT); // Bordes de separación vertical
            detailsTable.addCell(cell5);
        }

        // Si la tabla no ocupa toda el alto de la página, añadir una fila vacía para llenar el espacio restante
        if (listDetails.size() < 20) {  // Ajusta este número dependiendo de cuántas filas deseas por página
            for (int i = listDetails.size(); i < 20; i++) {  // Aquí llenas las filas vacías
                detailsTable.addCell(createCellEmptityBorderLeftRight());  // Añadir una celda vacía
                detailsTable.addCell(createCellEmptityBorderLeftRight());  // Añadir una celda vacía
                detailsTable.addCell(createCellEmptityBorderLeftRight());  // Añadir una celda vacía
                detailsTable.addCell(createCellEmptityBorderLeftRight());  // Añadir una celda vacía
                detailsTable.addCell(createCellEmptityBorderLeftRight());  // Añadir una celda vacía
            }
        }

        // Añadir la fila final con celdas vacías y bordes inferiores
        PdfPCell footer1 = new PdfPCell(new Phrase("")); // Vacía, pero con borde
        footer1.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.BOTTOM);
        detailsTable.addCell(footer1);

        PdfPCell footer2 = new PdfPCell(new Phrase("")); // Vacía, pero con borde
        footer2.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.BOTTOM);
        detailsTable.addCell(footer2);

        PdfPCell footer3 = new PdfPCell(new Phrase("")); // Vacía, pero con borde
        footer3.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.BOTTOM);
        detailsTable.addCell(footer3);

        PdfPCell footer4 = new PdfPCell(new Phrase("")); // Vacía, pero con borde
        footer4.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.BOTTOM);
        detailsTable.addCell(footer4);

        PdfPCell footer5 = new PdfPCell(new Phrase("")); // Vacía, pero con borde
        footer5.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.BOTTOM);
        detailsTable.addCell(footer5);

        // Añadir la tabla de detalles al documento
        return detailsTable;
    }


    private PdfPTable createTotalOrderText(OrderEntity order) {
        // Añadir la tabla de detalles al documento
        // Aquí agregamos una fila adicional para el total de soles.
        PdfPTable totalTable = new PdfPTable(1); // Crear una tabla de una sola columna
        totalTable.setWidthPercentage(100);
        totalTable.setSpacingAfter(10f);   // Espacio después de este apartado

        // Crear una celda con el texto "TOTAL DE SOLES"
        PdfPCell totalCell = new PdfPCell(new Phrase("SON: " + Util.convertDoubleToSoles(order.getTotal()) + " SOLES", new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD)));
        totalCell.setHorizontalAlignment(Element.ALIGN_LEFT); // Alineación a la izquierda
        totalCell.setBorder(Rectangle.NO_BORDER); // Sin borde para la celda

        // Añadir la celda con el total a la tabla
        totalTable.addCell(totalCell);
        return totalTable;
    }


    private PdfPTable createTableFooter(OrderEntity order) throws DocumentException, WriterException, IOException {
        // Crear la tabla principal con 3 columnas
        PdfPTable mainTable = new PdfPTable(3);
        mainTable.setWidthPercentage(100); // Establecer el ancho de la tabla al 100%

        // Establecer los anchos de las columnas (25%, 35%, 40%)
        float[] columnWidths = {25f, 35f, 40f}; // Primera y segunda columna vacías, la tercera ocupa el espacio completo
        mainTable.setWidths(columnWidths);


        // Configurar las celdas para la tabla
        PdfPCell cell1 = new PdfPCell();
        cell1.setBorder(Rectangle.NO_BORDER); // Sin bordes
        // Añadir una imagen a la celda de la izquierda
        Image image = Util.genereteCodeQR("HOLA MUNDO", 100, 100);
        image.scaleToFit(70, 70); // Ajusta el tamaño de la imagen
        cell1.addElement(image);
        cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);  // Alineación vertical centrada
        mainTable.addCell(cell1);

        // Añadir las columna central vacia
        PdfPCell secondCell = new PdfPCell(new Phrase(""));
        secondCell.setBorder(Rectangle.NO_BORDER); // Sin bordes
        mainTable.addCell(secondCell);

        // Crear la tercera celda, que contendrá la subtabla
        PdfPCell thirdCell = new PdfPCell();
        thirdCell.setBorder(Rectangle.NO_BORDER); // Sin bordes
        thirdCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        thirdCell.addElement(createSubTable(order)); // Añadir la subtabla
        mainTable.addCell(thirdCell);

        return mainTable;
    }

    private PdfPTable createSubTable(OrderEntity order) throws DocumentException {
        // Crear la subtabla con 2 columnas
        PdfPTable subTable = new PdfPTable(2);
        subTable.setWidthPercentage(100); // Establecer el ancho de la tabla al 100%

        // Establecer los anchos de las columnas
        float[] subColumnWidths = {60f, 40f};
        subTable.setWidths(subColumnWidths);

        // Añadir los textos en la primera columna (descripción)
        PdfPCell cell1 = new PdfPCell(new Phrase("SUBTOTAL", new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD)));
        cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);//Alinear a la derecha
        cell1.setBorder(Rectangle.NO_BORDER); // Sin borde en la celda
        subTable.addCell(cell1);

        PdfPCell cell2 = new PdfPCell(new Phrase(String.valueOf(Math.round(order.getSubtotal()*100.0)/100.0), new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL)));
        cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);//Alinear a la derecha
        cell2.setBorder(Rectangle.NO_BORDER); // Sin borde en la celda
        subTable.addCell(cell2);

        // Añadir los textos de IGV
        cell1 = new PdfPCell(new Phrase("IGV", new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD)));
        cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);//Alinear a la derecha
        cell1.setBorder(Rectangle.NO_BORDER); // Sin borde en la celda
        subTable.addCell(cell1);

        cell2 = new PdfPCell(new Phrase(String.valueOf(String.valueOf(Math.round(order.getIgv()*100.0)/100.0)), new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL)));
        cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);//Alinear a la derecha
        cell2.setBorder(Rectangle.NO_BORDER); // Sin borde en la celda
        subTable.addCell(cell2);

        // Añadir los textos del total
        cell1 = new PdfPCell(new Phrase("TOTAL", new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD)));
        cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);//Alinear a la derecha
        cell1.setBorder(Rectangle.NO_BORDER); // Sin borde en la celda
        subTable.addCell(cell1);

        cell2 = new PdfPCell(new Phrase(String.valueOf(order.getTotal()), new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL)));
        cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);//Alinear a la derecha
        cell2.setBorder(Rectangle.TOP); // Sin borde en la celda
        subTable.addCell(cell2);

        return subTable;
    }

    /********************************** METODOS DE AYUDA *************************************************/
    private List<DetailOrderEntity> dataList() {
        List<DetailOrderEntity> list = new ArrayList<>();
        DetailOrderEntity data1 = new DetailOrderEntity();
        ProductEntity product1 = new ProductEntity();
        product1.setName("MOUSE INALAMBRICO DE CELULAR");
        product1.setPrice(120.40);
        data1.setProduct(product1);
        data1.setQuantity(140);
        data1.setTotal(12330.345);
        DetailOrderEntity data2 = new DetailOrderEntity();
        ProductEntity product2 = new ProductEntity();
        product2.setName("TECLADO MECANICO FANTASTICO");
        product2.setPrice(420.80);
        data2.setProduct(product2);
        data2.setQuantity(340);
        data2.setTotal(3455.344);
        list.add(data1);
        list.add(data2);
        return list;
    }

    private PdfPCell createCellEmptityBorderLeftRight() {
        PdfPCell cell = new PdfPCell(new Phrase(" "));
        cell.setBorder(Rectangle.LEFT | Rectangle.RIGHT); // Bordes de separación vertical
        return cell;
    }


}
