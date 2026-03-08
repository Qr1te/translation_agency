package com.qritiooo.translationagency.service.impl;

import com.qritiooo.translationagency.cache.CacheStore;
import com.qritiooo.translationagency.cache.CacheableService;
import com.qritiooo.translationagency.cache.HashMapCacheStore;
import com.qritiooo.translationagency.dto.request.TranslatorRequest;
import com.qritiooo.translationagency.dto.request.TranslatorToolRequest;
import com.qritiooo.translationagency.dto.response.TranslatorResponse;
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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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

        Map<Integer, TranslatorTool> existingByToolId = new HashMap<>();
        for (TranslatorTool existing : translator.getTranslatorTools()) {
            if (existing.getTool() != null && existing.getTool().getId() != null) {
                existingByToolId.put(existing.getTool().getId(), existing);
            }
        }

        Set<Integer> requestedToolIds = new LinkedHashSet<>();
        for (TranslatorToolRequest toolRequest : toolRequests) {
            if (toolRequest.getToolId() == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "toolId is required for each tool item"
                );
            }
            if (!requestedToolIds.add(toolRequest.getToolId())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Duplicate toolId in request: " + toolRequest.getToolId()
                );
            }
        }

        List<TranslatorTool> toRemove = new ArrayList<>();
        for (TranslatorTool existing : translator.getTranslatorTools()) {
            Integer existingToolId = existing.getTool() != null ? existing.getTool().getId() : null;
            if (existingToolId == null || !requestedToolIds.contains(existingToolId)) {
                toRemove.add(existing);
            }
        }
        translator.getTranslatorTools().removeAll(toRemove);

        for (TranslatorToolRequest toolRequest : toolRequests) {
            Integer toolId = toolRequest.getToolId();
            CatTool tool = catToolRepo.findById(toolId).orElseThrow(
                    () -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "CAT tool not found with id: " + toolId
                    )
            );

            TranslatorTool translatorTool = existingByToolId.get(toolId);
            if (translatorTool == null) {
                translatorTool = new TranslatorTool();
                translatorTool.setTranslator(translator);
                translatorTool.setTool(tool);
                translator.getTranslatorTools().add(translatorTool);
            }

            translatorTool.setLicenseExpiryDate(toolRequest.getLicenseExpiryDate());
            translatorTool.setProficiencyLevel(toolRequest.getProficiencyLevel());
        }
    }

    private Translator getTranslatorOrThrow(Integer id) {
        return translatorRepo.findById(id).orElseThrow(
                () -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Translator not found with id: " + id
                )
        );
    }
}

