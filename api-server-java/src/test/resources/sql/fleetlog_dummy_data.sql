INSERT INTO fleetlog (name) VALUES ('Fleet1');

INSERT INTO fleetlogrobots (name, fleet_id) VALUES ('Robot1', 'Fleet1'), ('Robot2', 'Fleet1'), ('Robot3', 'Fleet1');

INSERT INTO fleetloglog (seq, unix_millis_time, tier, text, fleet_id) VALUES
                                                                          (1, UNIX_TIMESTAMP()*1000, 'info', 'Starting operations', 'Fleet1'),
                                                                          (2, UNIX_TIMESTAMP()*1000, 'warning', 'Low battery', 'Fleet1'),
                                                                          (3, UNIX_TIMESTAMP()*1000, 'info', 'Battery replaced', 'Fleet1'),
                                                                          (4, UNIX_TIMESTAMP()*1000, 'error', 'Sensor malfunction', 'Fleet1'),
                                                                          (5, UNIX_TIMESTAMP()*1000, 'info', 'Operations resumed', 'Fleet1');

SET @RobotID1 = (SELECT id FROM fleetlogrobots WHERE name = 'Robot1');
SET @RobotID2 = (SELECT id FROM fleetlogrobots WHERE name = 'Robot2');
SET @RobotID3 = (SELECT id FROM fleetlogrobots WHERE name = 'Robot3');

INSERT INTO fleetlogrobotslog (seq, unix_millis_time, tier, text, robot_id) VALUES
                                                                                (1, UNIX_TIMESTAMP()*1000, 'info', 'Routine check complete', @RobotID1),
                                                                                (2, UNIX_TIMESTAMP()*1000, 'warning', 'High temperature detected', @RobotID1),
                                                                                (3, UNIX_TIMESTAMP()*1000, 'info', 'Cooling initiated', @RobotID1),
                                                                                (4, UNIX_TIMESTAMP()*1000, 'info', 'Temperature normal', @RobotID1);

INSERT INTO fleetlogrobotslog (seq, unix_millis_time, tier, text, robot_id) VALUES
                                                                                (1, UNIX_TIMESTAMP()*1000, 'info', 'Routine check complete', @RobotID2),
                                                                                (2, UNIX_TIMESTAMP()*1000, 'warning', 'High temperature detected', @RobotID2),
                                                                                (3, UNIX_TIMESTAMP()*1000, 'info', 'Cooling initiated', @RobotID2),
                                                                                (4, UNIX_TIMESTAMP()*1000, 'info', 'Temperature normal', @RobotID2);

INSERT INTO fleetlogrobotslog (seq, unix_millis_time, tier, text, robot_id) VALUES
                                                                                (1, UNIX_TIMESTAMP()*1000, 'info', 'Routine check complete', @RobotID3),
                                                                                (2, UNIX_TIMESTAMP()*1000, 'warning', 'High temperature detected', @RobotID3),
                                                                                (3, UNIX_TIMESTAMP()*1000, 'info', 'Cooling initiated', @RobotID3),
                                                                                (4, UNIX_TIMESTAMP()*1000, 'info', 'Temperature normal', @RobotID3);
