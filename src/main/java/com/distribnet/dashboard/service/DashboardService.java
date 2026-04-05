package com.distribnet.dashboard.service;

import com.distribnet.common.model.Tenant;
import com.distribnet.dealers.repository.DealerRepository;
import com.distribnet.orders.model.Order;
import com.distribnet.orders.repository.OrderRepository;
import com.distribnet.payments.model.Payment;
import com.distribnet.payments.repository.PaymentRepository;
import com.distribnet.retailers.repository.RetailerRepository;
import com.distribnet.salesmen.repository.SalesmanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final DealerRepository dealerRepository;
    private final RetailerRepository retailerRepository;
    private final SalesmanRepository salesmanRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    public Map<String, Object> getKpis(Tenant tenant) {
        List<Order> orders = orderRepository.findByTenant(tenant);
        List<Payment> payments = paymentRepository.findByTenant(tenant);

        BigDecimal totalRevenue = payments.stream()
                .filter(payment -> payment.getStatus() == Payment.PaymentStatus.VERIFIED)
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return Map.of(
                "tenantId", tenant.getId().toString(),
                "totalRevenue", totalRevenue,
                "activeDealers", dealerRepository.findByTenant(tenant).size(),
                "activeRetailers", retailerRepository.findByTenant(tenant).size(),
                "activeSalesmen", salesmanRepository.findByTenant(tenant).size(),
                "orderVolume", orders.size(),
                "pendingOrders", orders.stream().filter(order -> order.getStatus() == Order.OrderStatus.SUBMITTED).count(),
                "verifiedPayments", payments.stream().filter(payment -> payment.getStatus() == Payment.PaymentStatus.VERIFIED).count(),
                "overduePayments", payments.stream().filter(payment -> payment.getStatus() == Payment.PaymentStatus.REJECTED).count()
        );
    }

    public List<Map<String, Object>> getRecentOrders(Tenant tenant, int limit) {
        return orderRepository.findByTenant(tenant).stream()
                .sorted(Comparator.comparing(Order::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(limit)
                .map(order -> Map.<String, Object>of(
                        "id", order.getId().toString(),
                        "orderNumber", order.getOrderNumber(),
                        "status", order.getStatus().name().toLowerCase(),
                        "total", order.getTotal(),
                        "createdAt", order.getCreatedAt()
                ))
                .toList();
    }

    public List<Map<String, Object>> getTopSalesmen(Tenant tenant, int limit) {
        return salesmanRepository.findByTenant(tenant).stream()
                .sorted(Comparator.comparing(salesman -> salesman.getMonthlyAchieved() == null ? BigDecimal.ZERO : salesman.getMonthlyAchieved(), Comparator.reverseOrder()))
                .limit(limit)
                .map(salesman -> Map.<String, Object>of(
                        "id", salesman.getId().toString(),
                        "userId", salesman.getUser() != null ? salesman.getUser().getId().toString() : "",
                        "region", salesman.getRegion() == null ? "" : salesman.getRegion(),
                        "monthlyAchieved", salesman.getMonthlyAchieved(),
                        "monthlyTarget", salesman.getMonthlyTarget(),
                        "achievementPct", salesman.getAchievementPct()
                ))
                .toList();
    }

    public Map<String, Long> getOrderStatusBreakdown(Tenant tenant) {
        Map<String, Long> breakdown = new LinkedHashMap<>();
        for (Order.OrderStatus status : Order.OrderStatus.values()) {
            long count = orderRepository.findByTenant(tenant).stream()
                    .filter(order -> order.getStatus() == status)
                    .count();
            breakdown.put(status.name().toLowerCase(), count);
        }
        return breakdown;
    }
}
