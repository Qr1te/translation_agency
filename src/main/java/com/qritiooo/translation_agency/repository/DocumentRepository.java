package com.qritiooo.translation_agency.repository;

import com.qritiooo.translation_agency.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Integer> {
    List<Document> findByOrder_Id(Integer orderId);
}
