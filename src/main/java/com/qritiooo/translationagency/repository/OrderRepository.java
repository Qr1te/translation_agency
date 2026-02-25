package com.qritiooo.translationagency.repository;

import com.qritiooo.translationagency.model.Order;
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
    List<Order> findAll();

    @Override
    @EntityGraph(attributePaths = {"client", "translator", "documents"})
    Optional<Order> findById(Integer id);

    @EntityGraph(attributePaths = {"client", "translator", "documents"})
    List<Order> findByStatus(String status);

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
            left join t.languages l
            where (:status is null or o.status = :status)
              and (:languageCode is null or lower(l.code) = lower(:languageCode))
            """)
    Page<Order> searchByNestedJpql(
            @Param("status") String status,
            @Param("languageCode") String languageCode,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"client", "translator", "documents"})
    @Query(
            value = """
                    select distinct o.*
                    from orders o
                    left join translators t on t.id = o.translator_id
                    left join translator_languages tl on tl.translator_id = t.id
                    left join languages l on l.id = tl.language_id
                    where (:status is null or o.status = :status)
                      and (:languageCode is null or lower(l.code) = lower(:languageCode))
                    """,
            countQuery = """
                    select count(distinct o.id)
                    from orders o
                    left join translators t on t.id = o.translator_id
                    left join translator_languages tl on tl.translator_id = t.id
                    left join languages l on l.id = tl.language_id
                    where (:status is null or o.status = :status)
                      and (:languageCode is null or lower(l.code) = lower(:languageCode))
                    """,
            nativeQuery = true
    )
    Page<Order> searchByNestedNative(
            @Param("status") String status,
            @Param("languageCode") String languageCode,
            Pageable pageable
    );
}


