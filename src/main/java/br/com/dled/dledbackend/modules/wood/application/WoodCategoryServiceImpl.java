package br.com.dled.dledbackend.modules.wood.application;

import br.com.dled.dledbackend.modules.wood.application.dto.WoodCategoryDto;
import br.com.dled.dledbackend.modules.wood.application.dto.WoodCategoryUpsertDto;
import br.com.dled.dledbackend.modules.wood.application.exception.WoodCategoryNotFoundException;
import br.com.dled.dledbackend.modules.wood.domain.WoodCategory;
import br.com.dled.dledbackend.modules.wood.infrastructure.WoodCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class WoodCategoryServiceImpl implements IWoodCategoryService {
    private final WoodCategoryRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<WoodCategoryDto> getAll() {
        return repository.findAll().stream()
                .filter(WoodCategory::isActive)
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<WoodCategoryDto> getRootCategories() {
        return repository.findRootCategories().stream()
                .filter(WoodCategory::isActive)
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public WoodCategoryDto getById(Long categoryId) {
        return toDto(findActiveCategory(categoryId));
    }

    @Override
    public WoodCategoryDto create(WoodCategoryUpsertDto input) {
        WoodCategory category = new WoodCategory();
        applyInput(category, input);
        return toDto(repository.save(category));
    }

    @Override
    public WoodCategoryDto update(Long categoryId, WoodCategoryUpsertDto input) {
        WoodCategory category = repository.findById(categoryId)
                .orElseThrow(() -> new WoodCategoryNotFoundException("Wood category not found."));
        applyInput(category, input);
        return toDto(repository.save(category));
    }

    @Override
    public void delete(Long categoryId) {
        WoodCategory category = repository.findById(categoryId)
                .orElseThrow(() -> new WoodCategoryNotFoundException("Wood category not found."));
        repository.delete(category);
    }

    private WoodCategory findActiveCategory(Long categoryId) {
        return repository.findById(categoryId)
                .filter(WoodCategory::isActive)
                .orElseThrow(() -> new WoodCategoryNotFoundException("Wood category not found."));
    }

    private void applyInput(WoodCategory category, WoodCategoryUpsertDto input) {
        category.setName(input.name());
        category.setImgCategoryUrl(input.imgCategoryUrl());
        category.setParent(resolveParent(input.parentId(), category.getId()));
        category.setActive(input.active());
    }

    private WoodCategory resolveParent(Long parentId, Long currentCategoryId) {
        if (parentId == null || parentId.equals(currentCategoryId)) {
            return null;
        }

        return repository.findById(parentId)
                .orElseThrow(() -> new WoodCategoryNotFoundException("Parent wood category not found."));
    }

    private WoodCategoryDto toDto(WoodCategory category) {
        return new WoodCategoryDto(
                category.getId(),
                category.getName(),
                category.getImgCategoryUrl(),
                category.getParent() == null ? null : category.getParent().getId(),
                category.isActive()
        );
    }
}
