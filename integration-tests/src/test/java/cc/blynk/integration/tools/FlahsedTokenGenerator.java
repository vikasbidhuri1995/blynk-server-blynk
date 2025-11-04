package cc.blynk.integration.tools;

import cc.blynk.server.core.BlockingIOProcessor;
import cc.blynk.server.db.DBManager;
import cc.blynk.server.db.model.FlashedToken;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 01.03.17.
 */
public class FlahsedTokenGenerator {

    public static void main(String[] args) throws Exception{
        FlashedToken[] flashedTokens = generateTokens("test@blynk.cc", 100, "Grow", 2);
        DBManager dbManager = new DBManager("db-test.properties", new BlockingIOProcessor(4, 100), true);

        dbManager.insertFlashedTokens(flashedTokens);

        for (FlashedToken token : flashedTokens) {
            Path path = Paths.get("/home/doom369/Downloads/grow",  token.token + "_" + token.deviceId + ".jpg");
            generateQR(token.token, path);
        }
    }

    private static FlashedToken[] generateTokens(String email, int count, String appName, int deviceCount) {
        FlashedToken[] flashedTokens = new FlashedToken[count * deviceCount];

        int counter = 0;
        for (int deviceId = 0; deviceId < deviceCount; deviceId++) {
            for (int i = 0; i < count; i++) {
                String token = UUID.randomUUID().toString().replace("-", "");
                flashedTokens[counter++] = new FlashedToken(email, token, appName, 1, deviceId);
                System.out.println("Token : " + token + ", deviceId : " + deviceId + ", appName : " + appName);
            }
        }
        return flashedTokens;
    }

    private static void generateQR(String text, Path outputFile) throws Exception {
        try (OutputStream out = Files.newOutputStream(outputFile)) {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 250, 250, hints);
            MatrixToImageWriter.writeToStream(bitMatrix, "JPG", out);
        }
    }

}
