package br.com.dled.dledbackend.modules.wood.application;

import br.com.dled.dledbackend.modules.wood.application.dto.WoodProductFullDto;
import br.com.dled.dledbackend.modules.wood.application.dto.WoodProductListDto;
import br.com.dled.dledbackend.modules.wood.application.dto.WoodProductUpsertDto;
import br.com.dled.dledbackend.modules.wood.application.dto.WoodProductVariationDto;
import br.com.dled.dledbackend.modules.wood.application.dto.WoodProductVariationUpsertDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IWoodProductService {
    List<WoodProductListDto> getAll();
    WoodProductFullDto getById(Long productId);
    WoodProductFullDto create(WoodProductUpsertDto input);
    WoodProductFullDto update(Long productId, WoodProductUpsertDto input);
    void delete(Long productId);
    WoodProductFullDto uploadProductImage(Long productId, MultipartFile file);
    WoodProductVariationDto createVariation(Long productId, WoodProductVariationUpsertDto input);
    WoodProductVariationDto updateVariation(Long productId, Long variationId, WoodProductVariationUpsertDto input);
    void deleteVariation(Long productId, Long variationId);
    WoodProductVariationDto uploadVariationLabel(Long productId, Long variationId, MultipartFile file);
}
