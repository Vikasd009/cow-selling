package com.vikas.cowselling.entity;

import com.vikas.cowselling.enums.CowGender;
import com.vikas.cowselling.enums.CowStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cows")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100)
    private String breed;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CowGender gender;

    @Column(nullable = false)
    private Integer age;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Double milkProduction;

    private Double weight;

    @Column(length = 50)
    private String color;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(nullable = false, length = 100)
    private String state;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CowStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate(){
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate(){
        updatedAt = LocalDateTime.now();
    }

    @OneToMany(
            mappedBy = "cow",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<CowImage> images = new ArrayList<>();

    @Column(length = 1000)
    private String rejectionReason;
}
