package com.vodafone.orderapi.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="Vodafone_Product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="Product_Id")
    private long productId;
    @Column(name="Product_Name",length = 100, nullable = false)
    private String name;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name="DOP")
    private LocalDate dop;
    @Column(name="Unit_Price")
    private long unitPrice;
    @Column(name="Sale_Price")
    private long salePrice;
    @Column(name="Qty")
    private long qty;
    @Column(name="Buffer_Level")
    private long bufferLevel;
    @Enumerated(EnumType.STRING)
    @Column(name="Product_Type")
    private ProductType productType;
    @ManyToOne(fetch = FetchType.EAGER,cascade = CascadeType.MERGE)
    @JoinColumn(foreignKey = @ForeignKey(name = "Order_Id"),
            name = "Order_Id")
    private Order order;
}
