package backend.controller;

import backend.dto.EnderecoDTO;
import backend.dto.MonitoradorDTO;
import backend.entitie.Endereco;
import backend.entitie.Monitorador;
import backend.service.MonitoradorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/monitoradores")
public class MonitoradorController {

    private final MonitoradorService monitoradorService;

    @Autowired
    public MonitoradorController(MonitoradorService monitoradorService) {
        this.monitoradorService = monitoradorService;
    }

//    @PostMapping
//    public ResponseEntity<Monitorador> createMonitorador(@RequestBody Monitorador monitorador) {
//        Monitorador savedMonitorador = monitoradorService.salvarMonitoradorComEnderecos(monitorador);
//        return ResponseEntity.ok(savedMonitorador);
//    }

    @GetMapping
    public ResponseEntity<List<Monitorador>> getAllMonitoradores() {
        return ResponseEntity.ok(monitoradorService.getAllMonitoradores());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Monitorador> getMonitoradorById(@PathVariable Long id) {
        return monitoradorService.getMonitoradorById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @GetMapping("/pf")
    public ResponseEntity<List<Monitorador>> buscarPF() {
        List<Monitorador> listaPF = monitoradorService.buscarPF();
        return ResponseEntity.ok(listaPF);
    }

    @GetMapping("/pj")
    public ResponseEntity<List<Monitorador>> buscarPJ() {
        List<Monitorador> listaPJ = monitoradorService.buscarPJ();
        return ResponseEntity.ok(listaPJ);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Monitorador> updateMonitorador(@PathVariable Long id, @RequestBody Monitorador monitorador) {
        if (!monitoradorService.getMonitoradorById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        monitorador.setId(id);
        return ResponseEntity.ok(monitoradorService.updateMonitorador(monitorador));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMonitorador(@PathVariable Long id) {
        if (!monitoradorService.getMonitoradorById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        monitoradorService.deleteMonitorador(id);
        return ResponseEntity.ok().build();
    }

    // Outros endpoints podem ser adicionados aqui

    @PostMapping
    public ResponseEntity<Monitorador> createMonitoradorComEnderecos(@RequestBody MonitoradorDTO monitoradorDTO) {
        Monitorador monitorador = convertToEntity(monitoradorDTO);
        Monitorador savedMonitorador = monitoradorService.salvarMonitoradorComEnderecos(monitorador);
        return ResponseEntity.ok(savedMonitorador);
    }

    private Monitorador convertToEntity(MonitoradorDTO monitoradorDTO) {
        Monitorador monitorador = new Monitorador();
        monitorador.setId(monitoradorDTO.getId());
        monitorador.setTipoPessoa(monitoradorDTO.getTipoPessoa());
        monitorador.setCpf(monitoradorDTO.getCpf());
        monitorador.setCnpj(monitoradorDTO.getCnpj());
        monitorador.setNome(monitoradorDTO.getNome());
        monitorador.setRazaoSocial(monitoradorDTO.getRazaoSocial());
        monitorador.setTelefone(monitoradorDTO.getTelefone());
        monitorador.setEmail(monitoradorDTO.getEmail());
        monitorador.setRg(monitoradorDTO.getRg());
        monitorador.setInscricaoEstadual(monitoradorDTO.getInscricaoEstadual());
        monitorador.setDataNascimento(monitoradorDTO.getDataNascimento());
        monitorador.setAtivo(monitoradorDTO.getAtivo());

        if (monitoradorDTO.getEnderecos() != null) {
            List<Endereco> enderecos = monitoradorDTO.getEnderecos().stream()
                    .map(this::convertEnderecoToEntity)
                    .collect(Collectors.toList());
            monitorador.setEnderecos(enderecos);
        }

        return monitorador;
    }

    private Endereco convertEnderecoToEntity(EnderecoDTO enderecoDTO) {
        Endereco endereco = new Endereco();
        endereco.setId(enderecoDTO.getId());
        endereco.setLogradouro(enderecoDTO.getLogradouro());
        endereco.setNumero(enderecoDTO.getNumero());
        endereco.setCep(enderecoDTO.getCep());
        endereco.setBairro(enderecoDTO.getBairro());
        endereco.setCidade(enderecoDTO.getCidade());
        endereco.setEstado(enderecoDTO.getEstado());
        // O Monitorador será associado no serviço

        return endereco;
    }
}
