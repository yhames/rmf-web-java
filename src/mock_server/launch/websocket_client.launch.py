import launch
from launch import LaunchDescription
from launch.actions import ExecuteProcess
from ament_index_python.packages import get_package_share_directory


# mock_server_dir = get_package_share_directory('mock_server')
# websocket_client = launch.actions.IncludeLaunchDescription(
#                     launch.launch_description_sources.PythonLaunchDescriptionSource(
#                     mock_server_dir + '/mock_server/websocket_client.py')
#                     )

def generate_launch_description():
    return LaunchDescription([
        ExecuteProcess(
                cmd=[
                    "python3",
                    "/root/workspace/src/mock_server/mock_server/websocket_client.py",
                ],
                name='websocket-client',
                output="screen",
            )
    ])
