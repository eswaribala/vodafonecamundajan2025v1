package com.vodafone.orderapi.repositories;

import com.vodafone.orderapi.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order,Long> {
}
