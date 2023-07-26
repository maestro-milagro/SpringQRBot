package com.example.SpringQRBot.QRTools;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Hashtable;
import java.util.UUID;



public class ReadQRCode {


    public static String readQR(String path) throws IOException, NotFoundException {
        BufferedImage readerImage = ImageIO.read(new FileInputStream(path));
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(readerImage)));
        Result resultObj = new MultiFormatReader().decode(binaryBitmap);
        return resultObj.getText();
    }
    public static String encodeText(String text, int width, int height)
            throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Hashtable hashtable = new Hashtable();
        hashtable.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hashtable);
        Path path = FileSystems.getDefault().getPath(String.format("./image/%s.%s", UUID.randomUUID(), "png"));
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
        return path.toAbsolutePath().toString();
    }
}



