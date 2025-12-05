package com.example.graphql_springboot.controllers;


import com.example.graphql_springboot.dto.CompteInput;
import com.example.graphql_springboot.dto.Stats;
import com.example.graphql_springboot.dto.TransactionRequest;
import com.example.graphql_springboot.dto.TransactionStats;
import com.example.graphql_springboot.entities.Compte;
import com.example.graphql_springboot.entities.Transaction;
import com.example.graphql_springboot.repositories.CompteRepository;
import com.example.graphql_springboot.repositories.TransactionRepository;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
@AllArgsConstructor
public class CompteControllerGraphQL {
    private CompteRepository compteRepository;
    private TransactionRepository transactionRepository;

    @QueryMapping
    public List<Compte> allComptes(){
        return compteRepository.findAll();
    }

    @QueryMapping
    public Compte compteById(@Argument Long id){
        Compte compte = compteRepository.findById(id).orElse(null);
        if(compte == null) throw new RuntimeException(String.format("Compte %s not found", id));
        else return compte;
    }

    @MutationMapping
    public Compte saveCompte(@Argument("compte") CompteInput compteInput){
        Compte compte = new Compte();
        compte.setSolde(compteInput.solde());
        compte.setType(compteInput.type());
        
        // Convertir la String en Date
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date date = formatter.parse(compteInput.dateCreation());
            compte.setDateCreation(date);
        } catch (ParseException e) {
            throw new RuntimeException("Format de date invalide. Utilisez le format yyyy-MM-dd", e);
        }
        
        return compteRepository.save(compte);
    }

    @QueryMapping
    public Stats totalSolde() {
        long count = compteRepository.count();
        double sum = compteRepository.sumSoldes();
        double average = count > 0 ? sum / count : 0;
        return new Stats(count, sum, average);
    }

    @MutationMapping
    public Transaction addTransaction(@Argument("transaction") TransactionRequest transactionRequest) {
        Compte compte = compteRepository.findById(transactionRequest.compteId())
                .orElseThrow(() -> new RuntimeException("Compte " + transactionRequest.compteId() + " not found"));

        Transaction transaction = new Transaction();
        transaction.setMontant(transactionRequest.montant());
        transaction.setType(transactionRequest.type());
        transaction.setCompte(compte);
        
        // Convertir la String en Date
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date date = formatter.parse(transactionRequest.date());
            transaction.setDate(date);
        } catch (ParseException e) {
            throw new RuntimeException("Format de date invalide. Utilisez le format yyyy-MM-dd", e);
        }

        return transactionRepository.save(transaction);
    }

    @QueryMapping
    public List<Transaction> compteTransactions(@Argument Long id) {
        Compte compte = compteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Compte " + id + " not found"));
        return transactionRepository.findByCompte(compte);
    }

    @QueryMapping
    public List<Transaction> allTransactions() {
        return transactionRepository.findAll();
    }

    @QueryMapping
    public TransactionStats transactionStats() {
        long count = transactionRepository.count();
        double sumDepots = transactionRepository.sumByType(com.example.graphql_springboot.enums.TypeTransaction.DEPOT);
        double sumRetraits = transactionRepository.sumByType(com.example.graphql_springboot.enums.TypeTransaction.RETRAIT);

        return new TransactionStats(count, sumDepots, sumRetraits);
    }
}