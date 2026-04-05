package com.distribnet.products.service;

import com.distribnet.common.model.Tenant;
import com.distribnet.products.dto.ProductDto;
import com.distribnet.products.model.Product;
import com.distribnet.products.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    public List<ProductDto> findAllForTenant(Tenant tenant) {
        return productRepository.findByTenant(tenant).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public ProductDto findById(Tenant tenant, UUID id) {
        return productRepository.findByTenantAndId(tenant, id)
                .map(this::toDto)
                .orElse(null);
    }

    @Transactional
    public ProductDto create(Tenant tenant, ProductDto dto) {
        Product product = new Product();
        product.setTenant(tenant);
        product.setSkuCode(dto.getSkuCode());
        product.setName(dto.getName());
        product.setCategory(dto.getCategory());
        product.setUnit(dto.getUnit());
        product.setBasePrice(dto.getBasePrice());
        product.setTradePrice(dto.getTradePrice());
        product.setMrp(dto.getMrp());
        product.setDiscountPct(dto.getDiscountPct());
        product.setStockQty(dto.getStockQty());
        product.setReorderLevel(dto.getReorderLevel());
        product.setSalesThisMonth(dto.getSalesThisMonth());
        product.setSalesTotal(dto.getSalesTotal());
        product.setStatus(Product.ProductStatus.valueOf(dto.getStatus().toUpperCase()));
        return toDto(productRepository.save(product));
    }

    @Transactional
    public ProductDto update(Tenant tenant, UUID id, ProductDto dto) {
        return productRepository.findByTenantAndId(tenant, id).map(existing -> {
            existing.setSkuCode(dto.getSkuCode());
            existing.setName(dto.getName());
            existing.setCategory(dto.getCategory());
            existing.setUnit(dto.getUnit());
            existing.setBasePrice(dto.getBasePrice());
            existing.setTradePrice(dto.getTradePrice());
            existing.setMrp(dto.getMrp());
            existing.setDiscountPct(dto.getDiscountPct());
            existing.setStockQty(dto.getStockQty());
            existing.setReorderLevel(dto.getReorderLevel());
            existing.setSalesThisMonth(dto.getSalesThisMonth());
            existing.setSalesTotal(dto.getSalesTotal());
            existing.setStatus(Product.ProductStatus.valueOf(dto.getStatus().toUpperCase()));
            return toDto(productRepository.save(existing));
        }).orElse(null);
    }

    @Transactional
    public boolean remove(Tenant tenant, UUID id) {
        return productRepository.findByTenantAndId(tenant, id).map(existing -> {
            productRepository.delete(existing);
            return true;
        }).orElse(false);
    }

    private ProductDto toDto(Product product) {
        ProductDto dto = new ProductDto();
        dto.setId(product.getId().toString());
        dto.setSkuCode(product.getSkuCode());
        dto.setName(product.getName());
        dto.setCategory(product.getCategory());
        dto.setUnit(product.getUnit());
        dto.setBasePrice(product.getBasePrice());
        dto.setTradePrice(product.getTradePrice());
        dto.setMrp(product.getMrp());
        dto.setDiscountPct(product.getDiscountPct());
        dto.setStockQty(product.getStockQty());
        dto.setReorderLevel(product.getReorderLevel());
        dto.setSalesThisMonth(product.getSalesThisMonth());
        dto.setSalesTotal(product.getSalesTotal());
        dto.setStatus(product.getStatus().name());
        return dto;
    }
}
