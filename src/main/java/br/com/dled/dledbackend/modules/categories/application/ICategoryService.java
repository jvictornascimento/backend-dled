package br.com.dled.dledbackend.modules.categories.application;

import br.com.dled.dledbackend.modules.categories.application.dto.CategoryDTO;
import br.com.dled.dledbackend.modules.categories.application.dto.CategorySimpleDTO;
import br.com.dled.dledbackend.modules.categories.application.dto.CategoryTreeDTO;

import java.util.List;

public interface ICategoryService {
    List<CategoryTreeDTO> getAllCategory();
    List<CategorySimpleDTO> getListRootCategory();
    CategoryDTO getCategoryById(Long categoryId);
}
