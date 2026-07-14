package br.com.dled.dledbackend.modules.products.infrastructure;

import br.com.dled.dledbackend.infrastructure.config.CloudinaryProperties;
import br.com.dled.dledbackend.modules.products.application.exception.ProductImageBadRequestException;
import br.com.dled.dledbackend.modules.products.application.storage.ProductImageStorageService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;

@Service
public class CloudinaryProductImageStorageService implements ProductImageStorageService {
    private final CloudinaryProperties properties;
    private final RestClient restClient;

    public CloudinaryProductImageStorageService(CloudinaryProperties properties) {
        this.properties = properties;
        this.restClient = RestClient.builder().build();
    }

    @Override
    @SuppressWarnings("unchecked")
    public StoredImage upload(Long productId, String imageType, MultipartFile file) {
        validateConfiguration();
        validateFile(file);

        long timestamp = Instant.now().getEpochSecond();
        String folder = normalizeFolder(productId, imageType);
        String publicId = productId + "-" + imageType + "-" + timestamp;
        String signature = sha1("folder=" + folder + "&public_id=" + publicId + "&timestamp=" + timestamp + properties.apiSecret());

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", asResource(file));
        body.add("api_key", properties.apiKey());
        body.add("timestamp", String.valueOf(timestamp));
        body.add("folder", folder);
        body.add("public_id", publicId);
        body.add("signature", signature);

        Map<String, Object> response = restClient.post()
                .uri("https://api.cloudinary.com/v1_1/{cloudName}/image/upload", properties.cloudName())
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(body)
                .retrieve()
                .body(Map.class);

        if (response == null || response.get("secure_url") == null || response.get("public_id") == null) {
            throw new ProductImageBadRequestException("Cloudinary upload did not return a valid image URL.");
        }

        return new StoredImage(String.valueOf(response.get("secure_url")), String.valueOf(response.get("public_id")));
    }

    @Override
    public void delete(String publicId) {
        if (publicId == null || publicId.isBlank()) {
            return;
        }

        validateConfiguration();

        long timestamp = Instant.now().getEpochSecond();
        String signature = sha1("public_id=" + publicId + "&timestamp=" + timestamp + properties.apiSecret());

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("api_key", properties.apiKey());
        body.add("timestamp", String.valueOf(timestamp));
        body.add("public_id", publicId);
        body.add("signature", signature);

        restClient.post()
                .uri("https://api.cloudinary.com/v1_1/{cloudName}/image/destroy", properties.cloudName())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .toBodilessEntity();
    }

    private void validateConfiguration() {
        if (isBlank(properties.cloudName()) || isBlank(properties.apiKey()) || isBlank(properties.apiSecret())) {
            throw new ProductImageBadRequestException("Cloudinary environment variables are not configured.");
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ProductImageBadRequestException("Image file is required.");
        }
    }

    private String normalizeFolder(Long productId, String imageType) {
        String baseFolder = isBlank(properties.folder()) ? "dled/products" : properties.folder();
        return baseFolder + "/" + productId + "/" + imageType;
    }

    private ByteArrayResource asResource(MultipartFile file) {
        try {
            return new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return Objects.requireNonNullElse(file.getOriginalFilename(), "upload.bin");
                }
            };
        } catch (Exception e) {
            throw new ProductImageBadRequestException("Could not read uploaded image.");
        }
    }

    private String sha1(String value) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            byte[] hash = messageDigest.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            for (byte current : hash) {
                builder.append(String.format("%02x", current));
            }
            return builder.toString();
        } catch (Exception e) {
            throw new ProductImageBadRequestException("Could not sign Cloudinary request.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
