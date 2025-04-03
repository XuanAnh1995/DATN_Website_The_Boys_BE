package backend.datn.controllers;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/barcode")
public class BarcodeController {


    @GetMapping("/{maSP}")
    public Resource generateBarcode(@PathVariable String maSP) {
        try {
            int width = 300, height = 100;
            BitMatrix matrix = new MultiFormatWriter().encode(maSP, BarcodeFormat.CODE_128, width, height);

            Path path = Paths.get("D:/barcode_" + maSP + ".png");
            MatrixToImageWriter.writeToPath(matrix, "PNG", path);

            return new UrlResource(path.toUri());
        } catch (Exception e) {
            throw new RuntimeException("Lỗi tạo mã vạch!", e);
        }
    }
}