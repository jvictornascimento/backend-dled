package br.com.dled.dledbackend.modules.products.application;

import br.com.dled.dledbackend.modules.products.application.dto.ProductCardDto;
import br.com.dled.dledbackend.modules.products.application.dto.ProductDetailDto;

import java.util.List;

public interface IProductService {
    List<ProductCardDto> getAll();
    ProductDetailDto getById(Long productId);
}
