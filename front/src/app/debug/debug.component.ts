import { Component, OnInit } from '@angular/core';
import { AsincTaskDTO } from '../dtos/asinc-task';
import { AsincTasksService } from '../services/debug/asinc-tasks.service';
import { SocketConnectionListDTO } from '../dtos/socket-connection-list';
import { SocketConnectionsService } from '../services/debug/socket-connections.service';

@Component({
  selector: 'app-debug',
  templateUrl: './debug.component.html',
  styleUrls: ['./debug.component.css']
})
export class DebugComponent implements OnInit {

  tasks: AsincTaskDTO[] = [];
  connections: SocketConnectionListDTO[] = [];

  constructor(private asinkTasksService: AsincTasksService, private socketConnectionsService: SocketConnectionsService) { }

  ngOnInit() {
    this.asinkTasksService.getAsincTasks().subscribe((tasks: AsincTaskDTO[]) => {
      this.tasks = tasks;
    });
    this.socketConnectionsService.getSocketConnections().subscribe((connections: SocketConnectionListDTO[]) => {
      this.connections = connections;
    });

  }

}
