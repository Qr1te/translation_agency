package com.qritiooo.translationagency.service.impl;

import com.qritiooo.translationagency.config.CacheNames;
import com.qritiooo.translationagency.dto.request.ClientRequest;
import com.qritiooo.translationagency.dto.response.ClientResponse;
import com.qritiooo.translationagency.exception.NotFoundException;
import com.qritiooo.translationagency.mapper.ClientMapper;
import com.qritiooo.translationagency.model.Client;
import com.qritiooo.translationagency.repository.ClientRepository;
import com.qritiooo.translationagency.repository.OrderRepository;
import com.qritiooo.translationagency.service.ClientService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = CacheNames.CLIENTS_ALL)
public class ClientServiceImpl implements ClientService {

    private final ClientRepository repo;
    private final OrderRepository orderRepo;

    @Override
    @CacheEvict(allEntries = true)
    public ClientResponse create(ClientRequest request) {
        Client c = new Client();
        ClientMapper.updateEntity(c, request);
        return saveAndMap(c);
    }

    @Override
    @CacheEvict(allEntries = true)
    public ClientResponse update(Integer id, ClientRequest request) {
        Client c = getClientOrThrow(id);
        ClientMapper.updateEntity(c, request);
        return saveAndMap(c);
    }

    @Override
    @CacheEvict(allEntries = true)
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
    @Cacheable(sync = true)
    public List<ClientResponse> getAll() {
        return repo.findAll().stream().map(ClientMapper::toResponse).toList();
    }

    @Override
    @Transactional
    @CacheEvict(allEntries = true)
    public void delete(Integer id) {
        Client client = getClientOrThrow(id);
        orderRepo.findByClient_Id(id).forEach(order -> order.setClient(null));
        repo.delete(client);
    }

    private Client getClientOrThrow(Integer id) {
        return repo.findById(id).orElseThrow(
                () -> new NotFoundException("Client not found with id: " + id)
        );
    }

    private ClientResponse saveAndMap(Client client) {
        return ClientMapper.toResponse(repo.save(client));
    }
}

