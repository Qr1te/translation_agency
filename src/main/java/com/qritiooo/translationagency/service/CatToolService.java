package com.qritiooo.translationagency.service;

import com.qritiooo.translationagency.dto.request.CatToolRequest;
import com.qritiooo.translationagency.dto.response.CatToolResponse;
import java.util.List;

public interface CatToolService {
    CatToolResponse create(CatToolRequest request);

    CatToolResponse update(Integer id, CatToolRequest request);

    CatToolResponse patch(Integer id, CatToolRequest request);

    CatToolResponse getById(Integer id);

    List<CatToolResponse> getAll();

    void delete(Integer id);
}
