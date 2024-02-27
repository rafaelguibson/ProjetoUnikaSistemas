import { Injectable } from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import { Observable } from 'rxjs';
import { Monitorador } from "../model/monitorador";
import {Endereco} from "../model/endereco";

@Injectable({
  providedIn: 'root'
})
export class MonitoradorHttpClientService {
  private baseUrl = 'http://localhost:8080/api/monitoradores';

  constructor(private http: HttpClient) { }

  getData(): Observable<Monitorador[]> {
    return this.http.get<Monitorador[]>(this.baseUrl);
  }

  getAllPF(): Observable<Monitorador[]> {
    const url = `${this.baseUrl}/pf`;
    return this.http.get<Monitorador[]>(url);
  }

  getAllPJ(): Observable<Monitorador[]> {
    const url = `${this.baseUrl}/pj`;
    return this.http.get<Monitorador[]>(url);
  }

  saveMonitorador(monitorador: Monitorador): Observable<Monitorador> {
    return this.http.post<Monitorador>(this.baseUrl, monitorador);
  }



  updateMonitorador(id: number, monitorador: Monitorador): Observable<Monitorador> {
    const url = `${this.baseUrl}/${id}`;
    return this.http.put<Monitorador>(url, monitorador);
  }

  deleteMonitorador(id: number | undefined): Observable<void> {
    const url = `${this.baseUrl}/${id}`;
    return this.http.delete<void>(url);
  }

  filtrar(filtro: Monitorador): Observable<Monitorador[]> {
    const url = `${this.baseUrl}/filtrar`;
    return this.http.post<Monitorador[]>(url, filtro);
  }

  uploadFile(fileBytes: Uint8Array): Observable<Monitorador[]> {
    const url = `${this.baseUrl}/upload`;

    return this.http.post<Monitorador[]>(url, fileBytes);
  }
  buscarCep(cep: string): Observable<Endereco> {
    console.log(cep)
    const url = `https://viacep.com.br/ws/${cep}/json/`;
    return this.http.get<Endereco>(url);
  }
  exportMonitoradoresToExcel(): Observable<HttpResponse<Blob>> {
    const url = `${this.baseUrl}/export/excel`;

    // @ts-ignore
    return this.http.get(url, {
      observe: 'response',
      responseType: 'blob' as 'json',
    });
  }
}
