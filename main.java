import edu.princeton.cs.introcs.StdDraw;
import org.firmata4j.I2CDevice;
import org.firmata4j.Pin;
import org.firmata4j.firmata.FirmataDevice;
import org.firmata4j.IODevice;
import org.firmata4j.ssd1306.SSD1306;
import	java.io.IOException;
import java.util.Timer;

public class main{
    public static void main(String[] args) throws InterruptedException, IOException
    {
        // Creates a FirmataDevice object with port name.
        IODevice device=new FirmataDevice("COM3");
        // Starts up the FirmataDevice object.
        device.start();
        //this ensures connection is okay
        device.ensureInitializationIsDone();
        //the firmata number for the soil moisture sensor is 15
        Pin soilSensor=device.getPin(15);
        //this sets the mode to analog
        soilSensor.setMode(Pin.Mode.ANALOG);
        //the firmata number for the pump is 2
        Pin pump=device.getPin(2);
        //this sets the mode to output
        pump.setMode(Pin.Mode.OUTPUT);
        //the firmata number for the button is 6
        Pin button=device.getPin(6);
        //this sets the mode to input
        button.setMode(Pin.Mode.INPUT);
        // this creates an I2C communication object b/w the Arduino chip and the OLED over I2C wires
        // 0x3C is the standard address over these wires for the SSD1306
        I2CDevice i2cObject=device.getI2CDevice((byte) 0x3C);
        // this creates an SSD1306 object using the I2C object with the right pixel size for the OLED which is 128 x 64
        SSD1306 Oled=new SSD1306(i2cObject, SSD1306.Size.SSD1306_128_64);
        // this initializes the OLED (SSD1306) object
        Oled.init();
        //this creates the timer object to schedule tasks
        Timer timer=new Timer();
        //this creates a task that makes use of the mentioned hardware components
        plantTask task=new plantTask(soilSensor,Oled,pump,button);
        //runs every 1 second with no delay
        timer.schedule(task,0,1000);
        //this sets the minimum and maximum value on the x axis
        StdDraw.setXscale(0, 40);
        //this sets the minimum and maximum value on the y axis
        StdDraw.setYscale(0, 900);
        //this sets the pen thickness
        StdDraw.setPenRadius(0.005);
        //this sets the pen color for drawing the x and y axis lines
        StdDraw.setPenColor(StdDraw.BLUE);
        //this draws the x axis line
        StdDraw.line(0, 0, 100,0 );
        //this draws the y axis line
        StdDraw.line(0, 0, 0, 1000);
        //this draws the label for the x axis
        StdDraw.text(15, -16, "time");
        //this draws the title of the entire graph
        StdDraw.text(20, 900, "A GRAPH OF MOISTURE VALUE OF PLANT AGAINST TIME");
        //this draws the label for the y axis
        StdDraw.text(-0.5, 450, "Soil level", 90);
       // this sets the pen color for plotting the graph points
        StdDraw.setPenColor(StdDraw.RED);
    }
    //for the testing
    public static int getValue(int testValue){
        int pump=0;
        //if the moisture value is more than 690 which means it is dry,pump is on
        if (testValue > 690) {
            pump=1;
        }
        //if the moisture value is more than or equal to 650 and less than or equal to 690 which means it is moist,the pump is on
        else if ((testValue > 650|| testValue==650) && (testValue < 690 ||testValue == 690)) {
            pump=1;
        }
        // if the moisture value is less than 650 which means it is wet,pump is off
        else if (testValue < 650) {
            pump=0;
        }

        return pump;
    }

}

