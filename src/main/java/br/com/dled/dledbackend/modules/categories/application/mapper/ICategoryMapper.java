package br.com.dled.dledbackend.modules.categories.application.mapper;

import br.com.dled.dledbackend.modules.categories.application.dto.CategoryDTO;
import br.com.dled.dledbackend.modules.categories.application.dto.CategoryForFilterDTO;
import br.com.dled.dledbackend.modules.categories.application.dto.CategoryTreeDTO;
import br.com.dled.dledbackend.modules.categories.domain.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;


@Mapper(componentModel = "spring")
public interface ICategoryMapper {
    @Mapping(target = "children", expression = "java(mapChildren(category.getChildren()))")
    CategoryForFilterDTO fromOutSimpleList(Category category);
    @Mapping(target = "parentId", expression = "java(category.getParent() != null ? category.getParent().getId() : null)")
    CategoryDTO fromOut(Category category);
    @Mapping(target = "children", expression = "java(mapChildren(category.getChildren()))")
    CategoryTreeDTO  fromOutList(Category category);
    default List<CategoryTreeDTO> mapChildren(Set<Category> children) {
        if (children == null) return List.of();
        return children.stream()
                .map(this::fromOutList)
                .toList();
    }
}
