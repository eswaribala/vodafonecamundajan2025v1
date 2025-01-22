package com.vodafone.orderapi.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Table(name="Vodafone_Order")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="Order_Id")
    private long orderId;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name="Order_Date")
    private LocalDate orderDate;
    @Column(name="Order_Amount")
    private long orderAmount;

    @Enumerated(EnumType.STRING)
    @Column(name="Order_Status")
    private OrderStatus orderStatus;
}
