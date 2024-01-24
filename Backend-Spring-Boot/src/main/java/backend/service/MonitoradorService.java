package backend.service;

import backend.entitie.Endereco;
import backend.entitie.Monitorador;
import backend.enums.Status;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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

    public List<Monitorador> gerarLista(FileInputStream file) throws IOException {
        List<Monitorador> monitoradores = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file)) {
            Sheet sheet = workbook.getSheetAt(0); // Supondo que a planilha desejada está na primeira aba

            Iterator<Row> rowIterator = sheet.iterator();

            // Ignora as duas primeiras linhas
            if (rowIterator.hasNext()) {
                rowIterator.next(); // Primeira linha
            }
            if (rowIterator.hasNext()) {
                rowIterator.next(); // Segunda linha
            }

            // Percorre todas as linhas da planilha
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Monitorador monitorador = criarMonitoradorDaLinha(row);
                if (monitorador != null) {
                    monitoradores.add(monitorador);
                }
            }
        }

        return monitoradores;
    }

    private Monitorador criarMonitoradorDaLinha(Row row) {
        try {
            Cell cellTipoPessoa = row.getCell(1); // Coluna do Tipo Pessoa

            String tipoPessoa = cellTipoPessoa.getStringCellValue().toLowerCase(); // Converter para minúsculas para comparar

            Monitorador monitorador = new Monitorador();

            // Verificar o tipo de pessoa e preencher os campos apropriados
            if ("Física".equalsIgnoreCase(tipoPessoa)) {
                monitorador.setTipoPessoa(TipoPessoa.PF);
                monitorador.setNome(row.getCell(3).getStringCellValue()); // Nome
                monitorador.setCnpj(row.getCell(2).getStringCellValue()); // CPF
                monitorador.setTelefone(row.getCell(4).getStringCellValue()); // Telefone
                monitorador.setEmail(row.getCell(5).getStringCellValue()); // Email
                monitorador.setRg(row.getCell(6).getStringCellValue()); // RG
                monitorador.setDataNascimento(new Date()); // Data de Nascimento
                monitorador.setStatus(Status.valueOf(row.getCell(8).getStringCellValue().toUpperCase())); // Status
            } else if ("Jurídica".equalsIgnoreCase(tipoPessoa)) {
                monitorador.setTipoPessoa(TipoPessoa.PJ);
                monitorador.setRazaoSocial(row.getCell(3).getStringCellValue()); //Razão Social
                monitorador.setCnpj(row.getCell(2).getStringCellValue()); //CNPJ
                monitorador.setTelefone(row.getCell(4).getStringCellValue()); // Telefone
                monitorador.setEmail(row.getCell(5).getStringCellValue()); // Email
                monitorador.setInscricaoEstadual(row.getCell(6).getStringCellValue()); // Inscrição Estadual
                monitorador.setStatus(Status.valueOf(row.getCell(8).getStringCellValue().toUpperCase())); // Status
            } else {
                // Trate casos em que o tipo de pessoa não é reconhecido
                return null;
            }

            return monitorador;
        } catch (Exception e) {
            // Se houver algum erro ao ler a linha, retorna null
            e.printStackTrace();
            return null;
        }
    }
}

