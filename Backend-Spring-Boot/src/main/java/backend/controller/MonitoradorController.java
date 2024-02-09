package backend.controller;

import backend.dto.EnderecoDTO;
import backend.dto.MonitoradorDTO;
import backend.entitie.Endereco;
import backend.entitie.Monitorador;
import backend.exceptions.DataNascimentoException;
import backend.service.MonitoradorService;
import backend.service.RegistroDuplicadoException;
import backend.exceptions.CampoObrigatorioException;
import backend.exceptions.EnderecoInvalidaException;
import backend.exceptions.NomeRazaoSocialInvalidaException;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/api/monitoradores", produces = "application/json; charset=UTF-8")
public class MonitoradorController {

    private final MonitoradorService monitoradorService;


    @Autowired
    public MonitoradorController(MonitoradorService monitoradorService) {
        this.monitoradorService = monitoradorService;
    }

    @PostMapping
    public ResponseEntity<Monitorador> createMonitorador(@RequestBody Monitorador monitorador) {
        Monitorador monitoradorSemMascara = monitoradorService.removerMascaraMonitorador(monitorador);
        Monitorador savedMonitorador = monitoradorService.salvarMonitoradorComEnderecos(monitoradorSemMascara);
        return ResponseEntity.ok(savedMonitorador);
    }

    @PostMapping("/saveAll")
    public ResponseEntity<Monitorador> saveAllMonitorador (@RequestBody List<Monitorador> monitorador) {
        monitoradorService.saveAllMonitorador(monitorador);
        return ResponseEntity.ok().build();
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

    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportMonitoradoresExcel() {
        try {
            byte[] excelData = monitoradorService.exportMonitoradoresToExcel();

            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=monitoradores.xlsx");
            headers.set(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelData);
        } catch (IOException e) {
            // Tratar exceção apropriadamente
            return ResponseEntity.internalServerError().build();
        }
    }
    @GetMapping(value = "/export/report")
    public ResponseEntity<byte[]> pdf() throws JRException {
        byte[] file = monitoradorService.exportReport();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        String filename = "relatorio-monitoradores.pdf";
        headers.setContentDispositionFormData(filename, filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return new ResponseEntity<>(file, headers, HttpStatus.OK);
    }

    @GetMapping(value = "/export/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> getMonitoradoresPdf() {
        try {
            byte[] pdfContent = monitoradorService.generateMonitoradorPDF();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            String filename = "relatorio-monitoradores.pdf";
            headers.setContentDispositionFormData(filename, filename);
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
        } catch (Exception e) {
            // Tratar exceção
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/upload")
    public List<Monitorador> uploadFile(@RequestBody byte[] fileBytes) throws IOException {
        // Especifique o caminho onde deseja salvar o arquivo
        String filePath = "src/main/resources/upload.xlsx";

        // Crie um FileOutputStream para escrever os bytes no arquivo
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(fileBytes);
        } catch (IOException e) {
            e.printStackTrace();
            // Trate o erro adequadamente
            return null;
        }

        FileInputStream fis = new FileInputStream(filePath);
        // Suponha que você tenha uma lista de monitoradores
        List<Monitorador> monitoradores = monitoradorService.gerarLista(fis); // Implemente esse método

        // Agora, 'monitoradores' é uma lista de Monitorador que você deseja retornar como JSON
        return monitoradores;
    }

    @ExceptionHandler(NomeRazaoSocialInvalidaException.class)
    public ResponseEntity<String> handleNomeRazaoSocialInvalida(NomeRazaoSocialInvalidaException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(RegistroDuplicadoException.class)
    public ResponseEntity<String> handleRegistroDuplicado(RegistroDuplicadoException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleCpfInvalido(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(EnderecoInvalidaException.class)
    public ResponseEntity<String> handleEnderecoInvalida (EnderecoInvalidaException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(CampoObrigatorioException.class)
    public ResponseEntity<String> handleCamposObrigatorios (CampoObrigatorioException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(DataNascimentoException.class)
    public ResponseEntity<String> handleDataNascimento (DataNascimentoException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ResponseEntity.badRequest().body(errors);
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
