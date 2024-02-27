import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {MonitoradorHttpClientService} from "../../service/monitorador-http-client.service";
import {MatSnackBar} from "@angular/material/snack-bar";
import {DialogComponent} from "../dialog/dialog.component";
import {TipoPessoa} from "../../model/enum/tipo-pessoa";
import {Monitorador} from "../../model/monitorador";
import {TableCommunicationServiceService} from "../../service/table-communication-service.service";

@Component({
  selector: 'app-edit-dialog',
  templateUrl: './edit-dialog.component.html',
  styleUrl: './edit-dialog.component.css'
})
export class EditDialogComponent implements OnInit{
  monitoradorForm!: FormGroup;
  errorMensagem: string = '';
  showFeedbackPanel: boolean = false;
  constructor(public dialogRef: MatDialogRef<DialogComponent>,
              @Inject(MAT_DIALOG_DATA) public data: { tipoPessoa: TipoPessoa, monitorador: Monitorador },
              private formBuilder: FormBuilder,
              private monitoradorService: MonitoradorHttpClientService,
              private tableCommunicationService: TableCommunicationServiceService
  ) {
  }
  ngOnInit() {
    this.monitoradorForm = this.formBuilder.group({
      id: [''],
      tipoPessoa: [this.data.tipoPessoa],
      dataCadastro: [null],
      nomeRazaoSocial: ['', [Validators.required]],
      cpfCnpj: ['', [Validators.required]],
      rgIe: ['', [Validators.required]],
      telefone: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      dataNascimento: [null, this.data.tipoPessoa === TipoPessoa.PF ? [Validators.required] : []],
      status: [true],
    });
    this.monitoradorForm.patchValue({
      id: this.data.monitorador.id,
      nomeRazaoSocial: this.data.monitorador.nomeRazaoSocial,
      cpfCnpj: this.data.monitorador.cpfCnpj,
      rgIe:this.data.monitorador.rgIe,
      telefone:this.data.monitorador.telefone,
      email: this.data.monitorador.email,
      dataNascimento:this.data.monitorador.dataNascimento,
      status: this.data.monitorador.status
    });
  }
  fecharModal() {
    // Use o MatDialogRef para fechar o modal
    this.dialogRef.close();

  }
  atualizarMonitorador() {
    const monitorador: Monitorador = this.monitoradorForm.value as Monitorador;
    const id: number = monitorador.id!;
    this.monitoradorService.updateMonitorador(id,monitorador).subscribe(response => {
      console.log('Monitorador atualizado com sucesso:', response);
      this.atualizarDataTable();
      this.fecharModal();
      this.monitoradorForm.reset();
      // this.dialogRef.close();
    }, (error) => {
      this.showErrorMensage(error.error);
    });
  }

  showErrorMensage(msg: string ) {
    this.errorMensagem = msg ;
    this.showFeedbackPanel = true;
    this.scheduleMessageClear();
  }
  // Função para agendar a limpeza da mensagem após 10 segundos
  private scheduleMessageClear() {
    setTimeout(() => {
      this.errorMensagem = ''; // Limpar a mensagem após 5 segundos
      this.showFeedbackPanel = false;
    }, 5000); // 5000 milissegundos = 5 segundos
  }

  atualizarDataTable() {
    this.tableCommunicationService.callMethod('loadDataTable');
  }
  protected readonly TipoPessoa = TipoPessoa;
}
