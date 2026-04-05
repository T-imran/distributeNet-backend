package com.distribnet.orders.service;

import com.distribnet.common.model.Tenant;
import com.distribnet.orders.dto.OrderDto;
import com.distribnet.orders.dto.OrderItemDto;
import com.distribnet.orders.model.Order;
import com.distribnet.orders.model.OrderItem;
import com.distribnet.orders.repository.OrderRepository;
import com.distribnet.products.repository.ProductRepository;
import com.distribnet.retailers.repository.RetailerRepository;
import com.distribnet.salesmen.repository.SalesmanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final RetailerRepository retailerRepository;
    private final SalesmanRepository salesmanRepository;
    private final ProductRepository productRepository;

    public List<OrderDto> findAllForTenant(Tenant tenant) {
        return orderRepository.findByTenant(tenant).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public OrderDto findById(Tenant tenant, UUID id) {
        return orderRepository.findByTenantAndId(tenant, id)
                .map(this::toDto)
                .orElse(null);
    }

    @Transactional
    public OrderDto create(Tenant tenant, OrderDto dto) {
        Order order = new Order();
        order.setTenant(tenant);
        order.setOrderNumber(dto.getOrderNumber());
        order.setStatus(Order.OrderStatus.valueOf(dto.getStatus().toUpperCase()));
        order.setRetailer(retailerRepository.findById(UUID.fromString(dto.getRetailerId())).orElse(null));
        order.setDealer(dto.getDealerId() != null ? retailerRepository.findById(UUID.fromString(dto.getDealerId())).map(r -> r.getDealer()).orElse(null) : null);
        order.setSalesman(dto.getSalesmanId() != null ? salesmanRepository.findById(UUID.fromString(dto.getSalesmanId())).orElse(null) : null);
        order.setSubtotal(dto.getSubtotal());
        order.setDiscount(dto.getDiscount());
        order.setTotal(dto.getTotal());
        order.setApprovedAt(LocalDateTime.now());
        order.setApprovedBy(null);

        List<OrderItem> items = dto.getItems().stream().map(this::toEntity).collect(Collectors.toList());
        items.forEach(item -> item.setTenant(tenant));
        items.forEach(item -> item.setOrder(order));
        order.setItems(items);
        return toDto(orderRepository.save(order));
    }

    @Transactional
    public OrderDto approve(Tenant tenant, UUID id) {
        return orderRepository.findByTenantAndId(tenant, id).map(order -> {
            order.setStatus(Order.OrderStatus.APPROVED);
            order.setApprovedAt(LocalDateTime.now());
            return toDto(orderRepository.save(order));
        }).orElse(null);
    }

    @Transactional
    public boolean cancel(Tenant tenant, UUID id) {
        return orderRepository.findByTenantAndId(tenant, id).map(order -> {
            order.setStatus(Order.OrderStatus.CANCELLED);
            orderRepository.save(order);
            return true;
        }).orElse(false);
    }

    private OrderItem toEntity(OrderItemDto itemDto) {
        OrderItem item = new OrderItem();
        item.setSkuCode(itemDto.getSkuCode());
        item.setProductName(itemDto.getProductName());
        item.setQuantity(itemDto.getQuantity());
        item.setUnitPrice(itemDto.getUnitPrice());
        item.setDiscount(itemDto.getDiscount());
        item.setTotal(itemDto.getTotal());
        productRepository.findById(UUID.fromString(itemDto.getSkuId())).ifPresent(item::setProduct);
        return item;
    }

    private OrderDto toDto(Order order) {
        OrderDto dto = new OrderDto();
        dto.setId(order.getId().toString());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setStatus(order.getStatus().name());
        dto.setRetailerId(order.getRetailer() != null ? order.getRetailer().getId().toString() : null);
        dto.setDealerId(order.getDealer() != null ? order.getDealer().getId().toString() : null);
        dto.setSalesmanId(order.getSalesman() != null ? order.getSalesman().getId().toString() : null);
        dto.setSubtotal(order.getSubtotal());
        dto.setDiscount(order.getDiscount());
        dto.setTotal(order.getTotal());
        dto.setItems(order.getItems().stream().map(this::toDto).collect(Collectors.toList()));
        return dto;
    }

    private OrderItemDto toDto(OrderItem item) {
        OrderItemDto dto = new OrderItemDto();
        dto.setSkuId(item.getProduct() != null ? item.getProduct().getId().toString() : null);
        dto.setSkuCode(item.getSkuCode());
        dto.setProductName(item.getProductName());
        dto.setQuantity(item.getQuantity());
        dto.setUnitPrice(item.getUnitPrice());
        dto.setDiscount(item.getDiscount());
        dto.setTotal(item.getTotal());
        return dto;
    }
}
