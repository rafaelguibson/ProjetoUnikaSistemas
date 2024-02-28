import {Component, Inject, Input} from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MonitoradorHttpClientService } from '../../service/monitorador-http-client.service';
import { Monitorador } from '../../model/monitorador';
import {finalize, Subscription, throwError} from "rxjs";
import {HttpEventType} from "@angular/common/http";

@Component({
  selector: 'app-upload-dialog',
  templateUrl: './upload-dialog.component.html',
  styleUrls: ['./upload-dialog.component.css'], // Corrigido para styleUrls
})
export class UploadDialogComponent {
  status: "initial" | "uploading" | "success" | "fail" = "initial"; // Variable to store file status
  file: File | null = null; // Variable to store file
  monitoradores: Monitorador[] = [];
  fileName = '';
  showFeedBackPanel:boolean = false;
  errorMensagem!: string;
  
  constructor(
    public dialogRef: MatDialogRef<UploadDialogComponent>,
    private httpService: MonitoradorHttpClientService,
  ) {}
  // On file Select
  onChange(event: any) {
    this.file = event?.target?.files[0];

    if (this.file) {
      this.status = "initial";
    }
    this.dialogRef.updateSize('300px','350px')
  }

  onUpload() {
    if (this.file) {
      const allowedExtensions = ['xls', 'xlsx'];
      const fileExtension = this.getFileExtension(this.file.name);
      if (this.file.size > 20971520) {
        this.showFeedbackMessage('Tamanho máximo de arquivo 20MB.');

        return;
      }
      if (!allowedExtensions.includes(fileExtension)) {
        this.showFeedbackMessage('Somente arquivos de Excel suportados. (.xls ou .xlsx)');
        return;
      }
      this.httpService.uploadFile(this.file).subscribe(
        (response) => {
          // Handle successful response
          console.log(response);
          this.status = 'success';
        },
        (error) => {
          // Handle error
          console.error(error);
          this.status = 'fail';
        }
      );
    }
  }
  // Método para converter o tamanho do arquivo de bytes para megabytes

  getFileSizeInMB(sizeInBytes: number): string {
    const sizeInMB = sizeInBytes / (1024 * 1024);
    return sizeInMB.toFixed(5) ;
  }
  getFileNameWithoutExtension(fileName: string): string {
    const lastIndex = fileName.lastIndexOf('.');
    return lastIndex !== -1 ? fileName.substring(0, lastIndex) : fileName;
  }
  getFileExtension(fileName: string): string {
    const parts = fileName.split('.');
    if (parts.length > 1) {
      return parts[parts.length - 1];
    }
    return ''; // Sem extensão
  }
  showFeedbackMessage(msg:string){
    this.errorMensagem = msg;
    this.showFeedBackPanel = true;
    this.dialogRef.updateSize('300px','420px')
  }
  fecharModal() {
    this.dialogRef.close();
  }

  closeFeedbackPanel() {
    this.dialogRef.updateSize('300px','350px')
    this.showFeedBackPanel = false;
  }
}
