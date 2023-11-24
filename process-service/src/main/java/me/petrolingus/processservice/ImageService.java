package me.petrolingus.processservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.concurrent.ThreadLocalRandom;

@RestController
public class ImageService {

    @Value("#{environment['BREAKDOWN_PROBABILITY']?:0}")
    private double breakdownProbability;

    private final Mandelbrot mandelbrot;

    private float hue;

    private float saturation;

    public ImageService(Mandelbrot mandelbrot) {
        this.mandelbrot = mandelbrot;
        this.hue = ThreadLocalRandom.current().nextFloat();
        this.saturation = 0.5f * ThreadLocalRandom.current().nextFloat() + 0.2f;
    }

    // MediaType.IMAGE_JPEG_VALUE | MediaType.APPLICATION_OCTET_STREAM_VALUE
    @PostMapping(value = "/api/v1/generate-mandelbrot-tile", produces = MediaType.IMAGE_PNG_VALUE)
    public @ResponseBody byte[] generateMandelbrotTile(@RequestBody Params params) throws IOException {

        if (ThreadLocalRandom.current().nextDouble() < breakdownProbability) {
            System.exit(-1);
        }

        // Generate image
        int[] data = mandelbrot.getMandelbrotImage(params.size, params.xc, params.yc, params.scale, params.iterations, hue, saturation);

        // Convert int array to byte array
        ByteBuffer byteBuffer = ByteBuffer.allocate(data.length * 4);
        IntBuffer intBuffer = byteBuffer.asIntBuffer();
        intBuffer.put(data);
        byte[] array = byteBuffer.array();

        // Return result
        BufferedImage image = new BufferedImage(params.size, params.size, BufferedImage.TYPE_INT_RGB);
        image.setRGB(0, 0, params.size, params.size, data, 0, params.size);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, "png", os);
        InputStream in = new ByteArrayInputStream(os.toByteArray());
        return in.readAllBytes();
    }
}
