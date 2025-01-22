package com.vodafone.orderapi.services;

import com.vodafone.orderapi.models.Product;
import com.vodafone.orderapi.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductImpl  implements ProductDao{
   @Autowired
    private ProductRepository productRepository;
    @Override
    public Product addProduct(Product product) {

        return this.productRepository.save(product);
    }

    @Override
    public List<Product> getAllProducts() {
        return this.productRepository.findAll();
    }

    @Override
    public List<Product> getProductByOrderId(long orderId) {
        return this.productRepository.findProductByOrderId(orderId);
    }

    @Override
    public Product updateProduct(long productId, long unitPrice, long salePrice) {
        Product product=this.productRepository.findById(productId).orElse(null);
        if(product!=null){
            product.setUnitPrice(unitPrice);
            product.setSalePrice(salePrice);
            return this.productRepository.save(product);
        }else
            return null;


    }

    @Override
    public boolean deleteProduct(long productId) {
        boolean status=false;
        Product product=this.productRepository.findById(productId).orElse(null);
        if(product!=null){
            this.productRepository.deleteById(productId);
            status=true;
        }
        return status;
    }
}
