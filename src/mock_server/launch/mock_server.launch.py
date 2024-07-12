import launch
import launch_ros.actions
from ament_index_python.packages import get_package_share_directory


def generate_launch_description():
    mock_server_dir = get_package_share_directory('mock_server')
    ros_bag_launch = launch.actions.IncludeLaunchDescription(
                launch.launch_description_sources.PythonLaunchDescriptionSource(
                mock_server_dir + '/launch/ros_bag_play.launch.py'))
    ws_client_launch = launch.actions.IncludeLaunchDescription(
                launch.launch_description_sources.PythonLaunchDescriptionSource(
                mock_server_dir + '/launch/websocket_client.launch.py'))

    return launch.LaunchDescription([
        launch_ros.actions.Node(
            package='mock_server',
            executable='ros_pub_sub',
            name='ros_pub_sub'),
        ros_bag_launch, 
        ws_client_launch
  ])