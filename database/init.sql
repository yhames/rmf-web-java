------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS alert (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    original_id VARCHAR(255) NOT NULL,
    category VARCHAR(7) NOT NULL,
    unix_millis_created_time BIGINT NOT NULL,
    acknowledged_by VARCHAR(255),
    unix_millis_acknowledged_time BIGINT
    );

CREATE INDEX IF NOT EXISTS IDX_ALERT_ORIGINAL_ID_00 ON alert (original_id);
CREATE INDEX IF NOT EXISTS IDX_ALERT_CATEGORY_00 ON alert (category);
CREATE INDEX IF NOT EXISTS IDX_ALERT_UNIX_MILLIS_CREATED_TIME_00 ON alert (unix_millis_created_time);
CREATE INDEX IF NOT EXISTS IDX_ALERT_ACKNOWLEDGED_BY_00 ON alert (acknowledged_by);
CREATE INDEX IF NOT EXISTS IDX_ALERT_UNIX_MILLIS_ACKNOWLEDGED_TIME_00 ON alert (unix_millis_acknowledged_time);
------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS beaconstate (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    online INT NOT NULL,
    category VARCHAR(255),
    activated INT NOT NULL,
    level VARCHAR(255)
    );

CREATE INDEX IF NOT EXISTS IDX_BEACONSTATE_ONLINE_00 ON beaconstate (online);
CREATE INDEX IF NOT EXISTS IDX_BEACONSTATE_CATEGORY_00 ON beaconstate (category);
CREATE INDEX IF NOT EXISTS IDX_BEACONSTATE_ACTIVATED_00 ON beaconstate (activated);
CREATE INDEX IF NOT EXISTS IDX_BEACONSTATE_LEVEL_00 ON beaconstate (level);
------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS buildingmap (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    data JSONB NOT NULL
    );
------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS dispenserhealth (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    health_status VARCHAR(255),
    health_message TEXT
    );
------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS dispenserstate (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    data JSONB NOT NULL
    );
------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS doorhealth (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    health_status VARCHAR(255),
    health_message TEXT
    );
------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS doorstate (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    data JSONB NOT NULL
    );
------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS fleetlog (
    name VARCHAR(255) NOT NULL PRIMARY KEY
    );
------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------
CREATE SEQUENCE IF NOT EXISTS FLEETLOGLOG_SEQ
    INCREMENT BY 1000
    START WITH 1
    MINVALUE 1
    MAXVALUE 2147483647
    CACHE 10000;

CREATE TABLE IF NOT EXISTS fleetloglog (
    id INT PRIMARY KEY DEFAULT nextval('FLEETLOGLOG_SEQ'),
    seq INT NOT NULL,
    unix_millis_time BIGINT NOT NULL,
    tier VARCHAR(255) NOT NULL,
    text TEXT NOT NULL,
    fleet_id VARCHAR(255) NOT NULL,

    CONSTRAINT UIDX_FLEETLOGLOG_FLEET_ID_SEQ_00 UNIQUE (fleet_id, seq),
    CONSTRAINT FK_FLEETLOGLOG_FLEET_ID FOREIGN KEY (fleet_id) REFERENCES fleetlog(name) ON DELETE CASCADE
    );

CREATE INDEX IF NOT EXISTS IDX_FLEETLOGLOG_UNIX_MILLIS_TIME_00 ON fleetloglog (unix_millis_time);
------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS fleetlogrobots (
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    fleet_id VARCHAR(255) NOT NULL,

    CONSTRAINT UIDX_FLEETLOGROBOTS_FLEET_ID_NAME_00 UNIQUE (fleet_id, name),
    CONSTRAINT FK_FLEETLOGROBOTS_FLEET_ID FOREIGN KEY (fleet_id) REFERENCES fleetlog(name) ON DELETE CASCADE
    );
------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------
CREATE SEQUENCE IF NOT EXISTS FLEETLOGROBOTSLOG_SEQ
    INCREMENT BY 1000
    START WITH 1
    MINVALUE 1
    MAXVALUE 2147483647
    CACHE 10000;

