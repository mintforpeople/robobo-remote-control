# Remote Control ROS

This module allows communication with the Robobo educational robot using ROS topics and services.
For more information on this ROS topics and services [click here](https://github.com/mintforpeople/robobo-programming/wiki/ROS)
For definitions of the messages and services [click here](https://github.com/mintforpeople/robobo-ros-msgs)

## Design

This module starts a ROS starts a series of nodes, including a master node and a node for each type of responsability (publish, subscribe and service).
This "responsability" nodes instantiate classes that satisfy each concrete responsability.
This classes and nodes are divided on packages according to their responsability type (publishers (topics), subscribers, services).

## Services

Services provided are:

* MovePanTilt
* MoveWheels
* PlaySound
* ResetWheels
* SetCamera
* SetEmotion
* SetFrequency
* SetLed
* Talk

Each service is served from a class in the services package and registered on the CommandNode.

> The services are depracated but still funciontal

## Topics

The node and classes under this package are in charge of publishing messages (containing Status data) to topics. This publishers are:

* BaseBattery
* PhoneBattery
* Pan
* Tilt
* AmbientLight
* Emotion
* Accel
* Orientation
* UnlockMove
* UnlockTalk
* Wheels
* Led
* Tap
* Fling
* Irs
* DetectedObject
* Tag
* LaneBasic
* LanePro
* QrCode
* Line
* QrCodeLost
* QrCodeAppear

## Subscribers

Most of the subscribers correspond to the migration of a service for a topic (since services were used to command the educational robot).

Appart from the services there are subscribers for ModuleControl, that watches for messages commanding the robot to turn on or off an specific module.
