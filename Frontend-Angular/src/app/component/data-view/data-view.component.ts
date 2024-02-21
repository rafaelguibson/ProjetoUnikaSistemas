import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {DialogComponent} from "../dialog/dialog.component";
import {Monitorador} from "../../model/monitorador";
import {TipoPessoa} from "../../model/enum/tipo-pessoa";

@Component({
  selector: 'app-data-view',
  templateUrl: './data-view.component.html',
  styleUrl: './data-view.component.css'
})
export class DataViewComponent implements OnInit{
  constructor(public dialogRef: MatDialogRef<DialogComponent>,
              @Inject(MAT_DIALOG_DATA) public data: { monitorador: Monitorador },

  ) {}

  ngOnInit(){
    console.log(TipoPessoa.PF)
    console.log(this.data.monitorador.tipoPessoa);
  }

  fecharModal() {
    // Use o MatDialogRef para fechar o modal
    this.dialogRef.close();
  }

  protected readonly TipoPessoa = TipoPessoa;
}
