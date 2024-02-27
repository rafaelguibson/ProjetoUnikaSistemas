import {Component} from '@angular/core';
import {MatDialog} from "@angular/material/dialog";
import {DialogComponent} from "../dialog/dialog.component";
import {TipoPessoa} from "../../model/enum/tipo-pessoa";
import {MonitoradorHttpClientService} from "../../service/monitorador-http-client.service";
import {TableCommunicationServiceService} from "../../service/table-communication-service.service";
import {UploadDialogComponent} from "../upload-dialog/upload-dialog.component";

@Component({
  selector: 'app-menu-bar',
  templateUrl: './menu-bar.component.html',
  styleUrl: './menu-bar.component.css'
})
export class MenuBarComponent {

  //Variavel de controle dos toggle menu PF e PJ
  toggle_pf_menu: boolean = true;
  toggle_pj_menu: boolean = true;
  toggle_report_menu: boolean = true;

  //Variavel de controle dos toggle menu PF
  toggPF() {
    return this.toggle_pf_menu = !this.toggle_pf_menu;
  }
  //Variavel de controle dos toggle menu PJ
  toggPJ() {
    return this.toggle_pj_menu = !this.toggle_pj_menu;
  }

  constructor(public dialog:MatDialog,
              private monitoradorService: MonitoradorHttpClientService,
              private tableCommunicationService: TableCommunicationServiceService) { }

  openDialog(tipoPessoa: TipoPessoa) {
  if(tipoPessoa === TipoPessoa.PF)  this.toggle_pf_menu = !this.toggle_pf_menu;
  if(tipoPessoa === TipoPessoa.PJ)  this.toggle_pj_menu = !this.toggle_pj_menu;
    this.dialog.open(DialogComponent,
      {
        height: '295px',
        width: '800px',
        data: {tipoPessoa }
      });
  }
  protected readonly TipoPessoa = TipoPessoa;


  toggReport() {
    this.toggle_report_menu = !this.toggle_report_menu;
  }

  exportToExcel(): void {
    this.monitoradorService.exportMonitoradoresToExcel().subscribe((response) => {
      const blob = new Blob([response.body as BlobPart], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });

      const downloadLink = document.createElement('a');
      const url = window.URL.createObjectURL(blob);
      downloadLink.href = url;
      downloadLink.download = 'monitoradores.xlsx';

      document.body.appendChild(downloadLink);
      downloadLink.click();
      document.body.removeChild(downloadLink);
    });
  }
  importFromExcel() {
    this.dialog.open(UploadDialogComponent,
      {
        height: '295px',
        width: '800px',
      });
  }
  visualizarPF() {
    console.log('clicado');
    this.tableCommunicationService.callMethod('loadDataTablePF');
  }

  visualizarPJ() {
    this.tableCommunicationService.callMethod('loadDataTablePJ');
  }

  vizualizarTodos() {
    this.tableCommunicationService.callMethod('loadDataTable');
  }
  filtrarPF() {
    this.tableCommunicationService.callMethod('filterPF');
  }
  filtrarPJ() {
    this.tableCommunicationService.callMethod('filterPJ');
  }

}
