package me.petrolingus.uiservice;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Collections;

@RestController
public class UiService {

    private final RestTemplate restTemplate;

    public UiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping(value = "/api/v1/get-mandelbrot-image", produces = MediaType.IMAGE_PNG_VALUE)
    public @ResponseBody byte[] getMandelbrotImage(@RequestParam(defaultValue = "512") int size,
                                                   @RequestParam(defaultValue = "-1") double xc,
                                                   @RequestParam(defaultValue = "0") double yc,
                                                   @RequestParam(defaultValue = "2") double scale,
                                                   @RequestParam(defaultValue = "128") int iterations,
                                                   @RequestParam(defaultValue = "4") int subdivision) throws IOException {

        final int tilesInRow = (int) Math.pow(2, subdivision);
        final int tilesCount = tilesInRow * tilesInRow;
        final int tileSize = size / tilesInRow;
        final double tileScale = scale / tilesInRow;

        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);

        for (int i = 0; i < tilesInRow; i++) {
            int y = i * tileSize;
            for (int j = 0; j < tilesInRow; j++) {
                int x = j * tileSize;

                double xcTile = (xc + j * tileScale) - (tilesInRow / 2.0 - 0.5) * tileScale;
                double ycTile = (yc - i * tileScale) + (tilesInRow / 2.0 - 0.5) * tileScale;

                String url = "http://localhost:80/api/v1/generate-mandelbrot-tile";
                String uri = urlGenerator(url, tileSize, xcTile, ycTile, tileScale, iterations);

                byte[] pixels = restTemplate.getForObject(uri, byte[].class);

                if (pixels == null) {
                    continue;
                }

                InputStream in = new ByteArrayInputStream(pixels);
                BufferedImage read = ImageIO.read(in);

                int[] arr = read.getRGB(0, 0, tileSize, tileSize, null, 0, tileSize);

                image.setRGB(x, y, tileSize, tileSize, arr, 0, tileSize);

            }
        }

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, "png", os);
        InputStream in = new ByteArrayInputStream(os.toByteArray());
        return in.readAllBytes();

    }

    private String urlGenerator(String url, int size, double xc, double yc, double scale, int iterations) {
        return url +
                "?size=" + size +
                "&xc=" + xc +
                "&yc=" + yc +
                "&scale=" + scale +
                "&maxIterations=" + iterations;
    }
}
