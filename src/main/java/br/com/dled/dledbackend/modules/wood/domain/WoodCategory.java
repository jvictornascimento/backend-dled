package br.com.dled.dledbackend.modules.wood.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "wood_category")
@Data
@NoArgsConstructor
public class WoodCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private String name;
    private String imgCategoryUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @EqualsAndHashCode.Exclude
    private WoodCategory parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    private Set<WoodCategory> children = new HashSet<>();

    @ManyToMany(mappedBy = "categories")
    @EqualsAndHashCode.Exclude
    private Set<WoodProduct> products = new HashSet<>();

    @NotNull
    private boolean active;
}
