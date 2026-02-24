package com.qritiooo.translationagency.service.impl;

import com.qritiooo.translationagency.dto.request.ClientRequest;
import com.qritiooo.translationagency.dto.response.ClientResponse;
import com.qritiooo.translationagency.mapper.ClientMapper;
import com.qritiooo.translationagency.model.Client;
import com.qritiooo.translationagency.repository.ClientRepository;
import com.qritiooo.translationagency.service.ClientService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

