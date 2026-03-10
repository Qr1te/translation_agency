package com.qritiooo.translationagency.mapper;

import com.qritiooo.translationagency.dto.request.ClientRequest;
import com.qritiooo.translationagency.dto.response.ClientResponse;
import com.qritiooo.translationagency.model.Client;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ClientMapper {

    public static ClientResponse toResponse(Client c) {
        return new ClientResponse(c.getId(), c.getFirstName(), c.getLastName(), c.getEmail());
    }

    public static void updateEntity(Client c, ClientRequest request) {
        c.setFirstName(request.getFirstName());
        c.setLastName(request.getLastName());
        c.setEmail(request.getEmail());
    }

    public static void patchEntity(Client c, ClientRequest request) {
        if (request.getFirstName() != null) {
            c.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            c.setLastName(request.getLastName());
        }
        if (request.getEmail() != null) {
            c.setEmail(request.getEmail());
        }
    }
}

