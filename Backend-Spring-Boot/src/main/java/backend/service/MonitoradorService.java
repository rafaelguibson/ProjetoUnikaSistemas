package backend.service;

import backend.entitie.Endereco;
import backend.entitie.Monitorador;
import backend.repository.EnderecoRepository;
import backend.repository.MonitoradorRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class MonitoradorService {

    @Autowired
    private final MonitoradorRepository monitoradorRepository;

    @Autowired
    private final EnderecoRepository enderecoRepository;

    @Autowired
    public MonitoradorService(MonitoradorRepository monitoradorRepository, EnderecoRepository enderecoRepository) {
        this.monitoradorRepository = monitoradorRepository;
        this.enderecoRepository = enderecoRepository;
    }

    public Monitorador saveMonitorador(Monitorador monitorador) {
        // Adicione aqui qualquer lógica de negócios antes de salvar

        monitorador.setCpf(monitorador.getCpf().replaceAll("\\D", ""));
        monitorador.setTelefone(monitorador.getTelefone().replaceAll("\\D", ""));
        return monitoradorRepository.save(monitorador);
    }

    @Transactional
    public Monitorador salvarMonitoradorComEnderecos(Monitorador monitorador) {
        // Primeiro, salve o monitorador
        Monitorador monitoradorSalvo = monitoradorRepository.save(monitorador);

        // Em seguida, associe os endereços ao monitorador e salve os endereços
        List<Endereco> enderecos = monitorador.getEnderecos();
        if (enderecos != null && !enderecos.isEmpty()) {
            for (Endereco endereco : enderecos) {
                endereco.setMonitorador(monitoradorSalvo);
                enderecoRepository.save(endereco);
            }
        }

        // Retorne o monitorador salvo com os endereços associados
        return monitoradorSalvo;
    }


    public Optional<Monitorador> getMonitoradorById(Long id) {
        return monitoradorRepository.findById(id);
    }

    public List<Monitorador> getAllMonitoradores() {
        return monitoradorRepository.findAll();
    }

    public Monitorador updateMonitorador(Monitorador monitorador) {
        // Adicione lógica de validação ou negócios antes de atualizar

        return monitoradorRepository.save(monitorador);
    }

    public void deleteMonitorador(Long id) {
        monitoradorRepository.deleteById(id);
    }

    public List<Monitorador> buscarPF() {
        return monitoradorRepository.findByTipoPessoa("FISICA");
    }

    public List<Monitorador> buscarPJ() {
        return monitoradorRepository.findByTipoPessoa("JURIDICA");
    }

    public void deleteAllMonitoradores(List<Monitorador> list) {
        monitoradorRepository.deleteAll(list);
    }
    // Outros métodos relacionados ao negócio podem ser adicionados aqui
}

