package com.demo.rippling.controller;

import com.demo.rippling.dto.ClientResponse;
import com.demo.rippling.dto.CreateClientRequest;
import com.demo.rippling.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClientResponse createClient(@RequestBody CreateClientRequest req) {
        return clientService.createClient(req);
    }
}
