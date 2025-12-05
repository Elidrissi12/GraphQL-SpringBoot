package com.example.graphql_springboot.dto;

import com.example.graphql_springboot.enums.TypeTransaction;

public record TransactionRequest(
        Long compteId,
        double montant,
        String date,
        TypeTransaction type
) {
}
