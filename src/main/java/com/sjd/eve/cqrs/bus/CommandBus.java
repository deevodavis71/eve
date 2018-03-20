package com.sjd.eve.cqrs.bus;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sjd.eve.cqrs.core.AggregateRoot;
import com.sjd.eve.cqrs.core.Command;
import com.sjd.eve.cqrs.core.Event;
import com.sjd.eve.cqrs.domain.Aggregate;
import com.sjd.eve.cqrs.domain.AggregateEvent;
import com.sjd.eve.cqrs.repository.AggregateRepository;
import com.sjd.eve.cqrs.repository.AggregateEventRepository;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * User: stevedavis
 * Date: 20/03/2018
 * Time: 16:47
 * Description:
 */
@Component
public class CommandBus {

    @Autowired
    private AggregateRepository aggRepository;

    @Autowired
    private AggregateEventRepository eveRepository;

    private Map<Class<? extends Command>, Class<? extends AggregateRoot>> registrations = new HashMap<>();

    public void registerCommand(Class<? extends Command> command, Class<? extends AggregateRoot> aggregate) {

        registrations.put(command, aggregate);

    }

    public String process(Command command, String aggregateId) throws Exception {

        // Start TX

        // Create the empty aggregate
        AggregateRoot agg = initialiseAggregate(command);

        // Get the aggregate type (to see if it already exists - no aggregate, no events)
        if (aggregateId == null) {

            // Add an entry into the aggregates table

            aggregateId = UUID.randomUUID().toString();

            Aggregate a = new Aggregate();
            a.setAggregateId(aggregateId);
            a.setAggregateType(agg.getClass().getCanonicalName());

            aggRepository.save(a);

        } else {

            // Check that we have an aggregate with this ID

            Aggregate a = aggRepository.findOne(aggregateId);
            if (a == null)
                throw new Exception("Aggregate Id not found:" + aggregateId);

        }



        // Set the ID
        agg.setAggregateId(aggregateId);

        // Load any events from the eventstore for this persistence Id
        if (agg != null) {

            List<AggregateEvent> events = eveRepository.findByAggregateIdOrderByEventId(aggregateId);

            // Apply the events in sequence to rebuild the aggregate to its current state
            if (events != null && !events.isEmpty()) {

                ObjectMapper mapper = new ObjectMapper();

                // Get each event type and reconstruct it
                for(AggregateEvent event: events) {

                    Event absEvent = (Event) mapper.readValue(event.getEventData(), Class.forName(event.getEventType()));
                    applyEventToAggregate(agg, absEvent);

                }

            }

        }


        // Apply this command to the aggregate, getting back a list of events it generated
        List<Event> newEvents = applyCommandToAggregate(agg, command);

        // Persist any new events to the event store
        if (newEvents != null && !newEvents.isEmpty())
            persistNewEvents(agg, newEvents);

        return aggregateId;

        // Commit TX

    }

    private void persistNewEvents(AggregateRoot agg, List<Event> newEvents) throws Exception {

        // If we have any new events we save these back now
        if (newEvents != null && !newEvents.isEmpty()) {

            for(Event ae: newEvents) {

                AggregateEvent eve = new AggregateEvent();
                eve.setAggregateId(agg.getAggregateId());

                Calendar cal = Calendar.getInstance();
                Date date = cal.getTime();
                String dateAsHex = Long.toHexString(date.getTime()).toUpperCase();

                eve.setEventId(dateAsHex);
                eve.setEventType(ae.getClass().getCanonicalName());

                ObjectMapper mapper = new ObjectMapper();
                String eventData = mapper.writeValueAsString(ae);
                eve.setEventData(eventData);

                eveRepository.save(eve);

            }

        }

    }

    private List<Event> applyCommandToAggregate(AggregateRoot agg, Command absCommand) throws Exception {

        // Locate the handler method that takes this command
        Method method = ReflectionUtils.findMethod(agg.getClass(), "process", absCommand.getClass());
        if (method != null) {

            return (List<Event>) method.invoke(agg, absCommand);

        }

        return null;
    }

    private void applyEventToAggregate(AggregateRoot agg, Event absEvent) throws Exception {

        // Locate the handler method that takes this event
        Method method = ReflectionUtils.findMethod(agg.getClass(), "apply", absEvent.getClass());
        if (method != null) {

            method.invoke(agg, absEvent);

        }

    }

    private AggregateRoot initialiseAggregate(Command command) throws Exception {

        // Get the aggregate
        Class<? extends AggregateRoot> clz = registrations.get(command.getClass());

        if (clz != null) {

            // Build the initial aggregate
            return clz.newInstance();

        }

        throw new NotImplementedException();
    }

}
