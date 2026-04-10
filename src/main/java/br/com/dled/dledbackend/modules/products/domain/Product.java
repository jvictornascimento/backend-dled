package br.com.dled.dledbackend.modules.products.domain;

import br.com.dled.dledbackend.modules.categories.domain.Category;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String name;
    private int codigoRusso;
    private int codigoMali;
    @ManyToMany
    @JoinTable(
            name = "product_category",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories = new HashSet<>();
    @Enumerated(EnumType.STRING)
    @NotNull
    private ProductStatus status;
    private String descricao;
    @Column(columnDefinition = "TEXT")
    private String restricoesDeUso;
    @Column(columnDefinition = "TEXT")
    private String recomendacoesDeUso;
    @Column(columnDefinition = "TEXT")
    private String observacoesEspeciais;
    @Column(columnDefinition = "TEXT")
    private String observacoesInternas;
    private int ip;
    private int amper;
    private int watts;
    @NotNull
    private long gtin;
    private int volt;
    private String imgUrl;
    private String imgPublicId;
    private Double price;
    private String iconUrl;
    private String iconPublicId;
    private String temperaturaDeCor;
    private int ledsPorMetro;
    private String tipoLed;
    private String fluxoLuminoso;
    private String indiceDeReproducaoDeCor;
    private int quantidePorRolo;
    private int sessaoDeCorte;
    private int espessura;
    private boolean blindada;
    private String dimensao;
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<ProductGalleryImage> galleryImages = new ArrayList<>();
    private LocalDateTime createdAt;
    @NotNull
    private boolean active;

    @PrePersist
    private void prePersist(){
        createdAt = LocalDateTime.now();
    }

}
