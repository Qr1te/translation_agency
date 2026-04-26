package com.qritiooo.translationagency.repository;

import com.qritiooo.translationagency.model.Document;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Integer> {
    List<Document> findByOrder_Id(Integer orderId);

    @EntityGraph(attributePaths = {"order"})
    Page<Document> findByOrder_Id(Integer orderId, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"order"})
    Page<Document> findAll(Pageable pageable);
}

