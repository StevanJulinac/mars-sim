/**
 * Mars Simulation Project
 * CommanderInfo.java
 * @version 3.1.0 2018-09-24
 * @author Manny Kung
 */

package org.mars_sim.msp.core.terminal;

import org.beryx.textio.*;
import org.beryx.textio.app.AppUtil;
import org.beryx.textio.web.RunnerData;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.beryx.textio.ReadInterruptionStrategy.Action.ABORT;

/**
 * Illustrates how to use read handlers to allow going back to a previous field.
 */
public class CommanderInfo implements BiConsumer<TextIO, RunnerData> {
    private static class Contact {
        String firstName;
        String lastName;
        String gender;
        String age;
        
        @Override
        public String toString() {
            return "\n\tFirst Name: " + firstName +
                    "\n\tLast Name: " + lastName +
                    "\n\tGender: " + gender +
                    "\n\tAge: " + age
                    ;
        }
    }

    private final Contact contact = new Contact();
    private final List<Runnable> operations = new ArrayList<>();

    public static void main(String[] args) {
        TextIO textIO = TextIoFactory.getTextIO();
        new CommanderInfo().accept(textIO, null);
    }

    @Override
    public void accept(TextIO textIO, RunnerData runnerData) {
        TextTerminal<?> terminal = textIO.getTextTerminal();
        String initData = (runnerData == null) ? null : runnerData.getInitData();
        AppUtil.printGsonMessage(terminal, initData);

        addTask(textIO, "First name", () -> contact.firstName, s -> contact.firstName = s);
        addTask(textIO, "Last name", () -> contact.lastName, s -> contact.lastName = s);
        addTask(textIO, "Gender", () -> contact.gender, s -> contact.gender = s);
        addTask(textIO, "Age", () -> contact.age, s -> contact.age = s);

        String backKeyStroke = "Ctrl-U";
        boolean registered = terminal.registerHandler(backKeyStroke, t -> new ReadHandlerData(ABORT));
        if(registered) {
            terminal.println("During data entry you can press '" + backKeyStroke + "' to go back to the previous field.\n");
        }
        int step = 0;
        while(step < operations.size()) {
            terminal.setBookmark("bookmark_" + step);
            try {
                operations.get(step).run();
            } catch (ReadAbortedException e) {
                if(step > 0) step--;
                terminal.resetToBookmark("bookmark_" + step);
                continue;
            }
            step++;
        }

        terminal.println("\nCommander's Profile: " + contact);

        textIO.newStringInputReader().withMinLength(0).read("\nPress enter to terminate...");
        textIO.dispose();
    }

    private void addTask(TextIO textIO, String prompt, Supplier<String> defaultValueSupplier, Consumer<String> valueSetter) {
        operations.add(() -> valueSetter.accept(textIO.newStringInputReader()
                .withDefaultValue(defaultValueSupplier.get())
                .read(prompt)));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ": reading commander's profile.\n" +
                "(Illustrates how to use read handlers to allow going back to a previous field.)";
    }
}
