package com.vodafone.orderapi.services;

import com.vodafone.orderapi.models.Product;

import java.util.List;

public interface ProductDao {

    Product addProduct(Product product);
    List<Product> getAllProducts();
    List<Product> getProductByOrderId(long orderId);

    Product updateProduct(long productId, long unitPrice,long salePrice);

    boolean deleteProduct(long productId);


}
