CREATE TABLE IF NOT EXISTS "alert" (
    "id" VARCHAR(255) NOT NULL PRIMARY KEY,
    "original_id" VARCHAR(255) NOT NULL,
    "category" VARCHAR(7) NOT NULL,
    "unix_millis_created_time" BIGINT NOT NULL,
    "acknowledged_by" VARCHAR(255),
    "unix_millis_acknowledged_time" BIGINT
);
CREATE INDEX "IDX_ALERT_ORIGINAL_ID_00" ON "alert" ("original_id");
CREATE INDEX "IDX_ALERT_CATEGORY_00" ON "alert" ("category");
CREATE INDEX "IDX_ALERT_UNIX_MILLIS_CREATED_TIME_00" ON "alert" ("unix_millis_created_time");
CREATE INDEX "IDX_ALERT_ACKNOWLEDGED_BY_00" ON "alert" ("acknowledged_by");
CREATE INDEX "IDX_ALERT_UNIX_MILLIS_ACKNOWLEDGED_TIME_00" ON "alert" ("unix_millis_acknowledged_time");

CREATE TABLE IF NOT EXISTS "beaconstate" (
    "id" VARCHAR(255) NOT NULL PRIMARY KEY,
    "online" INT NOT NULL,
    "category" VARCHAR(255),
    "activated" INT NOT NULL,
    "level" VARCHAR(255)
);
CREATE INDEX "IDX_BEACONSTATE_ONLINE_00" ON "beaconstate" ("online");
CREATE INDEX "IDX_BEACONSTATE_CATEGORY_00" ON "beaconstate" ("category");
CREATE INDEX "IDX_BEACONSTATE_ACTIVATED_00" ON "beaconstate" ("activated");
CREATE INDEX "IDX_BEACONSTATE_LEVEL_00" ON "beaconstate" ("level");

CREATE TABLE IF NOT EXISTS "buildingmap" (
    "id" VARCHAR(255) NOT NULL PRIMARY KEY,
    "data" JSON NOT NULL
);

CREATE TABLE IF NOT EXISTS "dispenserhealth" (
    "id" VARCHAR(255) NOT NULL PRIMARY KEY,
    "health_status" VARCHAR(255),
    "health_message" TEXT
);

CREATE TABLE IF NOT EXISTS "dispenserstate" (
    "id" VARCHAR(255) NOT NULL PRIMARY KEY,
    "data" JSON NOT NULL
);

CREATE TABLE IF NOT EXISTS "doorhealth" (
    "id" VARCHAR(255) NOT NULL PRIMARY KEY,
    "health_status" VARCHAR(255),
    "health_message" TEXT
);

CREATE TABLE IF NOT EXISTS "doorstate" (
    "id" VARCHAR(255) NOT NULL PRIMARY KEY,
    "data" JSON NOT NULL
);

