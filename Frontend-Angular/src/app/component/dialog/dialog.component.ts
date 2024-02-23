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
import {Endereco} from "../../model/endereco";
import {MatTableDataSource} from "@angular/material/table";

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
  enderecoForm!: FormGroup;
  showAddressForm:boolean = false;
  dataSource = new MatTableDataSource<Endereco>();
  displayedColumns: string[] = ['cep', 'logradouro', 'complemento', 'numero', 'bairro', 'cidade', 'estado'];
  constructor(public dialogRef: MatDialogRef<DialogComponent>,
              @Inject(MAT_DIALOG_DATA) public data: { tipoPessoa: TipoPessoa },
              private formBuilder: FormBuilder,
              private monitoradorService: MonitoradorHttpClientService
  ) {
  }

  ngOnInit() {
    // if(this.data.tipoPessoa == TipoPessoa.PF) {
    //   this.dialogRef.updateSize('530px', '480px')
    // }
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
    this.enderecoForm = this.formBuilder.group({
      monitorador: ['', Validators.required],
      logradouro: ['', Validators.required],
      complemento: [''],
      numero: [''],
      cep: ['', [Validators.required, Validators.pattern(/^\d{5}-\d{3}$/)]],
      bairro: [''],
      localidade: ['', Validators.required],
      uf: ['', Validators.required],
      desabilitarNumero: [false]
    });
  }

  fecharModal() {
    // Use o MatDialogRef para fechar o modal
    this.dialogRef.close();
  }

  onSubmit() {
    if (this.enderecoForm.valid) {
      // Create an Endereco object from the form values
      const endereco: Endereco = this.enderecoForm.value as Endereco;

      // Perform any additional logic, such as submitting the form data
      console.log('Submitted Endereco:', endereco);
    }
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

  showAddress() {

      this.showAddressForm = !this.showAddressForm;
      if(this.showAddressForm) {
        this.dialogRef.updateSize('800px', '700px')
      }
      if (!this.showAddressForm) {
        this.dialogRef.updateSize('800px', '230px')
      }
  }
}
