package com.qritiooo.translationagency.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "translators")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Translator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String fullName;

    private BigDecimal ratePerPage;

    @OneToMany(mappedBy = "translator", fetch = FetchType.LAZY)
    private List<Order> orders = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY, targetClass = Language.class)
    @CollectionTable(
            name = "translator_languages",
            joinColumns = @JoinColumn(name = "translator_id")
    )
    @Column(name = "language_code", nullable = false)
    @Enumerated(EnumType.STRING)
    private Set<Language> languages = new HashSet<>();

    @OneToMany(
            mappedBy = "translator",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<TranslatorTool> translatorTools = new ArrayList<>();
}
