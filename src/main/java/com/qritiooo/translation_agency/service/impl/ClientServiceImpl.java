package com.qritiooo.translation_agency.service.impl;

import com.qritiooo.translation_agency.dto.ClientDto;
import com.qritiooo.translation_agency.mapper.ClientMapper;
import com.qritiooo.translation_agency.model.Client;
import com.qritiooo.translation_agency.repository.ClientRepository;
import com.qritiooo.translation_agency.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository repo;

    @Override
    public ClientDto create(ClientDto dto) {
        Client c = new Client();
        ClientMapper.updateEntity(c, dto);
        return ClientMapper.toDto(repo.save(c));
    }

    @Override
    public ClientDto update(Integer id, ClientDto dto) {
        Client c = repo.findById(id).orElseThrow();
        ClientMapper.updateEntity(c, dto);
        return ClientMapper.toDto(repo.save(c));
    }

    @Override
    public ClientDto getById(Integer id) {
        return ClientMapper.toDto(repo.findById(id).orElseThrow());
    }

    @Override
    public List<ClientDto> getAll() {
        return repo.findAll().stream().map(ClientMapper::toDto).toList();
    }

    @Override
    public void delete(Integer id) {
        repo.deleteById(id);
    }
}
