package com.qritiooo.translation_agency.service;

import com.qritiooo.translation_agency.dto.request.ClientRequest;
import com.qritiooo.translation_agency.dto.response.ClientResponse;

import java.util.List;

public interface ClientService {
    ClientResponse create(ClientRequest request);
    ClientResponse update(Integer id, ClientRequest request);
    ClientResponse getById(Integer id);
    List<ClientResponse> getAll();
    void delete(Integer id);
}
