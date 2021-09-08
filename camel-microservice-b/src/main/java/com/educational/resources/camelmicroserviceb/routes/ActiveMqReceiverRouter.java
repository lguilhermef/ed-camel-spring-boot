package com.educational.resources.camelmicroserviceb.routes;

import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ActiveMqReceiverRouter extends RouteBuilder {

    @Autowired
    private MyCurrencyExchangeProcessor myCurrencyExchangeProcessor;

    @Autowired
    private MyCurrencyExchangeTransformer myCurrencyExchangeTransformer;

    @Override
    public void configure() throws Exception {
        /*from("activemq:my-queue")
                .to("log:received-message-from-active-mq");
        from("activemq:my-queue")
                .unmarshal()
                .json(JsonLibrary.Jackson, CurrencyExchange.class)
                .bean(myCurrencyExchangeProcessor)
                .bean(myCurrencyExchangeTransformer)
                .to("log:received-message-from-active-mq");
        */

        /*from("activemq:my-activemq-xml-queue")
                .unmarshal()
                .jacksonxml(CurrencyExchange.class)
                .to("log:received-message-from-xml-active-mq");

         */

        from("activemq:split-queue")
                .to("log:received-message-from-xml-active-mq");
    }
}

@Component
class MyCurrencyExchangeProcessor {

    Logger logger = LoggerFactory.getLogger(ActiveMqReceiverRouter.class);

    public CurrencyExchange processMessage(CurrencyExchange currencyExchange) {

        logger.info("Do some processing with currencyExchange.getConversionMultiple()",
                currencyExchange.getConversionMultiple());

        return currencyExchange;
    }
}

@Component
class MyCurrencyExchangeTransformer {

    public CurrencyExchange processMessage(CurrencyExchange currencyExchange) {
        currencyExchange.setConversionMultiple(
                currencyExchange.getConversionMultiple().multiply(BigDecimal.TEN));

        return currencyExchange;
    }
}
