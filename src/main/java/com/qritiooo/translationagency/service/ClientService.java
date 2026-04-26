package com.qritiooo.translationagency.service;

import com.qritiooo.translationagency.dto.request.ClientRequest;
import com.qritiooo.translationagency.dto.response.ClientResponse;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClientService {
    ClientResponse create(ClientRequest request);

    List<ClientResponse> createBulkTransactional(List<ClientRequest> requests);

    List<ClientResponse> createBulkNonTransactional(List<ClientRequest> requests);

    ClientResponse update(Integer id, ClientRequest request);

    ClientResponse patch(Integer id, ClientRequest request);

    ClientResponse getById(Integer id);

    Page<ClientResponse> getAll(Pageable pageable);

    void delete(Integer id);
}

