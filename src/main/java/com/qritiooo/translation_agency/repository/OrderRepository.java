package com.qritiooo.translation_agency.repository;

import com.qritiooo.translation_agency.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {

    List<Order> findByStatus(String status);
    List<Order> findByClient_Id(Integer clientId);
    List<Order> findByTranslator_Id(Integer translatorId);
}

