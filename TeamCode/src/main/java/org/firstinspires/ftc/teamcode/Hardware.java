package org.firstinspires.ftc.teamcode;

//import com.acmerobotics.roadrunner.geometry.Pose2d;
//import com.acmerobotics.roadrunner.localization.ThreeTrackingWheelLocalizer;
import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;

//import org.openftc.revextensions2.ExpansionHubEx;
//import org.openftc.revextensions2.ExpansionHubMotor;
//import org.openftc.revextensions2.RevBulkData;


import static java.lang.Math.abs;

public class Hardware
{
    //drive motor declaration
    public DcMotorEx frontLeft;
    public DcMotorEx frontRight;
    public DcMotorEx backLeft;
    public DcMotorEx backRight;

    public BNO055IMU imu;
    public double adjust = 0;


    //helper class variables
    public static LinearOpMode currentOpMode;

    public double y;
    public double x;
    public double theta;

    //constructor method
    public Hardware(HardwareMap hardwareMap)
    {
        //drive motor initialization
        frontLeft = hardwareMap.get(DcMotorEx.class, "Front Left");
        frontRight = hardwareMap.get(DcMotorEx.class, "Front Right");
        backLeft = hardwareMap.get(DcMotorEx.class, "Back Left");
        backRight = hardwareMap.get(DcMotorEx.class, "Back Right");

        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);


        //helper class variables
        y = 0;
        x = 0;
        theta = 0;

    }

    //robot-oriented drive method
    public void robotODrive(double forward, double sideways, double rotation)
    {
        //adds all the inputs together to get the number to scale it by
        double scale = abs(rotation) + abs(forward) + abs(sideways);

        //scales the inputs when needed
        if (scale > 1) {
            forward /= scale;
            rotation /= scale;
            sideways /= scale;
        }
        //setting the motor powers to move
        frontLeft.setPower(forward - rotation - sideways);
        backLeft.setPower(forward - rotation + sideways);
        frontRight.setPower(-(forward + rotation + sideways));
        backRight.setPower(-(forward + rotation - sideways));
    }

    //field oriented drive
    public void fieldODrive(double forward, double sideways, double rotation, boolean reset)
    {
        double P = Math.hypot(sideways, forward);
        Orientation angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.RADIANS);

        double robotAngle = Math.atan2(forward, -sideways);

        if(reset)
            adjust = angles.firstAngle;

        double v5 = P * Math.sin(robotAngle - angles.firstAngle + adjust) + P * Math.cos(robotAngle - angles.firstAngle + adjust) - rotation;
        double v6 = P * Math.sin(robotAngle - angles.firstAngle + adjust) - P * Math.cos(robotAngle - angles.firstAngle + adjust) + rotation;
        double v7 = P * Math.sin(robotAngle - angles.firstAngle + adjust) - P * Math.cos(robotAngle - angles.firstAngle + adjust) - rotation;
        double v8 = P * Math.sin(robotAngle - angles.firstAngle + adjust) + P * Math.cos(robotAngle - angles.firstAngle + adjust) + rotation;

        frontLeft.setPower(v5);
        frontRight.setPower(v6);
        backLeft.setPower(v7);
        backRight.setPower(v8);
    }
}