package com.govmt.sgd.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.govmt.sgd.dto.request.OrgaoRequest;
import com.govmt.sgd.dto.response.OrgaoResponse;
import com.govmt.sgd.service.OrgaoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/orgaos")
@RequiredArgsConstructor
public class OrgaoController {

    private final OrgaoService orgaoService;

    @PostMapping
    public ResponseEntity<OrgaoResponse> create(@RequestBody OrgaoRequest request) {
        OrgaoResponse response = orgaoService.createOrgao(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<OrgaoResponse>> getAll() {
        return ResponseEntity.ok(orgaoService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrgaoResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(orgaoService.findById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        orgaoService.deleteOrgao(id);
        return ResponseEntity.noContent().build();
    }
}
