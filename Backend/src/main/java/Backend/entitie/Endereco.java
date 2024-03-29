package Backend.entitie;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "endereco")
public class Endereco implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "monitorador_id", nullable = false)
    private Monitorador monitorador;

    @Column(name = "logradouro", nullable = false, length = 255)
    private String logradouro;

    @Column(name = "complemento", nullable = true, length = 255)
    private String complemento;

    @Column(name = "numero", length = 20)
    private String numero;

    @Column(name = "cep", nullable = false, length = 10)
    private String cep;

    @Column(name = "bairro", length = 100)
    private String bairro;

    @Column(name = "cidade", nullable = false, length = 100)
    @JsonProperty("localidade")
    private String cidade;

    @Column(name = "estado", nullable = false, length = 50)
    @JsonProperty("uf")
    private String estado;

}
