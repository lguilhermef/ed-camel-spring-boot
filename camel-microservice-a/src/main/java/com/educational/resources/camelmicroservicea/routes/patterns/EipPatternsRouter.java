package com.educational.resources.camelmicroservicea.routes.patterns;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class EipPatternsRouter extends RouteBuilder {


    @Override
    public void configure() throws Exception {

        //Pipeline
        //Content Based Routing - choice()
        //Multicast multicast()
        //Split
/*
        from("timer:multicast-timer?period=10000")
                .multicast()
                .to("log:something1", "log:something2");
*/
        from("file:files/csv")
                .unmarshal().csv()
                .split(body())
                .to("activemq:split-queue");
    }
}
