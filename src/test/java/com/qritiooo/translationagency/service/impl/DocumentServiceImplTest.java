package com.qritiooo.translationagency.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.qritiooo.translationagency.cache.CacheKey;
import com.qritiooo.translationagency.cache.CacheManager;
import com.qritiooo.translationagency.dto.request.DocumentRequest;
import com.qritiooo.translationagency.dto.response.DocumentResponse;
import com.qritiooo.translationagency.model.Document;
import com.qritiooo.translationagency.model.Order;
import com.qritiooo.translationagency.repository.DocumentRepository;
import com.qritiooo.translationagency.repository.OrderRepository;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DocumentServiceImplTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CacheManager cacheManager;

    @InjectMocks
    private DocumentServiceImpl documentService;

    @Test
    void create_ShouldBindOrder_WhenOrderIdPresent() {
        DocumentRequest request = new DocumentRequest("Passport", 10, 7);
        Order order = new Order();
        order.setId(7);
        when(orderRepository.findById(7)).thenReturn(Optional.of(order));
        when(documentRepository.save(any(Document.class))).thenAnswer(invocation -> {
            Document document = invocation.getArgument(0);
            document.setId(1);
            return document;
        });

        DocumentResponse response = documentService.create(request);

        assertEquals(1, response.getId());
        assertEquals(7, response.getOrderId());
        verify(cacheManager).invalidate(Document.class, Order.class);
    }

    @Test
    void update_ShouldSaveDocument_WhenDocumentExists() {
        DocumentRequest request = new DocumentRequest("Contract", 5, null);
        Document document = new Document(2, "Old", 1, null);
        when(documentRepository.findById(2)).thenReturn(Optional.of(document));
        when(documentRepository.save(document)).thenReturn(document);

        DocumentResponse response = documentService.update(2, request);

        assertEquals("Contract", response.getType());
        assertEquals(5, response.getPages());
    }


    @Test
    void update_ShouldBindOrder_WhenOrderIdPresent() {
        DocumentRequest request = new DocumentRequest("Contract", 5, 8);
        Document document = new Document(22, "Old", 1, null);
        Order order = new Order();
        order.setId(8);
        when(documentRepository.findById(22)).thenReturn(Optional.of(document));
        when(orderRepository.findById(8)).thenReturn(Optional.of(order));
        when(documentRepository.save(document)).thenReturn(document);

        DocumentResponse response = documentService.update(22, request);

        assertEquals(8, response.getOrderId());
        assertEquals(1, order.getDocuments().size());
    }
    @Test
    void patch_ShouldChangeOnlyProvidedFields() {
        DocumentRequest request = new DocumentRequest(null, 9, null);
        Document document = new Document(3, "Invoice", 4, null);
        when(documentRepository.findById(3)).thenReturn(Optional.of(document));
        when(documentRepository.save(document)).thenReturn(document);

        DocumentResponse response = documentService.patch(3, request);

        assertEquals("Invoice", response.getType());
        assertEquals(9, response.getPages());
    }

    @Test
    void getById_ShouldReturnDocument_WhenFound() {
        Document document = new Document(4, "Report", 6, null);
        when(documentRepository.findById(4)).thenReturn(Optional.of(document));

        DocumentResponse response = documentService.getById(4);

        assertEquals(4, response.getId());
        assertEquals("Report", response.getType());
    }

    @Test
    @SuppressWarnings("unchecked")
    void getAll_ShouldFilterByOrderId_WhenProvided() {
        Document withOrder = new Document();
        withOrder.setId(10);
        withOrder.setType("Book");
        withOrder.setPages(100);
        Order order = new Order();
        order.setId(55);
        withOrder.setOrder(order);
        when(documentRepository.findByOrder_Id(55)).thenReturn(List.of(withOrder));
        when(cacheManager.computeIfAbsent(any(CacheKey.class), any(Supplier.class)))
                .thenAnswer(invocation -> {
                    Supplier<List<DocumentResponse>> supplier = invocation.getArgument(1);
                    return supplier.get();
                });

        List<DocumentResponse> result = documentService.getAll(55);

        assertEquals(1, result.size());
        assertEquals(55, result.getFirst().getOrderId());
    }

    @Test
    @SuppressWarnings("unchecked")
    void getAll_ShouldReturnAll_WhenOrderIdIsNull() {
        Document first = new Document(1, "A", 1, null);
        Document second = new Document(2, "B", 2, null);
        when(documentRepository.findAll()).thenReturn(List.of(first, second));
        when(cacheManager.computeIfAbsent(any(CacheKey.class), any(Supplier.class)))
                .thenAnswer(invocation -> {
                    Supplier<List<DocumentResponse>> supplier = invocation.getArgument(1);
                    return supplier.get();
                });

        List<DocumentResponse> result = documentService.getAll(null);

        assertEquals(2, result.size());
    }

    @Test
    void delete_ShouldUnbindAndDelete_WhenDocumentHasOrder() {
        Order order = new Order();
        order.setId(9);
        Document document = new Document(15, "Receipt", 2, order);
        order.getDocuments().add(document);
        when(documentRepository.findById(15)).thenReturn(Optional.of(document));

        documentService.delete(15);

        assertNull(document.getOrder());
        assertEquals(0, order.getDocuments().size());
        verify(documentRepository).delete(document);
        verify(cacheManager).invalidate(Document.class, Order.class);
    }


    @Test
    void create_ShouldSaveWithoutOrder_WhenOrderIdIsNull() {
        DocumentRequest request = new DocumentRequest("Passport", 10, null);
        when(documentRepository.save(any(Document.class))).thenAnswer(invocation -> {
            Document document = invocation.getArgument(0);
            document.setId(21);
            return document;
        });

        DocumentResponse response = documentService.create(request);

        assertEquals(21, response.getId());
        assertNull(response.getOrderId());
    }

    @Test
    void patch_ShouldRebindDocument_WhenOrderChanges() {
        Order oldOrder = new Order();
        oldOrder.setId(1);
        Order newOrder = new Order();
        newOrder.setId(2);
        Document document = new Document(30, "Invoice", 4, oldOrder);
        oldOrder.getDocuments().add(document);
        DocumentRequest request = new DocumentRequest(null, null, 2);

        when(documentRepository.findById(30)).thenReturn(Optional.of(document));
        when(orderRepository.findById(2)).thenReturn(Optional.of(newOrder));
        when(documentRepository.save(document)).thenReturn(document);

        DocumentResponse response = documentService.patch(30, request);

        assertEquals(2, response.getOrderId());
        assertEquals(0, oldOrder.getDocuments().size());
        assertEquals(1, newOrder.getDocuments().size());
    }

    @Test
    void patch_ShouldNotDuplicateDocument_WhenAlreadyBoundToSameOrder() {
        Order order = new Order();
        Document document = new Document(31, "Invoice", 4, order);
        order.getDocuments().add(document);
        DocumentRequest request = new DocumentRequest(null, null, 77);
        order.setId(77);

        when(documentRepository.findById(31)).thenReturn(Optional.of(document));
        when(orderRepository.findById(77)).thenReturn(Optional.of(order));
        when(documentRepository.save(document)).thenReturn(document);

        DocumentResponse response = documentService.patch(31, request);

        assertEquals(77, response.getOrderId());
        assertEquals(1, order.getDocuments().size());
        assertEquals(document, order.getDocuments().getFirst());
    }

    @Test
    void patch_ShouldBindNewOrder_WhenOldOrderHasNullId() {
        Order oldOrder = new Order();
        Order newOrder = new Order();
        newOrder.setId(88);
        Document document = new Document(32, "Invoice", 4, oldOrder);
        oldOrder.getDocuments().add(document);
        DocumentRequest request = new DocumentRequest(null, null, 88);

        when(documentRepository.findById(32)).thenReturn(Optional.of(document));
        when(orderRepository.findById(88)).thenReturn(Optional.of(newOrder));
        when(documentRepository.save(document)).thenReturn(document);

        DocumentResponse response = documentService.patch(32, request);

        assertEquals(88, response.getOrderId());
        assertEquals(1, oldOrder.getDocuments().size());
        assertEquals(1, newOrder.getDocuments().size());
        assertEquals(document, newOrder.getDocuments().getFirst());
    }    @Test
    void delete_ShouldDelete_WhenDocumentHasNoOrder() {
        Document document = new Document(16, "Receipt", 2, null);
        when(documentRepository.findById(16)).thenReturn(Optional.of(document));

        documentService.delete(16);

        verify(documentRepository).delete(document);
        verify(cacheManager).invalidate(Document.class, Order.class);
    }
    @Test
    void create_ShouldThrow_WhenOrderMissing() {
        DocumentRequest request = new DocumentRequest("Passport", 10, 99);
        when(orderRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> documentService.create(request));
    }
}




