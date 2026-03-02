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

    public static void patchEntity(Client c, ClientRequest request) {
        if (request.getFullName() != null) {
            c.setFullName(request.getFullName());
        }
        if (request.getEmail() != null) {
            c.setEmail(request.getEmail());
        }
    }
}

