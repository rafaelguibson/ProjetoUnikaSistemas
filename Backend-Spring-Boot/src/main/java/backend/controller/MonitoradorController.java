package backend.controller;

import backend.dto.EnderecoDTO;
import backend.dto.MonitoradorDTO;
import backend.entitie.Endereco;
import backend.entitie.Monitorador;
import backend.service.MonitoradorService;
import backend.validators.CpfCnpjInvalidoException;
import backend.validators.NomeRazaoSocialInvalidaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/monitoradores", produces = "application/json; charset=UTF-8")
public class MonitoradorController {

    private final MonitoradorService monitoradorService;

    @Autowired
    public MonitoradorController(MonitoradorService monitoradorService) {
        this.monitoradorService = monitoradorService;
    }

    @PostMapping
    public ResponseEntity<Monitorador> createMonitorador(@RequestBody Monitorador monitorador) {
        Monitorador savedMonitorador = monitoradorService.salvarMonitoradorComEnderecos(monitorador);
        return ResponseEntity.ok(savedMonitorador);
    }

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

    @PatchMapping("/{id}")
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

    @PostMapping("/deleteAll")
    public ResponseEntity<Void> deleteAllMonitoradores(@RequestBody List<Monitorador> monitorador) {
        monitoradorService.deleteAllMonitoradores(monitorador);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/filtrar")
    public ResponseEntity<List<Monitorador>> filtrar(@RequestBody Monitorador filtro) {
        List<Monitorador> resultado = monitoradorService.filtrar(filtro);
        return ResponseEntity.ok(resultado);
    }

    @PostMapping("/validar")
    public ResponseEntity<String> validarMonitorador(@RequestBody Monitorador monitorador) {
        monitoradorService.validarMonitorador(monitorador);
        return ResponseEntity.ok("Validação bem-sucedida.");
    }

    @ExceptionHandler(NomeRazaoSocialInvalidaException.class)
    public ResponseEntity<String> handleNomeRazaoSocialInvalida(NomeRazaoSocialInvalidaException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(CpfCnpjInvalidoException.class)
    public ResponseEntity<String> handleCpfCnpjInvalido(CpfCnpjInvalidoException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
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
        monitorador.setStatus(monitoradorDTO.getStatus());

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
