package com.luxon.assignment.service;

import com.luxon.assignment.dto.ExchangeRequestDto;
import com.luxon.assignment.entity.Account;
import com.luxon.assignment.entity.Balance;
import com.luxon.assignment.enums.ExchangeType;
import com.luxon.assignment.exception.InvalidAmountException;
import com.luxon.assignment.exception.ResourceNotFoundException;
import com.luxon.assignment.repository.AccountRepository;
import com.luxon.assignment.repository.BalanceRepository;
import com.luxon.assignment.repository.RateRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;

@Service
public class ExchangeService {

    private final AccountRepository accountRepository;
    private final BalanceRepository balanceRepository;
    private final RateRepository rateRepository;

    public ExchangeService(AccountRepository accountRepository, BalanceRepository balanceRepository, RateRepository rateRepository) {
        this.accountRepository = accountRepository;
        this.balanceRepository = balanceRepository;
        this.rateRepository = rateRepository;
    }

    @Transactional
    public ResponseEntity<?> execute(ExchangeRequestDto exchangeRequestDto) {
        Account account = accountRepository.findById(exchangeRequestDto.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account with id " + exchangeRequestDto.getAccountId() + " not found"));

        if (ExchangeType.SELL == exchangeRequestDto.getExchangeType()) {
            Balance baseBalance = account.getBalances().stream()
                    .filter(balance -> exchangeRequestDto.getBaseCurrency() == balance.getInstrument())
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("Balance not found for base currency " + exchangeRequestDto.getBaseCurrency()));
            if (baseBalance.getQty().compareTo(exchangeRequestDto.getAmount()) < 0) {
                throw new InvalidAmountException("Amount you want to exchange is higher than amount you have in your account");
            } else {
                baseBalance.setQty(baseBalance.getQty().subtract(exchangeRequestDto.getAmount()));
                balanceRepository.save(baseBalance);
            }

        }

        return ResponseEntity.ok(200);
    }

    @PostConstruct
    public void doSome() {
        Account account = accountRepository.save(Account.builder().id(1).name("Jack Black").build());
        List<Balance> userBalance = Collections.singletonList(Balance.builder().account(account).build());
        account.setBalances(userBalance);
    }
}
