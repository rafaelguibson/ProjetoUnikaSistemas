package Backend.entitie;

import Backend.enums.EstadoCivil;
import Backend.enums.TipoPessoa;
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
    private TipoPessoa tipoPessoa;

    @Enumerated(EnumType.STRING)
    private EstadoCivil estadoCivil;

    private String nomeRazaoSocial;
    private String cpfCnpj;
    private String rgIe;
    private String telefone;
    private String email;
    private Date dataNascimento;
    private Boolean status;

    @Column(updatable = false)
    private Date dataCadastro;


    @Transient
    private Date dataInicial;

    @Transient
    private Date dataFinal;

    @Transient
    private boolean selected;

    @JsonManagedReference
    @OneToMany(mappedBy = "monitorador", orphanRemoval = true)
    private List<Endereco> enderecos;

    @PrePersist
    protected void onCreate() {
        dataCadastro = new Date();
    }
}
