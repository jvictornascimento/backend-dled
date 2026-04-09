package br.com.dled.dledbackend.modules.products.application.mapper;

import br.com.dled.dledbackend.modules.products.application.dto.ProductCardDto;
import br.com.dled.dledbackend.modules.products.domain.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IProductMapper {
    ProductCardDto fromOutList(Product product);
}
