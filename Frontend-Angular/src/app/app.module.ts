import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { MaterialModule} from "./material/material.module";
import { DataTableComponent } from './component/data-table/data-table.component';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { MenuBarComponent } from './component/menu-bar/menu-bar.component';
import {MonitoradorHttpServiceService} from "./service/monitorador-http-service.service";
import {HttpClientModule} from "@angular/common/http";


@NgModule({
  declarations: [
    AppComponent,
    DataTableComponent,
    MenuBarComponent,
    MenuBarComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    MaterialModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    HttpClientModule
  ],
  providers: [
    provideAnimationsAsync(),
    MonitoradorHttpServiceService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
