// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import java.util.List;

import org.photonvision.PhotonCamera;
import org.photonvision.targeting.PhotonPipelineResult;
import org.photonvision.targeting.PhotonTrackedTarget;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The methods in this class are called automatically corresponding to each mode, as described in
 * the TimedRobot documentation. If you change the name of this class or the package after creating
 * this project, you must also update the Main.java file in the project.
 */
public class Robot extends TimedRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();
  private final Field2d m_field = new Field2d();
  private PhotonCamera camera;
  private Joystick joystickController;
  static int teleopCounter = 0;

  /** Constructor
   * Do not put code in here that relies on other systems to be ready.
   *
   * The HAL (hardware abstraction layer) and other systems like SmartDashboard, Field2d,
   * and SendableChooser may not be fully initialized when the constructor runs.
   */
  public Robot() {

  }

  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    System.out.println("robotInit.");

    NetworkTableInstance.getDefault().startServer(); // Starts the NetworkTables server

    camera = new PhotonCamera("Arducam_OV9281_USB_Camera");

    // Initialize the joystick
    joystickController = new Joystick(0);


    for (int i = 0; i < 6; i++) {
      System.out.println("Joystick " + i + " name: " + DriverStation.getJoystickName(i));
  }

    // Do this in either robot or subsystem init
    SmartDashboard.putData("Field", m_field);
    // Do this in either robot periodic or subsystem periodic
    Pose2d testPose = new Pose2d();
    m_field.setRobotPose(testPose);

    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);
  }

  /**
   * This function is called every 20 ms, no matter the mode. Use this for items like diagnostics
   * that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before LiveWindow and
   * SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {}

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
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
    switch (m_autoSelected) {
      case kCustomAuto:
        // Put custom auto code here
        break;
      case kDefaultAuto:
      default:
        // Put default auto code here
        break;
    }
  }

  /** This function is called once when teleop is enabled. */
  @Override
  public void teleopInit() 
  {
    // Query the latest result from PhotonVision
    List<PhotonPipelineResult> results = camera.getAllUnreadResults();
    if (!results.isEmpty())
    {
      PhotonPipelineResult result = results.get(results.size() - 1);
      boolean hasTargets = result.hasTargets();
      System.out.println("hasTargets = " + hasTargets);

      if (hasTargets)
      {
        // Get a list of currently tracked targets.
        List<PhotonTrackedTarget> targets = result.getTargets();

        // Show the Photoncamera data for all of the AprilTag targets found
        for (PhotonTrackedTarget target : targets)
        {
          // Get information from target.
          //double yaw = target.getYaw();
          //double pitch = target.getPitch();
          //double area = target.getArea();
          //double skew = target.getSkew();

          //System.out.println("yaw = " + yaw);
          //System.out.println("pitch = " + pitch);
          //System.out.println("area = " + area);
          //System.out.println("skew = " + skew);

          System.out.println("-".repeat(20) + "April tag: " + target.getFiducialId() + "-".repeat(20));
          System.out.println(target.toString());
        }
      }
    }
  }

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {
    // Increment the counter
    //teleopCounter++;

    // Run the code once every 100 iterations
    //if (teleopCounter % 100 == 0) {
      //double leftX = joystickController.getRawAxis(0);
      //double leftY = joystickController.getRawAxis(1);
      //System.out.println("LeftX: " + leftX + ", LeftY: " + leftY);
    //}

    boolean xButton = joystickController.getRawButton(2); 
    if (xButton) {
      System.out.println("X Button is pressed");
    }
  }

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
