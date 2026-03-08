package com.qritiooo.translationagency.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "translator_tools")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TranslatorTool {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private LocalDate licenseExpiryDate;

    @Enumerated(EnumType.STRING)
    private ProficiencyLevel proficiencyLevel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "translator_id", nullable = false)
    private Translator translator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tool_id", nullable = false)
    private CatTool tool;
}
