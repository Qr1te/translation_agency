package com.qritiooo.translationagency.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.qritiooo.translationagency.cache.CacheKey;
import com.qritiooo.translationagency.cache.CacheManager;
import com.qritiooo.translationagency.dto.request.ClientRequest;
import com.qritiooo.translationagency.dto.response.ClientResponse;
import com.qritiooo.translationagency.exception.BadRequestException;
import com.qritiooo.translationagency.exception.ConflictException;
import com.qritiooo.translationagency.exception.NotFoundException;
import com.qritiooo.translationagency.model.Client;
import com.qritiooo.translationagency.model.Order;
import com.qritiooo.translationagency.repository.ClientRepository;
import com.qritiooo.translationagency.repository.OrderRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ClientServiceImplTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Validator validator;

    @InjectMocks
    private ClientServiceImpl clientService;

    @BeforeEach
    void setUpValidator() {
        org.mockito.Mockito.lenient().when(validator.validate(any(ClientRequest.class))).thenReturn(Collections.emptySet());
    }

    @Test
    void create_ShouldSaveClient_WhenEmailIsUnique() {
        ClientRequest request = new ClientRequest("Anna", "Kovalenko", "anna@test.com");
        when(clientRepository.existsByEmailIgnoreCase("anna@test.com")).thenReturn(false);
        when(clientRepository.saveAndFlush(any(Client.class))).thenAnswer(invocation -> {
            Client client = invocation.getArgument(0);
            client.setId(101);
            return client;
        });

        ClientResponse result = clientService.create(request);

        assertEquals(101, result.getId());
        assertEquals("anna@test.com", result.getEmail());
        verify(cacheManager).invalidate(Client.class, Order.class);
    }

    @Test
    void create_ShouldSaveClient_WhenEmailIsNull() {
        ClientRequest request = new ClientRequest("Anna", "Kovalenko", null);
        when(clientRepository.saveAndFlush(any(Client.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ClientResponse result = clientService.create(request);

        assertNull(result.getEmail());
        verify(clientRepository, never()).existsByEmailIgnoreCase(anyString());
    }


    @Test
    void create_ShouldSaveClient_WhenEmailIsBlank() {
        ClientRequest request = new ClientRequest("Anna", "Kovalenko", "   ");
        when(clientRepository.saveAndFlush(any(Client.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ClientResponse result = clientService.create(request);

        assertEquals("   ", result.getEmail());
        verify(clientRepository, never()).existsByEmailIgnoreCase(anyString());
    }
    @Test
    void create_ShouldThrowConflict_WhenEmailAlreadyExists() {
        ClientRequest request = new ClientRequest("Anna", "Kovalenko", "anna@test.com");
        when(clientRepository.existsByEmailIgnoreCase("anna@test.com")).thenReturn(true);

        assertThrows(ConflictException.class, () -> clientService.create(request));
        verify(clientRepository, never()).saveAndFlush(any(Client.class));
    }

    @Test
    void createBulkTransactional_ShouldCreateAllClients_WhenPayloadIsValid() {
        ClientRequest first = new ClientRequest("Anna", "Kovalenko", "anna@test.com");
        ClientRequest second = new ClientRequest("Ivan", "Petrov", "ivan@test.com");

        when(clientRepository.existsByEmailIgnoreCase(anyString())).thenReturn(false);
        AtomicInteger seq = new AtomicInteger(1);
        when(clientRepository.saveAndFlush(any(Client.class))).thenAnswer(invocation -> {
            Client client = invocation.getArgument(0);
            client.setId(seq.getAndIncrement());
            return client;
        });

        List<ClientResponse> result = clientService.createBulkTransactional(List.of(first, second));

        assertEquals(2, result.size());
        assertEquals("anna@test.com", result.getFirst().getEmail());
        assertEquals("ivan@test.com", result.get(1).getEmail());
        verify(clientRepository, times(2)).saveAndFlush(any(Client.class));
        verify(cacheManager, times(2)).invalidate(Client.class, Order.class);
    }

    @Test
    void createBulkNonTransactional_ShouldCreateAllClients_WhenPayloadIsValid() {
        ClientRequest first = new ClientRequest("Anna", "Kovalenko", "anna@test.com");
        ClientRequest second = new ClientRequest("Ivan", "Petrov", "ivan@test.com");
        when(clientRepository.existsByEmailIgnoreCase(anyString())).thenReturn(false);
        when(clientRepository.saveAndFlush(any(Client.class))).thenAnswer(invocation -> invocation.getArgument(0));

        List<ClientResponse> result = clientService.createBulkNonTransactional(List.of(first, second));

        assertEquals(2, result.size());
        verify(clientRepository, times(2)).saveAndFlush(any(Client.class));
    }

    @Test
    void createBulkTransactional_ShouldThrowConflict_WhenPayloadHasDuplicateEmails() {
        ClientRequest first = new ClientRequest("Anna", "Kovalenko", "anna@test.com");
        ClientRequest second = new ClientRequest("Ann", "K", "ANNA@test.com");
        List<ClientRequest> requests = List.of(first, second);
        when(clientRepository.existsByEmailIgnoreCase(anyString())).thenReturn(false, true);
        when(clientRepository.saveAndFlush(any(Client.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertThrows(
                ConflictException.class,
                () -> clientService.createBulkTransactional(requests)
        );
        verify(clientRepository, times(1)).saveAndFlush(any(Client.class));
    }

    @Test
    void createBulkTransactional_ShouldThrowBadRequest_WhenPayloadIsEmpty() {
        List<ClientRequest> requests = List.of();

        assertThrows(
                BadRequestException.class,
                () -> clientService.createBulkTransactional(requests)
        );
        verify(clientRepository, never()).saveAndFlush(any(Client.class));
    }

    @Test
    void createBulkTransactional_ShouldThrowBadRequest_WhenPayloadIsNull() {
        assertThrows(
                BadRequestException.class,
                () -> clientService.createBulkTransactional(null)
        );
        verify(clientRepository, never()).saveAndFlush(any(Client.class));
    }

    @Test
    void createBulkNonTransactional_ShouldStopOnConflictAfterFirstSave() {
        ClientRequest first = new ClientRequest("Anna", "Kovalenko", "anna@test.com");
        ClientRequest second = new ClientRequest("Ivan", "Petrov", "ivan@test.com");
        List<ClientRequest> requests = List.of(first, second);

        when(clientRepository.existsByEmailIgnoreCase(anyString())).thenReturn(false, true);
        when(clientRepository.saveAndFlush(any(Client.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertThrows(
                ConflictException.class,
                () -> clientService.createBulkNonTransactional(requests)
        );
        verify(clientRepository, times(1)).saveAndFlush(any(Client.class));
    }

    @Test
    void update_ShouldSave_WhenClientExistsAndEmailIsUniqueForOtherRecords() {
        Client existing = new Client(10, "Old", "Name", "old@test.com", List.of());
        ClientRequest request = new ClientRequest("New", "Name", "new@test.com");
        when(clientRepository.findById(10)).thenReturn(Optional.of(existing));
        when(clientRepository.existsByEmailIgnoreCaseAndIdNot("new@test.com", 10)).thenReturn(false);
        when(clientRepository.saveAndFlush(existing)).thenReturn(existing);

        ClientResponse result = clientService.update(10, request);

        assertEquals(10, result.getId());
        assertEquals("new@test.com", result.getEmail());
        verify(clientRepository).existsByEmailIgnoreCaseAndIdNot("new@test.com", 10);
    }

    @Test
    void update_ShouldThrowConflict_WhenEmailAlreadyUsedByAnotherClient() {
        Client existing = new Client(10, "Old", "Name", "old@test.com", List.of());
        ClientRequest request = new ClientRequest("New", "Name", "new@test.com");
        when(clientRepository.findById(10)).thenReturn(Optional.of(existing));
        when(clientRepository.existsByEmailIgnoreCaseAndIdNot("new@test.com", 10)).thenReturn(true);

        assertThrows(ConflictException.class, () -> clientService.update(10, request));
        verify(clientRepository, never()).saveAndFlush(any(Client.class));
    }

    @Test
    void patch_ShouldSave_WhenClientExists() {
        Client existing = new Client(11, "A", "B", "before@test.com", List.of());
        ClientRequest request = new ClientRequest("C", "D", "after@test.com");
        when(clientRepository.findById(11)).thenReturn(Optional.of(existing));
        when(clientRepository.existsByEmailIgnoreCaseAndIdNot("after@test.com", 11)).thenReturn(false);
        when(clientRepository.saveAndFlush(existing)).thenReturn(existing);

        ClientResponse result = clientService.patch(11, request);

        assertEquals(11, result.getId());
        assertEquals("after@test.com", result.getEmail());
        verify(clientRepository).saveAndFlush(existing);
    }

    @Test
    void getById_ShouldReturnClient_WhenFound() {
        Client existing = new Client(5, "A", "B", "a@test.com", List.of());
        when(clientRepository.findById(5)).thenReturn(Optional.of(existing));

        ClientResponse result = clientService.getById(5);

        assertEquals(5, result.getId());
        assertEquals("a@test.com", result.getEmail());
    }

    @Test
    void getById_ShouldThrowNotFound_WhenMissing() {
        when(clientRepository.findById(404)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> clientService.getById(404));
    }

    @Test
    @SuppressWarnings("unchecked")
    void getAll_ShouldUseCacheManagerSupplier() {
        Client c1 = new Client(1, "A", "B", "a@test.com", List.of());
        Client c2 = new Client(2, "C", "D", "c@test.com", List.of());
        when(cacheManager.computeIfAbsent(any(CacheKey.class), any(Supplier.class))).thenAnswer(invocation -> {
            Supplier<List<ClientResponse>> supplier = invocation.getArgument(1);
            return supplier.get();
        });
        when(clientRepository.findAll()).thenReturn(List.of(c1, c2));

        List<ClientResponse> result = clientService.getAll();

        assertEquals(2, result.size());
        verify(cacheManager).computeIfAbsent(any(CacheKey.class), any(Supplier.class));
    }

    @Test
    void delete_ShouldNullifyOrdersAndDeleteClient_WhenFound() {
        Client client = new Client(20, "A", "B", "a@test.com", List.of());
        Order order1 = new Order();
        order1.setClient(client);
        Order order2 = new Order();
        order2.setClient(client);
        when(clientRepository.findById(20)).thenReturn(Optional.of(client));
        when(orderRepository.findByClient_Id(20)).thenReturn(List.of(order1, order2));

        clientService.delete(20);

        assertNull(order1.getClient());
        assertNull(order2.getClient());
        verify(clientRepository).delete(client);
        verify(cacheManager).invalidate(Client.class, Order.class);
    }

    @Test
    void delete_ShouldThrowNotFound_WhenClientMissing() {
        when(clientRepository.findById(21)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> clientService.delete(21));
        verify(clientRepository, never()).delete(any(Client.class));
    }
}




