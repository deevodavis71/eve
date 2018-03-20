package com.sjd.eve.impl.event;

import com.sjd.eve.cqrs.core.Event;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * User: stevedavis
 * Date: 20/03/2018
 * Time: 16:58
 * Description:
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class NumberSubtractedEvent extends Event {

    private long data;

}