CREATE TABLE IF NOT EXISTS fleetlogrobotslog (
    id INT PRIMARY KEY DEFAULT nextval('FLEETLOGROBOTSLOG_SEQ'),
    seq INT NOT NULL,
    unix_millis_time BIGINT NOT NULL,
    tier VARCHAR(255) NOT NULL,
    text TEXT NOT NULL,
    robot_id INT NOT NULL,

    CONSTRAINT UIDX_FLEETLOGROBOTSLOG_ROBOT_ID_SEQ_00 UNIQUE (robot_id, seq),
    CONSTRAINT FK_FLEETLOGROBOTSLOG_ROBOT_ID FOREIGN KEY (robot_id) REFERENCES fleetlogrobots(id) ON DELETE CASCADE
    );

CREATE INDEX IF NOT EXISTS IDX_FLEETLOGROBOTSLOG_UNIX_MILLIS_TIME_00 ON fleetlogrobotslog (unix_millis_time);
------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS fleetstate (
    name VARCHAR(255) NOT NULL PRIMARY KEY,
    data JSONB NOT NULL
    );
------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS ingestorhealth (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    health_status VARCHAR(255),
    health_message TEXT
    );
------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS ingestorstate (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    data JSONB NOT NULL
    );
------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS lifthealth (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    health_status VARCHAR(255),
    health_message TEXT
    );
------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS liftstate (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    data JSONB NOT NULL
    );
------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS robothealth (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    health_status VARCHAR(255),
    health_message TEXT
    );
------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS scheduledtask (
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    task_request JSONB NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    last_ran TIMESTAMP WITH TIME ZONE,
    except_dates JSONB
    );
------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS scheduledtaskschedule (
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    uuid VARCHAR(255) NOT NULL,
    every SMALLINT,
    start_from TIMESTAMP WITH TIME ZONE,
    until TIMESTAMP WITH TIME ZONE,
    period VARCHAR(9) NOT NULL,
    at VARCHAR(255),
    scheduled_task_id INT NOT NULL,

    CONSTRAINT FK_SCHEDULEDTASKSCHEDULE_SCHEDULED_TASK_ID FOREIGN KEY (scheduled_task_id) REFERENCES scheduledtask(id) ON DELETE CASCADE
    );
------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS taskeventlog (
    task_id VARCHAR(255) NOT NULL PRIMARY KEY
    );
------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------
CREATE SEQUENCE IF NOT EXISTS TASKEVENTLOGLOG_SEQ
    INCREMENT BY 1000
    START WITH 1
    MINVALUE 1
    MAXVALUE 2147483647
    CACHE 10000;

CREATE TABLE IF NOT EXISTS taskeventloglog (
    id INT PRIMARY KEY DEFAULT nextval('TASKEVENTLOGLOG_SEQ'),
    seq INT NOT NULL,
    unix_millis_time BIGINT NOT NULL,
    tier VARCHAR(255) NOT NULL,
    text TEXT NOT NULL,
    task_id VARCHAR(255) NOT NULL,

    CONSTRAINT UIDX_TASKEVENTLOGLOG_TASK_ID_SEQ_00 UNIQUE (task_id, seq),
    CONSTRAINT FK_TASKEVENTLOGLOG_TASK_ID FOREIGN KEY (task_id) REFERENCES taskeventlog(task_id) ON DELETE CASCADE
    );

CREATE INDEX IF NOT EXISTS IDX_TASKEVENTLOGLOG_UNIX_MILLIS_TIME_00 ON taskeventloglog (unix_millis_time);
------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS taskeventlogphases (
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    phase VARCHAR(255) NOT NULL,
    task_id VARCHAR(255) NOT NULL,

    CONSTRAINT FK_TASKEVENTLOGPHASES_TASK_ID FOREIGN KEY (task_id) REFERENCES taskeventlog(task_id) ON DELETE CASCADE
    );
