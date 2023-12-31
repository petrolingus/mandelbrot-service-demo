package me.petrolingus.processservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ThreadLocalRandom;

@RestController
public class ImageService {

    @Value("#{environment['BREAKDOWN_PROBABILITY']?:0}")
    private double breakdownProbability;

    private final Mandelbrot mandelbrot;

    private final float hue;

    private final float saturation;

    public ImageService(Mandelbrot mandelbrot) {
        this.mandelbrot = mandelbrot;
        this.hue = ThreadLocalRandom.current().nextFloat();
        this.saturation = 0.5f * ThreadLocalRandom.current().nextFloat() + 0.2f;
    }

    // MediaType.IMAGE_JPEG_VALUE | MediaType.APPLICATION_OCTET_STREAM_VALUE
    @GetMapping(value = "/api/v1/generate-mandelbrot-tile", produces = MediaType.IMAGE_PNG_VALUE)
    public @ResponseBody byte[] generateMandelbrotTile(@RequestParam(defaultValue = "128") int size,
                                                       @RequestParam(defaultValue = "-1") double xc,
                                                       @RequestParam(defaultValue = "0") double yc,
                                                       @RequestParam(defaultValue = "2") double scale,
                                                       @RequestParam(defaultValue = "128") int iterations) throws IOException {

        if (ThreadLocalRandom.current().nextDouble() < breakdownProbability) {
            System.exit(-1);
        }

        // Generate image
        int[] data = mandelbrot.getMandelbrotImage(size, xc, yc, scale, iterations, hue, saturation);

        // Return result
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        image.setRGB(0, 0, size, size, data, 0, size);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, "png", os);
        InputStream in = new ByteArrayInputStream(os.toByteArray());
        return in.readAllBytes();
    }

    @GetMapping(value = "/")
    public String generateMandelbrotTile() {
        return "HELLO WORLD!";
    }
}
