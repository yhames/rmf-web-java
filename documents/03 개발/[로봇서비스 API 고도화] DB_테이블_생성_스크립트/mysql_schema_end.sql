CREATE TABLE IF NOT EXISTS alert (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    original_id VARCHAR(255) NOT NULL,
    category VARCHAR(7) NOT NULL,
    unix_millis_created_time BIGINT NOT NULL,
    acknowledged_by VARCHAR(255),
    unix_millis_acknowledged_time BIGINT,

    INDEX IDX_ALERT_ORIGINAL_ID_00 (original_id),
    INDEX IDX_ALERT_CATEGORY_00 (category),
    INDEX IDX_ALERT_UNIX_MILLIS_CREATED_TIME_00 (unix_millis_created_time),
    INDEX IDX_ALERT_ACKNOWLEDGED_BY_00 (acknowledged_by),
    INDEX IDX_ALERT_UNIX_MILLIS_ACKNOWLEDGED_TIME_00 (unix_millis_acknowledged_time)
);

CREATE TABLE IF NOT EXISTS beaconstate (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    online INT NOT NULL,
    category VARCHAR(255),
    activated INT NOT NULL,
    level VARCHAR(255),

    INDEX IDX_BEACONSTATE_ONLINE_00 (online),
    INDEX IDX_BEACONSTATE_CATEGORY_00 (category),
    INDEX IDX_BEACONSTATE_ACTIVATED_00 (activated),
    INDEX IDX_BEACONSTATE_LEVEL_00 (level)
);

CREATE TABLE IF NOT EXISTS buildingmap (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    data JSON NOT NULL
);

CREATE TABLE IF NOT EXISTS dispenserhealth (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    health_status VARCHAR(255),
    health_message TEXT
);

CREATE TABLE IF NOT EXISTS dispenserstate (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    data JSON NOT NULL
);

CREATE TABLE IF NOT EXISTS doorhealth (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    health_status VARCHAR(255),
    health_message TEXT
);

CREATE TABLE IF NOT EXISTS doorstate (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    data JSON NOT NULL
);

