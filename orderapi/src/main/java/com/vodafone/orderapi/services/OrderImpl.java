package com.vodafone.orderapi.services;

import com.vodafone.orderapi.exceptions.OrderNotFoundException;
import com.vodafone.orderapi.models.Order;
import com.vodafone.orderapi.models.OrderStatus;
import com.vodafone.orderapi.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderImpl  implements OrderDao{
    @Autowired
    private OrderRepository orderRepository;
    @Override
    public Order addOrder(Order order) {
        return this.orderRepository.save(order);
    }

    @Override
    public Order updateOrder(long orderId, long amount) {
        Order order = this.orderRepository.findById(orderId).orElseThrow(()-> new OrderNotFoundException("Order Not found..."));
         order.setOrderAmount(amount);
         return this.orderRepository.save(order);


    }

    @Override
    public Order updateOrderStatus(long orderId, OrderStatus orderStatus) throws OrderNotFoundException {
        Order order = this.orderRepository.findById(orderId).orElseThrow(()-> new OrderNotFoundException("Order Not found..."));
        order.setOrderStatus(orderStatus);
        return this.orderRepository.save(order);
    }
}
