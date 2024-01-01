package backend.service;

import backend.entitie.Endereco;
import backend.repository.EnderecoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class EnderecoService {

    private final EnderecoRepository enderecoRepository;

    @Autowired
    public EnderecoService(EnderecoRepository enderecoRepository) {
        this.enderecoRepository = enderecoRepository;
    }

    public Endereco saveEndereco(Endereco endereco) {
        // Adicione aqui qualquer lógica de negócios antes de salvar
        return enderecoRepository.save(endereco);
    }

    public Optional<Endereco> getEnderecoById(Long id) {
        return enderecoRepository.findById(id);
    }

    public List<Endereco> getAllEnderecos() {
        return enderecoRepository.findAll();
    }

    public Endereco updateEndereco(Endereco endereco) {
        // Adicione lógica de validação ou negócios antes de atualizar
        return enderecoRepository.save(endereco);
    }

    public void deleteEndereco(Long id) {
        enderecoRepository.deleteById(id);
    }

    // Métodos adicionais específicos para Endereco podem ser adicionados aqui
}
