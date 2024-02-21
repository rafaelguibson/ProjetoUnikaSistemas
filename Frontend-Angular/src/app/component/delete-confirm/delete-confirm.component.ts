import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {Monitorador} from "../../model/monitorador";
import {MonitoradorHttpClientService} from "../../service/monitorador-http-client.service";

@Component({
  selector: 'app-delete-confirm',
  templateUrl: './delete-confirm.component.html',
  styleUrl: './delete-confirm.component.css'
})
export class DeleteConfirmComponent {
  constructor(public dialogRef: MatDialogRef<DeleteConfirmComponent>,
              private httpService : MonitoradorHttpClientService,
              @Inject(MAT_DIALOG_DATA) public data: { monitorador: Monitorador },) {}


  fecharModal() {
    this.dialogRef.close();
  }

  deleteMonitorador(monitorador:Monitorador) {
    this.httpService.deleteMonitorador(this.data.monitorador.id).subscribe(
      (response) => {
        console.log('Monitorador excluído com sucesso:', response);
        // Atualize os dados após a exclusão
        this.httpService.getData().subscribe((data) => {
        });
      },
      (error) => {
        console.error('Erro ao excluir o monitorador:', error);
      }
    );
    this.dialogRef.close();
  }
}
