package br.com.dled.dledbackend.modules.categories.application;

import br.com.dled.dledbackend.modules.categories.application.dto.CategoryDTO;
import br.com.dled.dledbackend.modules.categories.application.dto.CategorySimpleDTO;
import br.com.dled.dledbackend.modules.categories.application.dto.CategoryTreeDTO;
import br.com.dled.dledbackend.modules.categories.application.exception.CategoryNotFoundException;
import br.com.dled.dledbackend.modules.categories.application.mapper.ICategoryMapper;
import br.com.dled.dledbackend.modules.categories.domain.Category;
import br.com.dled.dledbackend.modules.categories.infrastructure.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

import static br.com.dled.dledbackend.core.exceptions.BaseMessageError.CATEGORY_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements ICategoryService{
    private final CategoryRepository repository;
    private final ICategoryMapper mapper;

    @Override
    public List<CategoryTreeDTO> getAllCategory() {
        List<Category> categories = repository.findAll();

        return categories.stream()
                .filter(Category::isActive)
                .map(mapper::fromOutList)
                .toList();
    }
    @Override
    public CategoryDTO getCategoryById(Long categoryId) {
        var category = repository.findById(categoryId)
                .filter(Category::isActive)
                .orElseThrow(()-> new CategoryNotFoundException(CATEGORY_NOT_FOUND.getMassage()));
        return mapper.fromOut(category);
    }

    @Override
    public List<CategorySimpleDTO> getListRootCategory() {
        return repository.findRootCategories().stream()
                .filter(Category::isActive)
                .sorted(Comparator.comparing(Category::getName))
                .map(mapper::fromOutSimpleList)
                .toList();
    }
}
