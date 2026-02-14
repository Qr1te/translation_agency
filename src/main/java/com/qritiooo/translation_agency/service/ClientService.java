package com.qritiooo.translation_agency.service;

import com.qritiooo.translation_agency.dto.ClientDto;

import java.util.List;

public interface ClientService {
    ClientDto create(ClientDto dto);
    ClientDto update(Integer id, ClientDto dto);
    ClientDto getById(Integer id);
    List<ClientDto> getAll();
    void delete(Integer id);
}
