package br.com.dled.dledbackend.modules.products.infrastructure;

import br.com.dled.dledbackend.modules.products.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductRespository extends JpaRepository<Product,Long> {
    @Query("SELECT DISTINCT p FROM Product p " +
            "LEFT JOIN FETCH p.categories c " +
            "WHERE p.active = true")
    List<Product> findAllActiveWithCategories();

    @Query("SELECT DISTINCT p FROM Product p " +
            "LEFT JOIN FETCH p.categories c " +
            "WHERE p.id = :productId")
    Optional<Product> findByIdWithCategories(@Param("productId") Long productId);
}
