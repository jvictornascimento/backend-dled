package br.com.dled.dledbackend.modules.wood.infrastructure;

import br.com.dled.dledbackend.modules.wood.domain.WoodProductVariation;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WoodProductVariationRepository extends JpaRepository<WoodProductVariation, Long> {
    @EntityGraph(attributePaths = {"listImgs", "product"})
    Optional<WoodProductVariation> findByIdAndProductId(Long id, Long productId);
}
