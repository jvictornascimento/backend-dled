package br.com.dled.dledbackend.modules.categories.domain;

import br.com.dled.dledbackend.modules.products.domain.Product;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "CATEGORY")
@Data
@NoArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    @EqualsAndHashCode.Exclude
    private String name;

    // Categoria pai (null = categoria raiz)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @EqualsAndHashCode.Exclude
    private Category parent;

    // Subcategorias
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    private Set<Category> children = new HashSet<>();

    @ManyToMany(mappedBy = "categories")
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private Set<Product> products = new HashSet<>();

    @EqualsAndHashCode.Exclude
    private String imgUrl;

    @NotNull
    @EqualsAndHashCode.Exclude
    private boolean active;

}
