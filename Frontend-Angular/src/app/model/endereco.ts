import {Monitorador} from "./monitorador";

export interface Endereco {
  id: number;
  monitorador: Monitorador;
  logradouro: string;
  complemento: string;
  numero: string;
  cep: string;
  bairro: string;
  cidade: string;
  estado: string;
}
