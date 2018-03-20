package com.sjd.eve.impl.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sjd.eve.cqrs.bus.CommandBus;
import com.sjd.eve.impl.aggregate.CalculatorAggregate;
import com.sjd.eve.impl.command.AddNumberCommand;
import com.sjd.eve.impl.command.CreateCalculatorCommand;
import com.sjd.eve.impl.command.SubtractNumberCommand;
import lombok.RequiredArgsConstructor;

/**
 * User: stevedavis
 * Date: 20/03/2018
 * Time: 17:32
 * Description:
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CalculatorWebController {

    private CommandBus commandBus;

    @Autowired
    public CalculatorWebController(CommandBus commandBus) {

        this.commandBus = commandBus;

        this.commandBus.registerCommand(CreateCalculatorCommand.class, CalculatorAggregate.class);
        this.commandBus.registerCommand(AddNumberCommand.class, CalculatorAggregate.class);
        this.commandBus.registerCommand(SubtractNumberCommand.class, CalculatorAggregate.class);

    }

    @PostMapping("/create/{id}/{data}")
    public String createCalculator(@PathVariable("id") String id, @PathVariable("data") Long data) throws Exception {

        this.commandBus.process(new CreateCalculatorCommand(data), id);
        return id;

    }

    @PutMapping("/add/{id}/{data}")
    public void addNumber(@PathVariable("id") String id, @PathVariable("data") Long data) throws Exception {

        this.commandBus.process(new AddNumberCommand(data), id);

    }

    @PutMapping("/subtract/{id}/{data}")
    public void subtractNumber(@PathVariable("id") String id, @PathVariable("data") Long data) throws Exception {

        this.commandBus.process(new SubtractNumberCommand(data), id);

    }
}
