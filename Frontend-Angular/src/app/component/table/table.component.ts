import { Component, ViewChild } from '@angular/core';
import {MonitoradorHttpClientService} from "../../service/monitorador-http-client.service";
import { MatPaginator} from "@angular/material/paginator";
import { MatSort} from "@angular/material/sort";
import { MatTableDataSource} from "@angular/material/table";
import {Monitorador} from "../../model/monitorador";

@Component({
  selector: 'app-table',
  templateUrl: './table.component.html',
  styleUrl: './table.component.css'
})
export class TableComponent {
  displayedColumns = ['id', 'nomeRazaoSocial', 'cpfCnpj', 'telefone', 'email'];
  dataSource!:MatTableDataSource<Monitorador>
  post:any;
  constructor(private httpService : MonitoradorHttpClientService) {
    this.httpService.getData().subscribe(data => {
      console.log(data);
      this.post = data;

      this.dataSource = new MatTableDataSource(this.post);
    })
  }


}
