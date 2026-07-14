package br.com.dled.dledbackend.modules.products.application.storage;

import org.springframework.web.multipart.MultipartFile;

public interface ProductImageStorageService {
    StoredImage upload(Long productId, String imageType, MultipartFile file);
    void delete(String publicId);

    record StoredImage(String url, String publicId) {
    }
}
