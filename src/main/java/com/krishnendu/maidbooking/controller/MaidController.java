package com.krishnendu.maidbooking.controller;

import com.krishnendu.maidbooking.dto.AddMaidRequest;
import com.krishnendu.maidbooking.model.Maid;
import com.krishnendu.maidbooking.model.ServiceType;
import com.krishnendu.maidbooking.service.MaidService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/maids")
public class MaidController {

    private final MaidService maidService;

    public MaidController(MaidService maidService) {
        this.maidService = maidService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Maid add(@Valid @RequestBody AddMaidRequest request) {
        return maidService.add(request.toMaid());
    }

    @GetMapping("/{id}")
    public Maid get(@PathVariable String id) {
        return maidService.get(id);
    }

    @GetMapping
    public List<Maid> search(@RequestParam(required = false) String locality,
                             @RequestParam(required = false) ServiceType service,
                             @RequestParam(required = false) Double minRating,
                             @RequestParam(required = false) Double maxPrice) {
        return maidService.search(locality, service, minRating, maxPrice);
    }
}
