package com.example.graphql_springboot.dto;

public record TransactionStats(
        long count,
        double sumDepots,
        double sumRetraits
) {
}