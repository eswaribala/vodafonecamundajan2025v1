package com.vodafone.orderapi.configurations;

import com.github.javafaker.Faker;
import com.vodafone.orderapi.models.Order;
import com.vodafone.orderapi.models.OrderStatus;
import com.vodafone.orderapi.models.Product;
import com.vodafone.orderapi.models.ProductType;
import com.vodafone.orderapi.services.OrderImpl;
import com.vodafone.orderapi.services.ProductImpl;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.util.health.HealthStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;


import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Configuration
public class StoreOrderJobConfiguration {

    @Autowired
    private OrderImpl orderImpl;
    @Autowired
    private ProductImpl productImpl;

    @JobWorker(type = "storeorder",autoComplete = false)
    public Map<String, Order> saveOrder(final JobClient jobClient, final ActivatedJob activatedJob){
        Faker faker=new Faker();
        Map<String,Object> receivedData=activatedJob.getVariablesAsMap();

        receivedData.entrySet().stream().forEach(entrySet->{
            System.out.println(entrySet.getKey()+","+entrySet.getValue());
                    });

        Map<String, Order> orderMap=new HashMap<>();
        Date date=faker.date().birthday();
        LocalDate localDate= Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
        Order order=new Order(faker.number().numberBetween(1,1000000),localDate,0, OrderStatus.InProcess);

        //add to backend db
        Order orderResponse=orderImpl.addOrder(order);
        Product product=null;
        if(orderResponse!=null){
            product=new Product();
            product.setOrder(orderResponse);
            product.setQty(Long.parseLong(receivedData.get("qty").toString()));
            product.setName(receivedData.get("productName").toString());
            product.setUnitPrice(faker.number().numberBetween(20000,5000000));
            product.setSalePrice(product.getUnitPrice()*2);
            product.setBufferLevel(faker.number().numberBetween(50,200));
            ProductType productType =  getRandomEnumValue(ProductType.class);
            product.setProductType(productType);
            product.setDop(localDate);
            productImpl.addProduct(product);
            order=this.orderImpl.updateOrder(orderResponse.getOrderId(),product.getSalePrice());

        }

        orderMap.put("order",order);
        jobClient.newCompleteCommand(activatedJob.getKey())
                .variables(orderMap)
                .send()
                .exceptionally((throwable)->{
                    throw new RuntimeException("Job not found");
                });
      return orderMap;

    }
    public static <T extends Enum<?>> T getRandomEnumValue(Class<T> enumClass){
        T[] enumValues=enumClass.getEnumConstants();
        return enumValues[new Random().nextInt(enumValues.length)];
    }
}
