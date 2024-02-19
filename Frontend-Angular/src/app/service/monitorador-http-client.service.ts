import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {Monitorador} from "../model/monitorador";

@Injectable({
  providedIn: 'root'
})
export class MonitoradorHttpClientService {
  private baseUrl = 'http://localhost:8080/api/monitoradores';
  constructor(private http: HttpClient) { }

  getData() {
    return this.http.get(this.baseUrl);
  }

  getAllPF(): Observable<Monitorador[]> {
    const url = `${this.baseUrl}/PF`;
    return this.http.get<Monitorador[]>(url);
  }

  getAllPJ(): Observable<Monitorador[]> {
    const url = `${this.baseUrl}/PJ`;
    return this.http.get<Monitorador[]>(url);
  }
}
