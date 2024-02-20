import {Endereco} from "./endereco";
import {TipoPessoa} from "./enum/tipo-pessoa";
import {EstadoCivil} from "./enum/estado-civil";

export interface Monitorador {
  id?: number;
  tipoPessoa: TipoPessoa;
  estadoCivil?: EstadoCivil;
  nomeRazaoSocial: string;
  cpfCnpj: string;
  rgIe: string;
  telefone: string;
  email: string;
  dataNascimento: Date;
  status: boolean;
  dataCadastro?: Date;
  dataInicial?: Date;
  dataFinal?: Date;
  selected?: boolean;
  enderecos?: Endereco[];
}
