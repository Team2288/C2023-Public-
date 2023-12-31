package frc.robot.subsystems;

import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.SerialPort.Parity;
import edu.wpi.first.wpilibj.SerialPort.StopBits;

import frc.robot.Constants;

public class Lights {
    private SerialPort port;

    public Lights() {
        super();
        try {
            this.port = new SerialPort(115200, SerialPort.Port.kUSB1,8,Parity.kEven,StopBits.kOne);
        }catch(Exception e){
            System.err.println("Error setting up serial port, lights will probably not work");
        }
    }
    
    // Set state until state is set otherwise
    public void setState(LightStates state) {
        try {
            String command = Constants.LIGHT_STATES.getOrDefault(state,Constants.DEFAULT_LIGHT_STATE);
            this.port.writeString(command);
            this.port.flush();
        }catch(Exception e) {
            System.err.println("error setting light state for state: " +state.toString());
        }
    }

    // Control lights
    public void on() {
        this.setState(LightStates.ON);
    }
    public void off() {
        this.setState(LightStates.OFF);
    }    
}
