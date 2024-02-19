import {Component} from '@angular/core';
import {MatDialog} from "@angular/material/dialog";
import {DialogComponent} from "../dialog/dialog.component";
import {TipoPessoa} from "../../model/enum/tipo-pessoa";

@Component({
  selector: 'app-menu-bar',
  templateUrl: './menu-bar.component.html',
  styleUrl: './menu-bar.component.css'
})
export class MenuBarComponent {

  //Variavel de controle dos toggle menu PF e PJ
  toggle_pf_menu: boolean = true;
  toggle_pj_menu: boolean = true;

  //Variavel de controle dos toggle menu PF
  toggPF() {
    return this.toggle_pf_menu = !this.toggle_pf_menu;
  }
  //Variavel de controle dos toggle menu PJ
  toggPJ() {
    return this.toggle_pj_menu = !this.toggle_pj_menu;
  }

  constructor(public dialog:MatDialog) {
  }

  openDialog(tipoPessoa: TipoPessoa) {
  if(tipoPessoa === TipoPessoa.PF)  this.toggle_pf_menu = !this.toggle_pf_menu;
  if(tipoPessoa === TipoPessoa.PJ)  this.toggle_pj_menu = !this.toggle_pj_menu;
    this.dialog.open(DialogComponent,
      {
        height: '600px',
        width: '800px',
        data: {tipoPessoa }
      });
  }

  protected readonly TipoPessoa = TipoPessoa;
}
