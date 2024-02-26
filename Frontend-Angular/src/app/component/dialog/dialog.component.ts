import {AfterViewInit, Component, Inject, OnInit, ViewChild} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {MaterialModule} from "../../material/material.module";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatTooltipModule} from "@angular/material/tooltip";
import {TipoPessoa} from "../../model/enum/tipo-pessoa";
import {JsonPipe, NgIf} from "@angular/common";
import {MonitoradorHttpClientService} from "../../service/monitorador-http-client.service";
import {Monitorador} from "../../model/monitorador";

import {Endereco} from "../../model/endereco";
import {MatTableDataSource} from "@angular/material/table";
import {MatPaginator, MatPaginatorModule} from "@angular/material/paginator";
import {MatSnackBar, MatSnackBarHorizontalPosition, MatSnackBarVerticalPosition} from "@angular/material/snack-bar";

@Component({
  selector: 'app-dialog',
  templateUrl: './dialog.component.html',
  styleUrl: './dialog.component.css',
  standalone: true,
  imports: [MaterialModule, ReactiveFormsModule, MatTooltipModule, NgIf, FormsModule, JsonPipe, MatFormFieldModule, MatPaginatorModule],
})
export class DialogComponent implements OnInit, AfterViewInit {
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  protected readonly TipoPessoa = TipoPessoa;
  monitoradorForm!: FormGroup;
  enderecoForm!: FormGroup;
  showAddressForm:boolean = false;
  enderecoList: Endereco[] = [];
  showFeedbackPanel:boolean = false;
  errorMensagem:string = '';
  dataSource = new MatTableDataSource<Endereco>();
  displayedColumns: string[] = ['cep', 'logradouro', 'complemento', 'numero', 'bairro', 'cidade', 'estado'];
  constructor(public dialogRef: MatDialogRef<DialogComponent>,
              @Inject(MAT_DIALOG_DATA) public data: { tipoPessoa: TipoPessoa },
              private formBuilder: FormBuilder,
              private monitoradorService: MonitoradorHttpClientService,
              private snackBar: MatSnackBar
  ) {
  }

  ngOnInit() {
    this.monitoradorForm = this.formBuilder.group({
      id: [''],
      tipoPessoa: [this.data.tipoPessoa == TipoPessoa.PF ? 'PF': 'PJ'],
      estadoCivil: [null],
      dataCadastro: [null],
      nomeRazaoSocial: ['', [Validators.required]],
      cpfCnpj: ['', [Validators.required]],
      rgIe: ['', [Validators.required]],
      telefone: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      dataNascimento: [null, [Validators.required]],
      status: [true],
      enderecos: [[]],
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
    this.dataSource.paginator = this.paginator;
  }

  fecharModal() {
    // Use o MatDialogRef para fechar o modal
    this.dialogRef.close();
  }

  onAddAddress() {
    // if (this.enderecoForm.valid) {
      // Create an Endereco object from the form values
      const endereco: Endereco = this.enderecoForm.value as Endereco;
      this.adicionarEndereco(endereco);
      this.limparFormularioEndereco();
      console.log('Submitted Endereco:', endereco);
    // }
  }

  buscarCep(endereco:Endereco): void {
    const cep: string = endereco.cep;
    if (!cep) {
      console.error('CEP não fornecido');
      return;
    }

    this.monitoradorService.buscarCep(cep).subscribe(
      (enderecoRetornado) => {
        console.log('Endereço encontrado:', enderecoRetornado);

        // Configurar os valores nos campos do formulário de endereço
        this.enderecoForm.patchValue({
          logradouro: enderecoRetornado.logradouro,
          complemento: enderecoRetornado.complemento,
          numero: enderecoRetornado.numero,
          bairro: enderecoRetornado.bairro,
          localidade: enderecoRetornado.localidade,
          uf: enderecoRetornado.uf,
        });

      },
      (error) => {
        console.error('Erro na busca de CEP:', error);
        // Lógica de tratamento de erro
      }
    );
  }
  ngAfterViewInit() {

  }
  adicionarEndereco(endereco: Endereco) {
    // Adiciona o endereço à lista separada
    this.enderecoList.push(endereco);
    // Atualiza a fonte de dados da tabela
    this.dataSource.data = [...this.enderecoList];
    this.dataSource.paginator = this.paginator;
  }

  adicionarEnderecosAoMonitoradorForm() {
    // Adiciona a lista de endereços ao monitoradorForm
    this.monitoradorForm.get('enderecos')?.setValue(this.enderecoList);
  }

  cadastrarMonitorador(monitorador: Monitorador) {
    this.monitoradorService.saveMonitorador(this.monitoradorForm.value).subscribe(response => {
      console.log('Monitorador salvo com sucesso:', response);
      this.fecharModal();
      this.monitoradorForm.reset();
      // this.dialogRef.close();
    }, (error) => {
      this.errorMensagem = 'Erro ao salvar o monitorador: ' + error.error;
      this.showFeedbackPanel = true;
      console.error('Erro ao salvar o monitorador:', error.error);
    });

  }


  showAddress() {

      this.showAddressForm = !this.showAddressForm;
      if(this.showAddressForm) {
        this.dialogRef.updateSize('800px', '805px')
      }
      if (!this.showAddressForm) {
        this.dialogRef.updateSize('800px', '285px')
      }
  }
  private limparFormularioEndereco() {
    // Limpa os controles do formulário de endereço
    this.enderecoForm.reset();
  }


}
