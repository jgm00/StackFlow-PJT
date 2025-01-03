package ssafy.StackFlow.Domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ssafy.StackFlow.Domain.product.Product;
import ssafy.StackFlow.Domain.product.ProductStore;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String storeName;

    @Column(nullable = false)
    private String location; // 매장 위치

    @Column(nullable = false, unique = true)
    private String storeCode; // 매장 코드

    @ManyToMany
    @JoinTable(
            name = "store_product",
            joinColumns = @JoinColumn(name = "store_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    @JsonIgnore
    private List<Product> products = new ArrayList<>();

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<ProductStore> storeProducts = new ArrayList<>();
}