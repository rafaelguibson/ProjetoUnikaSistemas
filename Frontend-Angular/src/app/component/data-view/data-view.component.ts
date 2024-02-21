import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {DialogComponent} from "../dialog/dialog.component";
import {Monitorador} from "../../model/monitorador";
import {TipoPessoa} from "../../model/enum/tipo-pessoa";
import {Endereco} from "../../model/endereco";
import {MatTableDataSource} from "@angular/material/table";

@Component({
  selector: 'app-data-view',
  templateUrl: './data-view.component.html',
  styleUrl: './data-view.component.css'
})
export class DataViewComponent implements OnInit{
  dataSource = new MatTableDataSource<Endereco>();
  displayedColumns: string[] = ['cep', 'logradouro', 'complemento', 'numero', 'bairro', 'cidade', 'estado'];

  constructor(public dialogRef: MatDialogRef<DialogComponent>,
              @Inject(MAT_DIALOG_DATA) public data: { monitorador: Monitorador },

  ) {}

  ngOnInit(){
    // Verifica se há endereços antes de atribuir à fonte de dados
    if (this.data.monitorador && this.data.monitorador.enderecos) {
      this.dataSource.data = this.data.monitorador.enderecos;
    }
    console.log(this.data.monitorador.enderecos)
    console.log(this.data.monitorador.tipoPessoa);
  }

  fecharModal() {
    // Use o MatDialogRef para fechar o modal
    this.dialogRef.close();
  }

  protected readonly TipoPessoa = TipoPessoa;
}
