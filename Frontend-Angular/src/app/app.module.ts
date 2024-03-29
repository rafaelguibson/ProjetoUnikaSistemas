import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { MaterialModule} from "./material/material.module";
import { MenuBarComponent } from './component/menu-bar/menu-bar.component';
import { MonitoradorHttpClientService} from "./service/monitorador-http-client.service";
import {HttpClientModule} from "@angular/common/http";
import { TableComponent } from './component/table/table.component';
import {MAT_DATE_LOCALE} from "@angular/material/core";
import { DataViewComponent } from './component/data-view/data-view.component';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import { DeleteConfirmComponent } from './component/delete-confirm/delete-confirm.component';
import {NgxMaskDirective, NgxMaskPipe, provideNgxMask} from "ngx-mask";
import { UploadDialogComponent } from './component/upload-dialog/upload-dialog.component';
import { EditDialogComponent } from './component/edit-dialog/edit-dialog.component';
import { TableExcelInportsComponent } from './component/table-excel-inports/table-excel-inports.component';


@NgModule({
  declarations: [
    AppComponent,
    MenuBarComponent,
    MenuBarComponent,
    TableComponent,
    DataViewComponent,
    DeleteConfirmComponent,
    UploadDialogComponent,
    EditDialogComponent,
    TableExcelInportsComponent,

  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    MaterialModule,
    HttpClientModule,
    FormsModule,
    NgxMaskDirective,
    ReactiveFormsModule,
  ],
  providers: [
    {provide: MAT_DATE_LOCALE, useValue: `pt-BR`},
    provideAnimationsAsync(),
    provideNgxMask(),
    MonitoradorHttpClientService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
