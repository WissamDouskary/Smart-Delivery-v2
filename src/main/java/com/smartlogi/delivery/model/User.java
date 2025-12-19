package com.smartlogi.delivery.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, length = 36)
    private String id;

    @NotNull
    @Column(name = "email", nullable = false)
    private String email;

    @NotNull
    @Column(name = "password", nullable = false)
    private String password;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role roleEntity;

    @OneToOne(mappedBy = "user")
    @JsonIgnore
    private Livreur livreur;

    @OneToOne(mappedBy = "user")
    private Manager manager;

    @OneToOne(mappedBy = "user")
    private Sender sender;

    @OneToOne(mappedBy = "user")
    private Receiver receiver;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRoleEntity() {
        return roleEntity;
    }

    public void setRoleEntity(Role roleEntity) {
        this.roleEntity = roleEntity;
    }

    public String getRoleName() {
        return roleEntity != null ? roleEntity.getName() : null;
    }

    public Livreur getLivreur() { return livreur; }
    public void setLivreur(Livreur livreur) { this.livreur = livreur; }

    public Manager getManager() { return manager; }
    public void setManager(Manager manager) { this.manager = manager; }

    public Sender getSender() { return sender; }
    public void setSender(Sender sender) { this.sender = sender; }

    public Receiver getReceiver() { return receiver; }
    public void setReceiver(Receiver receiver) { this.receiver = receiver; }
}