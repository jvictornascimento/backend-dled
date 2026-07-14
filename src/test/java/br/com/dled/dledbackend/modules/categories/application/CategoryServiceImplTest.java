package br.com.dled.dledbackend.modules.categories.application;

import br.com.dled.dledbackend.modules.categories.application.dto.CategoryDTO;
import br.com.dled.dledbackend.modules.categories.application.dto.CategoryForFilterDTO;
import br.com.dled.dledbackend.modules.categories.application.dto.CategoryTreeDTO;
import br.com.dled.dledbackend.modules.categories.application.exception.CategoryNotFoundException;
import br.com.dled.dledbackend.modules.categories.application.mapper.ICategoryMapper;
import br.com.dled.dledbackend.modules.categories.domain.Category;
import br.com.dled.dledbackend.modules.categories.infrastructure.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository repository;

    @Mock
    private ICategoryMapper mapper;

    @InjectMocks
    private CategoryServiceImpl service;

    @Test
    void shouldReturnOnlyActiveCategoriesInTreeList() {
        Category active = createCategory(1L, "Ativa", true);
        Category inactive = createCategory(2L, "Inativa", false);
        CategoryTreeDTO mapped = new CategoryTreeDTO(1L, "Ativa", null, true, Collections.emptyList());

        when(repository.findAll()).thenReturn(Arrays.asList(active, inactive));
        when(mapper.fromOutList(active)).thenReturn(mapped);

        List<CategoryTreeDTO> result = service.getAllCategory();

        assertEquals(1, result.size());
        assertSame(mapped, result.get(0));
        verify(mapper).fromOutList(active);
        verify(mapper, never()).fromOutList(inactive);
    }

    @Test
    void shouldReturnCategoryByIdWhenActive() {
        Category category = createCategory(1L, "Categoria", true);
        CategoryDTO mapped = new CategoryDTO(1L, "Categoria", null, true, null);

        when(repository.findById(1L)).thenReturn(Optional.of(category));
        when(mapper.fromOut(category)).thenReturn(mapped);

        CategoryDTO result = service.getCategoryById(1L);

        assertSame(mapped, result);
        verify(mapper).fromOut(category);
    }

    @Test
    void shouldThrowWhenCategoryIsInactive() {
        Category category = createCategory(1L, "Categoria", false);

        when(repository.findById(1L)).thenReturn(Optional.of(category));

        assertThrows(CategoryNotFoundException.class, () -> service.getCategoryById(1L));
        verify(mapper, never()).fromOut(category);
    }

    @Test
    void shouldReturnActiveRootCategoriesSortedByName() {
        Category zeb = createCategory(3L, "Zebra", true);
        Category alpha = createCategory(1L, "Alpha", true);
        Category inactive = createCategory(2L, "Beta", false);

        CategoryForFilterDTO alphaDto = new CategoryForFilterDTO(1L, "Alpha", Collections.emptyList());
        CategoryForFilterDTO zebDto = new CategoryForFilterDTO(3L, "Zebra", Collections.emptyList());

        when(repository.findRootCategories()).thenReturn(Arrays.asList(zeb, inactive, alpha));
        when(mapper.fromOutSimpleList(alpha)).thenReturn(alphaDto);
        when(mapper.fromOutSimpleList(zeb)).thenReturn(zebDto);

        List<CategoryForFilterDTO> result = service.getListRootCategory();

        assertEquals(List.of(alphaDto, zebDto), result);
        verify(mapper, never()).fromOutSimpleList(inactive);
    }

    private Category createCategory(Long id, String name, boolean active) {
        Category category = new Category();
        category.setId(id);
        category.setName(name);
        category.setActive(active);
        return category;
    }
}
