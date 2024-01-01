package backend.entitie;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "monitorador")
public class Monitorador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tipo_pessoa", nullable = false)
    private String tipoPessoa;

    @Column(name = "cpf", unique = true, length = 11)
    private String cpf;

    @Column(name = "cnpj", unique = true, length = 14)
    private String cnpj;

    @Column(name = "nome", length = 255)
    private String nome;

    @Column(name = "razao_social", length = 255)
    private String razaoSocial;

    @Column(name = "telefone", length = 20)
    private String telefone;

    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @Column(name = "rg", length = 20)
    private String rg;

    @Column(name = "inscricao_estadual", length = 20)
    private String inscricaoEstadual;

    @Column(name = "data_nascimento")
    @Temporal(TemporalType.DATE)
    private Date dataNascimento;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo;

    @OneToMany(mappedBy = "monitorador", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Endereco> enderecos;

}


