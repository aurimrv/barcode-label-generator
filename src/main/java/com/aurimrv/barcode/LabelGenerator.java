package com.aurimrv.barcode;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import uk.org.okapibarcode.backend.Code93;
import uk.org.okapibarcode.output.Java2DRenderer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.*;

public class LabelGenerator {

    public static void barcodeGenerator(String productName, String productColor, String barcodeValue) throws Exception {
        // Create output directory if it doesn't exist
        File outputDir = new File("barcodes");
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        // Label size: 33mm x 22mm in points
        float widthPt = 33f * 2.8346f;
        float heightPt = 22f * 2.8346f;

        // Create 1-page PDF label with exact size
        Document document = new Document(new Rectangle(widthPt, heightPt), 0, 0, 0, 0);
        PdfWriter.getInstance(document, new FileOutputStream("barcodes/" + barcodeValue + ".pdf"));
        document.open();

        Font nameFont = new Font(Font.HELVETICA, 6, Font.BOLD); // Bold font for product name
        Font colorFont = new Font(Font.HELVETICA, 5);            // Slightly smaller for product color
        Font codeFont = new Font(Font.HELVETICA, 6);             // Same as before for barcode text

        // Add product name in bold
        Paragraph name = new Paragraph(productName, nameFont);
        name.setAlignment(Element.ALIGN_CENTER);
        document.add(name);

        // Add product color in smaller font
        Paragraph color = new Paragraph(productColor, colorFont);
        color.setAlignment(Element.ALIGN_CENTER);
        document.add(color);


        // Create Code93 barcode image
        Code93 code93 = new Code93();
        code93.setContent(barcodeValue);

        int marginLeft = 30; // in pixels — adjust as needed
        int imageWidth = 300 + marginLeft;
        int imageHeight = 80;

        BufferedImage barcodeImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = barcodeImage.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, imageWidth, imageHeight);
        g2d.translate(marginLeft, 0);

        Java2DRenderer renderer = new Java2DRenderer(g2d, 2.0, Color.WHITE, Color.BLACK);
        renderer.render(code93);

        // Convert to iText image
        Image barcode = Image.getInstance(barcodeImage, null);
        barcode.scaleToFit(widthPt - 5, heightPt / 2);
        barcode.setAlignment(Image.ALIGN_CENTER);
        document.add(barcode);

        // Add human-readable code
        Paragraph code = new Paragraph(barcodeValue, colorFont);
        code.setAlignment(Element.ALIGN_CENTER);
        document.add(code);

        document.close();
        System.out.println("✅ barcodes/" + barcodeValue + ".pdf generated successfully.");
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Usage: java LabelGenerator <input_csv_file>");
            System.exit(1);
        }

        String csvFile = args[0];
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split(",", 3); // Expecting 3 parts
                if (parts.length != 3) {
                    System.err.println("⚠️  Invalid line format: " + line);
                    continue;
                }

                String productName = parts[0].trim();
                String productColor = parts[1].trim();
                String barcodeValue = parts[2].trim();

                try {
                    barcodeGenerator(productName, productColor, barcodeValue);
                } catch (Exception e) {
                    System.err.println("❌ Failed to generate label for: " + barcodeValue);
                    e.printStackTrace();
                }
            }
        }
    }
}
