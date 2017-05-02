package org.usfirst.frc.team1027.robot;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.DigitalInput;

public class Robot extends IterativeRobot {
	int buttoncounter1;
	boolean side;
	DoubleSolenoid solenoid1;
	DoubleSolenoid solenoid2;
	DoubleSolenoid solenoid3;
	double angle1;
	RobotDrive myRobot;
	RobotDrive victor;
	Joystick joystick1;
	Joystick joystick2;
	Joystick xbox;
	AnalogGyro gyro;
	double Kp;
	DigitalInput limitSwitch;
	boolean solenoid1Toggle;
	boolean solenoid2Toggle;
	boolean solenoid3Toggle;
	CameraServer camera1;
	AnalogInput IRsensor;
	int i;
	double wheelbase;
	int step;
	double GearPickupDelay;
	int REM;
	double victorySpeed;
	double autoSpeed;
	long beginTime;
	long teleTime;
	String direction;
	
	public void robotInit() {
		buttoncounter1 = 0;
		//Rope Holder Piston
    	solenoid1 = new DoubleSolenoid(4,5);
    	//Gear Adjustment Piston
    	solenoid2 = new DoubleSolenoid(2,3);
    	//Gear pushing Piston
    	solenoid3 = new DoubleSolenoid(0,1);
    	//Rope Climbing Motor 1
    	victor = new RobotDrive(4,5);
    	//DRIVING MOTOR
    	myRobot = new RobotDrive(0,1,2,3);
    	//JOYSTICKS
    	xbox = new Joystick(0);
    	joystick1 = new Joystick(2);
    	joystick2 = new Joystick(1);
    	//IR SENSOR
    	IRsensor = new AnalogInput(1);
    	
    	solenoid1Toggle = false;
    	solenoid2Toggle = false;
    	solenoid3Toggle = false;
    	
    	gyro = new AnalogGyro(0);
    	
    	camera1 = CameraServer.getInstance();
    	camera1.startAutomaticCapture();
    	
    	gyro.reset();
   
    	
	}
    	
    
	public void disabledInit(){

    }
	
	public void disabledPeriodic() {
		
	}

    public double voltsToInches(double volts, double setback) {
    	return (1.668461 + (293.0885 - 1.668461))/(1 + (Math.pow(volts/0.1231562,1.175973))) - setback;
    }
	
    public void autonomousInit() {
    	//0 - 1st Option, 1 - 2nd Option, 4 - Testing
    	step = 1; 
    	//Initiate Gyro Heading
    	gyro.reset();
    	//Counter for Autonomous Option 1.
    	i = 0;
    	side = false;
    	beginTime = System.currentTimeMillis();
    	//Rotating Error Margin
    	REM = 0;
    	//Default Speed
    	autoSpeed = 0.7;
    	//Gyro Settings
    	wheelbase = 15;
    	Kp = 0.03;
    	//Time to Wait for Gear Pickup
    	GearPickupDelay = 4;
    	//Rotate Error Margin
    	REM = 15;
    	//Victory Spin Speed
    	victorySpeed = 1.0;
    	
    	direction = "right";
    }
    
    public void autonomousPeriodic() {
    	angle1 = gyro.getAngle();
    	System.out.println(angle1);
    	
    	//CORRECT GYRO ANGLE
    	if (angle1 < -180)
    		angle1 += 360;
    	if (angle1 > 180)
    		angle1 -= 360;   			
    	
    	
    	switch (step) {
    	//USED FOR FIRST AUTONOMOUS OPTION
	    	case 0: {
	    		if(i < 200){
	    			myRobot.drive(0.5, 0.0); //drives over line
	    		}
	    		else if (i < 400){
	    			myRobot.drive(0.5, 0.5); //VICTORY!
	    		}
	    		else if(i < 1000){
	    			myRobot.drive(0.0, 0.0); //stop at end or when 15 secs up
	    		}
	    	}
    	//USED FOR SECOND AUTONOMOUS OPTION
	    	case 1: {
	    		if (side) {
	    			move(3000, 0.4, true);
	    			Timer.delay(0.4);
	    			rotate(75, 0.7, direction);
	    			side = false;
	    			step = 1;
	    			gyro.reset();
	    			break;
	    		} else {
	    			
	    		
	    			
	    		//get to gear
		    		System.out.println("step 1");
		    		if (voltsToInches(IRsensor.getVoltage(), 12) >= 2.7) {
		        		myRobot.drive(0.5, -angle1*Kp*Math.pow((Math.E),-2/wheelbase));
		        	} else {
		        		
		        		//wait for gear to be picked up
		        		System.out.println("STEP 1-2: WAITNG FOR GEAR TO BE COLLECTED");
		        		myRobot.drive(0.0, 0.0);
		        		Timer.delay(0.3);
		        		solenoid3.set(DoubleSolenoid.Value.kForward);
		        		Timer.delay(0.4);
		        		solenoid3.set(DoubleSolenoid.Value.kOff);
		        		Timer.delay(1);
		        		//MOVE TO STEP 2
		        		step = 10;
		        	}
	    		}
	    		break;
	    	}
	    	case 2: {
	    		//BACKWARDS
	    		System.out.println("STEP 2-1: BACK IT UP");
	    		move(800, -autoSpeed, true);
	    		
	    		//ROTATE RIGHT 90 
	    		System.out.println("STEP 2-2: ROTATE RIGHT 90");
	    		rotate(90, 0.85, "right");
	    		
	    		//MOCH 10
	    		System.out.println("STEP 2-3: MOCH 10");
	    		move(850, 1.0, true);
	
	    		//ROTATE LEFT 90
	    		System.out.println("STEP 2-4: ROTATE LEFT 90");
	    		rotate(90, 0.85, "left");
	
	    		//PASS THE LINE!
	    		System.out.println("STEP 2-5: PASS THE LINE!");
	    		move(600, 1.0, true); 
	    		
	    		//MOVE TO STEP 3
	    		step = 3;
	    		break;
    		}
    	//victory case
	    	case 3: {
	    		System.out.println("Autonomous End. End Time: " + (System.currentTimeMillis() - beginTime));
	    		step = 6;
	    		break;
    		}
	    	
	    	case 4: {
	    		move(2000, 0.8, true);
	    		step = 5;
	    		break;
	    	}
    	}
    	
    	i++;
    }
    
