package br.com.dled.dledbackend.modules.wood.application;

import br.com.dled.dledbackend.modules.wood.application.dto.WoodCategoryDto;
import br.com.dled.dledbackend.modules.wood.application.dto.WoodCategoryUpsertDto;

import java.util.List;

public interface IWoodCategoryService {
    List<WoodCategoryDto> getAll();
    List<WoodCategoryDto> getRootCategories();
    WoodCategoryDto getById(Long categoryId);
    WoodCategoryDto create(WoodCategoryUpsertDto input);
    WoodCategoryDto update(Long categoryId, WoodCategoryUpsertDto input);
    void delete(Long categoryId);
}
