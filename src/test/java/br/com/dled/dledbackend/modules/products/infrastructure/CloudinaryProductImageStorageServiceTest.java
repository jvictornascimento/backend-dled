package br.com.dled.dledbackend.modules.products.infrastructure;

import br.com.dled.dledbackend.infrastructure.config.CloudinaryProperties;
import br.com.dled.dledbackend.modules.products.application.exception.ProductImageBadRequestException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

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
}
