package br.com.dled.dledbackend.modules.products.application;

import br.com.dled.dledbackend.modules.products.application.dto.ProductCardDto;

import java.util.List;

public interface IProductService {
    public List<ProductCardDto> getAll() ;
}
