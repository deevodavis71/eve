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
import com.sjd.eve.cqrs.core.AbstractAggregate;
import com.sjd.eve.cqrs.core.AbstractCommand;
import com.sjd.eve.cqrs.core.AbstractEvent;
import com.sjd.eve.cqrs.domain.Aggregate;
import com.sjd.eve.cqrs.domain.Event;
import com.sjd.eve.cqrs.repository.AggregateRepository;
import com.sjd.eve.cqrs.repository.EventRepository;
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
    private EventRepository eveRepository;

    private Map<Class<? extends AbstractCommand>, Class<? extends AbstractAggregate>> registrations = new HashMap<>();

    public void registerCommand(Class<? extends AbstractCommand> command, Class<? extends AbstractAggregate> aggregate) {

        registrations.put(command, aggregate);

    }

    public String process(AbstractCommand command, String aggregateId) throws Exception {

        // Start TX

        // Create the empty aggregate
        AbstractAggregate agg = initialiseAggregate(command);

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

            List<Event> events = eveRepository.findByAggregateIdOrderByEventId(aggregateId);

            // Apply the events in sequence to rebuild the aggregate to its current state
            if (events != null && !events.isEmpty()) {

                ObjectMapper mapper = new ObjectMapper();

                // Get each event type and reconstruct it
                for(Event event: events) {

                    AbstractEvent absEvent = (AbstractEvent) mapper.readValue(event.getEventData(), Class.forName(event.getEventType()));
                    applyEventToAggregate(agg, absEvent);

                }

            }

        }


        // Apply this command to the aggregate, getting back a list of events it generated
        List<AbstractEvent> newEvents = applyCommandToAggregate(agg, command);

        // Persist any new events to the event store
        if (newEvents != null && !newEvents.isEmpty())
            persistNewEvents(agg, newEvents);

        return aggregateId;

        // Commit TX

    }

    private void persistNewEvents(AbstractAggregate agg, List<AbstractEvent> newEvents) throws Exception {

        // If we have any new events we save these back now
        if (newEvents != null && !newEvents.isEmpty()) {

            for(AbstractEvent ae: newEvents) {

                Event eve = new Event();
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

    private List<AbstractEvent> applyCommandToAggregate(AbstractAggregate agg, AbstractCommand absCommand) throws Exception {

        // Locate the handler method that takes this command
        Method method = ReflectionUtils.findMethod(agg.getClass(), "process", absCommand.getClass());
        if (method != null) {

            return (List<AbstractEvent>) method.invoke(agg, absCommand);

        }

        return null;
    }

    private void applyEventToAggregate(AbstractAggregate agg, AbstractEvent absEvent) throws Exception {

        // Locate the handler method that takes this event
        Method method = ReflectionUtils.findMethod(agg.getClass(), "apply", absEvent.getClass());
        if (method != null) {

            method.invoke(agg, absEvent);

        }

    }

    private AbstractAggregate initialiseAggregate(AbstractCommand command) throws Exception {

        // Get the aggregate
        Class<? extends AbstractAggregate> clz = registrations.get(command.getClass());

        if (clz != null) {

            // Build the initial aggregate
            return clz.newInstance();

        }

        throw new NotImplementedException();
    }

}
