import {Component, Inject, OnInit, ViewChild} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {MonitoradorHttpClientService} from "../../service/monitorador-http-client.service";
import {DialogComponent} from "../dialog/dialog.component";
import {MatTableDataSource} from "@angular/material/table";
import {Monitorador} from "../../model/monitorador";
import {MatPaginator} from "@angular/material/paginator";
import {MatSort} from "@angular/material/sort";

@Component({
  selector: 'app-table-excel-inports',
  templateUrl: './table-excel-inports.component.html',
  styleUrl: './table-excel-inports.component.css'
})
export class TableExcelInportsComponent implements OnInit{
  displayedColumns = [ 'tipoPessoa', 'nomeRazaoSocial', 'cpfCnpj','rgIe', 'telefone', 'email','dataNascimento', 'menus'];
  dataSource!:MatTableDataSource<Monitorador>;
  @ViewChild(MatPaginator) paginator!:MatPaginator
  @ViewChild(MatSort) sort!:MatSort
  constructor(@Inject(MAT_DIALOG_DATA) public data: any,
    public dialogRef: MatDialogRef<TableExcelInportsComponent>,
    private httpService: MonitoradorHttpClientService,
  ) {this.loadDataTable();}

  ngOnInit() {

  }

  fecharModal() {
    this.dialogRef.close();
  }
  loadDataTable() {
      this.dataSource = new MatTableDataSource(this.data.monitoradores);
      this.dataSource.paginator = this.paginator;
      this.dataSource.sort = this.sort;
  }

  openDialogDelete(monitorador: Monitorador) {

  }
}
