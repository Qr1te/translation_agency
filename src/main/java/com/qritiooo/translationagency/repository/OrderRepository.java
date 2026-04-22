package com.qritiooo.translationagency.repository;

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

    Page<Order> findByStatus(OrderStatus status, Pageable pageable);

    @EntityGraph(attributePaths = {"client", "translator", "documents"})
    List<Order> findByClient_Id(Integer clientId);

    Page<Order> findByClient_Id(Integer clientId, Pageable pageable);

    @EntityGraph(attributePaths = {"client", "translator", "documents"})
    List<Order> findByTranslator_Id(Integer translatorId);

    Page<Order> findByTranslator_Id(Integer translatorId, Pageable pageable);

    @EntityGraph(attributePaths = {"client", "translator", "documents"})
    List<Order> findByTitle(String title);

    Page<Order> findByStatusAndClient_Id(
            OrderStatus status,
            Integer clientId,
            Pageable pageable
    );

    Page<Order> findByStatusAndTranslator_Id(
            OrderStatus status,
            Integer translatorId,
            Pageable pageable
    );

    Page<Order> findByClient_IdAndTranslator_Id(
            Integer clientId,
            Integer translatorId,
            Pageable pageable
    );

    Page<Order> findByStatusAndClient_IdAndTranslator_Id(
            OrderStatus status,
            Integer clientId,
            Integer translatorId,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {
            "client",
            "translator",
            "documents",
            "sourceLanguage",
            "targetLanguage"
    })
    @Query(
            value = """
                    select distinct o
                    from Order o
                    left join o.documents d
                    where (:status is null or o.status = :status)
                      and (:clientId is null or o.client.id = :clientId)
                      and (:translatorId is null or o.translator.id = :translatorId)
                    order by o.id
                    """,
            countQuery = """
                    select count(o)
                    from Order o
                    where (:status is null or o.status = :status)
                      and (:clientId is null or o.client.id = :clientId)
                      and (:translatorId is null or o.translator.id = :translatorId)
                    """
    )
    Page<Order> findAllByFilters(
            @Param("status") OrderStatus status,
            @Param("clientId") Integer clientId,
            @Param("translatorId") Integer translatorId,
            Pageable pageable
    );

    @Query("""
            select distinct o
            from Order o
            left join fetch o.client c
            left join fetch o.translator t
            left join fetch o.documents d
            left join fetch o.sourceLanguage sl
            left join fetch o.targetLanguage tlg
            left join t.translatorLanguages tl
            left join tl.language l
            where (:status is null or o.status = :status)
              and (:languageCode is null or upper(l.code) = upper(:languageCode))
            order by o.id
            """)
    List<Order> findAllWithDetailsByStatusAndTranslatorLanguage(
            @Param("status") OrderStatus status,
            @Param("languageCode") String languageCode
    );

    @Query("""
            select distinct o
            from Order o
            left join fetch o.client c
            left join fetch o.translator t
            left join fetch o.documents d
            left join fetch o.sourceLanguage sl
            left join fetch o.targetLanguage tlg
            where o.id in :ids
            order by o.id
            """)
    List<Order> findAllWithDetailsByIdIn(@Param("ids") List<Integer> ids);
}
