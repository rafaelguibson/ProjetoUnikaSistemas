package Backend.controller;

import Backend.entitie.Monitorador;
import Backend.exceptions.*;
import Backend.service.MonitoradorService;

import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
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

    @PostMapping("/saveAll")
    public ResponseEntity<Void> saveAllMonitorador(@RequestBody List<Monitorador> monitoradores) {
        monitoradorService.saveAllMonitorador(monitoradores);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<Monitorador>> getAllMonitoradores() {
        return ResponseEntity.ok(monitoradorService.getAllMonitoradores());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Monitorador> getMonitoradorById(@PathVariable Long id) {
        return ResponseEntity.ok(monitoradorService.getMonitoradorById(id));
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

    @PutMapping(value = "/{id}")
    public ResponseEntity<Monitorador> updateMonitorador(@PathVariable Long id, @RequestBody Monitorador monitorador) {
        Monitorador newMonitorador = monitoradorService.updateMonitorador(id, monitorador);
        return ResponseEntity.ok().body(newMonitorador);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMonitorador(@PathVariable Long id, @RequestBody Monitorador monitorador) {
        Monitorador updatedMonitorador = monitoradorService.updateMonitorador(id, monitorador);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/deleteAll")
    public ResponseEntity<Void> deleteAllMonitoradores(@RequestBody List<Monitorador> monitoradores) {
        monitoradorService.deleteAllMonitoradores(monitoradores);
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
            // Tratar exceção adequadamente
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

    @PostMapping("/upload")
    public ResponseEntity<List<Monitorador>> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        List<Monitorador> monitoradores = monitoradorService.gerarLista(file);
        return ResponseEntity.ok(monitoradores);
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

}
