package com.bank.Hackathon_Java6.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bank.Hackathon_Java6.Dto.FavoriteAccountRequestDTO;
import com.bank.Hackathon_Java6.Dto.FavoriteAccountResponseDTO;
import com.bank.Hackathon_Java6.Dto.PagedResponseDTO;
import com.bank.Hackathon_Java6.Service.FavoriteAccountService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/customers/{customerId}/favorite-accounts")
@RequiredArgsConstructor
@Tag(name = "Favorite Accounts", description = "APIs for managing favorite payee accounts")
public class FavoriteAccountController {

    private final FavoriteAccountService favoriteAccountService;

    @GetMapping
    @Operation(summary = "List all favorite accounts for a customer",
               description = "Returns a paginated list of favorite accounts. Page size is 5 by default.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "List retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public ResponseEntity<PagedResponseDTO<FavoriteAccountResponseDTO>> getAllAccounts(
            @PathVariable Integer customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        validateAuthenticatedCustomer(customerId);
        return ResponseEntity.ok(favoriteAccountService.getAccountsByCustomer(customerId, page, size));
    }

    @GetMapping("/{accountId}")
    @Operation(summary = "Get a specific favorite account")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Account found"),
        @ApiResponse(responseCode = "404", description = "Account or customer not found")
    })
    public ResponseEntity<FavoriteAccountResponseDTO> getAccountById(
            @PathVariable Integer customerId,
            @PathVariable Integer accountId) {
        validateAuthenticatedCustomer(customerId);
        return ResponseEntity.ok(favoriteAccountService.getAccountById(customerId, accountId));
    }

    @PostMapping
    @Operation(summary = "Add a new favorite account",
               description = "Creates a new favorite account. Max 20 per customer.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Account created"),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "404", description = "Customer or bank not found"),
        @ApiResponse(responseCode = "422", description = "Max accounts limit reached")
    })
    public ResponseEntity<FavoriteAccountResponseDTO> createAccount(
            @PathVariable Integer customerId,
            @Valid @RequestBody FavoriteAccountRequestDTO requestDTO) {
        validateAuthenticatedCustomer(customerId);
        FavoriteAccountResponseDTO created = favoriteAccountService.createAccount(customerId, requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{accountId}")
    @Operation(summary = "Update a favorite account")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Account updated"),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<FavoriteAccountResponseDTO> updateAccount(
            @PathVariable Integer customerId,
            @PathVariable Integer accountId,
            @Valid @RequestBody FavoriteAccountRequestDTO requestDTO) {
        validateAuthenticatedCustomer(customerId);
        return ResponseEntity.ok(favoriteAccountService.updateAccount(customerId, accountId, requestDTO));
    }

    @DeleteMapping("/{accountId}")
    @Operation(summary = "Delete a favorite account")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Account deleted"),
        @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<Void> deleteAccount(
            @PathVariable Integer customerId,
            @PathVariable Integer accountId) {
        validateAuthenticatedCustomer(customerId);
        favoriteAccountService.deleteAccount(customerId, accountId);
        return ResponseEntity.noContent().build();
    }

    private void validateAuthenticatedCustomer(Integer customerId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !customerId.equals(authentication.getPrincipal())) {
            throw new AccessDeniedException("You can only access your own favorite accounts");
        }
    }
}
