package com.qritiooo.translation_agency.service.impl;

import com.qritiooo.translation_agency.dto.request.ClientRequest;
import com.qritiooo.translation_agency.dto.response.ClientResponse;
import com.qritiooo.translation_agency.mapper.ClientMapper;
import com.qritiooo.translation_agency.model.Client;
import com.qritiooo.translation_agency.repository.ClientRepository;
import com.qritiooo.translation_agency.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository repo;

    @Override
    public ClientResponse create(ClientRequest request) {
        Client c = new Client();
        ClientMapper.updateEntity(c, request);
        return ClientMapper.toResponse(repo.save(c));
    }

    @Override
    public ClientResponse update(Integer id, ClientRequest request) {
        Client c = repo.findById(id).orElseThrow();
        ClientMapper.updateEntity(c, request);
        return ClientMapper.toResponse(repo.save(c));
    }

    @Override
    public ClientResponse getById(Integer id) {
        return ClientMapper.toResponse(repo.findById(id).orElseThrow());
    }

    @Override
    public List<ClientResponse> getAll() {
        return repo.findAll().stream().map(ClientMapper::toResponse).toList();
    }

    @Override
    public void delete(Integer id) {
        repo.deleteById(id);
    }
}
