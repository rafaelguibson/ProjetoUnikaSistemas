import {Component, input, ViewChild} from '@angular/core';
import {MonitoradorHttpClientService} from "../../service/monitorador-http-client.service";
import { MatPaginator} from "@angular/material/paginator";
import { MatSort} from "@angular/material/sort";
import { MatTableDataSource} from "@angular/material/table";
import {Monitorador} from "../../model/monitorador";
import {TipoPessoa} from "../../model/enum/tipo-pessoa";
import {MatDialog} from "@angular/material/dialog";
import {DialogComponent} from "../dialog/dialog.component";
import {DataViewComponent} from "../data-view/data-view.component";
import {DeleteConfirmComponent} from "../delete-confirm/delete-confirm.component";
import {TableCommunicationServiceService} from "../../service/table-communication-service.service";

@Component({
  selector: 'app-table',
  templateUrl: './table.component.html',
  styleUrl: './table.component.css'
})
export class TableComponent {
  displayedColumns = ['id', 'tipoPessoa', 'nomeRazaoSocial', 'cpfCnpj','rgIe', 'telefone', 'email','dataNascimento', 'dataCadastro', 'menus'];
  dataSource!:MatTableDataSource<Monitorador>;
  @ViewChild(MatPaginator) paginator!:MatPaginator
  @ViewChild(MatSort) sort!:MatSort

  data:any;
  constructor(private httpService : MonitoradorHttpClientService,public dialog:MatDialog, private tableCommunicationService: TableCommunicationServiceService) {
    this.loadDataTable();

    // Assina o Observable para saber quando chamar os métodos específicos
    this.tableCommunicationService.callMethod$.subscribe(method => {
      if (method === 'loadDataTablePF') {
        this.loadDataTablePF();
      } else if (method === 'loadDataTablePJ') {
        this.loadDataTablePJ();
      } else if (method === 'loadDataTable') {
        this.loadDataTable();
      }
    });
  }

  loadDataTable() {
      this.httpService.getData().subscribe(data => {
          this.data = data;

          this.dataSource = new MatTableDataSource(this.data);
          this.dataSource.paginator = this.paginator;
          this.dataSource.sort = this.sort;
      });
  }
  openDialogDetails(monitorador: Monitorador) {
    this.dialog.open(DataViewComponent,
        {
          height: '420px',
          width: '700px',
          data: {monitorador }
        });
  }

  openDialogDelete(monitorador: Monitorador) {
      const dialogRef = this.dialog.open(DeleteConfirmComponent,
          {
              height: '200px',
              width: '400px',
              data: { monitorador }
          });

      dialogRef.afterClosed().subscribe(result => {
          this.loadDataTable();
      });
  }

  applyFilter(event : Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase()

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage()
    }
  }

  loadDataTablePF() {
    this.httpService.getAllPF().subscribe(data => {
      this.data = data;
      this.dataSource = new MatTableDataSource(this.data);
      this.dataSource.paginator = this.paginator;
      this.dataSource.sort = this.sort;
    });
  }

  loadDataTablePJ() {
    this.httpService.getAllPJ().subscribe(data => {
      this.data = data;
      this.dataSource = new MatTableDataSource(this.data);
      this.dataSource.paginator = this.paginator;
      this.dataSource.sort = this.sort;
    });
  }
}
