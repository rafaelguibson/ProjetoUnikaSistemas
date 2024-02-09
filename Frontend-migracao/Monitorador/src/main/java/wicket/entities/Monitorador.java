package wicket.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import wicket.enums.EstadoCivil;
import wicket.enums.Status;
import wicket.enums.TipoPessoa;

import java.beans.Transient;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Monitorador implements Serializable {
    private Long id;
    private TipoPessoa tipoPessoa; // Este campo deve ser alterado para usar o enum TipoPessoa (PF ou PJ)
    private String cpf;
    private String cnpj;
    private String nome;
    private String razaoSocial;
    private String telefone;
    private String email;
    private String rg;
    private String inscricaoEstadual;
    private Date dataNascimento;
    private EstadoCivil estadoCivil; // Adicionado o campo estadoCivil
    private Status status; // Substituído 'ativo' por 'status' que pode ser 'ATIVO' ou 'INATIVO'
    private Date dataCadastro; // Adicionado o campo dataCadastro
    private List<Endereco> enderecos;
    private boolean selected;
    private Date dataInicial;
    private Date dataFinal;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Monitorador that = (Monitorador) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Monitorador{" +
                "id=" + id +
                ", tipoPessoa='" + tipoPessoa + '\'' +
                ", cpf='" + cpf + '\'' +
                ", cnpj='" + cnpj + '\'' +
                ", nome='" + nome + '\'' +
                ", razaoSocial='" + razaoSocial + '\'' +
                ", telefone='" + telefone + '\'' +
                ", email='" + email + '\'' +
                ", rg='" + rg + '\'' +
                ", inscricaoEstadual='" + inscricaoEstadual + '\'' +
                ", dataNascimento=" + dataNascimento +
                ", estadoCivil='" + estadoCivil + '\'' +
                ", status='" + status + '\'' +
                ", dataCadastro=" + dataCadastro +
                ", enderecos=" + enderecos +
                '}';
    }
}
