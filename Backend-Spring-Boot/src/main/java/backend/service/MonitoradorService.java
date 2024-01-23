package backend.service;

import backend.entitie.Endereco;
import backend.entitie.Monitorador;
import backend.enums.TipoPessoa;
import backend.repository.EnderecoRepository;
import backend.repository.MonitoradorRepository;
import backend.validators.CampoObrigatorioException;
import backend.validators.CpfCnpjInvalidoException;
import backend.validators.EnderecoInvalidaException;
import backend.validators.NomeRazaoSocialInvalidaException;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import net.sf.jasperreports.engine.JRException;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class MonitoradorService {

    @Autowired
    private final MonitoradorRepository monitoradorRepository;

    @Autowired
    private final EnderecoRepository enderecoRepository;

    @Autowired
    private PdfGeneratorService pdfGeneratorService;
    @Autowired
    ReportService reportService;
    @Autowired
    ExcelGeneratorService excelService;
    private static final Pattern CPF_PATTERN =
            Pattern.compile("[0-9]{3}\\.?[0-9]{3}\\.?[0-9]{3}\\-?[0-9]{2}");

    @Autowired
    public MonitoradorService(MonitoradorRepository monitoradorRepository, EnderecoRepository enderecoRepository,PdfGeneratorService pdfGeneratorService,ReportService reportService,ExcelGeneratorService excelService) {
        this.monitoradorRepository = monitoradorRepository;
        this.enderecoRepository = enderecoRepository;
        this.pdfGeneratorService = pdfGeneratorService;
        this.reportService = reportService;
        this.excelService = excelService;
    }

    public Monitorador saveMonitorador(@Valid Monitorador monitorador) {


        return monitoradorRepository.save(removerMascaraMonitorador(monitorador));
    }

    public List<Monitorador> saveAllMonitorador(@Valid List<Monitorador> monitorador) {

        return monitoradorRepository.saveAll(monitorador);
    }

    @Transactional
    public Monitorador salvarMonitoradorComEnderecos(@Valid Monitorador monitorador) {
        //validação monitorador
        validarMonitorador(monitorador);

        //salva o monitorador
        Monitorador monitoradorSalvo = monitoradorRepository.save(monitorador);

        //associa os endereços ao monitorador e salve os endereços
        List<Endereco> enderecos = monitorador.getEnderecos();
        if (enderecos != null && !enderecos.isEmpty()) {
            for (Endereco endereco : enderecos) {
                endereco.setMonitorador(monitoradorSalvo);
                enderecoRepository.save(endereco);
            }
        }

        // a o monitorador salvo com os endereços associados
        return monitoradorSalvo;
    }


    public Optional<Monitorador> getMonitoradorById(Long id) {
        return monitoradorRepository.findById(id);
    }

    public List<Monitorador> getAllMonitoradores() {
        return monitoradorRepository.findAll();
    }

    public Monitorador updateMonitorador(Monitorador monitorador) {
        return monitoradorRepository.save(monitorador);
    }

    public void deleteMonitorador(Long id) {
        monitoradorRepository.deleteById(id);
    }

    public List<Monitorador> buscarPF() {
        return monitoradorRepository.findByTipoPessoa(TipoPessoa.PF);
    }

    public List<Monitorador> buscarPJ() {
        return monitoradorRepository.findByTipoPessoa(TipoPessoa.PJ);
    }

    public List<Monitorador> filtrar(Monitorador filtro) {
        return monitoradorRepository.filtrar(filtro);
    }

    public void deleteAllMonitoradores(List<Monitorador> list) {
        monitoradorRepository.deleteAll(list);
    }
    public Monitorador removerMascaraMonitorador(Monitorador monitorador) {
        if(monitorador.getTipoPessoa().equals(TipoPessoa.PF)) {
            monitorador.setCpf(monitorador.getCpf().replaceAll("\\D", ""));
        }
        if(monitorador.getTipoPessoa().equals(TipoPessoa.PJ)) {
            monitorador.setCnpj(monitorador.getCnpj().replaceAll("\\D", ""));
            monitorador.setInscricaoEstadual(monitorador.getInscricaoEstadual().replaceAll("\\D", ""));
        }
        monitorador.setRg(monitorador.getRg().replaceAll("\\D", ""));
        monitorador.setTelefone(monitorador.getTelefone().replaceAll("\\D", ""));
        return monitorador;
    }

    public List<Monitorador> removerMascaraListaMonitorador(List<Monitorador> monitoradorList) {
        for (Monitorador item: monitoradorList) {
            if(item.getTipoPessoa().equals(TipoPessoa.PF)) {
                item.setCpf(item.getCpf().replaceAll("\\D", ""));
            }
            if(item.getTipoPessoa().equals(TipoPessoa.PJ)) {
                item.setCnpj(item.getCnpj().replaceAll("\\D", ""));
                item.setInscricaoEstadual(item.getInscricaoEstadual().replaceAll("\\D", ""));
            }
            item.setRg(item.getRg().replaceAll("\\D", ""));
            item.setTelefone(item.getTelefone().replaceAll("\\D", ""));
        }
        return monitoradorList;
    }

    public void validarMonitorador(Monitorador monitorador) {
        if (monitorador.getTipoPessoa() == TipoPessoa.PF) {
            validarCampoObrigatorio(monitorador.getNome(), "Nome");
            validarCampoObrigatorio(monitorador.getCpf(), "CPF");
            validarCPF(monitorador.getCpf());
            validarCampoObrigatorio(monitorador.getRg(), "RG");
            validarCampoObrigatorio(monitorador.getTelefone(), "Telefone");
            validarCampoObrigatorio(monitorador.getDataNascimento(), "Data de Nascimento");
            validarCampoObrigatorio(monitorador.getStatus(), "Status");
        } else if (monitorador.getTipoPessoa() == TipoPessoa.PJ) {
            validarCampoObrigatorio(monitorador.getRazaoSocial(), "Razão Social");
            validarCampoObrigatorio(monitorador.getCnpj(), "CNPJ");
            validarCampoObrigatorio(monitorador.getInscricaoEstadual(), "Inscrição Estadual");
            validarCampoObrigatorio(monitorador.getTelefone(), "Telefone");
            validarCampoObrigatorio(monitorador.getStatus(), "Status");
        }

        validarCampoObrigatorio(monitorador.getEnderecos(), "Endereços");
    }

    private void validarCampoObrigatorio(Object campo, String nomeCampo) {
        if (Objects.isNull(campo) || (campo instanceof String && ((String) campo).isEmpty())
                || (campo instanceof Collection && ((Collection<?>) campo).isEmpty())) {
            throw new CampoObrigatorioException(nomeCampo);
        }
    }

    public void validarCPF(String cpf) throws IllegalArgumentException {
        if (!CPF_PATTERN.matcher(cpf).matches()) {
            throw new IllegalArgumentException("CPF inválido. Formato esperado: XXX.XXX.XXX-XX");
        }
        // Aqui você pode adicionar lógica adicional para validar os dígitos verificadores do CPF, se necessário.
    }




    private boolean todosOsCamposVazios(Monitorador filtro) {
        if (filtro == null) {
            return true;
        }

        return (isNullOrEmpty(filtro.getNome()) &&
                isNullOrEmpty(filtro.getCpf()) &&
                isNullOrEmpty(filtro.getCnpj()) &&
                isNullOrEmpty(filtro.getTelefone()) &&
                isNullOrEmpty(filtro.getEmail()) &&
                isNullOrEmpty(filtro.getRg()) &&
                isNullOrEmpty(filtro.getInscricaoEstadual()) &&
                filtro.getDataNascimento() == null &&
                filtro.getTipoPessoa() == null &&
                filtro.getStatus() == null);
    }

    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public byte[] generateMonitoradorPDF() throws Exception {
        List<Monitorador> monitoradores = getAllMonitoradores();
        return pdfGeneratorService.exportMonitoradorPDF(monitoradores);
    }

    public byte[] exportReport() throws JRException {
        return reportService.exportReport(getAllMonitoradores());
    }

    public byte[] exportMonitoradoresToExcel() throws IOException {
        return excelService.exportMonitoradoresToExcel(getAllMonitoradores());
    }

}

