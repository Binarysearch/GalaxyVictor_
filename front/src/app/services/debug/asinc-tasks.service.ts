import { HttpClient } from '@angular/common/http';
import { Injectable, isDevMode } from '@angular/core';
import { AsincTaskDTO } from 'src/app/dtos/asinc-task';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AsincTasksService {

  private host = (isDevMode()) ? 'http://localhost:8080' : 'https://galaxyvictor.com';

  private asincTasksUrl = this.host + '/api/asinc-tasks';

  constructor(private http: HttpClient) { }

  public getAsincTasks(): Observable<AsincTaskDTO[]> {
    return this.http.get<AsincTaskDTO[]>(this.asincTasksUrl);
  }
}
