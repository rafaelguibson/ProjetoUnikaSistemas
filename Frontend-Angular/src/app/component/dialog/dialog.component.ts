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
import {NgxMaskDirective, NgxMaskPipe, provideNgxMask} from "ngx-mask";
import {EstadoCivil} from "../../model/enum/estado-civil";
import {error} from "@angular/compiler-cli/src/transformers/util";

@Component({
  selector: 'app-dialog',
  templateUrl: './dialog.component.html',
  styleUrl: './dialog.component.css',
  standalone: true,
  imports: [MaterialModule, ReactiveFormsModule, MatTooltipModule, NgIf, FormsModule, JsonPipe, MatFormFieldModule, MatPaginatorModule, NgxMaskDirective, NgxMaskPipe],
  providers: [provideNgxMask()]
})
export class DialogComponent implements OnInit, AfterViewInit {
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  protected readonly TipoPessoa = TipoPessoa;
  monitoradorForm!: FormGroup;
  enderecoForm!: FormGroup;
  showAddressForm: boolean = false;
  enderecoList: Endereco[] = [];
  showFeedbackPanel: boolean = false;
  errorMensagem: string = '';
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
      tipoPessoa: [this.data.tipoPessoa],
      dataCadastro: [null],
      nomeRazaoSocial: ['', [Validators.required]],
      cpfCnpj: ['', [Validators.required]],
      rgIe: ['', [Validators.required]],
      telefone: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      dataNascimento: [null, this.data.tipoPessoa === TipoPessoa.PF ? [Validators.required] : []],
      status: [true],
      enderecos: [this.enderecoList],
    });
    this.enderecoForm = this.formBuilder.group({
      monitorador: [null],
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

    // }
  }

  buscarCep(endereco: Endereco): void {
    const cep: string = endereco.cep;
    if (!cep) {
      this.showErrorMensage('É necessário preencher o CEP!');
      return;
    }

    this.monitoradorService.buscarCep(cep).subscribe(
      (enderecoRetornado) => {
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
          this.showErrorMensage(error);
      }
    );
  }

  ngAfterViewInit() {

  }

  adicionarEndereco(endereco: Endereco) {
    this.enderecoList.push(endereco);
    // Atualiza a fonte de dados da tabela
    this.dataSource.data = [...this.enderecoList];
    this.dataSource.paginator = this.paginator;
  }

  adicionarEnderecosAoMonitoradorForm() {
    this.monitoradorForm.get('enderecos')?.setValue(this.enderecoList);
    console.log('Endereços adicionados: ', this.monitoradorForm.value );
  }

  cadastrarMonitorador() {
    this.adicionarEnderecosAoMonitoradorForm();
    const monitorador: Monitorador = this.monitoradorForm.value as Monitorador;
    console.log(monitorador);
    this.monitoradorService.saveMonitorador(monitorador).subscribe(response => {
      console.log('Monitorador salvo com sucesso:', response);
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

  showAddress() {

    if (this.monitoradorForm.valid) {
      this.showFeedbackPanel = false!
      this.showAddressForm = !this.showAddressForm;
      if (this.showAddressForm) {
        this.dialogRef.updateSize('800px', '805px')
      }
      if (!this.showAddressForm) {
        this.dialogRef.updateSize('800px', '285px')
      }
    } else {
      this.errorMensagem = 'Preencha todos os campos... ';
      this.showFeedbackPanel = true;
    }
  }

  private limparFormularioEndereco() {
    // Limpa os controles do formulário de endereço
    this.enderecoForm.reset();
  }


}
