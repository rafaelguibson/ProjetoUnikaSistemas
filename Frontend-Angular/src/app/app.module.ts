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


@NgModule({
  declarations: [
    AppComponent,
    MenuBarComponent,
    MenuBarComponent,
    TableComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    MaterialModule,
    HttpClientModule
  ],
  providers: [
    provideAnimationsAsync(),
    MonitoradorHttpClientService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
