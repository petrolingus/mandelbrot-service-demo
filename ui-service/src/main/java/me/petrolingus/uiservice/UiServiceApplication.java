package me.petrolingus.uiservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@SpringBootApplication
@RestController
public class UiServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UiServiceApplication.class, args);
    }

	@GetMapping(value = "/api/v1/mandelbrot", produces = MediaType.IMAGE_PNG_VALUE)
	public void generateMandelbrotTile(
			@RequestParam(defaultValue = "128") int size,
			@RequestParam(defaultValue = "-1") double xc,
			@RequestParam(defaultValue = "0") double yc,
			@RequestParam(defaultValue = "2") double scale,
			@RequestParam(defaultValue = "128") int iterations,
			@RequestParam(defaultValue = "1") int subdivision
	) throws IOException {



	}

}
