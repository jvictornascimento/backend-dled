package br.com.dled.dledbackend.modules.orders.application.mapper;

import br.com.dled.dledbackend.modules.orders.application.dto.OrderDto;
import br.com.dled.dledbackend.modules.orders.application.dto.OrderProductDto;
import br.com.dled.dledbackend.modules.orders.domain.Order;
import br.com.dled.dledbackend.modules.products.domain.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IOrderMapper {
    OrderDto fromOut(Order order);

    @Mapping(target = "status", expression = "java(product.getStatus() != null ? product.getStatus().name() : null)")
    OrderProductDto fromProduct(Product product);
}
