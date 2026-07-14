package br.com.dled.dledbackend.modules.products.application;

import br.com.dled.dledbackend.modules.products.application.dto.ProductCardDto;
import br.com.dled.dledbackend.modules.products.application.dto.ProductDetailDto;
import br.com.dled.dledbackend.modules.products.application.dto.ProductUpsertDTO;
import br.com.dled.dledbackend.modules.products.application.exception.ProductGalleryLimitException;
import br.com.dled.dledbackend.modules.products.application.exception.ProductImageBadRequestException;
import br.com.dled.dledbackend.modules.products.application.exception.ProductNotFoundException;
import br.com.dled.dledbackend.modules.products.application.mapper.IProductMapper;
import br.com.dled.dledbackend.modules.products.application.storage.ProductImageStorageService;
import br.com.dled.dledbackend.modules.categories.application.exception.CategoryNotFoundException;
import br.com.dled.dledbackend.modules.categories.domain.Category;
import br.com.dled.dledbackend.modules.categories.infrastructure.CategoryRepository;
import br.com.dled.dledbackend.modules.products.domain.Product;
import br.com.dled.dledbackend.modules.products.domain.ProductGalleryImage;
import br.com.dled.dledbackend.modules.products.infrastructure.ProductGalleryImageRepository;
import br.com.dled.dledbackend.modules.products.infrastructure.ProductRespository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static br.com.dled.dledbackend.core.exceptions.BaseMessageError.CATEGORY_NOT_FOUND;
import static br.com.dled.dledbackend.core.exceptions.BaseMessageError.PRODUCT_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements IProductService{
    private static final int MAX_GALLERY_IMAGES = 5;

    private final ProductRespository repository;
    private final ProductGalleryImageRepository productGalleryImageRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageStorageService productImageStorageService;
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
        deleteStoredImage(product.getImgPublicId());
        deleteStoredImage(product.getIconPublicId());
        product.getGalleryImages().forEach(image -> deleteStoredImage(image.getPublicId()));
        repository.delete(product);
    }

    @Override
    public ProductDetailDto uploadMainImage(Long productId, MultipartFile file) {
        Product product = repository.findByIdWithCategories(productId)
                .orElseThrow(() -> new ProductNotFoundException(PRODUCT_NOT_FOUND.getMassage()));

        replaceMainImage(product, file);
        return mapper.fromOut(repository.save(product));
    }

    @Override
    public ProductDetailDto uploadIconImage(Long productId, MultipartFile file) {
        Product product = repository.findByIdWithCategories(productId)
                .orElseThrow(() -> new ProductNotFoundException(PRODUCT_NOT_FOUND.getMassage()));

        replaceIconImage(product, file);
        return mapper.fromOut(repository.save(product));
    }

    @Override
    public ProductDetailDto addGalleryImage(Long productId, MultipartFile file) {
        Product product = repository.findByIdWithCategories(productId)
                .orElseThrow(() -> new ProductNotFoundException(PRODUCT_NOT_FOUND.getMassage()));

        if (product.getGalleryImages().size() >= MAX_GALLERY_IMAGES) {
            throw new ProductGalleryLimitException("Product gallery supports up to 5 images.");
        }

        ProductImageStorageService.StoredImage uploaded = productImageStorageService.upload(productId, "gallery", file);
        ProductGalleryImage image = new ProductGalleryImage();
        image.setProduct(product);
        image.setImageUrl(uploaded.url());
        image.setPublicId(uploaded.publicId());
        product.getGalleryImages().add(image);

        return mapper.fromOut(repository.save(product));
    }

    @Override
    public ProductDetailDto removeGalleryImage(Long productId, Long imageId) {
        Product product = repository.findByIdWithCategories(productId)
                .orElseThrow(() -> new ProductNotFoundException(PRODUCT_NOT_FOUND.getMassage()));

        ProductGalleryImage image = productGalleryImageRepository.findByIdAndProductId(imageId, productId)
                .orElseThrow(() -> new ProductNotFoundException(PRODUCT_NOT_FOUND.getMassage()));

        deleteStoredImage(image.getPublicId());
        product.getGalleryImages().removeIf(current -> current.getId().equals(imageId));
        productGalleryImageRepository.delete(image);

        return mapper.fromOut(repository.save(product));
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
        product.setPrice(input.price());
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

    private void replaceMainImage(Product product, MultipartFile file) {
        validateFile(file);
        deleteStoredImage(product.getImgPublicId());
        ProductImageStorageService.StoredImage uploaded = productImageStorageService.upload(product.getId(), "main", file);
        product.setImgUrl(uploaded.url());
        product.setImgPublicId(uploaded.publicId());
    }

    private void replaceIconImage(Product product, MultipartFile file) {
        validateFile(file);
        deleteStoredImage(product.getIconPublicId());
        ProductImageStorageService.StoredImage uploaded = productImageStorageService.upload(product.getId(), "icon", file);
        product.setIconUrl(uploaded.url());
        product.setIconPublicId(uploaded.publicId());
    }

    private void deleteStoredImage(String publicId) {
        if (publicId != null && !publicId.isBlank()) {
            productImageStorageService.delete(publicId);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ProductImageBadRequestException("Image file is required.");
        }
    }
}
