package br.com.dled.dledbackend.modules.products.application;

import br.com.dled.dledbackend.modules.products.application.dto.ProductCardDto;
import br.com.dled.dledbackend.modules.products.application.dto.ProductDetailDto;
import br.com.dled.dledbackend.modules.products.application.dto.ProductUpsertDTO;
import br.com.dled.dledbackend.modules.products.application.exception.ProductNotFoundException;
import br.com.dled.dledbackend.modules.products.application.mapper.IProductMapper;
import br.com.dled.dledbackend.modules.categories.application.exception.CategoryNotFoundException;
import br.com.dled.dledbackend.modules.categories.domain.Category;
import br.com.dled.dledbackend.modules.categories.infrastructure.CategoryRepository;
import br.com.dled.dledbackend.modules.products.domain.Product;
import br.com.dled.dledbackend.modules.products.infrastructure.ProductRespository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static br.com.dled.dledbackend.core.exceptions.BaseMessageError.CATEGORY_NOT_FOUND;
import static br.com.dled.dledbackend.core.exceptions.BaseMessageError.PRODUCT_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements IProductService{
    private final ProductRespository repository;
    private final CategoryRepository categoryRepository;
    private final IProductMapper mapper;
    @Override
    public List<ProductCardDto> getAll() {
        List<Product> products = repository.findAllActiveWithCategories();
        return products.stream()
                .map(mapper::fromOutList).toList();
    }

    @Override
    public ProductDetailDto getById(Long productId) {
        return mapper.fromOut(findActiveProduct(productId));
    }

    @Override
    public ProductDetailDto create(ProductUpsertDTO input) {
        Product product = new Product();
        applyInput(product, input);
        return mapper.fromOut(repository.save(product));
    }

    @Override
    public ProductDetailDto update(Long productId, ProductUpsertDTO input) {
        Product product = repository.findByIdWithCategories(productId)
                .orElseThrow(() -> new ProductNotFoundException(PRODUCT_NOT_FOUND.getMassage()));
        applyInput(product, input);
        return mapper.fromOut(repository.save(product));
    }

    @Override
    public void delete(Long productId) {
        Product product = repository.findByIdWithCategories(productId)
                .orElseThrow(() -> new ProductNotFoundException(PRODUCT_NOT_FOUND.getMassage()));
        repository.delete(product);
    }

    private Product findActiveProduct(Long productId) {
        return repository.findByIdWithCategories(productId)
                .filter(Product::isActive)
                .orElseThrow(() -> new ProductNotFoundException(PRODUCT_NOT_FOUND.getMassage()));
    }

    private void applyInput(Product product, ProductUpsertDTO input) {
        product.setName(input.name());
        product.setCodigoRusso(input.codigoRusso());
        product.setCodigoMali(input.codigoMali());
        product.setCategories(resolveCategories(input.categoryIds()));
        product.setStatus(input.status());
        product.setDescricao(input.descricao());
        product.setRestricoesDeUso(input.restricoesDeUso());
        product.setRecomendacoesDeUso(input.recomendacoesDeUso());
        product.setObservacoesEspeciais(input.observacoesEspeciais());
        product.setIp(input.ip());
        product.setAmper(input.amper());
        product.setWatts(input.watts());
        product.setGtin(input.gtin());
        product.setVolt(input.volt());
        product.setImgUrl(input.imgUrl());
        product.setPrice(input.price());
        product.setIconUrl(input.iconUrl());
        product.setTemperaturaDeCor(input.temperaturaDeCor());
        product.setLedsPorMetro(input.ledsPorMetro());
        product.setTipoLed(input.tipoLed());
        product.setFluxoLuminoso(input.fluxoLuminoso());
        product.setIndiceDeReproducaoDeCor(input.indiceDeReproducaoDeCor());
        product.setQuantidePorRolo(input.quantidePorRolo());
        product.setSessaoDeCorte(input.sessaoDeCorte());
        product.setEspessura(input.espessura());
        product.setBlindada(input.blindada());
        product.setDimensao(input.dimensao());
        product.setActive(input.active());
    }

    private Set<Category> resolveCategories(Set<Long> categoryIds) {
        List<Category> categories = categoryRepository.findAllById(categoryIds);
        if (categories.size() != categoryIds.size()) {
            throw new CategoryNotFoundException(CATEGORY_NOT_FOUND.getMassage());
        }
        return new LinkedHashSet<>(categories);
    }
}
