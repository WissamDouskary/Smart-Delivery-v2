package com.smartlogi.delivery.controller;

import com.smartlogi.delivery.dto.ApiResponse;
import com.smartlogi.delivery.dto.responseDTO.SearchResponseDTO;
import com.smartlogi.delivery.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search")
@Tag(name = "Search Management", description = "Endpoints for Searching")
@PreAuthorize("hasRole('ROLE_Manager')")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService){
        this.searchService = searchService;
    }

    @GetMapping
    @Operation(summary = "Search by keyword", description = "Search by keyword for colis, senders, receivers and livreurs")
    public ResponseEntity<ApiResponse<SearchResponseDTO>> search(@RequestParam String keyword) {
        return ResponseEntity.ok(new ApiResponse<>("Search fait avec success", searchService.searchAll(keyword)));
    }
}