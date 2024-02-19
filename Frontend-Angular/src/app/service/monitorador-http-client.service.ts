import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class MonitoradorHttpClientService {
  private baseUrl = 'http://localhost:8080/api/monitoradores';
  constructor(private http: HttpClient) { }

  getData() {
    return this.http.get(this.baseUrl);
  }
}
