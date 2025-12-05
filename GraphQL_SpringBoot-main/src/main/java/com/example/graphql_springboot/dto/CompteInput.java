package com.example.graphql_springboot.dto;

import com.example.graphql_springboot.enums.TypeCompte;

public record CompteInput(
        double solde,
        String dateCreation,
        TypeCompte type
) {
}

