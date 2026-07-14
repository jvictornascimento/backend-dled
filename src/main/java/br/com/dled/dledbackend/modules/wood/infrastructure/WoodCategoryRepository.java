package br.com.dled.dledbackend.modules.wood.infrastructure;

import br.com.dled.dledbackend.modules.wood.domain.WoodCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WoodCategoryRepository extends JpaRepository<WoodCategory, Long> {
    @Query("select category from WoodCategory category where category.parent is null order by category.name")
    List<WoodCategory> findRootCategories();
}
