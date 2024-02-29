import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders, HttpResponse} from '@angular/common/http';
import {catchError, Observable, throwError} from 'rxjs';
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
  saveAllMonitoradores(monitoradores: Monitorador[]): Observable<any> {
    const url = `${this.baseUrl}/saveAll`;

    return this.http.post(url, monitoradores, {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })
    });
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

  uploadFile(file: File): Observable<any> {
    const formData: FormData = new FormData();
    formData.append('file', file);

    const headers = new HttpHeaders();
    headers.append('Content-Type', 'multipart/form-data');

    return this.http.post<any>(`${this.baseUrl}/upload`, formData, {
      headers,
      reportProgress: true,
      responseType: 'json',
      observe: 'events',});
  }
  getMonitoradorPDF(): Observable<Blob> {
    return this.http.get(`${this.baseUrl}/export/report`, { responseType: 'blob' })
      .pipe(
        catchError((error) => {
          console.error('Erro na solicitação getMonitorador:', error);
          return throwError(error);
        })
      );
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
