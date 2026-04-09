package br.com.dled.dledbackend.modules.categories.application;

import br.com.dled.dledbackend.modules.categories.application.dto.CategoryDTO;
import br.com.dled.dledbackend.modules.categories.application.dto.CategoryForFilterDTO;
import br.com.dled.dledbackend.modules.categories.application.dto.CategoryTreeDTO;

import java.util.List;

public interface ICategoryService {
    List<CategoryTreeDTO> getAllCategory();
    List<CategoryForFilterDTO> getListRootCategory();
    CategoryDTO getCategoryById(Long categoryId);
}
