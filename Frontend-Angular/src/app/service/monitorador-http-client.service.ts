import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
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
    const url = `${this.baseUrl}/PF`;
    return this.http.get<Monitorador[]>(url);
  }

  getAllPJ(): Observable<Monitorador[]> {
    const url = `${this.baseUrl}/PJ`;
    return this.http.get<Monitorador[]>(url);
  }

  saveMonitorador(monitorador: Monitorador): Observable<Monitorador> {
    return this.http.post<Monitorador>(this.baseUrl, monitorador);
  }

  saveAllMonitorador(monitoradores: Monitorador[]): Observable<void> {
    const url = `${this.baseUrl}/saveAll`;
    return this.http.post<void>(url, monitoradores);
  }

  getAllMonitoradores(): Observable<Monitorador[]> {
    return this.http.get<Monitorador[]>(this.baseUrl);
  }

  getMonitoradorById(id: number): Observable<Monitorador> {
    const url = `${this.baseUrl}/${id}`;
    return this.http.get<Monitorador>(url);
  }

  buscarPF(): Observable<Monitorador[]> {
    const url = `${this.baseUrl}/pf`;
    return this.http.get<Monitorador[]>(url);
  }

  buscarPJ(): Observable<Monitorador[]> {
    const url = `${this.baseUrl}/pj`;
    return this.http.get<Monitorador[]>(url);
  }

  updateMonitorador(id: number, monitorador: Monitorador): Observable<Monitorador> {
    const url = `${this.baseUrl}/${id}`;
    return this.http.patch<Monitorador>(url, monitorador);
  }

  deleteMonitorador(id: number | undefined): Observable<void> {
    const url = `${this.baseUrl}/${id}`;
    return this.http.delete<void>(url);
  }

  deleteAllMonitoradores(monitoradores: Monitorador[]): Observable<void> {
    const url = `${this.baseUrl}/deleteAll`;
    return this.http.post<void>(url, monitoradores);
  }

  filtrar(filtro: Monitorador): Observable<Monitorador[]> {
    const url = `${this.baseUrl}/filtrar`;
    return this.http.post<Monitorador[]>(url, filtro);
  }

  buscarCep(cep: string): Observable<Endereco> {
    console.log(cep)
    const url = `https://viacep.com.br/ws/${cep}/json/`;
    return this.http.get<Endereco>(url);
  }
}
