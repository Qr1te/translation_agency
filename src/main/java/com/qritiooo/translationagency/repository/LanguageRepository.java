package com.qritiooo.translationagency.repository;

import com.qritiooo.translationagency.model.Language;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LanguageRepository extends JpaRepository<Language, Integer> {
    Optional<Language> findByCodeIgnoreCase(String code);
}
