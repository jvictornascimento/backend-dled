package br.com.dled.dledbackend.modules.wood.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "wood_product")
@Data
@NoArgsConstructor
public class WoodProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String imgUrl;
    private String imgPublicId;
    private String caixa;
    private String woodType;
    private String finish;
    private Double thicknessMm;
    private Double widthMm;
    private Double heightMm;
    private Double lengthMm;
    private Double weightKg;

    @ManyToMany
    @JoinTable(
            name = "wood_product_category",
            joinColumns = @JoinColumn(name = "wood_product_id"),
            inverseJoinColumns = @JoinColumn(name = "wood_category_id")
    )
    private Set<WoodCategory> categories = new HashSet<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<WoodProductVariation> variations = new ArrayList<>();

    @NotNull
    private boolean active;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    private void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    private void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
