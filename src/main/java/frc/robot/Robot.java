// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

/** Typical FRC Project folder structure:
 * src/main/java/frc/robot/
 * ├── Robot.java
 * ├── Main.java
 * ├── RobotContainer.java
 * ├── commands/
 * │    └── ExampleCommand.java
 * └── subsystems/
 *      └── DriveSubsystem.java
 * └── controls/
 *      └── Controller.java
 *
 * Robot.java handles robot states such as autonomous, teleop..
 * - This is mostly the 'when' the robot does things.
 * 
 * RobotContainer.java creates subsystems, commands, controllers, button bindings, and the autonomous routine.
 * - This is mostly 'what' the robot does.
 * 
 * The methods in this class are called automatically corresponding to each mode, as described in
 * the TimedRobot documentation.
 */
public class Robot extends TimedRobot {
  private RobotContainer robotContainer;
  //private Joystick joystickController;
  static int teleopCounter = 0;

  /** Constructor
   * Do not put code in here that relies on other systems to be ready.
   *
   * The HAL (hardware abstraction layer) and other systems like SmartDashboard, Field2d,
   * and SendableChooser may not be fully initialized when the constructor runs.
   */
  public Robot() {
    robotContainer = new RobotContainer();
  }

  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    System.out.println("robotInit.");

    NetworkTableInstance.getDefault().startServer(); // Starts the NetworkTables server
  }

  /**
   * This function is called every 20 ms, no matter the mode. Use this for items like diagnostics
   * that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before LiveWindow and
   * SmartDashboard integrated updating.
   *
   * The CommandScheduler is responsible for managing the execution of commands in a command-based robot program.
   * It is mandatory to iterate through the Triggers, Commands, and SubSystems
   */
  @Override
  public void robotPeriodic() {
    CommandScheduler.getInstance().run();
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select between different
   * autonomous modes using the dashboard. The sendable chooser code works with the Java
   * SmartDashboard. If you prefer the LabVIEW Dashboard, remove all of the chooser code and
   * uncomment the getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to the switch structure
   * below with additional strings. If using the SendableChooser make sure to add them to the
   * chooser code above as well.
   */
  @Override
  public void autonomousInit() {}

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {}

  /** This function is called once when teleop is enabled. */
  @Override
  public void teleopInit() {
    robotContainer.configureBindings();
  }

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {}

  /** This function is called once when the robot is disabled. */
  @Override
  public void disabledInit() {}

  /** This function is called periodically when disabled. */
  @Override
  public void disabledPeriodic() {}

  /** This function is called once when test mode is enabled. */
  @Override
  public void testInit() {}

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {}

  /** This function is called once when the robot is first started up. */
  @Override
  public void simulationInit() {}

  /** This function is called periodically whilst in simulation. */
  @Override
  public void simulationPeriodic() {}
}
