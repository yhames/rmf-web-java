import Debug from 'debug';
import { io, Socket } from 'socket.io-client';
import {
  ApiServerModelsTortoiseModelsAlertsAlertLeaf,
  ApiServerModelsTortoiseModelsBeaconsBeaconStateLeaf as BeaconState,
  BuildingMap,
  DispenserHealth,
  DispenserState,
  DoorHealth,
  DoorState,
  ApiServerModelsRmfApiFleetStateFleetState,
  IngestorHealth,
  IngestorState,
  LiftHealth,
  LiftState,
  TaskEventLog,
  TaskState,
} from './openapi';

type Alert = ApiServerModelsTortoiseModelsAlertsAlertLeaf;
type FleetState = ApiServerModelsRmfApiFleetStateFleetState;

const debug = Debug('rmf-client');

// https://stackoverflow.com/questions/52667959/what-is-the-purpose-of-bivariancehack-in-typescript-types
export type Listener<T = unknown> = { bivarianceHack(resp: T): void }['bivarianceHack'];
export interface Subscription {
  room: string;
  listener: Listener;
}

export class SioClient {
  public sio: Socket;
  private _subscriptions: Record<string, number> = {};

  constructor(...args: Parameters<typeof io>) {
    if (args.length > 1 && typeof args[1] === "object") {
      args[1] = { ...args[1], withCredentials: true, reconnection: true, reconnectionAttempts: Infinity, reconnectionDelay: 1000, reconnectionDelayMax: 5000};
    } else {
      args.push({ withCredentials: true });
    }
    this.sio = io(...args);
    this.sio.on('disconnect', () => this.onDisconnect());

  }

  onDisconnect(): void {
    console.log('Disconnected from server. try reconnect.');
    this.reconnect();
  }

  reconnect(): void {
    setTimeout(() => {
      fetch('http://localhost:8090/token/refresh', {
        method: 'POST',
        credentials: 'include',
        headers: {
          'Content-Type': 'application/json',
        },
      })
      .then(response => {
        if (!response.ok) {
          throw new Error('Network response was not ok');
        }
      })
      .then(data => {
        console.log('Token refreshed:', data);
        this.sio.connect();

        Object.keys(this._subscriptions).forEach(room => {
          this.sio.emit('subscribe', { room });
          console.log(`Re-subscribed to ${room}`);
        });
      })
      .catch(error => {
        console.error('Error refreshing token:', error);
        window.location.href = '/login';
      });
    }, 5000);
  }


  subscribe<T>(room: string, listener: Listener<T>): Subscription {
    const subs = this._subscriptions[room] || 0;
    if (subs === 0) {
      this.sio.emit('subscribe', { room });
      debug(`subscribed to ${room}`);
    } else {
      debug(`reusing previous subscription to ${room}`);
    }
    this.sio.on(room, listener);
    this._subscriptions[room] = subs + 1;
    return { room, listener };
  }

  unsubscribe(sub: Subscription): void {
    const subCount = this._subscriptions[sub.room] || 0;
    if (!subCount) {
      debug(`tried to unsubscribe from ${sub.room}, but no subscriptions exist`);
      // continue regardless
    }
    if (subCount <= 1) {
      this.sio.emit('unsubscribe', { room: sub.room });
      delete this._subscriptions[sub.room];
      debug(`unsubscribed to ${sub.room}`);
    } else {
      this._subscriptions[sub.room] = subCount - 1;
      debug(
        `skipping unsubscribe to ${sub.room} because there are still ${subCount - 1} subscribers`,
      );
    }
    this.sio.off(sub.room, sub.listener);
  }

  subscribeBeaconState(listener: Listener<BeaconState>): Subscription {
    return this.subscribe<BeaconState>('/beacons', listener);
  }

  subscribeBuildingMap(listener: Listener<BuildingMap>): Subscription {
    return this.subscribe<BuildingMap>(`/building_map`, listener);
  }

  subscribeDoorState(doorName: string, listener: Listener<DoorState>): Subscription {
    return this.subscribe<DoorState>(`/doors/${doorName}/state`, listener);
  }

  subscribeDoorHealth(doorName: string, listener: Listener<DoorHealth>): Subscription {
    return this.subscribe<DoorHealth>(`/doors/${doorName}/health`, listener);
  }

  subscribeLiftState(liftName: string, listener: Listener<LiftState>): Subscription {
    return this.subscribe<LiftState>(`/lifts/${liftName}/state`, listener);
  }

  subscribeLiftHealth(liftName: string, listener: Listener<LiftHealth>): Subscription {
    return this.subscribe<LiftHealth>(`/doors/${liftName}/health`, listener);
  }

  subscribeDispenserState(guid: string, listener: Listener<DispenserState>): Subscription {
    return this.subscribe<DispenserState>(`/dispensers/${guid}/state`, listener);
  }

  subscribeDispenserHealth(guid: string, listener: Listener<DispenserHealth>): Subscription {
    return this.subscribe<DispenserHealth>(`/dispensers/${guid}/health`, listener);
  }

  subscribeIngestorState(guid: string, listener: Listener<IngestorState>): Subscription {
    return this.subscribe<IngestorState>(`/ingestors/${guid}/state`, listener);
  }

  subscribeIngestorHealth(guid: string, listener: Listener<IngestorHealth>): Subscription {
    return this.subscribe<IngestorHealth>(`/ingestors/${guid}/health`, listener);
  }

  subscribeFleetState(name: string, listener: Listener<FleetState>): Subscription {
    return this.subscribe<FleetState>(`/fleets/${name}/state`, listener);
  }

  subscribeTaskState(taskId: string, listener: Listener<TaskState>): Subscription {
    return this.subscribe<TaskState>(`/tasks/${taskId}/state`, listener);
  }

  subscribeTaskLogs(taskId: string, listener: Listener<TaskEventLog>): Subscription {
    return this.subscribe<TaskEventLog>(`/tasks/${taskId}/log`, listener);
  }

  subscribeAlerts(listener: Listener<Alert>): Subscription {
    return this.subscribe<Alert>(`/alerts`, listener);
  }
}

export * from './openapi';
