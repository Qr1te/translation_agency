package com.qritiooo.translationagency.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
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

    private String firstName;
    private String lastName;

    private BigDecimal ratePerPage;

    @OneToMany(mappedBy = "translator", fetch = FetchType.LAZY)
    private List<Order> orders = new ArrayList<>();

    @OneToMany(
            mappedBy = "translator",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<TranslatorLanguage> translatorLanguages = new ArrayList<>();
}
