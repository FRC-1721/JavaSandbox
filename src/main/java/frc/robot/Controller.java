package frc.robot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.Constants.OperatorConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/** Fluent Interface
 * 
 * A Fluent Interface is a design pattern that allows chaining method calls in a way that reads like natural language.
 * 
 * It can do this by having methods return the current object (this) or another object that allows further method calls.
 * 
 * Example...
 * new JoystickButton(driverController, Button.kA.value)
 *          .onTrue(new ExampleCommand());
 * 
 * This creates a JoystickButton object and immediately calls the onTrue method on it, passing in a new ExampleCommand object.
 */
public class Controller {
    private final Joystick joystick;
    List<Trigger> buttonTriggers = new ArrayList<>();
    private int axisCount;
    private int buttonCount;
    private boolean enabled = false;
    public enum RobotMode { DISABLED, AUTONOMOUS, TELEOP, TEST };

    /** Constructor */
    public Controller() {
        joystick = new Joystick(OperatorConstants.DRIVER_CONTROLLER_PORT);
        axisCount = 0;
        buttonCount = 0;
    }
    
    /**
     * Description of:
     *     buttonTriggers.add(new Trigger( () -> enabled && joystick.getRawButtonPressed(i)));
     * 
     * new Trigger
     * - Create a new Trigger object fro mthe WPILib
     * - A Trigger is a was to define custom condition that can activate a command
     * 
     * () -> enabled && joystick.getRawButtonPressed(i)
     * - This is a lambda expression
     * - It returns true when the button i is pressed and the controller is enabled
     * 
     * buttonTriggers.add
     * - Adds the newly created Trigger to the buttonTriggers list
     * - This allows us to store and later iterate over all triggers for binding command and other logic
     * 
     * @see enableButtonHandler for where the buttonTriggers are used
     */
    private void initialize() {
        buttonTriggers.clear();

        axisCount = joystick.getAxisCount();
        buttonCount = joystick.getButtonCount();
        IntStream.range(1, buttonCount + 1).forEach(i -> {
            buttonTriggers.add(new Trigger( () -> enabled && joystick.getRawButtonPressed(i)));
        });
    }

    /**
     * This method should be called after robotInit(). If it is called in robotInit() it will not work
     * because the HAL (Hardware Abstraction Layer) is not fully initialized yet.
     * 
     * Description of:
     *     trigger.onTrue(new InstantCommand(() -> System.out.println(trigger.toString())));
     * 
     * trigger.onTrue
     * - This is a method from WPILib's Trigger class
     * - It schedules a command to run once the trigger condition transition from false to true
     * 
     * new InstantCommand
     * - InstantCommand is a WPILib command that runs a short, one-time action and then finishes immediately
     * - It is useful for simple tasks like logging or setting a state
     * 
     * () -> System.out.println(trigger.toString())
     * - This is a lambda expression that defines an action to run
     * - It prints the string representation of the trigger object to the console
     * 
     * @param enableButtonHandling True if button handling should be enabled, false to disable it.
     */
    public void enableButtonHandler(boolean enableButtonHandling, RobotMode robotMode) {
        enabled = enableButtonHandling;   
        initialize(); 

        // TODO: Currently this only works for 1 mode. I need to dynamically change modes.
        if (robotMode == RobotMode.TEST) {
            for (Trigger trigger : buttonTriggers) {             
                trigger.onTrue(new InstantCommand(() -> System.out.println("Test " + trigger.toString())));       
            }
        }
        else if (robotMode == RobotMode.TELEOP) {
            for (Trigger trigger : buttonTriggers) {
                trigger.onTrue(new InstantCommand(() -> System.out.println("TELEOP " + trigger.toString())));
            }
        }
    }

    public void showStatus() {
        System.out.println("Joystick name: " + joystick.getName());
        System.out.println("Joystick axis count: " + joystick.getAxisCount());
        System.out.println("Joystick button count: " + joystick.getButtonCount());
    }
}
