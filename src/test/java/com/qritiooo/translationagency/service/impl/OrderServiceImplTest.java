package com.qritiooo.translationagency.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.qritiooo.translationagency.cache.CacheKey;
import com.qritiooo.translationagency.cache.CacheManager;
import com.qritiooo.translationagency.dto.request.OrderRequest;
import com.qritiooo.translationagency.dto.response.OrderResponse;
import com.qritiooo.translationagency.exception.BadRequestException;
import com.qritiooo.translationagency.exception.NotFoundException;
import com.qritiooo.translationagency.model.Client;
import com.qritiooo.translationagency.model.Document;
import com.qritiooo.translationagency.model.Language;
import com.qritiooo.translationagency.model.Order;
import com.qritiooo.translationagency.model.OrderStatus;
import com.qritiooo.translationagency.model.Translator;
import com.qritiooo.translationagency.model.TranslatorLanguage;
import com.qritiooo.translationagency.model.TranslatorLanguageId;
import com.qritiooo.translationagency.repository.ClientRepository;
import com.qritiooo.translationagency.repository.DocumentRepository;
import com.qritiooo.translationagency.repository.LanguageRepository;
import com.qritiooo.translationagency.repository.OrderRepository;
import com.qritiooo.translationagency.repository.TranslatorRepository;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private TranslatorRepository translatorRepository;

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private LanguageRepository languageRepository;

    @Mock
    private CacheManager cacheManager;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void create_ShouldPersistRelationsAndDocuments() {
        OrderRequest request = new OrderRequest(
                "New Order",
                OrderStatus.NEW,
                1,
                2,
                3,
                4,
                List.of(11, 12)
        );
        Language source = new Language(1, "EN", "English");
        Language target = new Language(2, "RU", "Russian");
        Client client = new Client();
        client.setId(3);
        Translator translator = translatorWithLanguages(4, source, target);
        Document first = new Document(11, "Doc1", 1, null);
        Document second = new Document(12, "Doc2", 2, null);

        when(languageRepository.findById(1)).thenReturn(Optional.of(source));
        when(languageRepository.findById(2)).thenReturn(Optional.of(target));
        when(clientRepository.findById(3)).thenReturn(Optional.of(client));
        when(translatorRepository.findById(4)).thenReturn(Optional.of(translator));
        when(documentRepository.findById(11)).thenReturn(Optional.of(first));
        when(documentRepository.findById(12)).thenReturn(Optional.of(second));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            if (order.getId() == null) {
                order.setId(100);
            }
            return order;
        });
        when(documentRepository.findByOrder_Id(100)).thenReturn(List.of(first, second));

        OrderResponse response = orderService.create(request);

        assertEquals(100, response.getId());
        assertEquals(2, response.getDocumentIds().size());
        verify(documentRepository).save(first);
        verify(documentRepository).save(second);
        verify(cacheManager).invalidate(Order.class);
    }

    @Test
    void update_ShouldUpdateOrderAndRelations() {
        Order existing = new Order();
        existing.setId(50);
        OrderRequest request = new OrderRequest(
                "Updated",
                OrderStatus.IN_PROGRESS,
                1,
                2,
                3,
                4,
                List.of()
        );
        Language source = new Language(1, "EN", "English");
        Language target = new Language(2, "RU", "Russian");
        Client client = new Client();
        client.setId(3);
        Translator translator = translatorWithLanguages(4, source, target);

        when(orderRepository.findById(50)).thenReturn(Optional.of(existing));
        when(orderRepository.save(existing)).thenReturn(existing);
        when(languageRepository.findById(1)).thenReturn(Optional.of(source));
        when(languageRepository.findById(2)).thenReturn(Optional.of(target));
        when(clientRepository.findById(3)).thenReturn(Optional.of(client));
        when(translatorRepository.findById(4)).thenReturn(Optional.of(translator));
        when(documentRepository.findByOrder_Id(50)).thenReturn(List.of());

        OrderResponse response = orderService.update(50, request);

        assertEquals("Updated", response.getTitle());
        assertEquals(OrderStatus.IN_PROGRESS, response.getStatus());
        verify(cacheManager).invalidate(Order.class);
    }

    @Test
    void patch_ShouldUpdateOnlyProvidedFieldsAndAssignDocuments() {
        Order existing = new Order();
        existing.setId(60);
        existing.setTitle("Old");
        existing.setStatus(OrderStatus.NEW);
        Document document = new Document(15, "Type", 1, null);
        OrderRequest request = new OrderRequest(
                "Patched",
                null,
                null,
                null,
                null,
                null,
                List.of(15)
        );

        when(orderRepository.findById(60)).thenReturn(Optional.of(existing));
        when(orderRepository.save(existing)).thenReturn(existing);
        when(documentRepository.findById(15)).thenReturn(Optional.of(document));
        when(documentRepository.findByOrder_Id(60)).thenReturn(List.of(document));

        OrderResponse response = orderService.patch(60, request);

        assertEquals("Patched", response.getTitle());
        assertEquals(OrderStatus.NEW, response.getStatus());
        verify(documentRepository).save(document);
    }

    @Test
    void getById_ShouldReturnOrder_WhenFound() {
        Order order = new Order();
        order.setId(9);
        order.setTitle("ById");
        order.setStatus(OrderStatus.NEW);
        when(orderRepository.findById(9)).thenReturn(Optional.of(order));

        OrderResponse response = orderService.getById(9);

        assertEquals(9, response.getId());
    }

    @Test
    @SuppressWarnings("unchecked")
    void getByTitle_ShouldReturnFirstResult_WhenExists() {
        Order order = new Order();
        order.setId(7);
        order.setTitle("Title");
        order.setStatus(OrderStatus.NEW);
        when(cacheManager.computeIfAbsent(any(CacheKey.class), any(Supplier.class)))
                .thenAnswer(invocation -> {
                    Supplier<OrderResponse> supplier = invocation.getArgument(1);
                    return supplier.get();
                });
        when(orderRepository.findByTitle("Title")).thenReturn(List.of(order));

        OrderResponse response = orderService.getByTitle("Title");

        assertEquals(7, response.getId());
    }

    @Test
    @SuppressWarnings("unchecked")
    void getByTitle_ShouldThrowNotFound_WhenAbsent() {
        when(cacheManager.computeIfAbsent(any(CacheKey.class), any(Supplier.class)))
                .thenAnswer(invocation -> {
                    Supplier<OrderResponse> supplier = invocation.getArgument(1);
                    return supplier.get();
                });
        when(orderRepository.findByTitle("Missing")).thenReturn(List.of());

        assertThrows(NotFoundException.class, () -> orderService.getByTitle("Missing"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void getAll_ShouldReturnMappedPageFromCacheSupplier() {
        Order order = new Order();
        order.setId(1);
        order.setTitle("Paged");
        order.setStatus(OrderStatus.NEW);
        PageRequest pageable = PageRequest.of(0, 10);
        PageRequest sortedPageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));
        Page<Order> page = new PageImpl<>(List.of(order), sortedPageable, 1);
        when(cacheManager.computeIfAbsent(any(CacheKey.class), any(Supplier.class)))
                .thenAnswer(invocation -> {
                    Supplier<Page<OrderResponse>> supplier = invocation.getArgument(1);
                    return supplier.get();
                });
        when(orderRepository.findByStatusAndClient_IdAndTranslator_Id(
                OrderStatus.NEW,
                1,
                2,
                sortedPageable
        )).thenReturn(page);
        when(orderRepository.findAllWithDetailsByIdIn(List.of(1))).thenReturn(List.of(order));

        Page<OrderResponse> result = orderService.getAll(OrderStatus.NEW, 1, 2, pageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    @SuppressWarnings("unchecked")
    void findByStatusAndTranslatorLanguageJpql_ShouldNormalizeLanguageAlias() {
        Order order = new Order();
        order.setId(5);
        order.setTitle("JPQL");
        order.setStatus(OrderStatus.NEW);
        when(cacheManager.computeIfAbsent(any(CacheKey.class), any(Supplier.class)))
                .thenAnswer(invocation -> {
                    Supplier<List<OrderResponse>> supplier = invocation.getArgument(1);
                    return supplier.get();
                });
        when(orderRepository.findAllWithDetailsByStatusAndTranslatorLanguage(OrderStatus.NEW, "EN"))
                .thenReturn(List.of(order));

        List<OrderResponse> result = orderService.findByStatusAndTranslatorLanguageJpql(
                OrderStatus.NEW,
                "english"
        );

        assertEquals(1, result.size());
        verify(orderRepository).findAllWithDetailsByStatusAndTranslatorLanguage(OrderStatus.NEW, "EN");
    }

    @Test
    @SuppressWarnings("unchecked")
    void findByStatusAndTranslatorLanguageNative_ShouldPassNull_WhenBlankLanguage() {
        when(cacheManager.computeIfAbsent(any(CacheKey.class), any(Supplier.class)))
                .thenAnswer(invocation -> {
                    Supplier<List<OrderResponse>> supplier = invocation.getArgument(1);
                    return supplier.get();
                });
        when(orderRepository.findAllWithDetailsByStatusAndTranslatorLanguage(OrderStatus.NEW, null))
                .thenReturn(List.of());

        List<OrderResponse> result = orderService.findByStatusAndTranslatorLanguageNative(
                OrderStatus.NEW,
                "   "
        );

        assertEquals(0, result.size());
        verify(orderRepository).findAllWithDetailsByStatusAndTranslatorLanguage(OrderStatus.NEW, null);
    }

    @Test
    @SuppressWarnings("unchecked")
    void findByStatusAndTranslatorLanguageNative_ShouldPassNull_WhenLanguageIsNull() {
        when(cacheManager.computeIfAbsent(any(CacheKey.class), any(Supplier.class)))
                .thenAnswer(invocation -> {
                    Supplier<List<OrderResponse>> supplier = invocation.getArgument(1);
                    return supplier.get();
                });
        when(orderRepository.findAllWithDetailsByStatusAndTranslatorLanguage(OrderStatus.NEW, null))
                .thenReturn(List.of());

        List<OrderResponse> result = orderService.findByStatusAndTranslatorLanguageNative(
                OrderStatus.NEW,
                null
        );

        assertEquals(0, result.size());
        verify(orderRepository).findAllWithDetailsByStatusAndTranslatorLanguage(OrderStatus.NEW, null);
    }

    @Test
    @SuppressWarnings("unchecked")
    void findByStatusAndTranslatorLanguageJpql_ShouldNormalizeAllSupportedAliases() {
        when(cacheManager.computeIfAbsent(any(CacheKey.class), any(Supplier.class)))
                .thenAnswer(invocation -> {
                    Supplier<List<OrderResponse>> supplier = invocation.getArgument(1);
                    return supplier.get();
                });
        when(orderRepository.findAllWithDetailsByStatusAndTranslatorLanguage(any(), any()))
                .thenReturn(List.of());

        orderService.findByStatusAndTranslatorLanguageJpql(OrderStatus.NEW, "english");
        orderService.findByStatusAndTranslatorLanguageJpql(OrderStatus.NEW, "russian");
        orderService.findByStatusAndTranslatorLanguageJpql(OrderStatus.NEW, "german");
        orderService.findByStatusAndTranslatorLanguageJpql(OrderStatus.NEW, "french");
        orderService.findByStatusAndTranslatorLanguageJpql(OrderStatus.NEW, "italian");
        orderService.findByStatusAndTranslatorLanguageJpql(OrderStatus.NEW, "spanish");
        orderService.findByStatusAndTranslatorLanguageJpql(OrderStatus.NEW, "polish");
        orderService.findByStatusAndTranslatorLanguageJpql(OrderStatus.NEW, "chinese");
        orderService.findByStatusAndTranslatorLanguageJpql(OrderStatus.NEW, "  by  ");

        verify(orderRepository).findAllWithDetailsByStatusAndTranslatorLanguage(OrderStatus.NEW, "EN");
        verify(orderRepository).findAllWithDetailsByStatusAndTranslatorLanguage(OrderStatus.NEW, "RU");
        verify(orderRepository).findAllWithDetailsByStatusAndTranslatorLanguage(OrderStatus.NEW, "DE");
        verify(orderRepository).findAllWithDetailsByStatusAndTranslatorLanguage(OrderStatus.NEW, "FR");
        verify(orderRepository).findAllWithDetailsByStatusAndTranslatorLanguage(OrderStatus.NEW, "IT");
        verify(orderRepository).findAllWithDetailsByStatusAndTranslatorLanguage(OrderStatus.NEW, "SP");
        verify(orderRepository).findAllWithDetailsByStatusAndTranslatorLanguage(OrderStatus.NEW, "PL");
        verify(orderRepository).findAllWithDetailsByStatusAndTranslatorLanguage(OrderStatus.NEW, "CN");
        verify(orderRepository).findAllWithDetailsByStatusAndTranslatorLanguage(OrderStatus.NEW, "BY");
    }

    @Test
    void create_ShouldWork_WhenOptionalRelationsAreMissing() {
        OrderRequest request = new OrderRequest(
                "Simple Order",
                OrderStatus.NEW,
                null,
                null,
                null,
                null,
                null
        );
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            if (order.getId() == null) {
                order.setId(101);
            }
            return order;
        });
        when(documentRepository.findByOrder_Id(101)).thenReturn(List.of());

        OrderResponse response = orderService.create(request);

        assertEquals(101, response.getId());
        assertEquals("Simple Order", response.getTitle());
    }

    @Test
    void delete_ShouldDeleteByIdAndInvalidateCache() {
        orderService.delete(123);

        verify(orderRepository).deleteById(123);
        verify(cacheManager).invalidate(Order.class);
    }
    @Test
    void create_ShouldThrowBadRequest_WhenTranslatorDoesNotKnowOrderLanguage() {
        OrderRequest request = new OrderRequest(
                "Invalid Order",
                OrderStatus.NEW,
                1,
                2,
                3,
                4,
                List.of()
        );
        Language source = new Language(1, "EN", "English");
        Language target = new Language(2, "RU", "Russian");
        Client client = new Client();
        client.setId(3);
        Translator translator = translatorWithLanguages(4, source);

        when(languageRepository.findById(1)).thenReturn(Optional.of(source));
        when(languageRepository.findById(2)).thenReturn(Optional.of(target));
        when(clientRepository.findById(3)).thenReturn(Optional.of(client));
        when(translatorRepository.findById(4)).thenReturn(Optional.of(translator));

        assertThrows(BadRequestException.class, () -> orderService.create(request));
    }
    private Translator translatorWithLanguages(
            Integer translatorId,
            Language... languages
    ) {
        Translator translator = new Translator();
        translator.setId(translatorId);
        List<TranslatorLanguage> translatorLanguages = java.util.Arrays.stream(languages)
                .map(language -> {
                    TranslatorLanguage translatorLanguage = new TranslatorLanguage();
                    translatorLanguage.setId(new TranslatorLanguageId(translatorId, language.getId()));
                    translatorLanguage.setTranslator(translator);
                    translatorLanguage.setLanguage(language);
                    return translatorLanguage;
                })
                .toList();
        translator.setTranslatorLanguages(translatorLanguages);
        return translator;
    }
}
