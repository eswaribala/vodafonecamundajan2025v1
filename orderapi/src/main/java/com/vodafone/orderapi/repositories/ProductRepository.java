package com.vodafone.orderapi.repositories;

import com.vodafone.orderapi.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("Select p from Product p where p.order.orderId=:orderId")
    public List<Product> findProductByOrderId(@Param("orderId") long orderId);

}
