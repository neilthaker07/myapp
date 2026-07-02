package com.demo.rippling.service;

import com.demo.rippling.dto.ClientResponse;
import com.demo.rippling.dto.CreateClientRequest;
import com.demo.rippling.entity.ClientEntity;
import com.demo.rippling.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;

    public ClientResponse createClient(CreateClientRequest req) {
        ClientEntity saved = clientRepository.save(ClientEntity.builder()
                .name(req.name())
                .preferredProviderAge(req.preferredProviderAge())
                .build());
        return new ClientResponse(saved.getClientId(), saved.getName(), saved.getPreferredProviderAge());
    }
}
