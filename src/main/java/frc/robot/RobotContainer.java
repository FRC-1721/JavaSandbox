package frc.robot;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.RunCommand;
import frc.robot.controls.Controller;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.PhotonVisionSubsystem;

/** The RobotContainer class is where the Subsystems, Commands, Controllers, button bindings,
 * and what runs in autonomous mode are prepared.
 * 
 * It is up to Robot.java to determine when to run things, but this class determines what to run.
 */
public class RobotContainer {
    private final Field2d m_field = new Field2d();
    public final PhotonVisionSubsystem photonVision = new PhotonVisionSubsystem();
    private Controller controller = new Controller(m_field, photonVision);    
    public final DriveSubsystem drive = new DriveSubsystem();
    private static final String kDefaultAuto = "Default";
    private static final String kCustomAuto = "My Auto";
    private final SendableChooser<String> m_chooser = new SendableChooser<>();
    private boolean isInitialized = false;

    public RobotContainer() {
        SmartDashboard.putData("Field", m_field);
        Pose2d testPose = new Pose2d();
        m_field.setRobotPose(testPose);

        m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
        m_chooser.addOption("My Auto", kCustomAuto);
        SmartDashboard.putData("Auto choices", m_chooser);
    }

/**
  * Scheduler registers it as the default command for DriveSubsystem.

    During each control loop iteration (~20ms):

    WPILib automatically calls the lambda: () -> drive.drive(...)

    That calls your drive.drive(...) method, which sets the desired wheel speeds.

    Then, in simulation, your DriveSubsystem.simulationPeriodic() updates the module positions and angles.

    The odometry (pose) is updated from the simulated wheel movement.

    If you hooked it to a Field2d widget, the robot moves on the simulated field.
 */
    public void configureBindings() {
        if (isInitialized) {
            return;
        }
        controller.initialize();
        isInitialized = true;
        // Set default drive command here
        drive.setDefaultCommand(
            new RunCommand(
                () -> drive.drive(0.5, 0.0, 0.0),  // Drives forward in sim
                drive
            )
        );
    }
}
