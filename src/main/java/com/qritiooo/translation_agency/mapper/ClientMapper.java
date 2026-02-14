package com.qritiooo.translation_agency.mapper;

import com.qritiooo.translation_agency.dto.ClientDto;
import com.qritiooo.translation_agency.model.Client;

public class ClientMapper {

    public static ClientDto toDto(Client c) {
        return new ClientDto(c.getId(), c.getFullName(), c.getEmail());
    }

    public static void updateEntity(Client c, ClientDto dto) {
        c.setFullName(dto.getFullName());
        c.setEmail(dto.getEmail());
    }
}
