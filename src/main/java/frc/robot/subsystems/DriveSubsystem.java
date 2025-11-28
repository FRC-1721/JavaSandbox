package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.sim.SwerveWheelSim;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;

public class DriveSubsystem extends SubsystemBase {

    // --- Create 4 simulated modules ---
    private final SwerveWheelSim frontLeft  = new SwerveWheelSim(1, 2);
    private final SwerveWheelSim frontRight = new SwerveWheelSim(3, 4);
    private final SwerveWheelSim backLeft   = new SwerveWheelSim(5, 6);
    private final SwerveWheelSim backRight  = new SwerveWheelSim(7, 8);

    private static final Translation2d frontLeftPosition  = new Translation2d( 0.3,  0.25);
    private static final Translation2d frontRightPosition = new Translation2d( 0.3, -0.25);
    private static final Translation2d backLeftPosition   = new Translation2d(-0.3,  0.25);
    private static final Translation2d backRightPosition  = new Translation2d(-0.3, -0.25);

    /** Kinematics is the math that converts desired robot movement (xSpeed, ySpeed, rot)
     * into individual wheel speeds and angles.
     */
    private static final SwerveDriveKinematics kinematics =
        new SwerveDriveKinematics(
            // x, y distance from center for each wheel
            frontLeftPosition, frontRightPosition, backLeftPosition, backRightPosition
        );

    private Pose2d pose = new Pose2d();

    public DriveSubsystem() {}

    // --------------------------------------------------
    // DRIVE COMMAND (you call this from teleop)
    // --------------------------------------------------
    public void drive(double xSpeed, double ySpeed, double rotSpeed) {
        // This example only applies rotation to the modules
        // To keep the example simple.
        // You will replace with full swerve kinematics later.
    }

    /** This method will be called once per scheduler run during simulation (every 20 ms).
     * Each update(0.02) call tells your simulated wheel module to move forward according to its current speed/angle.
     * For example: frontLeft sees desiredSpeed and desiredAngle, then updates its internal position.
     * 
     * After all four modules update, it calls updateOdometry() to recalculate the robot’s pose.
     */
    @Override
    public void simulationPeriodic() {
        // Update all 4 modules
        frontLeft.update(0.02);
        frontRight.update(0.02);
        backLeft.update(0.02);
        backRight.update(0.02);

        // Update odometry using sim data
        updateOdometry();
    }

    /** Odometry is the tracking of the robot’s position on the field.
     *  
     * Calculates robot position from the simulated wheels.
     * avgDist = average distance all four wheels have traveled.
     * heading = robot rotation (currently just the front-left wheel, simplified).
     * 
     * Updates the robot’s pose (x, y, rotation) used for field tracking.
     */
    private void updateOdometry() {
        // For now, only update heading + average distance
        double avgDist =
                (frontLeft.getDrivePosMeters() +
                frontRight.getDrivePosMeters() +
                backLeft.getDrivePosMeters() +
                backRight.getDrivePosMeters()) / 4.0;

        double heading = (frontLeft.getSteerAngleDeg()); // simplified

        pose = new Pose2d(avgDist, 0, Rotation2d.fromDegrees(heading));
    }

    public Pose2d getPose() { return pose; }
}
