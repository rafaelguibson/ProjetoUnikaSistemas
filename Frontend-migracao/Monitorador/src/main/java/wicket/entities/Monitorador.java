package wicket.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private List<Endereco> enderecos;
    private boolean selected;


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
                ", ativo=" + ativo +
                ", enderecos=" + enderecos +
                '}';
    }
}
