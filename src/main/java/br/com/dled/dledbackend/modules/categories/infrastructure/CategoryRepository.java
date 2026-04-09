package br.com.dled.dledbackend.modules.categories.infrastructure;

import br.com.dled.dledbackend.modules.categories.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("SELECT c FROM Category c WHERE c.parent IS NULL")
    List<Category> findRootCategories();

    Set<Category> findByActiveTrueAndProductsId(Long productID);
}
