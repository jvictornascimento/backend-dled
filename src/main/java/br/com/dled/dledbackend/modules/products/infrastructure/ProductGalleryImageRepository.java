package br.com.dled.dledbackend.modules.products.infrastructure;

import br.com.dled.dledbackend.modules.products.domain.ProductGalleryImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductGalleryImageRepository extends JpaRepository<ProductGalleryImage, Long> {
    Optional<ProductGalleryImage> findByIdAndProductId(Long id, Long productId);
}
