package com.luxon.assignment.controller;

import com.luxon.assignment.dto.ExchangeRequestDto;
import com.luxon.assignment.service.ExchangeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/api")
public class ExchangeController {

    private final ExchangeService exchangeService;

    public ExchangeController(ExchangeService exchangeService) {
        this.exchangeService = exchangeService;
    }

    @PostMapping("/exchange")
    public ResponseEntity<?> exchange(@Valid @RequestBody ExchangeRequestDto exchangeRequestDto){
        return exchangeService.execute(exchangeRequestDto);
    }
}
