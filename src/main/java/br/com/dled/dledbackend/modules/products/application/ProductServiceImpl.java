package br.com.dled.dledbackend.modules.products.application;

import br.com.dled.dledbackend.modules.products.application.dto.ProductCardDto;
import br.com.dled.dledbackend.modules.products.application.mapper.IProductMapper;
import br.com.dled.dledbackend.modules.products.domain.Product;
import br.com.dled.dledbackend.modules.products.infrastructure.ProductRespository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements IProductService{
    private final ProductRespository repository;
    private final IProductMapper mapper;
    @Override
    public List<ProductCardDto> getAll() {
        List<Product> products = repository.findAllActiveWithCategories();
        return products.stream()
                .map(mapper::fromOutList).toList();
    }
 }

