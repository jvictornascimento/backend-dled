package br.com.dled.dledbackend.modules.products.infrastructure;

import br.com.dled.dledbackend.infrastructure.config.CloudinaryProperties;
import br.com.dled.dledbackend.modules.products.application.exception.ProductImageBadRequestException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CloudinaryProductImageStorageServiceTest {
    private final CloudinaryProductImageStorageService service = new CloudinaryProductImageStorageService(
            new CloudinaryProperties("cloud", "key", "secret", "folder")
    );

    @Test
    void shouldRejectEmptyImageFile() {
        MockMultipartFile file = new MockMultipartFile("file", "image.png", "image/png", new byte[0]);

        ProductImageBadRequestException exception = assertThrows(ProductImageBadRequestException.class,
                () -> service.upload(1L, "main", file));

        assertEquals("Image file is required.", exception.getMessage());
    }

    @Test
    void shouldRejectUnsupportedImageContentType() {
        MockMultipartFile file = new MockMultipartFile("file", "image.svg", "image/svg+xml", "<svg/>".getBytes());

        ProductImageBadRequestException exception = assertThrows(ProductImageBadRequestException.class,
                () -> service.upload(1L, "main", file));

        assertEquals("Image file must be JPEG, PNG or WebP.", exception.getMessage());
    }

    @Test
    void shouldRejectUnsupportedImageExtension() {
        MockMultipartFile file = new MockMultipartFile("file", "image.txt", "image/png", "png".getBytes());

        ProductImageBadRequestException exception = assertThrows(ProductImageBadRequestException.class,
                () -> service.upload(1L, "main", file));

        assertEquals("Image file extension must be .jpg, .jpeg, .png or .webp.", exception.getMessage());
    }

    @Test
    void shouldRejectOversizedImageFile() {
        MockMultipartFile file = new MockMultipartFile("file", "image.png", "image/png", new byte[5 * 1024 * 1024 + 1]);

        ProductImageBadRequestException exception = assertThrows(ProductImageBadRequestException.class,
                () -> service.upload(1L, "main", file));

        assertEquals("Image file must be 5MB or smaller.", exception.getMessage());
    }

    @Test
    void shouldRejectDisguisedSvgFile() {
        MockMultipartFile file = new MockMultipartFile("file", "image.png", "image/png", "<svg/>".getBytes(StandardCharsets.UTF_8));

        ProductImageBadRequestException exception = assertThrows(ProductImageBadRequestException.class,
                () -> service.upload(1L, "main", file));

        assertEquals("Image file content must be a valid JPEG, PNG or WebP.", exception.getMessage());
    }

    @Test
    void shouldRejectMismatchedContentType() {
        MockMultipartFile file = new MockMultipartFile("file", "image.png", "image/jpeg", validPng());

        ProductImageBadRequestException exception = assertThrows(ProductImageBadRequestException.class,
                () -> service.upload(1L, "main", file));

        assertEquals("Image file content does not match declared content type.", exception.getMessage());
    }

    @Test
    void shouldRejectMismatchedExtension() {
        MockMultipartFile file = new MockMultipartFile("file", "image.jpg", "image/png", validPng());

        ProductImageBadRequestException exception = assertThrows(ProductImageBadRequestException.class,
                () -> service.upload(1L, "main", file));

        assertEquals("Image file content does not match file extension.", exception.getMessage());
    }

    @Test
    void shouldAcceptValidJpegSignature() {
        MockMultipartFile file = new MockMultipartFile("file", "image.jpg", "image/jpeg", validJpeg());

        assertDoesNotThrow(() -> service.validateFile(file));
    }

    @Test
    void shouldAcceptValidPngSignature() {
        MockMultipartFile file = new MockMultipartFile("file", "image.png", "image/png", validPng());

        assertDoesNotThrow(() -> service.validateFile(file));
    }

    @Test
    void shouldAcceptValidWebpSignature() {
        MockMultipartFile file = new MockMultipartFile("file", "image.webp", "image/webp", validWebp());

        assertDoesNotThrow(() -> service.validateFile(file));
    }

    private byte[] validJpeg() {
        return new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, 0x00};
    }

    private byte[] validPng() {
        return new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A, 0x00};
    }

    private byte[] validWebp() {
        return new byte[]{0x52, 0x49, 0x46, 0x46, 0x24, 0x00, 0x00, 0x00, 0x57, 0x45, 0x42, 0x50};
    }
}
