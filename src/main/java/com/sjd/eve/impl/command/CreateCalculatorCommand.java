package com.sjd.eve.impl.command;

import com.sjd.eve.cqrs.core.Command;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * User: stevedavis
 * Date: 20/03/2018
 * Time: 16:55
 * Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class CreateCalculatorCommand extends Command {

    private long initialValue;

}
