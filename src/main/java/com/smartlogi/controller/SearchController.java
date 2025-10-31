package com.smartlogi.controller;

import com.smartlogi.dto.ApiResponse;
import com.smartlogi.dto.responseDTO.LivreurResponseDTO;
import com.smartlogi.dto.responseDTO.SearchResponseDTO;
import com.smartlogi.service.SearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService){
        this.searchService = searchService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<SearchResponseDTO>> search(@RequestParam String keyword) {
        return ResponseEntity.ok(new ApiResponse<>("Search fait avec success", searchService.searchAll(keyword)));
    }
}