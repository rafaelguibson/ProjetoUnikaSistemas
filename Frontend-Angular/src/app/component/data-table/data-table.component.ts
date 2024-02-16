import { AfterViewInit, Component, ViewChild } from '@angular/core';
import { MatTable } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { DataTableDataSource } from './data-table-datasource';
import {Monitorador} from "../../model/monitorador";
import {MonitoradorHttpServiceService} from "../../service/monitorador-http-service.service";

@Component({
  selector: 'app-data-table',
  templateUrl: './data-table.component.html',
  styleUrl: './data-table.component.css'
})
export class DataTableComponent implements AfterViewInit {
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  @ViewChild(MatTable) table!: MatTable<Monitorador>;
  dataSource = new DataTableDataSource();
  monitoradorList!:Monitorador[];
  constructor(private monitoradorHtttpService: MonitoradorHttpServiceService) {
  }

  /** Columns displayed in the table. Columns IDs can be added, removed, or reordered. */
  displayedColumns = ['id', 'nomeRazaoSocial', 'cpfCnpj', 'telefone', 'email'];


  ngAfterViewInit(): void {
    this.dataSource.sort = this.sort;
    this.dataSource.paginator = this.paginator;
    this.table.dataSource = this.dataSource;


  }

  getAllMonitorador() {
    this.monitoradorHtttpService.getAllMonitoradores().subscribe(data=> {
      this.monitoradorList = data;
      console.log('Lista de monitoradores/n ', data);
    });
  }
}