------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS taskeventlogphasesevents (
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    event VARCHAR(255) NOT NULL,
    phase_id INT NOT NULL,

    CONSTRAINT FK_TASKEVENTLOGPHASESEVENTS_PHASE_ID FOREIGN KEY (phase_id) REFERENCES taskeventlogphases(id) ON DELETE CASCADE
    );
------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------
CREATE SEQUENCE IF NOT EXISTS TASKEVENTLOGPHASEEVENTSLOG_SEQ
    INCREMENT BY 1000
    START WITH 1
    MINVALUE 1
    MAXVALUE 2147483647
    CACHE 10000;

CREATE TABLE IF NOT EXISTS taskeventlogphaseseventslog (
    id INT PRIMARY KEY DEFAULT nextval('TASKEVENTLOGPHASEEVENTSLOG_SEQ'),
    seq INT NOT NULL,
    unix_millis_time BIGINT NOT NULL,
    tier VARCHAR(255) NOT NULL,
    text TEXT NOT NULL,
    event_id INT NOT NULL,

    CONSTRAINT UIDX_TASKEVENTLOGPHASESEVENTSLOG_ID_SEQ_00 UNIQUE (id, seq),
    CONSTRAINT FK_TASKEVENTLOGPHASESEVENTSLOG_EVENT_ID FOREIGN KEY (event_id) REFERENCES taskeventlogphasesevents(id) ON DELETE CASCADE
    );

CREATE INDEX IF NOT EXISTS IDX_TASKEVENTLOGPHASESEVENTSLOG_UNIX_MILLIS_TIME_00 ON taskeventlogphaseseventslog (unix_millis_time);
------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------
CREATE SEQUENCE IF NOT EXISTS TASKEVENTLOGPHASESLOG_SEQ
    INCREMENT BY 1000
    START WITH 1
    MINVALUE 1
    MAXVALUE 2147483647
    CACHE 10000;

CREATE TABLE IF NOT EXISTS taskeventlogphaseslog (
    id INT PRIMARY KEY DEFAULT nextval('TASKEVENTLOGPHASESLOG_SEQ'),
    seq INT NOT NULL,
    unix_millis_time BIGINT NOT NULL,
    tier VARCHAR(255) NOT NULL,
    text TEXT NOT NULL,
    phase_id INT NOT NULL,

    CONSTRAINT UIDX_TASKEVENTLOGPHASESLOG_ID_SEQ_00 UNIQUE (id, seq),
    CONSTRAINT FK_TASKEVENTLOGPHASESLOG_PHASE_ID FOREIGN KEY (phase_id) REFERENCES taskeventlogphases(id) ON DELETE CASCADE
    );

CREATE INDEX IF NOT EXISTS IDX_TASKEVENTLOGPHASESLOG_UNIX_MILLIS_TIME_00 ON taskeventlogphaseslog (unix_millis_time);
------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS taskfavorite (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    "name" VARCHAR(255) NOT NULL,
    unix_millis_earliest_start_time TIMESTAMP WITH TIME ZONE,
    priority JSONB,
    category VARCHAR(255) NOT NULL,
    description JSONB NOT NULL,
    "user" VARCHAR(255) NOT NULL
    );

CREATE INDEX IF NOT EXISTS IDX_TASKFAVORITE_NAME_00 ON taskfavorite ("name");
CREATE INDEX IF NOT EXISTS IDX_TASKFAVORITE_UNIX_MILLIS_EARLIEST_START_TIME_00 ON taskfavorite (unix_millis_earliest_start_time);
CREATE INDEX IF NOT EXISTS IDX_TASKFAVORITE_CATEGORY_00 ON taskfavorite (category);
CREATE INDEX IF NOT EXISTS IDX_TASKFAVORITE_USER ON taskfavorite ("user");
------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS taskrequest (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    request JSONB NOT NULL
    );
