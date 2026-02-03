package frc.robot.controls;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import frc.robot.Constants.FieldConstants;
import frc.robot.Constants.OperatorConstants;
import frc.robot.commands.DriveToPoseCommand;
import frc.robot.subsystems.PhotonVisionSubsystem;

import java.util.ArrayList;
import java.util.List;

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
 * A Trigger object represents a logical condition that can activate commands when it becomes true or false.
 *
 * 1) Define a Trigger object
 *    Trigger leftAnalogTriggerHorizontal;
 *    - This will declare a member variable, in the Controller class, of type Trigger
 *    - It has not been instantiated (created) yet, we just declared it
 *
 * 2) Instantiate the Trigger object
 *    leftAnalogTriggerHorizontal = new Trigger(() -> {
 *          leftAxisHorizontalPos = joystick.getLeftX();
 *          return (DriverStation.isTeleop() && Math.abs(leftAxisHorizontalPos) > 0.5);
 *          })
 *    - The call of 'new Trigger(...)' instantiates a new Trigger object
 *    - The '=' will then initialize the leftAnalogTriggerHorizontal member variable to the new Trigger object
 *    - The Trigger constructor takes a BooleanSupplier, which we can generate with a lambda.
 *    - In Java, a lambda expression is shorthand for creating an instance of a functional interface.
 *    - In other words:
 *      - The Trigger constructor expects a BooleanSupplier.
 *      - You give it a lambda that returns a boolean.
 *      - The Java compiler says: "That lambda matches the signature of BooleanSupplier.getAsBoolean()."
 *      - It automatically wraps your lambda in a BooleanSupplier object.
 *
 * 3) The new leftAnalogTriggerHorizontal Trigger must now be added to the WPILib Command Scheduling System
 *    leftAnalogTriggerHorizontal.whileTrue(new RunCommand(() -> {...}
 *    - The call to whileTrue will register a listener (an event binding) with the global CommandScheduler instance.
 *    - It says: "CommandScheduler - every time you run, check this trigger's Boolean supplier, and if it is true, schedule this command."
 *
 * 4) The Robot.robotPeriodic() method is called every 20 ms and iterates through all registered triggers, commands, and subsystems.
 * 
 * Note: A Trigger is a BooleanSupplier, which is a functional interface in Java
 * - A BooleanSupplier is an object that can supply (return) a boolean value when asked.
 */

public class Controller {
    Field2d field;
    PhotonVisionSubsystem m_photonVision;
    private final XboxController joystick;
    List<Trigger> buttonTriggers = new ArrayList<>();
    List<Trigger> axisTriggers = new ArrayList<>();
    Pose2d currentPose = new Pose2d();
    private double leftAxisHorizontalPos = 0.0;
    private double leftAxisVerticalPos = 0.0;

    Trigger leftAnalogTriggerHorizontal;
    Trigger leftAnalogTriggerVertical;
    Trigger rightAnalogTriggerHorizontal;
    Trigger rightAnalogTriggerVertical;
    Trigger xButton;
    Trigger squareButton;

    private DriveToPoseCommand driveToPoseCommand;
    
    private int buttonCount;
    public enum RobotMode { DISABLED, AUTONOMOUS, TELEOP, TEST };

    /** Constructor */
    public Controller(Field2d field2d, PhotonVisionSubsystem photonVision) {
        field = field2d;
        m_photonVision = photonVision;
        joystick = new XboxController(OperatorConstants.DRIVER_CONTROLLER_PORT);
        buttonCount = 0;
    }
    
    /** Play controller:
     * - Left analog stick left and right is XBoxController getLeftX (moving Left is negative)
     * - Left analog stick up and down is is XBoxController getLeftY (Up is negative, goes to -1)
     * 
     * @see enableButtonHandler for where the buttonTriggers are used
     */
    public void initialize() {
        buttonTriggers.clear();

        buttonCount = joystick.getButtonCount();

        squareButton = new JoystickButton(joystick, XboxController.Button.kA.value)
        .onTrue(new InstantCommand(() -> {
            // Only schedule if in teleop
            if (DriverStation.isTeleop()) {
                Pose2d pose = getPhotonPose();
                System.out.println("Square button pressed. PhotonVision pose: " + pose.toString());
                if ( (pose.getX() != 0.0 || pose.getY() != 0.0) &&
                     (driveToPoseCommand == null || !driveToPoseCommand.isScheduled())) {
                    driveToPoseCommand = new DriveToPoseCommand(this, pose);
                    driveToPoseCommand.schedule();
                }
            }
        }));    

        xButton = new JoystickButton(joystick, XboxController.Button.kB.value)
        .onTrue(new InstantCommand(() -> {
            // Only act if in teleop
            if (!DriverStation.isTeleop()) return;
    
            if (driveToPoseCommand != null && driveToPoseCommand.isScheduled()) {
                driveToPoseCommand.cancel();
                driveToPoseCommand = null;
            }
        }));

        leftAnalogTriggerHorizontal = new Trigger(() -> {
                leftAxisHorizontalPos = joystick.getLeftX();  // -1 to 1
                return (DriverStation.isTeleop() && Math.abs(leftAxisHorizontalPos) > 0.5);
            })
            .whileTrue(new RunCommand(() -> {
                if (driveToPoseCommand != null && driveToPoseCommand.isScheduled()) {
                    driveToPoseCommand.cancel();  // Stop the command immediately
                    driveToPoseCommand = null;
                }
        
                Translation2d currentTranslation = currentPose.getTranslation();
                Rotation2d currentRotation = currentPose.getRotation();
        
                // Offset X based on left stick
                double deltaX = leftAxisHorizontalPos * 0.1;  // scale for realism
                double deltaY = 0;
        
                currentTranslation = currentTranslation.plus(new Translation2d(deltaX, deltaY));
        
                // Clamp translation within field bounds
                double clampedX = MathUtil.clamp(currentTranslation.getX(), 0.0, FieldConstants.FIELD_LENGTH_METERS);
                double clampedY = MathUtil.clamp(currentTranslation.getY(), 0.0, FieldConstants.FIELD_WIDTH_METERS);
        
                currentTranslation = new Translation2d(clampedX, clampedY);
        
                currentPose = new Pose2d(currentTranslation, currentRotation);
                field.setRobotPose(currentPose);
            }));

        leftAnalogTriggerVertical = new Trigger(() -> {
                // Use XboxController method to read the left stick Y axis
                leftAxisVerticalPos = joystick.getLeftY();  // -1 to 1
                return (DriverStation.isTeleop() && Math.abs(leftAxisVerticalPos) > 0.5);
            })
            .whileTrue(new RunCommand(() -> {
                if (driveToPoseCommand != null && driveToPoseCommand.isScheduled()) {
                    driveToPoseCommand.cancel();  // Stop the command immediately
                    driveToPoseCommand = null;
                }
        
                Translation2d currentTranslation = currentPose.getTranslation();
                Rotation2d currentRotation = currentPose.getRotation();
        
                // Offset Y based on left stick (invert to match field coordinates)
                double deltaY = leftAxisVerticalPos * -0.1;  // scale and invert
                double deltaX = 0;
        
                currentTranslation = currentTranslation.plus(new Translation2d(deltaX, deltaY));
        
                // Clamp translation within field bounds
                double clampedX = MathUtil.clamp(currentTranslation.getX(), 0.0, FieldConstants.FIELD_LENGTH_METERS);
                double clampedY = MathUtil.clamp(currentTranslation.getY(), 0.0, FieldConstants.FIELD_WIDTH_METERS);
        
                currentTranslation = new Translation2d(clampedX, clampedY);
        
                currentPose = new Pose2d(currentTranslation, currentRotation);
                field.setRobotPose(currentPose);
            }));
    }

    public void showStatus() {
        System.out.println("Joystick name: " + joystick.getName());
        System.out.println("Joystick axis count: " + joystick.getAxisCount());
        System.out.println("Joystick button count: " + joystick.getButtonCount());
    }

    public Pose2d getCurrentPose() {        
        //System.out.println("getCurrentPose: " + currentPose.toString());
        return currentPose; 
    }

    public Pose2d getPhotonPose() {        
        // Attempt to get the pose from the PhotonVisionSubsystem
        Pose2d detectedPose = m_photonVision.getLatestPose(); // Replace with your actual method
        
        if (detectedPose != null) {
            // If a valid pose is detected, return it
            System.out.println("PhotonVision Pose found: " + detectedPose);
            return detectedPose;
        }
        else {
            // Return an "empty" pose (0,0,0) to indicate no detection
            Pose2d emptyPose = new Pose2d();
            System.out.println("PhotonVision Pose not found. Returning empty pose: " + emptyPose);
            return emptyPose;
        }
    }

    public void updatePose(double dx, double dy, double dtheta) {
        double newX = currentPose.getX() + dx;
        double newY = currentPose.getY() + dy;
        double newRot = currentPose.getRotation().getDegrees() + dtheta;
        currentPose = new Pose2d(newX, newY, Rotation2d.fromDegrees(newRot));
        //System.out.println("updatePose: " + currentPose.toString());
        field.setRobotPose(currentPose);
    }
}
