package com.demo.rippling.dto;

public record ClientResponse(
        Long clientId,
        String name,
        Integer preferredProviderAge
) {}
