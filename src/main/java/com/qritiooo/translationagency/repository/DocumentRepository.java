package com.qritiooo.translationagency.repository;

import com.qritiooo.translationagency.model.Document;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Integer> {
    List<Document> findByOrder_Id(Integer orderId);
}

