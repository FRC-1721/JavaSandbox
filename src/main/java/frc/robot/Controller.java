package frc.robot;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import frc.robot.Constants.FieldConstants;
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

/** Trigger
 * 
 * TBD
 */

/** Command 
 * 
 * TBD
 */
public class Controller {
    Field2d field;
    private final Joystick joystick;
    List<Trigger> buttonTriggers = new ArrayList<>();
    List<Trigger> axisTriggers = new ArrayList<>();
    Pose2d currentPose = new Pose2d();
    private double leftAxisHorizontalPos = 0.0;
    private double leftAxisVerticalPos = 0.0;
    private double rightAxisHorizontalPos = 0.0;

    Trigger leftAnalogTriggerHorizontal;
    Trigger leftAnalogTriggerVertical;
    Trigger rightAnalogTriggerHorizontal;
    Trigger rightAnalogTriggerVertical;
    private int buttonCount;
    private boolean enabled = false;
    public enum RobotMode { DISABLED, AUTONOMOUS, TELEOP, TEST };

    /** Constructor */
    public Controller(Field2d field2d) {
        field = field2d;
        joystick = new Joystick(OperatorConstants.DRIVER_CONTROLLER_PORT);
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
     * ----------------------------
     * Play controller:
     * - Left analog stick left and right is axis[0] (Left is negative)
     * - Left analog stick up and down is axis[1] (Up is negative, goes to -1)
     * - Right analog stick left and right is axis[2] (Left is negative)
     * - Right analog stick up and down is axis[2] (Up is negative, goes to -1)
     * - The digital pad is POVs[0]
     * 
     * @see enableButtonHandler for where the buttonTriggers are used
     */
    private void initialize() {
        buttonTriggers.clear();

        buttonCount = joystick.getButtonCount();

        IntStream.range(1, buttonCount + 1).forEach(i -> {
            buttonTriggers.add(new Trigger( () -> enabled && joystick.getRawButtonPressed(i)));
        });

        // The RunCommand() method will run repeatedly while active, as opposed to InstantCommand that runs once
        leftAnalogTriggerHorizontal = new Trigger(() -> {
            leftAxisHorizontalPos = joystick.getRawAxis(0);
            return (Math.abs(leftAxisHorizontalPos) > 0.5);
            })
            .whileTrue(new RunCommand(() -> 
                {
                    Translation2d currentTranslation = currentPose.getTranslation();
                    Rotation2d currentRotation = currentPose.getRotation();
                
                    // Create a new translation offset (X increment)
                    double deltaX = leftAxisHorizontalPos * .1; // scale for realism
                    double deltaY = 0; 
                
                    currentTranslation = currentTranslation.plus(new Translation2d(deltaX, deltaY));                    
                
                    // Clamp the translation within the Reefscape field bounds
                    double clampedX = MathUtil.clamp(currentTranslation.getX(), 0.0, FieldConstants.FIELD_LENGTH_METERS);
                    double clampedY = MathUtil.clamp(currentTranslation.getY(), 0.0, FieldConstants.FIELD_WIDTH_METERS);

                    // Reconstruct the translation after clamping
                    currentTranslation = new Translation2d(clampedX, clampedY);                
              
                    currentPose = new Pose2d(currentTranslation, currentRotation); 

                    field.setRobotPose(currentPose);
                    System.out.println("Left stick " + leftAxisHorizontalPos);
                }));
                
        leftAnalogTriggerVertical = new Trigger(() -> {
            leftAxisVerticalPos = joystick.getRawAxis(1);
            return (Math.abs(leftAxisVerticalPos) > 0.5);
            })
            .whileTrue(new RunCommand(() -> 
                {
                    Translation2d currentTranslation = currentPose.getTranslation();
                    Rotation2d currentRotation = currentPose.getRotation();
                
                    // Create a new translation offset (Y increment)
                    double deltaY = (leftAxisVerticalPos * .1)*-1; // scale for realism and inverting position                                         
                    double deltaX = 0; 

                    currentTranslation = currentTranslation.plus(new Translation2d(deltaX, deltaY));                    
                
                    // Clamp the translation within the Reefscape field bounds
                    double clampedX = MathUtil.clamp(currentTranslation.getX(), 0.0, FieldConstants.FIELD_LENGTH_METERS);
                    double clampedY = MathUtil.clamp(currentTranslation.getY(), 0.0, FieldConstants.FIELD_WIDTH_METERS);

                    // Reconstruct the translation after clamping
                    currentTranslation = new Translation2d(clampedX, clampedY);
                                
                    currentPose = new Pose2d(currentTranslation, currentRotation); 

                    field.setRobotPose(currentPose);
                    System.out.println("Left stick " + leftAxisVerticalPos);
                }));
            

        rightAnalogTriggerHorizontal = new Trigger(() -> {
            rightAxisHorizontalPos = joystick.getRawAxis(2);
            return (Math.abs(rightAxisHorizontalPos) > 0.5);
            })
            .onTrue(new InstantCommand(() -> System.out.println("Right stick " + rightAxisHorizontalPos)));
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
