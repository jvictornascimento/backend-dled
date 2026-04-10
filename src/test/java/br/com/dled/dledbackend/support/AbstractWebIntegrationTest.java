package br.com.dled.dledbackend.support;

import br.com.dled.dledbackend.modules.categories.domain.Category;
import br.com.dled.dledbackend.modules.categories.infrastructure.CategoryRepository;
import br.com.dled.dledbackend.modules.products.application.storage.ProductImageStorageService;
import br.com.dled.dledbackend.modules.products.domain.Product;
import br.com.dled.dledbackend.modules.products.domain.ProductStatus;
import br.com.dled.dledbackend.modules.products.infrastructure.ProductRespository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Import(AbstractWebIntegrationTest.TestProductImageStorageConfig.class)
public abstract class AbstractWebIntegrationTest {

    protected static final String API_KEY_HEADER = "X-API-Key";
    protected static final String API_KEY_VALUE = "test-api-key";

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected CategoryRepository categoryRepository;

    @Autowired
    protected ProductRespository productRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void resetDatabase() {
        jdbcTemplate.execute("DELETE FROM product_gallery_image");
        jdbcTemplate.execute("DELETE FROM product_category");
        jdbcTemplate.execute("DELETE FROM product");
        jdbcTemplate.execute("ALTER TABLE product ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.execute("DELETE FROM category");
        jdbcTemplate.execute("ALTER TABLE category ALTER COLUMN id RESTART WITH 1");
    }

    protected Category saveRootCategory(String name) {
        Category category = new Category();
        category.setName(name);
        category.setImgUrl(name.toLowerCase() + ".png");
        category.setActive(true);
        return categoryRepository.save(category);
    }

    protected Category saveChildCategory(String name, Category parent) {
        Category category = new Category();
        category.setName(name);
        category.setImgUrl(name.toLowerCase() + ".png");
        category.setActive(true);
        category.setParent(parent);
        return categoryRepository.save(category);
    }

    protected Product saveProduct(String name, Category category) {
        Product product = new Product();
        product.setName(name);
        product.setCodigoRusso(100);
        product.setCodigoMali(200);
        product.setDescricao("Product description");
        product.setIp(65);
        product.setAmper(5);
        product.setWatts(24);
        product.setGtin(7891234567890L);
        product.setVolt(12);
        product.setImgUrl("https://cloudinary.test/products/main.png");
        product.setImgPublicId("products/main");
        product.setPrice(199.9);
        product.setIconUrl("https://cloudinary.test/products/icon.png");
        product.setIconPublicId("products/icon");
        product.setTemperaturaDeCor("3000K");
        product.setLedsPorMetro(60);
        product.setTipoLed("SMD");
        product.setFluxoLuminoso("1200lm");
        product.setIndiceDeReproducaoDeCor("80");
        product.setQuantidePorRolo(5);
        product.setSessaoDeCorte(10);
        product.setEspessura(2);
        product.setStatus(ProductStatus.AVAILABLE);
        product.setRestricoesDeUso("Evitar ambientes com maresia.");
        product.setRecomendacoesDeUso("Indicado para ambientes internos.");
        product.setObservacoesEspeciais("Garantia de 12 meses.");
        product.setObservacoesInternas("Somente equipe comercial.");
        product.setBlindada(true);
        product.setDimensao("5m");
        product.setCreatedAt(LocalDateTime.of(2026, 4, 9, 12, 0));
        product.setActive(true);
        product.setCategories(Collections.singleton(category));
        return productRepository.save(product);
    }

    @TestConfiguration
    static class TestProductImageStorageConfig {
        @Bean
        @Primary
        ProductImageStorageService productImageStorageService() {
            return new ProductImageStorageService() {
                @Override
                public StoredImage upload(Long productId, String imageType, org.springframework.web.multipart.MultipartFile file) {
                    return new StoredImage(
                            "https://cloudinary.test/products/" + productId + "/" + imageType + "/" + file.getOriginalFilename(),
                            "products/" + productId + "/" + imageType + "/" + file.getOriginalFilename()
                    );
                }

                @Override
                public void delete(String publicId) {
                }
            };
        }
    }
}
