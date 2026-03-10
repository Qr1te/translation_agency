package com.qritiooo.translationagency.repository;

import com.qritiooo.translationagency.model.Translator;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TranslatorRepository extends JpaRepository<Translator, Integer> {

    @Override
    @EntityGraph(attributePaths = {"translatorLanguages", "translatorLanguages.language"})
    List<Translator> findAll();

    @Override
    @EntityGraph(attributePaths = {"translatorLanguages", "translatorLanguages.language"})
    Optional<Translator> findById(Integer id);
}

