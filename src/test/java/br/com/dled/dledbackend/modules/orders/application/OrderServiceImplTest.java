package br.com.dled.dledbackend.modules.orders.application;

import br.com.dled.dledbackend.modules.companies.application.exception.CompanyNotFoundException;
import br.com.dled.dledbackend.modules.companies.domain.Company;
import br.com.dled.dledbackend.modules.companies.domain.CompanyType;
import br.com.dled.dledbackend.modules.companies.infrastructure.CompanyRepository;
import br.com.dled.dledbackend.modules.orders.application.dto.OrderCompanyDto;
import br.com.dled.dledbackend.modules.orders.application.dto.OrderDto;
import br.com.dled.dledbackend.modules.orders.application.dto.OrderProductDto;
import br.com.dled.dledbackend.modules.orders.application.dto.OrderUpsertDto;
import br.com.dled.dledbackend.modules.orders.application.dto.PrintLabelProductDTO;
import br.com.dled.dledbackend.modules.orders.application.dto.PrintLabelProductRequestDto;
import br.com.dled.dledbackend.modules.orders.application.exception.OrderNotFoundException;
import br.com.dled.dledbackend.modules.orders.application.mapper.IOrderMapper;
import br.com.dled.dledbackend.modules.orders.domain.Order;
import br.com.dled.dledbackend.modules.orders.infrastructure.OrderRepository;
import br.com.dled.dledbackend.modules.products.application.exception.ProductNotFoundException;
import br.com.dled.dledbackend.modules.products.domain.Product;
import br.com.dled.dledbackend.modules.products.domain.ProductStatus;
import br.com.dled.dledbackend.modules.products.infrastructure.ProductRespository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository repository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private ProductRespository productRepository;

    @Mock
    private IOrderMapper mapper;

    @InjectMocks
    private OrderServiceImpl service;

    @Test
    void shouldReturnMappedOrders() {
        Order order = createOrder(1L);
        OrderDto dto = createDto();

        when(repository.findAllWithCompanyAndProducts()).thenReturn(List.of(order));
        when(mapper.fromOut(order)).thenReturn(dto);

        List<OrderDto> result = service.getAll();

        assertEquals(List.of(dto), result);
    }

    @Test
    void shouldReturnOrderById() {
        Order order = createOrder(1L);
        OrderDto dto = createDto();

        when(repository.findByIdWithCompanyAndProducts(1L)).thenReturn(Optional.of(order));
        when(mapper.fromOut(order)).thenReturn(dto);

        OrderDto result = service.getById(1L);

        assertSame(dto, result);
    }

    @Test
    void shouldCreateOrder() {
        Company company = createCompany(1L);
        Product product = createProduct(10L, "Driver 24W");
        Order saved = createOrder(1L);
        OrderUpsertDto input = new OrderUpsertDto(LocalDate.of(2026, 4, 10), "L-001", Set.of(10L), 1L);
        OrderDto dto = createDto();

        when(companyRepository.findById(1L)).thenReturn(Optional.of(company));
        when(productRepository.findAllById(Set.of(10L))).thenReturn(List.of(product));
        when(repository.save(org.mockito.ArgumentMatchers.any(Order.class))).thenReturn(saved);
        when(mapper.fromOut(saved)).thenReturn(dto);

        OrderDto result = service.create(input);

        assertSame(dto, result);
    }

    @Test
    void shouldUpdateOrder() {
        Order existing = createOrder(1L);
        Company company = createCompany(2L);
        Product product = createProduct(20L, "Driver 60W");
        OrderUpsertDto input = new OrderUpsertDto(LocalDate.of(2026, 4, 11), "L-002", Set.of(20L), 2L);
        OrderDto dto = new OrderDto(
                1L,
                LocalDate.of(2026, 4, 11),
                "L-002",
                List.of(new OrderProductDto(20L, "Driver 60W", "AVAILABLE")),
                new OrderCompanyDto(2L, "DLED", "DLED Lighting", CompanyType.OWN),
                LocalDateTime.of(2026, 4, 10, 9, 0)
        );

        when(repository.findByIdWithCompanyAndProducts(1L)).thenReturn(Optional.of(existing));
        when(companyRepository.findById(2L)).thenReturn(Optional.of(company));
        when(productRepository.findAllById(Set.of(20L))).thenReturn(List.of(product));
        when(repository.save(existing)).thenReturn(existing);
        when(mapper.fromOut(existing)).thenReturn(dto);

        OrderDto result = service.update(1L, input);

        assertSame(dto, result);
        assertEquals(LocalDate.of(2026, 4, 11), existing.getPurchaseDate());
        assertEquals("L-002", existing.getLot());
        assertEquals(2L, existing.getCompany().getId());
        assertEquals(1, existing.getProducts().size());
    }

    @Test
    void shouldDeleteOrder() {
        Order order = createOrder(1L);

        when(repository.findByIdWithCompanyAndProducts(1L)).thenReturn(Optional.of(order));

        service.delete(1L);

        verify(repository).delete(order);
    }

    @Test
    void shouldBuildProductLabel() {
        Order order = createOrder(1L);

        when(repository.findByIdWithCompanyAndProducts(1L)).thenReturn(Optional.of(order));

        PrintLabelProductDTO result = service.buildProductLabel(new PrintLabelProductRequestDto(1L, "L-001", 10L));

        assertEquals(1L, result.orderId());
        assertEquals("L-001", result.lot());
        assertEquals(10L, result.productId());
        assertEquals("Driver 24W", result.name());
        assertEquals("Product description", result.descricao());
        assertEquals(100, result.codigoRusso());
        assertEquals(200, result.codigoMali());
        assertEquals(7891234567890L, result.gtin());
        assertEquals(199.9, result.price());
        assertEquals(ProductStatus.AVAILABLE, result.status());
        assertEquals(24, result.watts());
        assertEquals(12, result.volt());
        assertEquals(5, result.amper());
        assertEquals(65, result.ip());
        assertEquals("3000K", result.temperaturaDeCor());
        assertEquals("5m", result.dimensao());
    }

    @Test
    void shouldThrowWhenBuildingProductLabelForDifferentLot() {
        Order order = createOrder(1L);

        when(repository.findByIdWithCompanyAndProducts(1L)).thenReturn(Optional.of(order));

        assertThrows(OrderNotFoundException.class, () ->
                service.buildProductLabel(new PrintLabelProductRequestDto(1L, "OTHER-LOT", 10L)));
    }

    @Test
    void shouldThrowWhenBuildingProductLabelForProductOutsideOrder() {
        Order order = createOrder(1L);

        when(repository.findByIdWithCompanyAndProducts(1L)).thenReturn(Optional.of(order));

        assertThrows(ProductNotFoundException.class, () ->
                service.buildProductLabel(new PrintLabelProductRequestDto(1L, "L-001", 999L)));
    }

    @Test
    void shouldThrowWhenOrderDoesNotExist() {
        when(repository.findByIdWithCompanyAndProducts(1L)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> service.getById(1L));
        assertThrows(OrderNotFoundException.class, () -> service.update(1L, new OrderUpsertDto(LocalDate.of(2026, 4, 10), "L-001", Set.of(10L), 1L)));
        assertThrows(OrderNotFoundException.class, () -> service.delete(1L));
    }

    @Test
    void shouldThrowWhenCompanyDoesNotExistDuringCreate() {
        when(companyRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CompanyNotFoundException.class, () ->
                service.create(new OrderUpsertDto(LocalDate.of(2026, 4, 10), "L-001", Set.of(10L), 1L)));
    }

    @Test
    void shouldThrowWhenProductDoesNotExistDuringCreate() {
        Company company = createCompany(1L);

        when(companyRepository.findById(1L)).thenReturn(Optional.of(company));
        when(productRepository.findAllById(Set.of(10L, 20L))).thenReturn(List.of(createProduct(10L, "Driver 24W")));

        assertThrows(ProductNotFoundException.class, () ->
                service.create(new OrderUpsertDto(LocalDate.of(2026, 4, 10), "L-001", Set.of(10L, 20L), 1L)));
    }

    private Order createOrder(Long id) {
        Order order = new Order();
        order.setId(id);
        order.setPurchaseDate(LocalDate.of(2026, 4, 10));
        order.setLot("L-001");
        order.setCompany(createCompany(1L));
        order.setProducts(new LinkedHashSet<>(List.of(createProduct(10L, "Driver 24W"))));
        order.setCreatedAt(LocalDateTime.of(2026, 4, 10, 9, 0));
        return order;
    }

    private Company createCompany(Long id) {
        Company company = new Company();
        company.setId(id);
        company.setShortName("DLED");
        company.setFullName("DLED Lighting");
        company.setType(CompanyType.OWN);
        company.setCreatedAt(LocalDateTime.of(2026, 4, 10, 8, 0));
        return company;
    }

    private Product createProduct(Long id, String name) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setCodigoRusso(100);
        product.setCodigoMali(200);
        product.setDescricao("Product description");
        product.setIp(65);
        product.setAmper(5);
        product.setWatts(24);
        product.setGtin(7891234567890L);
        product.setVolt(12);
        product.setPrice(199.9);
        product.setTemperaturaDeCor("3000K");
        product.setDimensao("5m");
        product.setStatus(ProductStatus.AVAILABLE);
        return product;
    }

    private OrderDto createDto() {
        return new OrderDto(
                1L,
                LocalDate.of(2026, 4, 10),
                "L-001",
                List.of(new OrderProductDto(10L, "Driver 24W", "AVAILABLE")),
                new OrderCompanyDto(1L, "DLED", "DLED Lighting", CompanyType.OWN),
                LocalDateTime.of(2026, 4, 10, 9, 0)
        );
    }
}
