package com.luxon.assignment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.luxon.assignment.dto.ExchangeRequestDto;
import com.luxon.assignment.enums.ExchangeType;
import com.luxon.assignment.enums.Instrument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ExchangeControllerTest {

    public static final String CONTENT_TYPE_JSON = "application/json";

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${user.read.oauth.clientId}")
    private String userReadClientId;

    @Value("${user.read.oauth.clientSecret}")
    private String userReadClientSecret;

    @Value("${user.write.oauth.clientId}")
    private String userWriteClientId;

    @Value("${user.write.oauth.clientSecret}")
    private String userWriteClientSecret;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac)
                .addFilter(springSecurityFilterChain).build();
    }

    @Test
    public void when_no_token_then_unauthorized() throws Exception {
        mockMvc.perform(post("/api/exchange")
                .content(objectMapper.writeValueAsBytes(exchangeRequestDto()))
                .contentType(CONTENT_TYPE_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void when_token_but_not_correct_scope_then_forbidden() throws Exception {
        String accessToken = obtainAccessToken(userReadClientId, userReadClientSecret);
        mockMvc.perform(post("/api/exchange")
                .header("Authorization", "Bearer " + accessToken)
                .content(objectMapper.writeValueAsBytes(exchangeRequestDto()))
                .contentType(CONTENT_TYPE_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void when_post_to_create_invalid_exchange_then_return_bad_request() throws Exception {
        String accessToken = obtainAccessToken(userWriteClientId, userWriteClientSecret);
        mockMvc.perform(post("/api/exchange")
                .header("Authorization", "Bearer " + accessToken)
                .content(objectMapper.writeValueAsBytes(exchangeRequestDtoMissingFields()))
                .contentType(CONTENT_TYPE_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("[\"Exchange type is mandatory\"]"));
    }


    private String obtainAccessToken(String clientId, String clientSecret) throws Exception {

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "client_credentials");

        ResultActions result
                = mockMvc.perform(post("/oauth/token")
                .params(params)
                .with(httpBasic(clientId, clientSecret))
                .accept(CONTENT_TYPE_JSON))
                .andExpect(status().isOk());

        String resultString = result.andReturn().getResponse().getContentAsString();

        JacksonJsonParser jsonParser = new JacksonJsonParser();
        return jsonParser.parseMap(resultString).get("access_token").toString();
    }

    private ExchangeRequestDto exchangeRequestDto() {
        ExchangeRequestDto exchangeRequestDto = new ExchangeRequestDto();
        exchangeRequestDto.setAccountId(1);
        exchangeRequestDto.setAmount(BigDecimal.TEN);
        exchangeRequestDto.setBaseCurrency(Instrument.BTC);
        exchangeRequestDto.setQuoteCurrency(Instrument.EUR);
        exchangeRequestDto.setExchangeType(ExchangeType.BUY);
        return exchangeRequestDto;
    }

    private ExchangeRequestDto exchangeRequestDtoMissingFields() {
        ExchangeRequestDto exchangeRequestDto = new ExchangeRequestDto();
        exchangeRequestDto.setAccountId(1);
        exchangeRequestDto.setAmount(BigDecimal.TEN);
        exchangeRequestDto.setBaseCurrency(Instrument.BTC);
        exchangeRequestDto.setQuoteCurrency(Instrument.EUR);
        return exchangeRequestDto;
    }

}
