package Backend.controller;
import Backend.entitie.Endereco;
import Backend.service.EnderecoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enderecos")
public class EnderecoController {

    private final EnderecoService enderecoService;

    @Autowired
    public EnderecoController(EnderecoService enderecoService) {
        this.enderecoService = enderecoService;
    }

    @PostMapping
    public ResponseEntity<Endereco> createEndereco(@RequestBody Endereco endereco) {
        Endereco savedEndereco = enderecoService.saveEndereco(endereco);
        return ResponseEntity.ok(savedEndereco);
    }

    @GetMapping
    public ResponseEntity<List<Endereco>> getAllEnderecos() {
        return ResponseEntity.ok(enderecoService.getAllEnderecos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Endereco> getEnderecoById(@PathVariable Long id) {
        return enderecoService.getEnderecoById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Endereco> updateEndereco(@PathVariable Long id, @RequestBody Endereco endereco) {
        if (enderecoService.getEnderecoById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        endereco.setId(id);
        return ResponseEntity.ok(enderecoService.updateEndereco(endereco));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEndereco(@PathVariable Long id) {
        if (enderecoService.getEnderecoById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        enderecoService.deleteEndereco(id);
        return ResponseEntity.ok().build();
    }

}
