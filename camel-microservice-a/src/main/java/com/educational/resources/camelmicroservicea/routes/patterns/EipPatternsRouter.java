package com.educational.resources.camelmicroservicea.routes.patterns;

import com.educational.resources.camelmicroservicea.CurrencyExchange;
import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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
                .to("log:something1", "log:something2", "log:something3");
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
                .unmarshal().json(JsonLibrary.Jackson, CurrencyExchange.class)
                .aggregate(simple("{body.to}"), new ArrayListAggregationStrategy())
                .completionSize(3)
                //.completionTimeout(HIGHEST)
                .to("log:aggregate-json");

        //routing slip
        //String routingSlip = "direct:endpoint1, direct:endpoint2, direct:endpoint3"
        String routingSlip = "direct:endpoint1,direct:endpoint2";

        from("timer:routingSlip?period=10000")
                .transform().constant("Hardcoded Message")
                .routingSlip(simple(routingSlip));

        from("direct:endpoint1")
                .to("log:directendpoint1");

        from("direct:endpoint2")
                .to("log:directendpoint2");

        from("direct:endpoint3")
                .to("log:directendpoint3");

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
        Object newBody = newExchange.getIn().getBody();
        ArrayList<Object> list = null;

        if (oldExchange == null) {
            list = new ArrayList<Object>();
            list.add(newBody);
            newExchange.getIn().setBody(list);

            return newExchange;

        } else {

            list = oldExchange.getIn().getBody(ArrayList.class);
            list.add(newBody);
            return oldExchange;
        }

    }
}
