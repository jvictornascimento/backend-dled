package br.com.dled.dledbackend.modules.orders.application;

import br.com.dled.dledbackend.modules.orders.application.dto.OrderDto;
import br.com.dled.dledbackend.modules.orders.application.dto.OrderUpsertDto;

import java.util.List;

public interface IOrderService {
    List<OrderDto> getAll();
    OrderDto getById(Long orderId);
    OrderDto create(OrderUpsertDto input);
    OrderDto update(Long orderId, OrderUpsertDto input);
    void delete(Long orderId);
}
