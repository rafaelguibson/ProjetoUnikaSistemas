package backend.dto;
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
    private String tipoPessoa;
    private String cpf;
    private String cnpj;
    private String nome;
    private String razaoSocial;
    private String telefone;
    private String email;
    private String rg;
    private String inscricaoEstadual;
    private Date dataNascimento;
    private Boolean ativo;
    private List<EnderecoDTO> enderecos;

}
