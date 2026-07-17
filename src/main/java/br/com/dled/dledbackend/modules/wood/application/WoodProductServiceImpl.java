package br.com.dled.dledbackend.modules.wood.application;

import br.com.dled.dledbackend.modules.products.application.exception.ProductImageBadRequestException;
import br.com.dled.dledbackend.modules.products.application.storage.ProductImageStorageService;
import br.com.dled.dledbackend.modules.wood.application.dto.WoodCategorySimpleDto;
import br.com.dled.dledbackend.modules.wood.application.dto.WoodProductFullDto;
import br.com.dled.dledbackend.modules.wood.application.dto.WoodProductListDto;
import br.com.dled.dledbackend.modules.wood.application.dto.WoodProductUpsertDto;
import br.com.dled.dledbackend.modules.wood.application.dto.WoodProductVariationDto;
import br.com.dled.dledbackend.modules.wood.application.dto.WoodProductVariationUpsertDto;
import br.com.dled.dledbackend.modules.wood.application.exception.WoodCategoryNotFoundException;
import br.com.dled.dledbackend.modules.wood.application.exception.WoodProductNotFoundException;
import br.com.dled.dledbackend.modules.wood.application.exception.WoodVariationNotFoundException;
import br.com.dled.dledbackend.modules.wood.domain.WoodCategory;
import br.com.dled.dledbackend.modules.wood.domain.WoodProduct;
import br.com.dled.dledbackend.modules.wood.domain.WoodProductVariation;
import br.com.dled.dledbackend.modules.wood.infrastructure.WoodCategoryRepository;
import br.com.dled.dledbackend.modules.wood.infrastructure.WoodProductRepository;
import br.com.dled.dledbackend.modules.wood.infrastructure.WoodProductVariationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class WoodProductServiceImpl implements IWoodProductService {
    private final WoodProductRepository productRepository;
    private final WoodCategoryRepository categoryRepository;
    private final WoodProductVariationRepository variationRepository;
    private final ProductImageStorageService imageStorageService;

    @Override
    @Transactional(readOnly = true)
    public List<WoodProductListDto> getAll() {
        return productRepository.findAllActiveWithCategories().stream()
                .map(this::toListDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public WoodProductFullDto getById(Long productId) {
        return toFullDto(findActiveProduct(productId));
    }

    @Override
    public WoodProductFullDto create(WoodProductUpsertDto input) {
        WoodProduct product = new WoodProduct();
        applyProductInput(product, input);
        return toFullDto(productRepository.save(product));
    }

    @Override
    public WoodProductFullDto update(Long productId, WoodProductUpsertDto input) {
        WoodProduct product = productRepository.findByIdWithDetails(productId)
                .orElseThrow(() -> new WoodProductNotFoundException("Wood product not found."));
        applyProductInput(product, input);
        return toFullDto(productRepository.save(product));
    }

    @Override
    public void delete(Long productId) {
        WoodProduct product = productRepository.findByIdWithDetails(productId)
                .orElseThrow(() -> new WoodProductNotFoundException("Wood product not found."));
        deleteStoredImage(product.getImgPublicId());
        product.getVariations().forEach(variation -> deleteStoredImage(variation.getLabelImagePublicId()));
        productRepository.delete(product);
    }

    @Override
    public WoodProductFullDto uploadProductImage(Long productId, MultipartFile file) {
        validateFile(file);
        WoodProduct product = productRepository.findByIdWithDetails(productId)
                .orElseThrow(() -> new WoodProductNotFoundException("Wood product not found."));
        deleteStoredImage(product.getImgPublicId());

        ProductImageStorageService.StoredImage uploaded = imageStorageService.upload(productId, "wood-main", file);
        product.setImgUrl(uploaded.url());
        product.setImgPublicId(uploaded.publicId());

        return toFullDto(productRepository.save(product));
    }

    @Override
    public WoodProductVariationDto createVariation(Long productId, WoodProductVariationUpsertDto input) {
        WoodProduct product = productRepository.findByIdWithDetails(productId)
                .orElseThrow(() -> new WoodProductNotFoundException("Wood product not found."));
        WoodProductVariation variation = new WoodProductVariation();
        variation.setProduct(product);
        applyVariationInput(variation, input);
        product.getVariations().add(variation);
        productRepository.save(product);
        return toVariationDto(variation);
    }

    @Override
    public WoodProductVariationDto updateVariation(Long productId, Long variationId, WoodProductVariationUpsertDto input) {
        WoodProductVariation variation = findVariation(productId, variationId);
        applyVariationInput(variation, input);
        return toVariationDto(variationRepository.save(variation));
    }

    @Override
    public void deleteVariation(Long productId, Long variationId) {
        WoodProductVariation variation = findVariation(productId, variationId);
        deleteStoredImage(variation.getLabelImagePublicId());
        variationRepository.delete(variation);
    }

    @Override
    public WoodProductVariationDto uploadVariationLabel(Long productId, Long variationId, MultipartFile file) {
        validateFile(file);
        WoodProductVariation variation = findVariation(productId, variationId);
        deleteStoredImage(variation.getLabelImagePublicId());

        ProductImageStorageService.StoredImage uploaded = imageStorageService.upload(productId, "wood-label-" + variationId, file);
        variation.setLabelImageUrl(uploaded.url());
        variation.setLabelImagePublicId(uploaded.publicId());

        return toVariationDto(variationRepository.save(variation));
    }

    private WoodProduct findActiveProduct(Long productId) {
        return productRepository.findByIdWithDetails(productId)
                .filter(WoodProduct::isActive)
                .orElseThrow(() -> new WoodProductNotFoundException("Wood product not found."));
    }

    private WoodProductVariation findVariation(Long productId, Long variationId) {
        return variationRepository.findByIdAndProductId(variationId, productId)
                .orElseThrow(() -> new WoodVariationNotFoundException("Wood product variation not found."));
    }

    private void applyProductInput(WoodProduct product, WoodProductUpsertDto input) {
        product.setName(input.name());
        product.setDescription(input.description());
        product.setImgUrl(input.imgUrl());
        product.setCaixa(input.caixa());
        product.setPrice(input.price());
        product.setWidthMm(input.widthMm());
        product.setHeightMm(input.heightMm());
        product.setLengthMm(input.lengthMm());
        product.setWeightKg(input.weightKg());
        product.setCategories(resolveCategories(input.categoryIds()));
        product.setActive(input.active());
    }

    private void applyVariationInput(WoodProductVariation variation, WoodProductVariationUpsertDto input) {
        variation.setColor(input.color());
        variation.setSku(input.sku());
        variation.setEan(input.ean());
        variation.setListImgs(input.listImgs() == null ? new ArrayList<>() : new ArrayList<>(input.listImgs()));
        variation.setActive(input.active());
    }

    private Set<WoodCategory> resolveCategories(Set<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return new LinkedHashSet<>();
        }

        List<WoodCategory> categories = categoryRepository.findAllById(categoryIds);
        if (categories.size() != categoryIds.size()) {
            throw new WoodCategoryNotFoundException("Wood category not found.");
        }

        return new LinkedHashSet<>(categories);
    }

    private WoodProductListDto toListDto(WoodProduct product) {
        return new WoodProductListDto(
                product.getId(),
                product.getImgUrl(),
                product.getName(),
                product.getCaixa(),
                product.isActive()
        );
    }

    private WoodProductFullDto toFullDto(WoodProduct product) {
        return new WoodProductFullDto(
                product.getId(),
                product.getImgUrl(),
                product.getName(),
                product.getDescription(),
                product.getCaixa(),
                product.getPrice(),
                product.getWidthMm(),
                product.getHeightMm(),
                product.getLengthMm(),
                product.getWeightKg(),
                toCategoryDtos(product.getCategories()),
                product.getVariations().stream()
                        .sorted(Comparator.comparing(WoodProductVariation::getId, Comparator.nullsLast(Long::compareTo)))
                        .map(this::toVariationDto)
                        .toList(),
                product.isActive(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }

    private Set<WoodCategorySimpleDto> toCategoryDtos(Set<WoodCategory> categories) {
        return categories.stream()
                .sorted(Comparator.comparing(WoodCategory::getName))
                .map(category -> new WoodCategorySimpleDto(category.getId(), category.getName()))
                .collect(java.util.stream.Collectors.toCollection(LinkedHashSet::new));
    }

    private WoodProductVariationDto toVariationDto(WoodProductVariation variation) {
        return new WoodProductVariationDto(
                variation.getId(),
                variation.getColor(),
                variation.getSku(),
                variation.getEan(),
                List.copyOf(variation.getListImgs()),
                variation.getLabelImageUrl(),
                variation.getLabelImagePublicId(),
                variation.isActive(),
                variation.getCreatedAt(),
                variation.getUpdatedAt()
        );
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ProductImageBadRequestException("Image file is required.");
        }
    }

    private void deleteStoredImage(String publicId) {
        if (publicId != null && !publicId.isBlank()) {
            imageStorageService.delete(publicId);
        }
    }
}
