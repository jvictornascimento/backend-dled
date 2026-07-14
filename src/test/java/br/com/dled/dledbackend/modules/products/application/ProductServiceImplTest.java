package br.com.dled.dledbackend.modules.products.application;

import br.com.dled.dledbackend.modules.products.application.dto.ProductCardDto;
import br.com.dled.dledbackend.modules.products.application.dto.ProductDetailDto;
import br.com.dled.dledbackend.modules.products.application.dto.ProductGalleryImageDto;
import br.com.dled.dledbackend.modules.products.application.exception.ProductGalleryLimitException;
import br.com.dled.dledbackend.modules.products.application.exception.ProductNotFoundException;
import br.com.dled.dledbackend.modules.products.application.mapper.IProductMapper;
import br.com.dled.dledbackend.modules.products.application.storage.ProductImageStorageService;
import br.com.dled.dledbackend.modules.products.domain.Product;
import br.com.dled.dledbackend.modules.products.domain.ProductGalleryImage;
import br.com.dled.dledbackend.modules.products.domain.ProductStatus;
import br.com.dled.dledbackend.modules.products.infrastructure.ProductGalleryImageRepository;
import br.com.dled.dledbackend.modules.products.infrastructure.ProductRespository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRespository repository;
    @Mock
    private ProductGalleryImageRepository productGalleryImageRepository;
    @Mock
    private ProductImageStorageService productImageStorageService;

    @Mock
    private IProductMapper mapper;

    @InjectMocks
    private ProductServiceImpl service;

    @Test
    void shouldReturnMappedProductCards() {
        Product product = createProduct(1L, true);
        ProductCardDto dto = new ProductCardDto(1L, "Produto", 99.9, "icon.png", 10, 20, ProductStatus.AVAILABLE, Collections.emptySet());

        when(repository.findAllActiveWithCategories()).thenReturn(List.of(product));
        when(mapper.fromOutList(product)).thenReturn(dto);

        List<ProductCardDto> result = service.getAll();

        assertEquals(1, result.size());
        assertSame(dto, result.get(0));
        verify(mapper).fromOutList(product);
    }

    @Test
    void shouldReturnProductDetailByIdWhenActive() {
        Product product = createProduct(1L, true);
        ProductDetailDto dto = new ProductDetailDto(
                1L,
                "Produto",
                10,
                20,
                Collections.emptySet(),
                ProductStatus.AVAILABLE,
                "Descricao",
                "Evitar locais umidos.",
                "Usar em ambiente interno.",
                "Lote premium.",
                65,
                5,
                24,
                7891234567890L,
                12,
                "img.png",
                99.9,
                "icon.png",
                "3000K",
                60,
                "SMD",
                "1200lm",
                "80",
                5,
                10,
                2,
                true,
                "5m",
                List.of(new ProductGalleryImageDto(1L, "gallery.png")),
                LocalDateTime.of(2026, 4, 9, 10, 0),
                true
        );

        when(repository.findByIdWithCategories(1L)).thenReturn(Optional.of(product));
        when(mapper.fromOut(product)).thenReturn(dto);

        ProductDetailDto result = service.getById(1L);

        assertSame(dto, result);
        verify(mapper).fromOut(product);
    }

    @Test
    void shouldThrowWhenProductDoesNotExist() {
        when(repository.findByIdWithCategories(1L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> service.getById(1L));
    }

    @Test
    void shouldThrowWhenProductIsInactive() {
        Product product = createProduct(1L, false);

        when(repository.findByIdWithCategories(1L)).thenReturn(Optional.of(product));

        assertThrows(ProductNotFoundException.class, () -> service.getById(1L));
    }

    @Test
    void shouldUploadMainImageForProduct() {
        Product product = createProduct(1L, true);
        MockMultipartFile file = new MockMultipartFile("file", "main.png", "image/png", new byte[]{1});
        ProductDetailDto dto = createDetailDto();

        when(repository.findByIdWithCategories(1L)).thenReturn(Optional.of(product));
        when(productImageStorageService.upload(1L, "main", file))
                .thenReturn(new ProductImageStorageService.StoredImage("https://cloudinary.test/main.png", "main/public"));
        when(repository.save(product)).thenReturn(product);
        when(mapper.fromOut(product)).thenReturn(dto);

        ProductDetailDto result = service.uploadMainImage(1L, file);

        assertSame(dto, result);
        assertEquals("https://cloudinary.test/main.png", product.getImgUrl());
        assertEquals("main/public", product.getImgPublicId());
    }

    @Test
    void shouldThrowWhenGalleryLimitIsReached() {
        Product product = createProduct(1L, true);
        for (long index = 2; index <= 5; index++) {
            ProductGalleryImage image = new ProductGalleryImage();
            image.setId(index);
            image.setImageUrl("gallery-" + index + ".png");
            image.setPublicId("gallery/" + index);
            image.setProduct(product);
            product.getGalleryImages().add(image);
        }

        when(repository.findByIdWithCategories(1L)).thenReturn(Optional.of(product));

        assertThrows(ProductGalleryLimitException.class, () ->
                service.addGalleryImage(1L, new MockMultipartFile("file", "gallery.png", "image/png", new byte[]{1})));
        verify(productImageStorageService, never()).upload(org.mockito.ArgumentMatchers.anyLong(), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any());
    }

    private Product createProduct(Long id, boolean active) {
        Product product = new Product();
        product.setId(id);
        product.setName("Produto");
        product.setCodigoRusso(10);
        product.setCodigoMali(20);
        product.setDescricao("Descricao");
        product.setStatus(ProductStatus.AVAILABLE);
        product.setRestricoesDeUso("Evitar locais umidos.");
        product.setRecomendacoesDeUso("Usar em ambiente interno.");
        product.setObservacoesEspeciais("Lote premium.");
        product.setObservacoesInternas("Somente time interno.");
        product.setIp(65);
        product.setAmper(5);
        product.setWatts(24);
        product.setGtin(7891234567890L);
        product.setVolt(12);
        product.setImgUrl("img.png");
        product.setImgPublicId("img-public");
        product.setPrice(99.9);
        product.setIconUrl("icon.png");
        product.setIconPublicId("icon-public");
        product.setTemperaturaDeCor("3000K");
        product.setLedsPorMetro(60);
        product.setTipoLed("SMD");
        product.setFluxoLuminoso("1200lm");
        product.setIndiceDeReproducaoDeCor("80");
        product.setQuantidePorRolo(5);
        product.setSessaoDeCorte(10);
        product.setEspessura(2);
        product.setBlindada(true);
        product.setDimensao("5m");
        ProductGalleryImage image = new ProductGalleryImage();
        image.setId(1L);
        image.setImageUrl("gallery.png");
        image.setPublicId("gallery/public-id");
        image.setProduct(product);
        product.getGalleryImages().add(image);
        product.setCreatedAt(LocalDateTime.of(2026, 4, 9, 10, 0));
        product.setActive(active);
        return product;
    }

    private ProductDetailDto createDetailDto() {
        return new ProductDetailDto(
                1L,
                "Produto",
                10,
                20,
                Collections.emptySet(),
                ProductStatus.AVAILABLE,
                "Descricao",
                "Evitar locais umidos.",
                "Usar em ambiente interno.",
                "Lote premium.",
                65,
                5,
                24,
                7891234567890L,
                12,
                "img.png",
                99.9,
                "icon.png",
                "3000K",
                60,
                "SMD",
                "1200lm",
                "80",
                5,
                10,
                2,
                true,
                "5m",
                List.of(new ProductGalleryImageDto(1L, "gallery.png")),
                LocalDateTime.of(2026, 4, 9, 10, 0),
                true
        );
    }
}
