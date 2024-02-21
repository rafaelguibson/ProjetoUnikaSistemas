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
  constructor(private httpService : MonitoradorHttpClientService,public dialog:MatDialog) {
    this.httpService.getData().subscribe(data => {
      console.log(data);
      this.data = data;

      this.dataSource = new MatTableDataSource(this.data);
      this.dataSource.paginator = this.paginator;
      this.dataSource.sort = this.sort;
    });
  }

  openDialog(monitorador: Monitorador) {
    this.dialog.open(DataViewComponent,
        {
          height: '300px',
          width: '700px',
          data: {monitorador }
        });
  }

  applyFilter(event : Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase()

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage()
    }
  }

  deleteMonitorador(id: number) {
    this.httpService.deleteMonitorador(id).subscribe(
        (response) => {
          console.log('Monitorador excluído com sucesso:', response);
          // Atualize os dados após a exclusão
          this.httpService.getData().subscribe((data) => {
            this.data = data;
            this.dataSource.data = this.data;
          });
        },
        (error) => {
          console.error('Erro ao excluir o monitorador:', error);
        }
    );
  }

  protected readonly input = input;


    protected readonly TipoPessoa = TipoPessoa;
}