CREATE TABLE IF NOT EXISTS "fleetlog" (
    "name" VARCHAR(255) NOT NULL PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS "fleetloglog" (
    "id" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    "seq" INT NOT NULL,
    "unix_millis_time" BIGINT NOT NULL,
    "tier" VARCHAR(255) NOT NULL,
    "text" TEXT NOT NULL,
    "fleet_id" VARCHAR(255) NOT NULL REFERENCES "fleetlog" ("name") ON DELETE CASCADE,
    CONSTRAINT "UIDX_FLEETLOGLOG_FLEET_ID_SEQ_00" UNIQUE ("fleet_id", "seq")
);
CREATE INDEX "IDX_FLEETLOGLOG_UNIX_MILLIS_TIME_00" ON "fleetloglog" ("unix_millis_time");

CREATE TABLE IF NOT EXISTS "fleetlogrobots" (
    "id" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    "name" VARCHAR(255) NOT NULL,
    "fleet_id" VARCHAR(255) NOT NULL REFERENCES "fleetlog" ("name") ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS "fleetlogrobotslog" (
    "id" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    "seq" INT NOT NULL,
    "unix_millis_time" BIGINT NOT NULL,
    "tier" VARCHAR(255) NOT NULL,
    "text" TEXT NOT NULL,
    "robot_id" INT NOT NULL REFERENCES "fleetlogrobots" ("id") ON DELETE CASCADE,
    CONSTRAINT "UIDX_FLEETLOGROBOTSLOG_ID_SEQ_00" UNIQUE ("id", "seq")
);
CREATE INDEX "IDX_FLEETLOGROBOTSLOGG_UNIX_MILLIS_TIME_00" ON "fleetlogrobotslog" ("unix_millis_time");

CREATE TABLE IF NOT EXISTS "fleetstate" (
    "name" VARCHAR(255) NOT NULL  PRIMARY KEY,
    "data" JSON NOT NULL
);

CREATE TABLE IF NOT EXISTS "ingestorhealth" (
    "id" VARCHAR(255) NOT NULL PRIMARY KEY,
    "health_status" VARCHAR(255),
    "health_message" TEXT
);

CREATE TABLE IF NOT EXISTS "ingestorstate" (
    "id" VARCHAR(255) NOT NULL PRIMARY KEY,
    "data" JSON NOT NULL
);

CREATE TABLE IF NOT EXISTS "lifthealth" (
    "id" VARCHAR(255) NOT NULL PRIMARY KEY,
    "health_status" VARCHAR(255),
    "health_message" TEXT
);

CREATE TABLE IF NOT EXISTS "liftstate" (
    "id" VARCHAR(255) NOT NULL PRIMARY KEY,
    "data" JSON NOT NULL
);

CREATE TABLE IF NOT EXISTS "robothealth" (
    "id" VARCHAR(255) NOT NULL PRIMARY KEY,
    "health_status" VARCHAR(255),
    "health_message" TEXT
);

CREATE TABLE IF NOT EXISTS "scheduledtask" (
    "id" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    "task_request" JSON NOT NULL,
    "created_by" VARCHAR(255) NOT NULL,
    "last_ran" TIMESTAMP,
    "except_dates" JSON
);

CREATE TABLE IF NOT EXISTS "scheduledtaskschedule" (
    "id" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    "every" SMALLINT,
    "start_from" TIMESTAMP,
    "until" TIMESTAMP,
    "period" VARCHAR(9) NOT NULL,
    "at" VARCHAR(255),
    "scheduled_task_id" INT NOT NULL REFERENCES "scheduledtask" ("id") ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS "taskeventlog" (
    "task_id" VARCHAR(255) NOT NULL PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS "taskeventloglog" (
    "id" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    "seq" INT NOT NULL,
    "unix_millis_time" BIGINT NOT NULL,
    "tier" VARCHAR(255) NOT NULL,
    "text" TEXT NOT NULL,
    "task_id" VARCHAR(255) NOT NULL REFERENCES "taskeventlog" ("task_id") ON DELETE CASCADE,
    CONSTRAINT "UIDX_TASKEVENTLOGLOG_TASK_ID_SEQ_00" UNIQUE ("task_id", "seq")
);
CREATE INDEX "IDX_TASKEVENTLOGLOG_UNIX_MILLIS_TIME_00" ON "taskeventloglog" ("unix_millis_time");

CREATE TABLE IF NOT EXISTS "taskeventlogphases" (
    "id" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    "phase" VARCHAR(255) NOT NULL,
    "task_id" VARCHAR(255) NOT NULL REFERENCES "taskeventlog" ("task_id") ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS "taskeventlogphasesevents" (
    "id" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    "event" VARCHAR(255) NOT NULL,
    "phase_id" INT NOT NULL REFERENCES "taskeventlogphases" ("id") ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS "taskeventlogphaseseventslog" (
    "id" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    "seq" INT NOT NULL,
    "unix_millis_time" BIGINT NOT NULL,
    "tier" VARCHAR(255) NOT NULL,
    "text" TEXT NOT NULL,
    "event_id" INT NOT NULL REFERENCES "taskeventlogphasesevents" ("id") ON DELETE CASCADE,
    CONSTRAINT "UIDX_TASKEVENTLOGPHASESEVENTSLOG_ID_SEQ_00" UNIQUE ("id", "seq")
);
CREATE INDEX "IDX_TASKEVENTLOGPHASESEVENTSLOG_UNIX_MILLIS_TIME_00" ON "taskeventlogphaseseventslog" ("unix_millis_time");

CREATE TABLE IF NOT EXISTS "taskeventlogphaseslog" (
    "id" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    "seq" INT NOT NULL,
    "unix_millis_time" BIGINT NOT NULL,
    "tier" VARCHAR(255) NOT NULL,
    "text" TEXT NOT NULL,
    "phase_id" INT NOT NULL REFERENCES "taskeventlogphases" ("id") ON DELETE CASCADE,
    CONSTRAINT "UIDX_TASKEVENTLOGPHASESLOG_ID_SEQ_00" UNIQUE ("id", "seq")
);
CREATE INDEX "IDX_TASKEVENTLOGPHASESLOG_UNIX_MILLIS_TIME_00" ON "taskeventlogphaseslog" ("unix_millis_time");

CREATE TABLE IF NOT EXISTS "taskfavorite" (
    "id" VARCHAR(255) NOT NULL PRIMARY KEY,
    "name" VARCHAR(255) NOT NULL,
    "unix_millis_earliest_start_time" TIMESTAMP,
    "priority" JSON,
    "category" VARCHAR(255) NOT NULL,
    "description" JSON NOT NULL,
    "user" VARCHAR(255) NOT NULL
);
CREATE INDEX "IDX_TASKFAVORITE_NAME_00" ON "taskfavorite" ("name");
CREATE INDEX "IDX_TASKFAVORITE_UNIX_MILLIS_EARLIEST_START_TIME_00" ON "taskfavorite" ("unix_millis_earliest_start_time");
CREATE INDEX "IDX_TASKFAVORITE_CATEGORY_00" ON "taskfavorite" ("category");
CREATE INDEX "IDX_TASKFAVORITE_USER" ON "taskfavorite" ("user");

CREATE TABLE IF NOT EXISTS "taskrequest" (
    "id" VARCHAR(255) NOT NULL PRIMARY KEY,
    "request" JSON NOT NULL
);

CREATE TABLE IF NOT EXISTS "taskstate" (
    "id" VARCHAR(255) NOT NULL PRIMARY KEY,
    "data" JSON NOT NULL,
    "category" VARCHAR(255),
    "assigned_to" VARCHAR(255),
    "unix_millis_start_time" TIMESTAMP,
    "unix_millis_finish_time" TIMESTAMP,
    "status" VARCHAR(255),
    "unix_millis_request_time" TIMESTAMP,
    "requester" VARCHAR(255)
);
CREATE INDEX "IDX_TASKSTATE_CATEGORY" ON "taskstate" ("category");
CREATE INDEX "IDX_TASKSTATE_ASSIGNED_TO" ON "taskstate" ("assigned_to");
CREATE INDEX "IDX_TASKSTATE_UNIX_MILLIS_START_TIME" ON "taskstate" ("unix_millis_start_time");
CREATE INDEX "IDX_TASKSTATE_UNIX_MILLIS_FINISH_TIME" ON "taskstate" ("unix_millis_finish_time");
CREATE INDEX "IDX_TASKSTATE_STATUS" ON "taskstate" ("status");
CREATE INDEX "IDX_TASKSTATE_UNIX_MILLIS_REQUEST_TIME" ON "taskstate" ("unix_millis_request_time");
CREATE INDEX "IDX_TASKSTATE_REQUESTER" ON "taskstate" ("requester");

CREATE TABLE IF NOT EXISTS "role" (
    "name" VARCHAR(255) NOT NULL PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS "resourcepermission" (
    "id" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    "authz_grp" VARCHAR(255) NOT NULL,
    "action" VARCHAR(255) NOT NULL,
    "role_id" VARCHAR(255) NOT NULL REFERENCES "role" ("name") ON DELETE CASCADE
);
CREATE INDEX "IDX_RESOURCEPERMISSION_AUTHZ_GRP_00" ON "resourcepermission" ("authz_grp");

CREATE TABLE IF NOT EXISTS "user" (
    "username" VARCHAR(255) NOT NULL PRIMARY KEY,
    "is_admin" INT NOT NULL
);

CREATE TABLE IF NOT EXISTS "user_role" (
    "user_id" VARCHAR(255) NOT NULL REFERENCES "user" ("username") ON DELETE CASCADE,
    "role_id" VARCHAR(255) NOT NULL REFERENCES "role" ("name") ON DELETE CASCADE
);
