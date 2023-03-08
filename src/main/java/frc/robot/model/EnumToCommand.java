package frc.robot.model;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.model.RobotStates;
import java.util.HashMap;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.Arm;
import frc.robot.subsystems.Intake;
import frc.robot.Constants;

public class EnumToCommand {
    HashMap<RobotStates.RobotStatesEnum, SequentialCommandGroup> corresponding_commands = new HashMap<RobotStates.RobotStatesEnum, SequentialCommandGroup>();

    public EnumToCommand(Elevator elevator, Arm arm, Intake intake){
        corresponding_commands.put( 
            RobotStates.RobotStatesEnum.CARRY,
            new SequentialCommandGroup(
                elevator.getPositionCommand(Constants.ELEVATOR_HIGH),
                arm.getPositionCommand(Constants.ARM_RETRACT),
                elevator.getPositionCommand(Constants.ELEVATOR_MID)
            )
        );

        corresponding_commands.put(
            RobotStates.RobotStatesEnum.PICK_UP_C,
            new SequentialCommandGroup(
                elevator.getPositionCommand(Constants.ELEVATOR_LOW),
                arm.getPositionCommand(Constants.ARM_STRAIGHT)
            )
        );

        corresponding_commands.put(
            RobotStates.RobotStatesEnum.PICK_UP_UC,
            new SequentialCommandGroup(
                elevator.getPositionCommand(Constants.ELEVATOR_LOW),
                arm.getPositionCommand(Constants.ARM_STRAIGHT)
            )
        );

        corresponding_commands.put(
            RobotStates.RobotStatesEnum.PICK_UP_TC,
            new SequentialCommandGroup(
                elevator.getPositionCommand(Constants.ELEVATOR_LOW),
                arm.getPositionCommand(Constants.ARM_STRAIGHT)
            )
        );

        corresponding_commands.put(
            RobotStates.RobotStatesEnum.PLACE_LOW,
            new SequentialCommandGroup(
                elevator.getPositionCommand(Constants.ELEVATOR_LOW),
                arm.getPositionCommand(Constants.ARM_DOWN)
            )
        );

        corresponding_commands.put(
            RobotStates.RobotStatesEnum.PLACE_CONE_M,
            new SequentialCommandGroup(
                elevator.getPositionCommand(Constants.ELEVATOR_MID),
                arm.getPositionCommand(Constants.ARM_DOWN)
            )
        );

        corresponding_commands.put(
            RobotStates.RobotStatesEnum.PLACE_CONE_H,
            new SequentialCommandGroup(
                elevator.getPositionCommand(Constants.ELEVATOR_HIGH),
                arm.getPositionCommand(Constants.ARM_DOWN)
            )
        );

        corresponding_commands.put(
            RobotStates.RobotStatesEnum.PLACE_CUBE_M,
            new SequentialCommandGroup(
                elevator.getPositionCommand(Constants.ELEVATOR_MID),
                arm.getPositionCommand(Constants.ARM_STRAIGHT)
            )
        );

        corresponding_commands.put(
            RobotStates.RobotStatesEnum.PLACE_CUBE_H,
            new SequentialCommandGroup(
                elevator.getPositionCommand(Constants.ELEVATOR_HIGH),
                arm.getPositionCommand(Constants.ARM_STRAIGHT)
            )
        );
    }

    public SequentialCommandGroup getSeqCommand(RobotStates.RobotStatesEnum state) {
        return corresponding_commands.get(state);
    }
}