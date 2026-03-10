package com.qritiooo.translationagency.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "translator_languages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TranslatorLanguage {

    @EmbeddedId
    private TranslatorLanguageId id = new TranslatorLanguageId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("translatorId")
    @JoinColumn(name = "translator_id", nullable = false)
    private Translator translator;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("languageId")
    @JoinColumn(name = "language_id", nullable = false)
    private Language language;

    @Enumerated(EnumType.STRING)
    private LanguageProficiencyLevel proficiencyLevel;
}
