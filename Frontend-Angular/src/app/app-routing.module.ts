import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {DataTableComponent} from "./component/data-table/data-table.component";


const routes: Routes = [
  { path: '', component: DataTableComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
