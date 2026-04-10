package br.com.dled.dledbackend.modules.orders.application;

import br.com.dled.dledbackend.modules.companies.application.exception.CompanyNotFoundException;
import br.com.dled.dledbackend.modules.companies.domain.Company;
import br.com.dled.dledbackend.modules.companies.infrastructure.CompanyRepository;
import br.com.dled.dledbackend.modules.orders.application.dto.OrderDto;
import br.com.dled.dledbackend.modules.orders.application.dto.OrderUpsertDto;
import br.com.dled.dledbackend.modules.orders.application.exception.OrderNotFoundException;
import br.com.dled.dledbackend.modules.orders.application.mapper.IOrderMapper;
import br.com.dled.dledbackend.modules.orders.domain.Order;
import br.com.dled.dledbackend.modules.orders.infrastructure.OrderRepository;
import br.com.dled.dledbackend.modules.products.application.exception.ProductNotFoundException;
import br.com.dled.dledbackend.modules.products.domain.Product;
import br.com.dled.dledbackend.modules.products.infrastructure.ProductRespository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static br.com.dled.dledbackend.core.exceptions.BaseMessageError.COMPANY_NOT_FOUND;
import static br.com.dled.dledbackend.core.exceptions.BaseMessageError.ORDER_NOT_FOUND;
import static br.com.dled.dledbackend.core.exceptions.BaseMessageError.PRODUCT_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements IOrderService {
    private final OrderRepository repository;
    private final CompanyRepository companyRepository;
    private final ProductRespository productRepository;
    private final IOrderMapper mapper;

    @Override
    public List<OrderDto> getAll() {
        return repository.findAllWithCompanyAndProducts().stream()
                .map(mapper::fromOut)
                .toList();
    }

    @Override
    public OrderDto getById(Long orderId) {
        return mapper.fromOut(findById(orderId));
    }

    @Override
    public OrderDto create(OrderUpsertDto input) {
        Order order = new Order();
        applyInput(order, input);
        return mapper.fromOut(repository.save(order));
    }

    @Override
    public OrderDto update(Long orderId, OrderUpsertDto input) {
        Order order = findById(orderId);
        applyInput(order, input);
        return mapper.fromOut(repository.save(order));
    }

    @Override
    public void delete(Long orderId) {
        repository.delete(findById(orderId));
    }

    private Order findById(Long orderId) {
        return repository.findByIdWithCompanyAndProducts(orderId)
                .orElseThrow(() -> new OrderNotFoundException(ORDER_NOT_FOUND.getMassage()));
    }

    private void applyInput(Order order, OrderUpsertDto input) {
        order.setPurchaseDate(input.purchaseDate());
        order.setLot(input.lot());
        order.setCompany(resolveCompany(input.companyId()));
        order.setProducts(resolveProducts(input.productIds()));
    }

    private Company resolveCompany(Long companyId) {
        return companyRepository.findById(companyId)
                .orElseThrow(() -> new CompanyNotFoundException(COMPANY_NOT_FOUND.getMassage()));
    }

    private Set<Product> resolveProducts(Set<Long> productIds) {
        List<Product> products = productRepository.findAllById(productIds);
        if (products.size() != productIds.size()) {
            throw new ProductNotFoundException(PRODUCT_NOT_FOUND.getMassage());
        }
        return new LinkedHashSet<>(products);
    }
}
