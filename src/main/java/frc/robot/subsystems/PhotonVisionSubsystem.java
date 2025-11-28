package frc.robot.subsystems;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.geometry.Translation3d;
import frc.robot.Constants.CameraConstants;

import org.photonvision.PhotonCamera;
import org.photonvision.targeting.PhotonPipelineResult;
import org.photonvision.targeting.PhotonTrackedTarget;

/** This subsystem is started in RobotContainer with:
 * public final PhotonVisionSubsystem photonVision = new PhotonVisionSubsystem();
 * 
 * This gets it started and it will run PhotonVisionSubsystem.periodic() automatically (every 20 ms).
 */
public class PhotonVisionSubsystem extends SubsystemBase {

    private final PhotonCamera frontCamera;
    private final PhotonCamera rearCamera;
    private final Timer printTimer = new Timer();

    public PhotonVisionSubsystem() {
        frontCamera = new PhotonCamera(CameraConstants.FRONT_CAMERA_NAME);
        rearCamera = new PhotonCamera(CameraConstants.REAR_CAMERA_NAME);
    }

    /** This method is used to poll the camera for latest results.
     * 
     * This method is only run in test mode.
     * 
     * It is done once every 5 seoncds and will print the April tag information.
     */
    @Override
    public void periodic() {
        if (!DriverStation.isTest())
            return;
    
        if (printTimer.get() == 0) {
            printTimer.start();
        }

        // Query the latest result from PhotonVision
        PhotonPipelineResult frontResult = frontCamera.getLatestResult();
        if (frontResult.hasTargets())
        {
            var best = frontResult.getBestTarget();

            // Only print every 5 seconds
            if (printTimer.hasElapsed(5.0)) {
                System.out.println("Front Camera");
                System.out.println("-".repeat(20) + "April tag: " + best.getFiducialId() + "-".repeat(20));
                System.out.println(best.toString());
            }
        }

        PhotonPipelineResult rearResult = rearCamera.getLatestResult();
        if (rearResult.hasTargets())
        {
            var best = rearResult.getBestTarget();

            // Only print every 5 seconds
            if (printTimer.hasElapsed(5.0)) {
                System.out.println("Rear Camera");
                System.out.println("-".repeat(20) + "April tag: " + best.getFiducialId() + "-".repeat(20));
                System.out.println(best.toString());
            }
        }

        if (printTimer.hasElapsed(5.0)) {
            printTimer.reset();
        }
    }

    /** This gets the latest Pose from the PhotonVision camera. It should not used a cached value
     * saved from periodic(), because that may be out of date.
     * 
     * PhotonTrackedTarget gives us:
     * - Yaw: horizontal angle from camera center to target.
     * - Pitch: vertical angle from camera center to target.
     * 
     * getBestCameraToTarget() gives us:
     * - A full 3D Transform from camera to target
     * - Includes X, Y, and Z translations in meters
     * - This requires that the camera is properly configured and calibrated in PhotonVision,
     *   with 3d pose estimations enabled
     * 
     * @return The latest Pose2d of the target AprilTag relative to the camera.
     */
    public Pose2d getLatestPose() {
        PhotonPipelineResult result = frontCamera.getLatestResult();
    
        if (result.hasTargets()) {
            PhotonTrackedTarget best = result.getBestTarget();
            Transform3d camToTarget = best.getBestCameraToTarget(); // in meters
            
            // Convert camera-relative 3D transform to field-relative 2D Pose
            // Assuming your camera is aligned with the robot
            Translation3d translation = camToTarget.getTranslation();
            Rotation2d rotation = camToTarget.getRotation().toRotation2d();
            
            //System.out.println("getLatestPose: X = " + translation.getX());
            //System.out.println("getLatestPose: Y = " + translation.getY());

            Pose2d estimatedPose = new Pose2d(
                translation.getX(), // forward distance in meters
                translation.getY(), // sideways distance in meters
                rotation           // heading
            );
            return estimatedPose;
        }
    
        // Return empty pose if no targets
        return new Pose2d();
    }
}