------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS taskstate (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    data JSONB NOT NULL,
    category VARCHAR(255),
    assigned_to VARCHAR(255),
    unix_millis_start_time TIMESTAMP WITH TIME ZONE,
    unix_millis_finish_time TIMESTAMP WITH TIME ZONE,
    status VARCHAR(255),
    unix_millis_request_time TIMESTAMP WITH TIME ZONE,
    requester VARCHAR(255)
    );

CREATE INDEX IF NOT EXISTS IDX_TASKSTATE_CATEGORY ON taskstate (category);
CREATE INDEX IF NOT EXISTS IDX_TASKSTATE_ASSIGNED_TO ON taskstate (assigned_to);
CREATE INDEX IF NOT EXISTS IDX_TASKSTATE_UNIX_MILLIS_START_TIME ON taskstate (unix_millis_start_time);
CREATE INDEX IF NOT EXISTS IDX_TASKSTATE_UNIX_MILLIS_FINISH_TIME ON taskstate (unix_millis_finish_time);
CREATE INDEX IF NOT EXISTS IDX_TASKSTATE_STATUS ON taskstate (status);
CREATE INDEX IF NOT EXISTS IDX_TASKSTATE_UNIX_MILLIS_REQUEST_TIME ON taskstate (unix_millis_request_time);
CREATE INDEX IF NOT EXISTS IDX_TASKSTATE_REQUESTER ON taskstate (requester);
------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS role (
    name VARCHAR(255) NOT NULL PRIMARY KEY
    );
------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS resourcepermission (
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    authz_grp VARCHAR(255) NOT NULL,
    action VARCHAR(255) NOT NULL,
    role_id VARCHAR(255) NOT NULL,

    CONSTRAINT FK_RESOURCEPERMISSION_ROLE_ID FOREIGN KEY (role_id) REFERENCES role(name) ON DELETE CASCADE
    );

CREATE INDEX IF NOT EXISTS IDX_RESOURCEPERMISSION_AUTHZ_GRP_00 ON resourcepermission (authz_grp);
------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS validation_code (
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    code VARCHAR(255) NOT NULL,
    expiration_time TIMESTAMP NOT NULL
    );
------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS email (
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    is_verified BOOLEAN NOT NULL,
    validation_code_id INT,
    CONSTRAINT FK_EMAIL_VALIDATION_CODE_ID FOREIGN KEY (validation_code_id) REFERENCES validation_code(id),
    CONSTRAINT UIDX_EMAIL_EMAIL_00 UNIQUE (email),
    CONSTRAINT UIDX_EMAIL_VALIDATION_CODE_ID_00 UNIQUE (validation_code_id)
    );
------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS "user" (
    username VARCHAR(255) NOT NULL PRIMARY KEY,
    password VARCHAR(255) NOT NULL,
    is_admin BOOLEAN NOT NULL,
    email_id INT NOT NULL,
    CONSTRAINT FK_USER_EMAIL_ID FOREIGN KEY (email_id) REFERENCES email(id),
    CONSTRAINT UIDX_USER_EMAIL_ID_00 UNIQUE (email_id)
    );
------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS user_role (
    -- id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    role_id VARCHAR(255) NOT NULL,
    PRIMARY KEY (user_id, role_id),

    CONSTRAINT FK_USER_ROLE_USER_ID FOREIGN KEY (user_id) REFERENCES "user"(username) ON DELETE CASCADE,
    CONSTRAINT FK_USER_ROLE_ROLE_ID FOREIGN KEY (role_id) REFERENCES role(name) ON DELETE CASCADE
    );
------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS refresh_token (
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    refresh_token VARCHAR(255) NOT NULL,
    ip VARCHAR(255) NOT NULL,

    CONSTRAINT UIDX_REFRESH_TOKEN_USER_ID_00 UNIQUE (user_id),
    CONSTRAINT FK_REFRESH_TOKEN_USER_ID FOREIGN KEY (user_id) REFERENCES "user"(username) ON DELETE CASCADE
    );
------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------
