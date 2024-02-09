import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { MainComponent } from './components/main/main.component';
import { HeaderComponent } from './components/header/header.component';
import { SidenavComponent } from './components/sidenav/sidenav.component';
import { DatatableComponent } from './components/datatable/datatable.component';
import { FiltersComponent } from './components/filters/filters.component';
import { FormMonitoradorComponent } from './components/form-monitorador/form-monitorador.component';
import { DialogDeleteComponent } from './components/dialog-delete/dialog-delete.component';


@NgModule({
  declarations: [
    AppComponent,
    MainComponent,
    HeaderComponent,
    SidenavComponent,
    DatatableComponent,
    FiltersComponent,
    FormMonitoradorComponent,
    DialogDeleteComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
