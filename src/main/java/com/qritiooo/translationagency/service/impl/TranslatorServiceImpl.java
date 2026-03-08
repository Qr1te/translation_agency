package com.qritiooo.translationagency.service.impl;

import com.qritiooo.translationagency.cache.CacheStore;
import com.qritiooo.translationagency.cache.CacheableService;
import com.qritiooo.translationagency.cache.HashMapCacheStore;
import com.qritiooo.translationagency.dto.request.TranslatorRequest;
import com.qritiooo.translationagency.dto.request.TranslatorToolRequest;
import com.qritiooo.translationagency.dto.response.TranslatorResponse;
import com.qritiooo.translationagency.exception.BadRequestException;
import com.qritiooo.translationagency.exception.NotFoundException;
import com.qritiooo.translationagency.mapper.TranslatorMapper;
import com.qritiooo.translationagency.model.CatTool;
import com.qritiooo.translationagency.model.Translator;
import com.qritiooo.translationagency.model.TranslatorTool;
import com.qritiooo.translationagency.repository.CatToolRepository;
import com.qritiooo.translationagency.repository.TranslatorRepository;
import com.qritiooo.translationagency.service.TranslatorService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TranslatorServiceImpl implements TranslatorService, CacheableService {

    private final TranslatorRepository translatorRepo;
    private final CatToolRepository catToolRepo;
    private final CacheStore cacheStore = new HashMapCacheStore();

    @Override
    @Transactional
    public TranslatorResponse create(TranslatorRequest request) {
        Translator t = new Translator();
        TranslatorMapper.updateEntity(t, request);

        if (request.getLanguages() != null) {
            t.setLanguages(new HashSet<>(request.getLanguages()));
        }
        syncTools(t, request.getTools());

        TranslatorResponse response = TranslatorMapper.toResponse(translatorRepo.save(t));
        invalidateCache();
        return response;
    }

    @Override
    @Transactional
    public TranslatorResponse update(Integer id, TranslatorRequest request) {
        Translator t = getTranslatorOrThrow(id);
        TranslatorMapper.updateEntity(t, request);

        if (request.getLanguages() != null) {
            t.setLanguages(new HashSet<>(request.getLanguages()));
        }
        syncTools(t, request.getTools());

        TranslatorResponse response = TranslatorMapper.toResponse(translatorRepo.save(t));
        invalidateCache();
        return response;
    }

    @Override
    @Transactional
    public TranslatorResponse patch(Integer id, TranslatorRequest request) {
        Translator t = getTranslatorOrThrow(id);
        TranslatorMapper.patchEntity(t, request);

        if (request.getLanguages() != null) {
            t.setLanguages(new HashSet<>(request.getLanguages()));
        }
        if (request.getTools() != null) {
            syncTools(t, request.getTools());
        }

        TranslatorResponse response = TranslatorMapper.toResponse(translatorRepo.save(t));
        invalidateCache();
        return response;
    }

    @Override
    public TranslatorResponse getById(Integer id) {
        return TranslatorMapper.toResponse(getTranslatorOrThrow(id));
    }

    @Override
    public List<TranslatorResponse> getAll() {
        return getOrLoad(
                "getAll",
                () -> translatorRepo.findAll().stream().map(TranslatorMapper::toResponse).toList()
        );
    }

    @Override
    public void delete(Integer id) {
        translatorRepo.deleteById(id);
        invalidateCache();
    }

    @Override
    public String getCacheNamespace() {
        return "translator";
    }

    @Override
    public CacheStore getCacheStore() {
        return cacheStore;
    }

    private void syncTools(Translator translator, List<TranslatorToolRequest> toolRequests) {
        if (toolRequests == null) {
            return;
        }

        Map<Integer, TranslatorTool> existingByToolId = mapExistingToolsById(translator);
        Set<Integer> requestedToolIds = collectRequestedToolIds(toolRequests);
        removeUnrequestedTools(translator, requestedToolIds);
        applyToolRequests(translator, toolRequests, existingByToolId);
    }

    private Translator getTranslatorOrThrow(Integer id) {
        return translatorRepo.findById(id).orElseThrow(
                () -> new NotFoundException("Translator not found with id: " + id)
        );
    }

    private Map<Integer, TranslatorTool> mapExistingToolsById(Translator translator) {
        Map<Integer, TranslatorTool> existingByToolId = new HashMap<>();
        for (TranslatorTool existing : translator.getTranslatorTools()) {
            if (existing.getTool() != null && existing.getTool().getId() != null) {
                existingByToolId.put(existing.getTool().getId(), existing);
            }
        }
        return existingByToolId;
    }

    private Set<Integer> collectRequestedToolIds(List<TranslatorToolRequest> toolRequests) {
        Set<Integer> requestedToolIds = new LinkedHashSet<>();
        for (TranslatorToolRequest toolRequest : toolRequests) {
            Integer toolId = toolRequest.getToolId();
            if (toolId == null) {
                throw new BadRequestException("toolId is required for each tool item");
            }
            if (!requestedToolIds.add(toolId)) {
                throw new BadRequestException("Duplicate toolId in request: " + toolId);
            }
        }
        return requestedToolIds;
    }

    private void removeUnrequestedTools(Translator translator, Set<Integer> requestedToolIds) {
        List<TranslatorTool> toRemove = new ArrayList<>();
        for (TranslatorTool existing : translator.getTranslatorTools()) {
            Integer existingToolId = existing.getTool() != null ? existing.getTool().getId() : null;
            if (existingToolId == null || !requestedToolIds.contains(existingToolId)) {
                toRemove.add(existing);
            }
        }
        translator.getTranslatorTools().removeAll(toRemove);
    }

    private void applyToolRequests(
            Translator translator,
            List<TranslatorToolRequest> toolRequests,
            Map<Integer, TranslatorTool> existingByToolId
    ) {
        for (TranslatorToolRequest toolRequest : toolRequests) {
            Integer toolId = toolRequest.getToolId();
            CatTool tool = getToolOrThrow(toolId);
            TranslatorTool translatorTool = getOrCreateTranslatorTool(
                    translator,
                    existingByToolId,
                    toolId,
                    tool
            );
            translatorTool.setLicenseExpiryDate(toolRequest.getLicenseExpiryDate());
            translatorTool.setProficiencyLevel(toolRequest.getProficiencyLevel());
        }
    }

    private CatTool getToolOrThrow(Integer toolId) {
        return catToolRepo.findById(toolId).orElseThrow(
                () -> new NotFoundException("CAT tool not found with id: " + toolId)
        );
    }

    private TranslatorTool getOrCreateTranslatorTool(
            Translator translator,
            Map<Integer, TranslatorTool> existingByToolId,
            Integer toolId,
            CatTool tool
    ) {
        TranslatorTool translatorTool = existingByToolId.get(toolId);
        if (translatorTool != null) {
            return translatorTool;
        }

        TranslatorTool newTranslatorTool = new TranslatorTool();
        newTranslatorTool.setTranslator(translator);
        newTranslatorTool.setTool(tool);
        translator.getTranslatorTools().add(newTranslatorTool);
        return newTranslatorTool;
    }
}

