package com.qritiooo.translationagency.service;

import com.qritiooo.translationagency.dto.request.ClientRequest;
import com.qritiooo.translationagency.dto.response.ClientResponse;
import java.util.List;

public interface ClientService {
    ClientResponse create(ClientRequest request);

    ClientResponse update(Integer id, ClientRequest request);

    ClientResponse getById(Integer id);

    List<ClientResponse> getAll();

    void delete(Integer id);
}

