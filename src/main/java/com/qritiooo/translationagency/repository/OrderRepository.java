package com.qritiooo.translationagency.repository;

import com.qritiooo.translationagency.model.Language;
import com.qritiooo.translationagency.model.Order;
import com.qritiooo.translationagency.model.OrderStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Order, Integer> {

    @Override
    @EntityGraph(attributePaths = {"client", "translator", "documents"})
    List<Order> findAll();

    @Override
    @EntityGraph(attributePaths = {"client", "translator", "documents"})
    Optional<Order> findById(Integer id);

    @EntityGraph(attributePaths = {"client", "translator", "documents"})
    List<Order> findByStatus(OrderStatus status);

    @EntityGraph(attributePaths = {"client", "translator", "documents"})
    List<Order> findByClient_Id(Integer clientId);

    @EntityGraph(attributePaths = {"client", "translator", "documents"})
    List<Order> findByTranslator_Id(Integer translatorId);

    @EntityGraph(attributePaths = {"client", "translator", "documents"})
    List<Order> findByTitle(String title);

    @EntityGraph(attributePaths = {"client", "translator", "documents"})
    @Query("""
            select distinct o
            from Order o
            left join o.translator t
            left join t.translatorLanguages tl
            left join tl.language l
            where (:status is null or o.status = :status)
              and (:language is null or l = :language)
            """)
    Page<Order> findByStatusAndTranslatorLanguageJpql(
            @Param("status") OrderStatus status,
            @Param("language") Language language,
            Pageable pageable
    );

    @Query(
            value = """
                    select distinct o.*
                    from orders o
                    left join translators t on t.id = o.translator_id
                    left join translator_languages tl on tl.translator_id = t.id
                    left join languages l on l.id = tl.language_id
                    where (:status is null or o.status = :status)
                      and (:languageCode is null or l.code = :languageCode)
                    """,
            countQuery = """
                    select count(distinct o.id)
                    from orders o
                    left join translators t on t.id = o.translator_id
                    left join translator_languages tl on tl.translator_id = t.id
                    left join languages l on l.id = tl.language_id
                    where (:status is null or o.status = :status)
                      and (:languageCode is null or l.code = :languageCode)
                    """,
            nativeQuery = true
    )
    Page<Order> findByStatusAndTranslatorLanguageNative(
            @Param("status") String status,
            @Param("languageCode") String languageCode,
            Pageable pageable
    );
}
