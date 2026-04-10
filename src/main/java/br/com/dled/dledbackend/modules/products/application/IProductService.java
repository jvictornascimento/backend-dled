package br.com.dled.dledbackend.modules.products.application;

import br.com.dled.dledbackend.modules.products.application.dto.ProductCardDto;
import br.com.dled.dledbackend.modules.products.application.dto.ProductDetailDto;
import br.com.dled.dledbackend.modules.products.application.dto.ProductUpsertDTO;

import java.util.List;

public interface IProductService {
    List<ProductCardDto> getAll();
    ProductDetailDto getById(Long productId);
    ProductDetailDto create(ProductUpsertDTO input);
    ProductDetailDto update(Long productId, ProductUpsertDTO input);
    void delete(Long productId);
}