CREATE TABLE IF NOT EXISTS fleetlog (
    name VARCHAR(255) NOT NULL PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS fleetloglog (
    id INT AUTO_INCREMENT PRIMARY KEY,
    seq INT NOT NULL,
    unix_millis_time BIGINT NOT NULL,
    tier VARCHAR(255) NOT NULL,
    text TEXT NOT NULL,
    fleet_id VARCHAR(255) NOT NULL,

    CONSTRAINT UIDX_FLEETLOGLOG_FLEET_ID_SEQ_00 UNIQUE (fleet_id, seq),
    CONSTRAINT FK_FLEETLOGLOG_FLEET_ID FOREIGN KEY (fleet_id) REFERENCES fleetlog(name) ON DELETE CASCADE,
    INDEX IDX_FLEETLOGLOG_UNIX_MILLIS_TIME_00 (unix_millis_time)
);

CREATE TABLE IF NOT EXISTS fleetlogrobots (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    fleet_id VARCHAR(255) NOT NULL,

    CONSTRAINT FK_FLEETLOGROBOTS_FLEET_ID FOREIGN KEY (fleet_id) REFERENCES fleetlog(name) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS fleetlogrobotslog (
    id INT AUTO_INCREMENT PRIMARY KEY,
    seq INT NOT NULL,
    unix_millis_time BIGINT NOT NULL,
    tier VARCHAR(255) NOT NULL,
    text TEXT NOT NULL,
    robot_id INT NOT NULL,

    CONSTRAINT UIDX_FLEETLOGROBOTSLOG_ID_SEQ_00 UNIQUE (id, seq),
    CONSTRAINT FK_FLEETLOGROBOTSLOG_ROBOT_ID FOREIGN KEY (robot_id) REFERENCES fleetlogrobots(id) ON DELETE CASCADE,
    INDEX IDX_FLEETLOGROBOTSLOG_UNIX_MILLIS_TIME_00 (unix_millis_time)
);

CREATE TABLE IF NOT EXISTS fleetstate (
    name VARCHAR(255) NOT NULL PRIMARY KEY,
    data JSON NOT NULL
);

CREATE TABLE IF NOT EXISTS ingestorhealth (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    health_status VARCHAR(255),
    health_message TEXT
);

CREATE TABLE IF NOT EXISTS ingestorstate (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    data JSON NOT NULL
);

CREATE TABLE IF NOT EXISTS lifthealth (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    health_status VARCHAR(255),
    health_message TEXT
);

CREATE TABLE IF NOT EXISTS liftstate (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    data JSON NOT NULL
);

CREATE TABLE IF NOT EXISTS robothealth (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    health_status VARCHAR(255),
    health_message TEXT
);

CREATE TABLE IF NOT EXISTS scheduledtask (
    id INT AUTO_INCREMENT PRIMARY KEY,
    task_request JSON NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    last_ran TIMESTAMP,
    except_dates JSON
);

CREATE TABLE IF NOT EXISTS scheduledtaskschedule (
    id INT AUTO_INCREMENT PRIMARY KEY,
    every SMALLINT,
    start_from TIMESTAMP,
    until TIMESTAMP,
    period VARCHAR(9) NOT NULL,
    at VARCHAR(255),
    scheduled_task_id INT NOT NULL,

    CONSTRAINT FK_SCHEDULEDTASKSCHEDULE_SCHEDULED_TASK_ID FOREIGN KEY (scheduled_task_id) REFERENCES scheduledtask(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS taskeventlog (
    task_id VARCHAR(255) NOT NULL PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS taskeventloglog (
    id INT AUTO_INCREMENT PRIMARY KEY,
    seq INT NOT NULL,
    unix_millis_time BIGINT NOT NULL,
    tier VARCHAR(255) NOT NULL,
    text TEXT NOT NULL,
    task_id VARCHAR(255) NOT NULL,

    CONSTRAINT UIDX_TASKEVENTLOGLOG_TASK_ID_SEQ_00 UNIQUE (task_id, seq),
    CONSTRAINT FK_TASKEVENTLOGLOG_TASK_ID FOREIGN KEY (task_id) REFERENCES taskeventlog(task_id) ON DELETE CASCADE,
    INDEX IDX_TASKEVENTLOGLOG_UNIX_MILLIS_TIME_00 (unix_millis_time)
);

CREATE TABLE IF NOT EXISTS taskeventlogphases (
    id INT AUTO_INCREMENT PRIMARY KEY,
    phase VARCHAR(255) NOT NULL,
    task_id VARCHAR(255) NOT NULL,

    CONSTRAINT FK_TASKEVENTLOGPHASES_TASK_ID FOREIGN KEY (task_id) REFERENCES taskeventlog(task_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS taskeventlogphasesevents (
    id INT AUTO_INCREMENT PRIMARY KEY,
    event VARCHAR(255) NOT NULL,
    phase_id INT NOT NULL,

    CONSTRAINT FK_TASKEVENTLOGPHASESEVENTS_PHASE_ID FOREIGN KEY (phase_id) REFERENCES taskeventlogphases(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS taskeventlogphaseseventslog (
    id INT AUTO_INCREMENT PRIMARY KEY,
    seq INT NOT NULL,
    unix_millis_time BIGINT NOT NULL,
    tier VARCHAR(255) NOT NULL,
    text TEXT NOT NULL,
    event_id INT NOT NULL,

    CONSTRAINT UIDX_TASKEVENTLOGPHASESEVENTSLOG_ID_SEQ_00 UNIQUE (id, seq),
    CONSTRAINT FK_TASKEVENTLOGPHASESEVENTSLOG_EVENT_ID FOREIGN KEY (event_id) REFERENCES taskeventlogphasesevents(id) ON DELETE CASCADE,
    INDEX IDX_TASKEVENTLOGPHASESEVENTSLOG_UNIX_MILLIS_TIME_00 (unix_millis_time)
);

CREATE TABLE IF NOT EXISTS taskeventlogphaseslog (
    id INT AUTO_INCREMENT PRIMARY KEY,
    seq INT NOT NULL,
    unix_millis_time BIGINT NOT NULL,
    tier VARCHAR(255) NOT NULL,
    text TEXT NOT NULL,
    phase_id INT NOT NULL,

    CONSTRAINT UIDX_TASKEVENTLOGPHASESLOG_ID_SEQ_00 UNIQUE (id, seq),
    CONSTRAINT FK_TASKEVENTLOGPHASESLOG_PHASE_ID FOREIGN KEY (phase_id) REFERENCES taskeventlogphases(id) ON DELETE CASCADE,
    INDEX IDX_TASKEVENTLOGPHASESLOG_UNIX_MILLIS_TIME_00 (unix_millis_time)
);

CREATE TABLE IF NOT EXISTS taskfavorite (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    unix_millis_earliest_start_time TIMESTAMP,
    priority JSON,
    category VARCHAR(255) NOT NULL,
    description JSON NOT NULL,
    user VARCHAR(255) NOT NULL,


    INDEX IDX_TASKFAVORITE_NAME_00 (name),
    INDEX IDX_TASKFAVORITE_UNIX_MILLIS_EARLIEST_START_TIME_00 (unix_millis_earliest_start_time),
    INDEX IDX_TASKFAVORITE_CATEGORY_00 (category),
    INDEX IDX_TASKFAVORITE_USER (user)
);


CREATE TABLE IF NOT EXISTS taskrequest (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    request JSON NOT NULL
);

CREATE TABLE IF NOT EXISTS taskstate (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    data JSON NOT NULL,
    category VARCHAR(255),
    assigned_to VARCHAR(255),
    unix_millis_start_time TIMESTAMP,
    unix_millis_finish_time TIMESTAMP,
    status VARCHAR(255),
    unix_millis_request_time TIMESTAMP,
    requester VARCHAR(255),

    INDEX IDX_TASKSTATE_CATEGORY (category),
    INDEX IDX_TASKSTATE_ASSIGNED_TO (assigned_to),
    INDEX IDX_TASKSTATE_UNIX_MILLIS_START_TIME (unix_millis_start_time),
    INDEX IDX_TASKSTATE_UNIX_MILLIS_FINISH_TIME (unix_millis_finish_time),
    INDEX IDX_TASKSTATE_STATUS (status),
    INDEX IDX_TASKSTATE_UNIX_MILLIS_REQUEST_TIME (unix_millis_request_time),
    INDEX IDX_TASKSTATE_REQUESTER (requester)
);

CREATE TABLE IF NOT EXISTS validation_code (
    id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) NOT NULL,
    expiration_time TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS email (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) NOT NULL,
    is_verified TINYINT NOT NULL,
    validation_code_id INT,
    CONSTRAINT FK_EMAIL_VALIDATION_CODE_ID FOREIGN KEY (validation_code_id) REFERENCES validation_code(id),
    CONSTRAINT UIDX_EMAIL_EMAIL_00 UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS role (
    name VARCHAR(255) NOT NULL PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS resourcepermission (
    id INT AUTO_INCREMENT PRIMARY KEY,
    authz_grp VARCHAR(255) NOT NULL,
    action VARCHAR(255) NOT NULL,
    role_id VARCHAR(255) NOT NULL,

    CONSTRAINT FK_RESOURCEPERMISSION_ROLE_ID FOREIGN KEY (role_id) REFERENCES role(name) ON DELETE CASCADE,
    INDEX IDX_RESOURCEPERMISSION_AUTHZ_GRP_00 (authz_grp)
);

CREATE TABLE IF NOT EXISTS user (
    username VARCHAR(50) NOT NULL PRIMARY KEY,
    password VARCHAR(50) NOT NULL,
    is_admin INT NOT NULL,
    email_id INT NOT NULL,
    CONSTRAINT FK_USER_EMAIL_ID FOREIGN KEY (email_id) REFERENCES email(id)
);

CREATE TABLE IF NOT EXISTS user_role (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(50) NOT NULL,
    role_id VARCHAR(255) NOT NULL,

    CONSTRAINT FK_USER_ROLE_USER_ID FOREIGN KEY (user_id) REFERENCES user(username) ON DELETE CASCADE,
    CONSTRAINT FK_USER_ROLE_ROLE_ID FOREIGN KEY (role_id) REFERENCES role(name) ON DELETE CASCADE,
    CONSTRAINT UIDX_USER_ROLE_USER_ID_ROLE_ID_00 UNIQUE (user_id, role_id)
);
