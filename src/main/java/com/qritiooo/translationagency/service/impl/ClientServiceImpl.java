package com.qritiooo.translationagency.service.impl;

import com.qritiooo.translationagency.cache.CacheStore;
import com.qritiooo.translationagency.cache.CacheableService;
import com.qritiooo.translationagency.cache.HashMapCacheStore;
import com.qritiooo.translationagency.dto.request.ClientRequest;
import com.qritiooo.translationagency.dto.response.ClientResponse;
import com.qritiooo.translationagency.exception.NotFoundException;
import com.qritiooo.translationagency.mapper.ClientMapper;
import com.qritiooo.translationagency.model.Client;
import com.qritiooo.translationagency.repository.ClientRepository;
import com.qritiooo.translationagency.service.ClientService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService, CacheableService {

    private final ClientRepository repo;
    private final CacheStore cacheStore = new HashMapCacheStore();

    @Override
    public ClientResponse create(ClientRequest request) {
        Client c = new Client();
        ClientMapper.updateEntity(c, request);
        return saveAndMap(c);
    }

    @Override
    public ClientResponse update(Integer id, ClientRequest request) {
        Client c = getClientOrThrow(id);
        ClientMapper.updateEntity(c, request);
        return saveAndMap(c);
    }

    @Override
    public ClientResponse patch(Integer id, ClientRequest request) {
        Client c = getClientOrThrow(id);
        ClientMapper.patchEntity(c, request);
        return saveAndMap(c);
    }

    @Override
    public ClientResponse getById(Integer id) {
        return ClientMapper.toResponse(getClientOrThrow(id));
    }

    @Override
    public List<ClientResponse> getAll() {
        return getOrLoad(
                "getAll",
                () -> repo.findAll().stream().map(ClientMapper::toResponse).toList()
        );
    }

    @Override
    public void delete(Integer id) {
        repo.deleteById(id);
        invalidateCache();
    }

    @Override
    public String getCacheNamespace() {
        return "client";
    }

    @Override
    public CacheStore getCacheStore() {
        return cacheStore;
    }

    private Client getClientOrThrow(Integer id) {
        return repo.findById(id).orElseThrow(
                () -> new NotFoundException("Client not found with id: " + id)
        );
    }

    private ClientResponse saveAndMap(Client client) {
        ClientResponse response = ClientMapper.toResponse(repo.save(client));
        invalidateCache();
        return response;
    }
}

