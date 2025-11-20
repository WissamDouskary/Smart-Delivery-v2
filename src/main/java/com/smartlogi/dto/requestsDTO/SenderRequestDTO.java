package com.smartlogi.dto.requestsDTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class SenderRequestDTO {

    @NotBlank(message = "Le nom ne peut pas être vide")
    @Size(min = 2, max = 50, message = "Le nom doit contenir entre 2 et 50 caractères")
    private String nom;

    @NotBlank(message = "Le prénom ne peut pas être vide")
    @Size(min = 2, max = 50, message = "Le prénom doit contenir entre 2 et 50 caractères")
    private String prenom;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    private String email;

    @NotNull(message = "Le mot de passe est obligatoire")
    @NotBlank(message = "Le mot de passe ne peut être vide")
    private String password;

    @NotBlank(message = "Le téléphone est obligatoire")
    @Size(min = 10, max = 15, message = "Le numéro de téléphone doit contenir entre 10 et 15 chiffres")
    private String telephone;

    @NotBlank(message = "L'adresse est obligatoire")
    @Size(min = 5, max = 100, message = "L'adresse doit contenir entre 5 et 100 caractères")
    private String adresse;

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
