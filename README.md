# Robobo Remote Control

This repository contains the Android modules to allow remote control of the Robobo educational robot.

## Modules

The modules are:

### remote-control

This module contains the base logic that is used by all modules to allow the comunications for controlling remotelly the robot.

### remote-control-ros

This module contains the code to allow the comunication and remote control using the Robot Operating System (ROS), specifically with ROS 1.
For more information [click here](remote-control-ros/README.md)

### remote-control-ws

This module contains the code to allow comunication using websockets, being the primary users the Python, JavaScript, and Scratch libraries.
