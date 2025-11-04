package cc.blynk.integration.tools;

import cc.blynk.server.core.BlockingIOProcessor;
import cc.blynk.server.db.DBManager;
import cc.blynk.server.db.model.Redeem;
import cc.blynk.utils.TokenGeneratorUtil;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Used for Redeem QRs generation.
 *
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 22.03.16.
 */
public class QRGenerator {

    public static void main(String[] args) throws Exception {
        DBManager dbManager = new DBManager("db.properties", new BlockingIOProcessor(4, 100), true);
        List<Redeem> redeems = generateQR(10, "/home/doom369/QR/test", "test", 50000);
        dbManager.insertRedeems(redeems);
    }

    private static List<Redeem> generateQR(int count, String outputFolder, String campaign, int reward) throws Exception {
        var redeems = new ArrayList<Redeem>(count);
        for (int i = 0; i < count; i++) {
            String token = TokenGeneratorUtil.generateNewToken();

            var redeem = new Redeem(token, campaign, reward);
            redeems.add(redeem);

            Path path = Paths.get(outputFolder, String.format("%d.jpg", i));
            generateQR(redeem.formatToken(), path);
        }
        return redeems;
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
