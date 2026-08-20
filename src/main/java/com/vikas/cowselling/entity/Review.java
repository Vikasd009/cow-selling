package com.vikas.cowselling.entity;

import jakarta.persistence.*;
        import lombok.*;

        import java.time.LocalDateTime;

@Entity
@Table(
        name = "reviews",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                "reviewer_id",
                                "seller_id",
                                "cow_id"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer rating;

    @Column(length = 1000)
    private String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "reviewer_id",
            nullable = false
    )
    private User reviewer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "seller_id",
            nullable = false
    )
    private User seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "cow_id",
            nullable = false
    )
    private Cow cow;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
