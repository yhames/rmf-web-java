#! /bin/bash

source /opt/ros/humble/setup.bash
source /root/workspace/install/setup.bash

ros2 launch mock_server mock_server.launch.py
