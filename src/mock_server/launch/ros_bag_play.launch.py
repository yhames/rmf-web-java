from launch import LaunchDescription
from launch.actions import ExecuteProcess

def generate_launch_description():
    return LaunchDescription([
        ExecuteProcess(
                cmd=[
                    "ros2",
                    "bag",
                    "play",
                    "/root/workspace/src/mock_server/bags/rosbag2_2024_03_26-06_07_58/rosbag2_2024_03_26-06_07_58_0.db3",
                ],
                name='ros-bag-play',
                output="screen",
            )
    ])