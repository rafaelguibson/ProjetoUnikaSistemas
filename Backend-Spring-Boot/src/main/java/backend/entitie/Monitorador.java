package backend.entitie;
import backend.enums.Status;
import backend.enums.TipoPessoa;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.br.CNPJ;
import org.hibernate.validator.constraints.br.CPF;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "monitorador")
public class Monitorador implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_pessoa", nullable = false)
    private TipoPessoa tipoPessoa;

    @CPF
    @Column(name = "cpf", unique = true, length = 11)
    private String cpf;

    @CNPJ
    @Column(name = "cnpj", unique = true, length = 14)
    private String cnpj;

    @Column(name = "nome", length = 255)
    private String nome;

    @Column(name = "razao_social", length = 255)
    private String razaoSocial;

    @Column(name = "telefone", length = 20)
    private String telefone;

    @Email
    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @Column(name = "rg", length = 20)
    private String rg;

    @Column(name = "inscricao_estadual", length = 20)
    private String inscricaoEstadual;

    @Column(name = "data_nascimento")
    @Temporal(TemporalType.DATE)
    private Date dataNascimento;

    @Column(name = "estado_civil", length = 50)
    private String estadoCivil;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @Column(name = "data_cadastro")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataCadastro;

    @JsonManagedReference
    @OneToMany(mappedBy = "monitorador", orphanRemoval = true)
    private List<Endereco> enderecos;

    @Transient
    private boolean selected;

    @Transient
    private Date dataInicial;
    @Transient
    private Date dataFinal;

    @PrePersist
    protected void onCreate() {
        dataCadastro = new Date();
    }
}
