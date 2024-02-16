import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {Monitorador} from "../model/monitorador";

@Injectable({
  providedIn: 'root'
})
export class MonitoradorHttpServiceService {

  private baseUrl = 'http://localhost:8080/api/monitoradores';

  constructor(private http: HttpClient) {}

  getAllMonitoradores(): Observable<Monitorador[]> {
    return this.http.get<Monitorador[]>(this.baseUrl);
  }

  getMonitoradorById(id: number): Observable<Monitorador> {
    return this.http.get<Monitorador>(`${this.baseUrl}/${id}`);
  }

  createMonitorador(monitorador: Monitorador): Observable<Monitorador> {
    return this.http.post<Monitorador>(this.baseUrl, monitorador);
  }

  updateMonitorador(id: number, monitorador: Monitorador): Observable<Monitorador> {
    return this.http.patch<Monitorador>(`${this.baseUrl}/${id}`, monitorador);
  }

  deleteMonitorador(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

}
