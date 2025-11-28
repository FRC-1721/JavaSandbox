package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.controls.Controller;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

public class DriveToPoseCommand extends Command {

    private final Controller controller;
    private final Pose2d targetPose;
    private final double kP = 0.5;  // proportional gain for movement
    private final double rotationP = 0.5; // proportional gain for rotation

    public DriveToPoseCommand(Controller controller, Pose2d targetPose) {
        System.out.println("DriveToPoseCommand: " + targetPose.toString());
        this.controller = controller;
        this.targetPose = targetPose;

    }

    @Override
    public void execute() {
        Pose2d currentPose = controller.getCurrentPose();

        double dx = targetPose.getX() - currentPose.getX();
        double dy = targetPose.getY() - currentPose.getY();
        double dtheta = targetPose.getRotation().minus(currentPose.getRotation()).getDegrees();

        // Apply proportional control (scaled small steps)
        double stepX = dx * kP * 0.02;  // assume 20ms per loop
        double stepY = dy * kP * 0.02;
        double stepTheta = dtheta * rotationP * 0.02;

        controller.updatePose(stepX, stepY, stepTheta);
    }

    @Override
    public boolean isFinished() {
        Pose2d pose = controller.getCurrentPose();
        double posError = pose.getTranslation().getDistance(targetPose.getTranslation());
        double rotError = Math.abs(pose.getRotation().minus(targetPose.getRotation()).getDegrees());

        /*
        System.out.println("isFinished: target.x = " + targetPose.getX());
        System.out.println("isFinished: target.y = " + targetPose.getX());
        System.out.println("isFinished: current.x = " + pose.getX());
        System.out.println("isFinished: current.y = " + pose.getX());
        */

        return posError < 0.01 && rotError < 1.0; // stop when close enough
    }

    /** This is automatically called if the command is cancelled.
     * You could also stop any movement here.
     */
    @Override
    public void end(boolean interrupted) {

    }
}
