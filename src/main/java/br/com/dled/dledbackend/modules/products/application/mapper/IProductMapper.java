package br.com.dled.dledbackend.modules.products.application.mapper;

import br.com.dled.dledbackend.modules.products.application.dto.ProductCardDto;
import br.com.dled.dledbackend.modules.products.application.dto.ProductDetailDto;
import br.com.dled.dledbackend.modules.products.application.dto.ProductGalleryImageDto;
import br.com.dled.dledbackend.modules.products.domain.Product;
import br.com.dled.dledbackend.modules.products.domain.ProductGalleryImage;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IProductMapper {
    ProductCardDto fromOutList(Product product);
    ProductDetailDto fromOut(Product product);
    ProductGalleryImageDto fromGalleryImage(ProductGalleryImage image);
}
