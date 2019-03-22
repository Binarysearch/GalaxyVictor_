import { HttpClient } from '@angular/common/http';
import { Injectable, isDevMode } from '@angular/core';
import { Observable } from 'rxjs';
import { SocketConnectionListDTO } from '../../dtos/socket-connection-list';

@Injectable({
  providedIn: 'root'
})
export class SocketConnectionsService  {

  private host = (isDevMode()) ? 'http://localhost:8080' : 'https://galaxyvictor.com';

  private socketConnectionsUrl = this.host + '/api/socket-connections';

  constructor(private http: HttpClient) { }

  public getSocketConnections(): Observable<SocketConnectionListDTO[]> {
    return this.http.get<SocketConnectionListDTO[]>(this.socketConnectionsUrl);
  }
}