    public void move(long time, double speed, boolean useGyro) {
    	long initTime = System.currentTimeMillis();
    	gyro.reset();
    	angle1 = gyro.getAngle();
    	
    	while (initTime + time > System.currentTimeMillis()) {
        	angle1 = gyro.getAngle();
        	if (useGyro)
        		myRobot.drive(speed, speed < 0 ? angle1*Kp*Math.pow((Math.E),-3/wheelbase) : -angle1*Kp*Math.pow((Math.E),-3/wheelbase));
        	else
        		myRobot.tankDrive(speed, speed);
    		Timer.delay(0.004);
    	}
    }
    
    public void rotate(int degree, double speed, String dir) {
    	gyro.reset();
    	long beginRotateTime = System.currentTimeMillis();
    	angle1 = gyro.getAngle();
    	if (dir == "left") {
    		while (angle1 > -degree+REM && System.currentTimeMillis() < beginRotateTime + 2000) {
        		angle1 = gyro.getAngle();
        		myRobot.tankDrive(-speed, speed);
    			Timer.delay(0.004); 
    		}
    	} else {
    		while (angle1 < degree-REM && System.currentTimeMillis() < beginRotateTime + 2000) {
        		angle1 = gyro.getAngle();     		
        		myRobot.tankDrive(speed, -speed);
    			Timer.delay(0.004);
    		}
    	}
    }

    public void teleopInit() {
    	teleTime = System.currentTimeMillis();
    	
    }

    
    public void teleopPeriodic() {
    	/* 
    	 * solenoid1 = Rope Holder Piston
    	 * solenoid2 = Gear Toggle Piston
    	 * solenoid3 = Drive Gear Piston
    	 * victor1 = Rope Climbing Motor 1
    	 * victor2 = Rope Climing Motor 2
    	 */
    	teleTime = System.currentTimeMillis();
    	
    	//Op 1: Driving When Button 8 Pressed Reduce To Half Speed
    	if (joystick2.getRawButton(1)) 	
    		myRobot.tankDrive(-joystick1.getRawAxis(1)*0.55, -joystick2.getRawAxis(1)*0.55);
    	else if (joystick1.getRawButton(1)) 	
    		myRobot.tankDrive(-joystick1.getRawAxis(1), -joystick2.getRawAxis(1));
    	else	
    		myRobot.tankDrive(-joystick1.getRawAxis(1)*0.85, -joystick2.getRawAxis(1)*0.85);
    	
    	//Op 1: Switch Driving Gears (A) Button
    	
    	if (xbox.getRawButton(4)) {
    		solenoid3.set(DoubleSolenoid.Value.kForward);
    		Timer.delay(0.4);
    		solenoid3.set(DoubleSolenoid.Value.kOff);
    	}
    	
    	if (xbox.getRawButton(2)) {
    		solenoid3.set(DoubleSolenoid.Value.kReverse);
    		Timer.delay(0.4);
    		solenoid3.set(DoubleSolenoid.Value.kOff);
    	}
//    	
    	//Op 1: Toggle Gear Piston
    	
    	if (joystick1.getRawButton(1) && joystick2.getRawButton(1))
    		rotate(180, 1.0, "right");
    	
    	//Op 2: Open/Close The Rope Holder
    	if (xbox.getRawButton(6)) {
    		solenoid1Toggle = !solenoid1Toggle;
    		if (solenoid1Toggle)
    			solenoid1.set(DoubleSolenoid.Value.kForward);
    		else
    			solenoid1.set(DoubleSolenoid.Value.kReverse);
    		Timer.delay(0.2);
			solenoid1.set(DoubleSolenoid.Value.kOff);
    	}
//    	
//    	if (xbox.getRawButton(1)) {
//    		solenoid3Toggle = !solenoid3Toggle;
//    		if (solenoid3Toggle)
//    			solenoid3.set(DoubleSolenoid.Value.kForward);
//    		else
//    			solenoid3.set(DoubleSolenoid.Value.kReverse);
//    		Timer.delay(0.2);
//			solenoid3.set(DoubleSolenoid.Value.kOff);
//    	}
//    	
    	
    	//Op 2: Toggle Gear Piston
    	if (xbox.getRawButton(3)) {
    		solenoid2.set(DoubleSolenoid.Value.kForward);
    		Timer.delay(0.4);
			solenoid2.set(DoubleSolenoid.Value.kOff);
    	}
    	
    	if (xbox.getRawButton(1)) {
    		solenoid2.set(DoubleSolenoid.Value.kReverse);
    		Timer.delay(0.4);
			solenoid2.set(DoubleSolenoid.Value.kOff);
    	}
    	
        victor.drive(xbox.getRawAxis(3), 0.0);
    	
    	System.out.println("Current Time: " + teleTime);
    }
	
    
    
	public void testPeriodic() {    	
        
    }
}
