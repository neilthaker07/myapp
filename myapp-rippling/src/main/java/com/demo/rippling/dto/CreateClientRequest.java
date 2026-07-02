package com.demo.rippling.dto;

public record CreateClientRequest(
        String name,
        Integer preferredProviderAge
) {}
