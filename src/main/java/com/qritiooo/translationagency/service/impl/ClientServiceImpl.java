package com.qritiooo.translationagency.service.impl;

import com.qritiooo.translationagency.cache.CacheKey;
import com.qritiooo.translationagency.cache.CacheManager;
import com.qritiooo.translationagency.dto.request.ClientRequest;
import com.qritiooo.translationagency.dto.response.ClientResponse;
import com.qritiooo.translationagency.exception.BadRequestException;
import com.qritiooo.translationagency.exception.ConflictException;
import com.qritiooo.translationagency.exception.NotFoundException;
import com.qritiooo.translationagency.mapper.ClientMapper;
import com.qritiooo.translationagency.model.Client;
import com.qritiooo.translationagency.model.Order;
import com.qritiooo.translationagency.repository.ClientRepository;
import com.qritiooo.translationagency.repository.OrderRepository;
import com.qritiooo.translationagency.service.ClientService;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository repo;
    private final OrderRepository orderRepo;
    private final CacheManager cacheManager;

    @Override
    public ClientResponse create(ClientRequest request) {
        Client c = new Client();
        ClientMapper.updateEntity(c, request);
        return saveAndMap(c);
    }

    @Override
    @Transactional
    public List<ClientResponse> createBulkTransactional(List<ClientRequest> requests) {
        return createBulkInternal(requests);
    }

    @Override
    public List<ClientResponse> createBulkNonTransactional(List<ClientRequest> requests) {
        return createBulkInternal(requests);
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
        CacheKey key = new CacheKey(Client.class, "getAll");
        return cacheManager.computeIfAbsent(
                key,
                () -> repo.findAll().stream().map(ClientMapper::toResponse).toList()
        );
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        Client client = getClientOrThrow(id);
        orderRepo.findByClient_Id(id).forEach(order -> order.setClient(null));
        repo.delete(client);
        cacheManager.invalidate(Client.class, Order.class);
    }

    private Client getClientOrThrow(Integer id) {
        return repo.findById(id).orElseThrow(
                () -> new NotFoundException("Client not found with id: " + id)
        );
    }

    private ClientResponse saveAndMap(Client client) {
        ensureEmailIsUnique(client);
        ClientResponse response = ClientMapper.toResponse(repo.save(client));
        cacheManager.invalidate(Client.class, Order.class);
        return response;
    }

    private List<ClientResponse> createBulkInternal(List<ClientRequest> requests) {
        List<ClientRequest> validatedRequests = Optional.ofNullable(requests)
                .filter(list -> !list.isEmpty())
                .orElseThrow(() -> new BadRequestException("Bulk request must not be empty"));

        ensureNoDuplicateEmailsInPayload(validatedRequests);

        return validatedRequests.stream()
                .map(this::mapToClient)
                .map(this::saveAndMap)
                .toList();
    }

    private Client mapToClient(ClientRequest request) {
        Client client = new Client();
        ClientMapper.updateEntity(client, request);
        return client;
    }

    private void ensureNoDuplicateEmailsInPayload(List<ClientRequest> requests) {
        Set<String> uniqueEmails = new LinkedHashSet<>();
        List<String> duplicates = requests.stream()
                .map(ClientRequest::getEmail)
                .filter(Objects::nonNull)
                .map(email -> email.trim().toLowerCase(Locale.ROOT))
                .filter(email -> !uniqueEmails.add(email))
                .distinct()
                .toList();

        if (!duplicates.isEmpty()) {
            throw new ConflictException("Duplicate emails in bulk request: " + duplicates);
        }
    }

    private void ensureEmailIsUnique(Client client) {
        String email = client.getEmail();
        if (email == null || email.isBlank()) {
            return;
        }

        boolean exists = client.getId() == null
                ? repo.existsByEmailIgnoreCase(email)
                : repo.existsByEmailIgnoreCaseAndIdNot(email, client.getId());

        if (exists) {
            throw new ConflictException("Client with email already exists: " + email);
        }
    }
}

