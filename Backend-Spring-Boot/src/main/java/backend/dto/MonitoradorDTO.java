package backend.dto;

import backend.enums.Status;
import backend.enums.TipoPessoa;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonitoradorDTO {

    private Long id;
    private TipoPessoa tipoPessoa;
    private String cpf;
    private String cnpj;
    private String nome;
    private String razaoSocial;
    private String telefone;
    private String email;
    private String rg;
    private String inscricaoEstadual;
    private Date dataNascimento;
    private String estadoCivil;
    private Status status;
    private Date dataCadastro;
    private List<EnderecoDTO> enderecos; // Supondo que você tenha um EnderecoDTO
    private boolean selected;
}

