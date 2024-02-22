import {Component, Inject, OnInit, ViewChild} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {MaterialModule} from "../../material/material.module";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatTooltipModule} from "@angular/material/tooltip";
import {TipoPessoa} from "../../model/enum/tipo-pessoa";
import {JsonPipe, NgIf} from "@angular/common";
import {MonitoradorHttpClientService} from "../../service/monitorador-http-client.service";
import {Monitorador} from "../../model/monitorador";
import {MatTab, MatTabGroup} from "@angular/material/tabs";

@Component({
  selector: 'app-dialog',
  templateUrl: './dialog.component.html',
  styleUrl: './dialog.component.css',
  standalone: true,
  imports: [MaterialModule, ReactiveFormsModule, MatTooltipModule, NgIf, FormsModule, JsonPipe, MatFormFieldModule, MatTabGroup, MatTab],
})
export class DialogComponent implements OnInit {
  @ViewChild('tabGroup') tabGroup!: MatTabGroup;
  protected readonly TipoPessoa = TipoPessoa;
  monitoradorForm!: FormGroup;
  enderecoTab:boolean = true;
  monitoradorTab:boolean = false;

  constructor(public dialogRef: MatDialogRef<DialogComponent>,
              @Inject(MAT_DIALOG_DATA) public data: { tipoPessoa: TipoPessoa },
              private formBuilder: FormBuilder,
              private monitoradorService: MonitoradorHttpClientService
  ) {
  }

  ngOnInit() {
    if(this.data.tipoPessoa == TipoPessoa.PF) {
      this.dialogRef.updateSize('530px', '480px')
    }
    this.monitoradorForm = this.formBuilder.group({
      id: ['', [Validators.required]],
      tipoPessoa: [this.data.tipoPessoa == TipoPessoa.PF ? 'PF': 'PJ', [Validators.required]],
      estadoCivil: [null, [Validators.required]],
      dataCadastro: [null, [Validators.required]],
      nomeRazaoSocial: ['', [Validators.required]],
      cpfCnpj: ['', [Validators.required]],
      rgIe: ['', [Validators.required]],
      telefone: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      dataNascimento: [null, [Validators.required]],
      status: [true],
    });
    // Acesse o tipoPessoa aqui
    console.log('Tipo de Pessoa:', this.data.tipoPessoa);
  }

  fecharModal() {
    // Use o MatDialogRef para fechar o modal
    this.dialogRef.close();
  }


  CadastrarMonitorador(monitorador: Monitorador) {
    this.monitoradorService.saveMonitorador(this.monitoradorForm.value).subscribe(response => {
      console.log('Monitorador salvo com sucesso:', response);
      this.dialogRef.close();
    }, (error) => {
      console.error('Erro ao salvar o monitorador:', error);
    });
    this.fecharModal();
    this.monitoradorForm.reset();
  }

  avancar() {
    // if (this.monitoradorForm.valid) {
      // Assuming you want to switch to the second tab
      this.tabGroup.selectedIndex = 1;
      this.enderecoTab = !this.enderecoTab;
    this.monitoradorTab = !this.monitoradorTab;
    // }

  }
  retornar() {
    // if (this.monitoradorForm.valid) {
    // Assuming you want to switch to the second tab
    this.tabGroup.selectedIndex = 0;
    this.monitoradorTab = !this.monitoradorTab;
    this.enderecoTab = !this.enderecoTab;
    // }

  }
}
