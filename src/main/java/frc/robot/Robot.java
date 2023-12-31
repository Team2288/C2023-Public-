// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.pathplanner.lib.server.PathPlannerServer;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

import java.util.HashMap;

import frc.robot.model.AutoHandler;
import frc.robot.model.EnumToCommand;
import frc.robot.model.RobotStates;
import frc.robot.model.RobotStates.RobotStatesEnum;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private Command m_autonomousCommand;
  private RobotContainer m_robotContainer;
  private EnumToCommand enumToCommand;
  private AutoHandler autoHandler;
  private HashMap<String, Command> events;

  // TELEOP
  private GenericHID controlBoard = new GenericHID(1);
  RobotStates curr_state = new RobotStates();
  public XboxController intakeController = new XboxController(2);
  public boolean isCone = false;
  public boolean hasCone = false;

  // AUTO

  /* 
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    m_robotContainer = new RobotContainer();
    PathPlannerServer.startServer(5811);  
  }

  /**
   * This function is called every robot packet, no matter the mode. Use this for items like
   * diagnostics that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before LiveWindow and
   * SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic(){
    CommandScheduler.getInstance().run();
  }

  /** This function is called once each time the robot enters Disabled mode. */
  @Override
  public void disabledInit() {}

  @Override
  public void disabledPeriodic() {}

  /** This autonomous runs the autonomous command selected by your {@link RobotContainer} class. */
  @Override
  public void autonomousInit() {
    // Reset encoder values on auto init
    m_robotContainer.m_elevator.resetEncoderPosition();
    m_robotContainer.m_arm.resetEncoderPosition();
    m_robotContainer.m_intakeArm.resetEncoder();
    m_robotContainer.m_intakeSwivel.resetEncoder();
    m_robotContainer.m_drivetrainSubsystem.zeroGyroscope(270.0);

    // Initialize commands
    enumToCommand = new EnumToCommand(
      m_robotContainer.m_elevator, 
      m_robotContainer.m_arm, 
      m_robotContainer.m_intake,
      m_robotContainer.m_intakeArm,
      m_robotContainer.m_intakeSwivel
    );

    m_robotContainer.m_intakeArm.motor.configMotionCruiseVelocity(15000);
    // enumToCommand.getCommand(RobotStatesEnum.PLACE_CUBE_MID_AUTO, false).schedule();
  }
 
  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
    // Update SmartDashboard during auto
    m_robotContainer.m_intakeArm.smartDashboard();
    m_robotContainer.m_arm.smartDashboard();
    m_robotContainer.m_elevator.smartDashboard();
  } 

  @Override
  public void teleopInit() {
    // End any incomplete auto commands
    if (m_autonomousCommand != null) {
      m_autonomousCommand.cancel();
    }

    // Reset encoder values on teleop init
    m_robotContainer.m_elevator.resetEncoderPosition();
    m_robotContainer.m_arm.resetEncoderPosition();
    m_robotContainer.m_intakeArm.resetEncoder();
    m_robotContainer.m_intakeSwivel.resetEncoder();

    // Initialize commands
    enumToCommand = new EnumToCommand(
      m_robotContainer.m_elevator,
      m_robotContainer.m_arm, 
      m_robotContainer.m_intake, 
      m_robotContainer.m_intakeArm, 
      m_robotContainer.m_intakeSwivel
    );

    // Put the robot in carry on init
    curr_state.setState(RobotStatesEnum.CARRY);

    m_robotContainer.m_intakeArm.motor.configMotionCruiseVelocity(30000);
  }

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {
    // Update SmartDashboard during teleop
    m_robotContainer.m_intakeArm.smartDashboard();
    m_robotContainer.m_arm.smartDashboard();
    m_robotContainer.m_elevator.smartDashboard();
    m_robotContainer.m_intakeSwivel.smartDashboard();    

    //Switch state between cone and cube
    if (controlBoard.getRawButtonPressed(4)) {
      isCone = !isCone;
    }
  
    // Robot control / State machine
    if (controlBoard.getRawButtonPressed(1)) {
        switch (curr_state.getState()) {
            case CARRY:
            case RETRACT_W_CARRY:
              enumToCommand.getCommand(RobotStatesEnum.PICK_UP_FLOOR, isCone).schedule();
              curr_state.setState(RobotStatesEnum.PICK_UP_FLOOR);
              break;
            default:
              System.out.println("Bad call for Button 1 on state" + curr_state.getState().name() + ": setting state to carry");
              enumToCommand.getCommand(RobotStatesEnum.CARRY, isCone).schedule();
              curr_state.setState(RobotStatesEnum.CARRY);
              break;
        }
    } else if (controlBoard.getRawButtonPressed(2)) { 
        switch (curr_state.getState()) {
          case PICK_UP_RAMP:
          case PICK_UP_FLOOR:
          case CARRY:
            enumToCommand.getCommand(RobotStatesEnum.CARRY, isCone).schedule();
            curr_state.setState(RobotStatesEnum.CARRY);
            break;
          default:
            enumToCommand.getCommand(RobotStatesEnum.CARRY, isCone).schedule();
            curr_state.setState(RobotStatesEnum.CARRY);
            break;
        }
    } else if (controlBoard.getRawButtonPressed(5)) {
        switch (curr_state.getState()) {
            case CARRY:
            case RETRACT_W_CARRY:
              enumToCommand.getCommand(RobotStatesEnum.PLACE_H, isCone).schedule();
              curr_state.setState(RobotStatesEnum.PLACE_H);
              break;
            default:
              System.out.println("Bad call for Button 5 on state" + curr_state.getState().name() + ": setting state to carry");
              enumToCommand.getCommand(RobotStatesEnum.CARRY, isCone).schedule();
              curr_state.setState(RobotStatesEnum.CARRY);
              break;
        }
    } else if (controlBoard.getRawButtonPressed(7)) {
        switch (curr_state.getState()) {
            case CARRY:
            case RETRACT_W_CARRY:
              enumToCommand.getCommand(RobotStatesEnum.PLACE_M, isCone).schedule();
              curr_state.setState(RobotStatesEnum.PLACE_M);
              break;
            default:
              System.out.println("Bad call for Button 7 on state" + curr_state.getState().name() + ": setting state to carry");
              enumToCommand.getCommand(RobotStatesEnum.CARRY, isCone).schedule();
              curr_state.setState(RobotStatesEnum.CARRY);
              break;
        }
    } else if (controlBoard.getRawButtonPressed(3)) {
        switch (curr_state.getState()) {
            case CARRY:
            case RETRACT_W_CARRY:
            case PICK_UP_RAMP:
              enumToCommand.getCommand(RobotStatesEnum.PICK_UP_RAMP, isCone).schedule();
              curr_state.setState(RobotStatesEnum.PICK_UP_RAMP);
              break;
            default:
              System.out.println("Bad call for Button 3 on state" + curr_state.getState().name() + ": setting state to carry");
              enumToCommand.getCommand(RobotStatesEnum.CARRY, isCone).schedule();
              curr_state.setState(RobotStatesEnum.CARRY);
              break;
        }
    }
    
    // Intake Controls
    if (controlBoard.getRawButtonPressed(6)) {
      m_robotContainer.m_intake.driveIntake(Constants.INTAKE_BACKWARD);
      hasCone = true;
    } else if (controlBoard.getRawButtonPressed(8)) {
      m_robotContainer.m_intake.driveIntake(Constants.INTAKE_FORWARD);
      hasCone = false;
    } else if (controlBoard.getRawButtonPressed(11)) {
      m_robotContainer.m_intake.stopIntake();

      if (hasCone) {
        m_robotContainer.m_intake.getIntakeCommand(Constants.INTAKE_BACKWARD_HOLD).schedule();
      } else {
        m_robotContainer.m_intake.getIntakeCommand(Constants.INTAKE_FORWARD_HOLD).schedule();
      }
    }
  }

  @Override
  public void testInit() {
    // Cancels all running commands at the start of test mode.
    CommandScheduler.getInstance().cancelAll();
  }

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {}
}