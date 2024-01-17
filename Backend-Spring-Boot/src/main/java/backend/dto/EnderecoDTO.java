package backend.dto;
import backend.enums.Estado; // Certifique-se de importar o enum Estado corretamente
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnderecoDTO {

    private Long id;
    private Long monitoradorId;
    private String logradouro;
    private String numero;
    private String cep;
    private String bairro;
    private String cidade;
    private Estado estado;

}
