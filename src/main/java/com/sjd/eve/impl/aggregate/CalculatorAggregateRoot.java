package com.sjd.eve.impl.aggregate;

import java.util.List;

import com.sjd.eve.cqrs.core.AggregateRoot;
import com.sjd.eve.cqrs.core.Event;
import com.sjd.eve.cqrs.utils.EventUtils;
import com.sjd.eve.impl.command.AddNumberCommand;
import com.sjd.eve.impl.command.CreateCalculatorCommand;
import com.sjd.eve.impl.command.SubtractNumberCommand;
import com.sjd.eve.impl.event.CalculatorCreatedEvent;
import com.sjd.eve.impl.event.NumberAddedEvent;
import com.sjd.eve.impl.event.NumberSubtractedEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * User: stevedavis
 * Date: 20/03/2018
 * Time: 17:01
 * Description:
 */
@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class CalculatorAggregateRoot extends AggregateRoot {

    private long total;

    public List<Event> process(CreateCalculatorCommand command) {

        log.info("COMMAND: CreateCalculatorCommand, {}", command.getInitialValue());

        total = command.getInitialValue();

        log.info("COMMAND: Current value : {}", total);

        return EventUtils.events(new CalculatorCreatedEvent(command.getInitialValue()));
    }

    public List<Event> process(AddNumberCommand command) {

        log.info("COMMAND: AddNumberCommand, {}", command.getData());

        total = total + command.getData();

        log.info("COMMAND: Current value : {}", total);

        return EventUtils.events(new NumberAddedEvent(command.getData()));
    }

    public List<Event> process(SubtractNumberCommand command) {

        log.info("COMMAND: SubtractNumberCommand, {}", command.getData());

        total = total - command.getData();

        log.info("COMMAND: Current value : {}", total);

        return EventUtils.events(new NumberSubtractedEvent(command.getData()));
    }

    public void apply(CalculatorCreatedEvent event) {

        log.info("EVENT: CalculatorCreatedEvent, {}", event.getInitialValue());

        total = event.getInitialValue();

    }

    public void apply(NumberAddedEvent event) {

        log.info("EVENT: NumberAddedEvent, {}", event.getData());

        total = total + event.getData();

    }

    public void apply(NumberSubtractedEvent event) {

        log.info("EVENT: NumberSubtractedEvent, {}", event.getData());

        total = total - event.getData();

    }
}
