import { Component, OnInit } from '@angular/core';
import { AsincTaskDTO } from '../dtos/asinc-task';
import { AsincTasksService } from '../services/debug/asinc-tasks.service';

@Component({
  selector: 'app-debug',
  templateUrl: './debug.component.html',
  styleUrls: ['./debug.component.css']
})
export class DebugComponent implements OnInit {

  tasks: AsincTaskDTO[] = [];

  constructor(private asinkTasksService: AsincTasksService) { }

  ngOnInit() {
    this.asinkTasksService.getAsincTasks().subscribe((tasks: AsincTaskDTO[]) => {
      this.tasks = tasks;
    });
  }

}
