# Copyright 2016 Open Source Robotics Foundation, Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

import json

import rclpy
from rclpy.node import Node
from rclpy.qos import QoSProfile
from rclpy.qos import QoSHistoryPolicy as History
from rclpy.qos import QoSDurabilityPolicy as Durability
from rclpy.qos import QoSReliabilityPolicy as Reliability

from rmf_door_msgs.msg import DoorState, DoorMode, DoorRequest
from rmf_task_msgs.msg import ApiRequest, ApiResponse

qos_map = QoSProfile(
    history=History.KEEP_ALL,
    depth=1,
    reliability=Reliability.RELIABLE,
    durability=Durability.TRANSIENT_LOCAL
)

qos_task = QoSProfile(
    depth=10,
    history=rclpy.qos.HistoryPolicy.KEEP_LAST,
    reliability=rclpy.qos.ReliabilityPolicy.RELIABLE,
    durability=rclpy.qos.DurabilityPolicy.TRANSIENT_LOCAL,
)

class RosPubSub(Node):

    def __init__(self):
        super().__init__('ros_pub_sub') 

        # dummy door data
        self.door_dummy = {
            'main_door': DoorMode(value=DoorMode.MODE_CLOSED),
            'coe_door': DoorMode(value=DoorMode.MODE_OPEN),
            'hardware_door': DoorMode(value=DoorMode.MODE_OPEN)
        }

        # publisher, subscriptions
        self.door_state_publisher = self.create_publisher(DoorState, 'door_states', 10)
        self.task_api_response_publisher = self.create_publisher(ApiResponse, 'task_api_responses', qos_task)
        self.door_request_subscription = self.create_subscription(
            DoorRequest,
            '/adapter_door_requests',
            self.door_request_callback,
            10)
        self.task_api_request_subscription = self.create_subscription(
            ApiRequest,
            '/task_api_requests',
            self.task_api_request_callback,
            10)
        self.publisher_timer = self.create_timer(3, self.publish_with_timer) # every 3 seconds

    def door_request_callback(self, msg):
        self.get_logger().info('I heard door request for "%s"' % msg.door_name)
        self.door_dummy[msg.door_name] = msg.requested_mode

    def task_api_request_callback(self, msg):
        self.get_logger().info('I heard task request with id "%s"' % msg.request_id)
        task_api_response = ApiResponse()        
        task_api_response.request_id = msg.request_id

        json_msg = {
            "state": {
                "booking": {
                    "id": msg.request_id,
                    "unix_millis_earliest_start_time": None
                },
                "category":"compose",
                "detail": {
                    "category": "go_to_place",
                    "phases": [{
                        "activity": {
                            "category":"go_to_place",
                            "description":{
                                "waypoint":"patrol_A1"
                            }
                        }
                    }]
                },
                "dispatch":{
                    "errors":[],
                    "status":"queued"
                },
                "status":"queued",
                "unix_millis_start_time": None
            },
            "success": True
        }
        task_api_response.json_msg = json.dumps(json_msg)
        
        self.task_api_response_publisher.publish(task_api_response)

    def publish_with_timer(self):
        # Door state
        for k, v in self.door_dummy.items():
            msg_door_state = DoorState()
            msg_door_state.door_name = k
            msg_door_state.door_time = self.get_clock().now().to_msg()
            msg_door_state.current_mode = v
            self.door_state_publisher.publish(msg_door_state)
        self.get_logger().info('Publishing Door States')


def main(args=None):
    rclpy.init(args=args)

    ros_pub_sub = RosPubSub()

    rclpy.spin(ros_pub_sub)

    # Destroy the node explicitly
    # (optional - otherwise it will be done automatically
    # when the garbage collector destroys the node object)
    ros_pub_sub.destroy_node()
    rclpy.shutdown()


if __name__ == '__main__':
    main()
