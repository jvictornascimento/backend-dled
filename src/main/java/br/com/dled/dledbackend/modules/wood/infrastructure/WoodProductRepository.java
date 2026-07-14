package br.com.dled.dledbackend.modules.wood.infrastructure;

import br.com.dled.dledbackend.modules.wood.domain.WoodProduct;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface WoodProductRepository extends JpaRepository<WoodProduct, Long> {
    @EntityGraph(attributePaths = {"categories"})
    @Query("select distinct product from WoodProduct product left join product.categories where product.active = true order by product.name")
    List<WoodProduct> findAllActiveWithCategories();

    @EntityGraph(attributePaths = {"categories", "variations"})
    @Query("select product from WoodProduct product where product.id = :productId")
    Optional<WoodProduct> findByIdWithDetails(Long productId);
}
