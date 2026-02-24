package com.qritiooo.translationagency.mapper;

import com.qritiooo.translationagency.dto.request.ClientRequest;
import com.qritiooo.translationagency.dto.response.ClientResponse;
import com.qritiooo.translationagency.model.Client;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ClientMapper {

    public static ClientResponse toResponse(Client c) {
        return new ClientResponse(c.getId(), c.getFullName(), c.getEmail());
    }

    public static void updateEntity(Client c, ClientRequest request) {
        c.setFullName(request.getFullName());
        c.setEmail(request.getEmail());
    }
}

