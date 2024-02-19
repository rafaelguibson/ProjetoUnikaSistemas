import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {FormControl, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {MaterialModule} from "../../material/material.module";

import {MatTooltipModule} from "@angular/material/tooltip";
import {TipoPessoa} from "../../model/enum/tipo-pessoa";

@Component({
  selector: 'app-dialog',
  templateUrl: './dialog.component.html',
  styleUrl: './dialog.component.css',
  standalone: true,
  imports: [MaterialModule, ReactiveFormsModule,MatTooltipModule],
})
export class DialogComponent  implements OnInit{
  constructor(public dialogRef: MatDialogRef<DialogComponent>,
              @Inject(MAT_DIALOG_DATA) public data: { tipoPessoa: TipoPessoa }
  ) {}
  ngOnInit() {
    // Acesse o tipoPessoa aqui
    console.log('Tipo de Pessoa:', this.data.tipoPessoa);
  }

  emailFormControl = new FormControl('', [Validators.required, Validators.email]);
}
