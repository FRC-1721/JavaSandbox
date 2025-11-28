package frc.robot.sim;

import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.sim.TalonFXSimState;

/** This simulates one wheel module (drive + steer).
 * 
 * The Motor is a Kraken x60.
 * The controller is a Talon FX, and it is integrated.
 * 
 * The Kraken x60 is a brushless motor that has a Talon FX controller built directly
 * into the motor housing. This is done instead of having an external motor controller
 * connected by wires.
 * 
 * The CAN interface is the communication interface to the controller and is part of the
 * Talon FX.
 */
public class SwerveWheelSim {
    private final TalonFX driveMotor;
    private final TalonFX steerMotor;

    private final TalonFXSimState driveSim;
    private final TalonFXSimState steerSim;

    private double drivePosMeters = 0;
    /** The drive velocity in meters per second */
    private double driveVelMps = 0;
    private double steerAngleDeg = 0;

    /** Constructs a SwerveWheelSim with given CAN IDs for drive and steer motors.
     * 
     * @param driveID The CAN ID for the drive motor TalonFX
     * @param steerID The CAN ID for the steer motor TalonFX
     */
    public SwerveWheelSim(int driveID, int steerID) {
        driveMotor = new TalonFX(driveID);
        steerMotor = new TalonFX(steerID);

        driveSim = driveMotor.getSimState();
        steerSim = steerMotor.getSimState();
    }

    /** Tells the wheel module to move forward according to its current speed/angle.
     * The dt parameter is the time step in seconds.
     * 
     * We are using a scheduler that runs every 20 ms (0.02 seconds).
     * - A dt of 0.02 means 20 milliseconds have passed since the last update.
     * - This is 'simulated' so we are using the motorVoltage. In a non-simulated
     *   environment, you would read the actual velocity from an encoder.
     * 
     * @param dt
     */
    public void update(double dt) {
        // Read motor voltages from simulation
        double driveVoltage = driveSim.getMotorVoltage();
        double steerVoltage = steerSim.getMotorVoltage();

        // --- VERY simple physics model ---
        driveVelMps = driveVoltage * 1.5; // tune later
        drivePosMeters += driveVelMps * dt;

        steerAngleDeg += steerVoltage * 40 * dt; // deg/sec
        steerAngleDeg %= 360;

        // Feed back into the TalonFX sim
        driveSim.setRotorVelocity(driveVelMps);
        driveSim.setRawRotorPosition(drivePosMeters);

        steerSim.setRotorVelocity(steerVoltage);
        steerSim.setRawRotorPosition(steerAngleDeg);
    }

    public double getDrivePosMeters() { return drivePosMeters; }
    public double getDriveVelMps() { return driveVelMps; }
    public double getSteerAngleDeg() { return steerAngleDeg; }
}
