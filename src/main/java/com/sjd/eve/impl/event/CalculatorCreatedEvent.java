package com.sjd.eve.impl.event;

import com.sjd.eve.cqrs.core.AbstractEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * User: stevedavis
 * Date: 20/03/2018
 * Time: 16:58
 * Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class CalculatorCreatedEvent extends AbstractEvent {

    private long initialValue;

}
