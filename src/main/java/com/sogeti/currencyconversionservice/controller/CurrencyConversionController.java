package com.sogeti.currencyconversionservice.controller;

import com.sogeti.currencyconversionservice.model.CurrencyConversion;
import com.sogeti.currencyconversionservice.proxy.CurrencyExchangeProxy;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;

@RestController
@RequiredArgsConstructor
public class CurrencyConversionController {

    private final CurrencyExchangeProxy proxy;

    @GetMapping("currency-conversion/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversion convert(@PathVariable String from,
                                      @PathVariable String to,
                                      @PathVariable BigDecimal quantity) {

        HashMap<String, String> uriVariables = new HashMap<>();
        uriVariables.put("from", from);
        uriVariables.put("to", to);

        ResponseEntity<CurrencyConversion> responseEntity = new RestTemplate().getForEntity(
                "http://localhost:8000/currency-exchange/from/{from}/to/{to}",
                CurrencyConversion.class,
                uriVariables);
        CurrencyConversion currencyConversion = responseEntity.getBody();

        return CurrencyConversion.builder()
                .id(currencyConversion.getId())
                .from(from)
                .to(to)
                .conversionMultiple(currencyConversion.getConversionMultiple())
                .quantity(quantity)
                .totalCalculatedAmount(quantity.multiply(currencyConversion.getConversionMultiple()))
                .environment(currencyConversion.getEnvironment())
                .build();
    }

    @GetMapping("currency-conversion-feign/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversion convertUsingFeign(@PathVariable String from,
                                      @PathVariable String to,
                                      @PathVariable BigDecimal quantity) {


        CurrencyConversion currencyConversion = proxy.retrieveExchangeValue(from, to);
        return CurrencyConversion.builder()
                .id(currencyConversion.getId())
                .from(from)
                .to(to)
                .conversionMultiple(currencyConversion.getConversionMultiple())
                .quantity(quantity)
                .totalCalculatedAmount(quantity.multiply(currencyConversion.getConversionMultiple()))
                .environment(currencyConversion.getEnvironment())
                .build();
    }
}
