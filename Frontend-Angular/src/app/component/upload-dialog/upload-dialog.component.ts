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
console.log(this.file)
    if (this.file) {
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
  fecharModal() {
    this.dialogRef.close();
  }
}
