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
  showFeedbackPanel: boolean = false;
  errorMensagem: string = '';
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

  deleteMonitorador(monitorador: Monitorador) {
    const index = this.dataSource.data.indexOf(monitorador);
    if (index !== -1) {
      this.dataSource.data.splice(index, 1);
      this.dataSource._updateChangeSubscription(); // Notificar a tabela sobre a alteração
    }
    console.log('Removido com sucesso')
  }
  showErrorMensage(msg: string ) {
    this.dialogRef.updateSize('1600px', '950px')
    this.errorMensagem = msg ;
    this.showFeedbackPanel = true;
    this.scheduleMessageClear();
  }
  private scheduleMessageClear() {
    setTimeout(() => {
      this.errorMensagem = ''; // Limpar a mensagem após 5 segundos
      this.showFeedbackPanel = false;
      this.dialogRef.updateSize('1600px', '900px')
    }, 5000); // 5000 milissegundos = 5 segundos
  }
  openDialogDelete(monitorador: Monitorador) {
    // Aqui você pode abrir um diálogo de confirmação se necessário

    this.deleteMonitorador(monitorador);
  }

  cadastrarMonitorador() {
    this.httpService.saveAllMonitoradores(this.data.monitoradores).subscribe(response => {
      this.showErrorMensage(response);
      console.log('Monitorador salvo com sucesso:', response);
      // this.dialogRef.close();
    }, (error) => {
      this.showErrorMensage(error.error);
      console.log('Erro Ao Salvar:', error.error);
    });
  }
}
