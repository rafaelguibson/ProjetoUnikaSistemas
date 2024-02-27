import { Component } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { MonitoradorHttpClientService } from '../../service/monitorador-http-client.service';
import { Monitorador } from '../../model/monitorador';
import { finalize, catchError } from 'rxjs/operators';
import { HttpEventType } from '@angular/common/http';
import { throwError } from 'rxjs';

@Component({
  selector: 'app-upload-dialog',
  templateUrl: './upload-dialog.component.html',
  styleUrls: ['./upload-dialog.component.css'],
})
export class UploadDialogComponent {
  status: 'initial' | 'uploading' | 'success' | 'fail' = 'initial';
  file: File | null = null;

  constructor(
    public dialogRef: MatDialogRef<UploadDialogComponent>,
    private httpService: MonitoradorHttpClientService
  ) {}

  onChange(event) {
    const file: File = event.target.files[0];

    if (file) {
      this.status = 'initial';
      this.file = file;
    }
  }

  onUpload() {
    if (this.file) {
      const reader = new FileReader();

      reader.onload = (e) => {
        const fileBytes = new Uint8Array(e.target?.result as ArrayBuffer);

        this.status = 'uploading';

        this.httpService
          .uploadFile(fileBytes)
          .pipe(
            finalize(() => {
              // Finaliza a operação, independentemente de sucesso ou falha
            }),
            catchError((error) => {
              this.status = 'fail';
              return throwError(() => error);
            })
          )
          .subscribe((event) => {
            if (event.type === HttpEventType.Response) {
              this.status = 'success';
            }
          });
      };

      reader.readAsArrayBuffer(this.file);
    }
  }

  fecharModal() {
    this.dialogRef.close();
  }
}
