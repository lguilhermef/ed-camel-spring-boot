package com.educational.resources.camelmicroservicea.routes.patterns;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EipPatternsRouter extends RouteBuilder {

    @Autowired
    SplitterComponent splitter;

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
        //Message,Message2,Message3
        /*
        from("file:files/csv")
                .unmarshal().csv()
                .split(body()) //each line becomes a different entry in the queue
                .to("activemq:split-queue");

        //Message, Message2, Message3
        from("file:files/csv")
                .convertBodyTo(String.class)
                .split(body(), ",")
                .to("activemq:splitqueue");

        from("file:files/csv")
                .convertBodyTo(String.class)
                .split(method(splitter))
                .to("activemq:split-queue");

         */

        //Aggregate
        //Messages => Aggregate => Endpoint
        //to, 3
        from("file:files/aggregate-json")
                .unmarshal().json()
                .aggregate(simple("{body.to}"), new ArrayListAggregationStrategy())
                .completionSize(3)
                //.completionTimeout(HIGHEST)
                .to("log:aggregate-json");
    }
}

@Component
class SplitterComponent {

    public List<String> splitInput(String str) {
        return List.of("ABC", "DEF", "GHI");
    }
}

@Component
class ArrayListAggregationStrategy implements AggregationStrategy {

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        return null;
    }
}
