package br.com.dled.dledbackend.modules.categories.application;

import br.com.dled.dledbackend.modules.categories.application.dto.CategoryDTO;
import br.com.dled.dledbackend.modules.categories.application.dto.CategoryForFilterDTO;
import br.com.dled.dledbackend.modules.categories.application.dto.CategoryTreeDTO;
import br.com.dled.dledbackend.modules.categories.application.dto.CategoryUpsertDTO;
import br.com.dled.dledbackend.modules.categories.application.exception.CategoryNotFoundException;
import br.com.dled.dledbackend.modules.categories.application.mapper.ICategoryMapper;
import br.com.dled.dledbackend.modules.categories.domain.Category;
import br.com.dled.dledbackend.modules.categories.infrastructure.CategoryRepository;
import lombok.RequiredArgsConstructor;
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
        return mapper.fromOut(findActiveCategory(categoryId));
    }

    @Override
    public List<CategoryForFilterDTO> getListRootCategory() {
        return repository.findRootCategories().stream()
                .filter(Category::isActive)
                .sorted(Comparator.comparing(Category::getName))
                .map(mapper::fromOutSimpleList)
                .toList();
    }

    @Override
    public CategoryDTO create(CategoryUpsertDTO input) {
        Category category = new Category();
        applyInput(category, input);
        return mapper.fromOut(repository.save(category));
    }

    @Override
    public CategoryDTO update(Long categoryId, CategoryUpsertDTO input) {
        Category category = repository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(CATEGORY_NOT_FOUND.getMassage()));
        applyInput(category, input);
        return mapper.fromOut(repository.save(category));
    }

    @Override
    public void delete(Long categoryId) {
        Category category = repository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(CATEGORY_NOT_FOUND.getMassage()));
        repository.delete(category);
    }

    private Category findActiveCategory(Long categoryId) {
        return repository.findById(categoryId)
                .filter(Category::isActive)
                .orElseThrow(() -> new CategoryNotFoundException(CATEGORY_NOT_FOUND.getMassage()));
    }

    private void applyInput(Category category, CategoryUpsertDTO input) {
        category.setName(input.name());
        category.setImgUrl(input.imgUrl());
        category.setActive(input.active());
        category.setParent(resolveParent(input.parentId(), category.getId()));
    }

    private Category resolveParent(Long parentId, Long currentCategoryId) {
        if (parentId == null) {
            return null;
        }

        if (currentCategoryId != null && currentCategoryId.equals(parentId)) {
            return null;
        }

        return repository.findById(parentId)
                .orElseThrow(() -> new CategoryNotFoundException(CATEGORY_NOT_FOUND.getMassage()));
    }
}
