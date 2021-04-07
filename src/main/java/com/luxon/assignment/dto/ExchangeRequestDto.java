package com.luxon.assignment.dto;

import com.luxon.assignment.enums.ExchangeType;
import com.luxon.assignment.enums.Instrument;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class ExchangeRequestDto {

    @NotNull(message = "Account id is mandatory")
    private Integer accountId;

    @NotNull(message = "Exchange type is mandatory")
    private ExchangeType exchangeType;

    @NotNull(message = "Base currency is mandatory")
    private Instrument baseCurrency;

    @NotNull(message = "Quote currency is mandatory")
    private Instrument quoteCurrency;

    @NotNull(message = "Amount is mandatory")
    private BigDecimal amount;

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public ExchangeType getExchangeType() {
        return exchangeType;
    }

    public void setExchangeType(ExchangeType exchangeType) {
        this.exchangeType = exchangeType;
    }

    public Instrument getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(Instrument baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public Instrument getQuoteCurrency() {
        return quoteCurrency;
    }

    public void setQuoteCurrency(Instrument quoteCurrency) {
        this.quoteCurrency = quoteCurrency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }


}
