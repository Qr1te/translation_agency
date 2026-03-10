package com.qritiooo.translationagency.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class TranslatorLanguageId implements Serializable {

    @Column(name = "translator_id")
    private Integer translatorId;

    @Column(name = "language_id")
    private Integer languageId;
}
