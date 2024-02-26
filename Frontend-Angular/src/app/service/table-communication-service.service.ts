import { Injectable } from '@angular/core';
import {Subject} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class TableCommunicationServiceService {
  constructor() { }
  private callMethodSubject = new Subject<string>();

  callMethod$ = this.callMethodSubject.asObservable();

  callMethod(method: string) {
    this.callMethodSubject.next(method);
  }
}
