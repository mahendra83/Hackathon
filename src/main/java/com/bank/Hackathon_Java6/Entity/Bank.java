package com.bank.Hackathon_Java6.Entity;



import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.*;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "BANK")
public class Bank {
	
	@Id
    @Column(name = "bankCode")
    private Integer bankCode;

    @NotBlank
    @Column(name = "bankName", nullable = false)
    private String bankName;

    @OneToMany(mappedBy = "bank", fetch = FetchType.LAZY)
    private List<FavoriteAccount> favoriteAccounts;


	
}

