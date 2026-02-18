package com.qritiooo.translation_agency.mapper;

import com.qritiooo.translation_agency.dto.request.ClientRequest;
import com.qritiooo.translation_agency.dto.response.ClientResponse;
import com.qritiooo.translation_agency.model.Client;
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
