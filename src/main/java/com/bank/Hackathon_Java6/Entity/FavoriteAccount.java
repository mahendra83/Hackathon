package com.bank.Hackathon_Java6.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "FAVORITE_ACCOUNT")
public class FavoriteAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "accountId")
    private Integer accountId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customerId", nullable = false)
    private Customer customer;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9 '\\-]+$", message = "Name must contain only letters, numbers, spaces, apostrophes, or hyphens")
    @Column(name = "accountName", nullable = false)
    private String accountName;

    @NotBlank
    @Size(max = 20, message = "IBAN must not exceed 20 characters")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "IBAN must contain only letters and numbers")
    @Column(name = "iban", nullable = false)
    private String iban;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bankCode", nullable = false)
    private Bank bank;

    @Column(name = "createdAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
