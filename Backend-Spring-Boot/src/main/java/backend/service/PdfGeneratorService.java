package backend.service;

import backend.entitie.Endereco;
import backend.entitie.Monitorador;
import backend.enums.TipoPessoa;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class PdfGeneratorService {

    public byte[] exportMonitoradorPDF(List<Monitorador> monitoradores) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Adiciona o título do relatório
        addReportTitle(document);

        for (Monitorador monitorador : monitoradores) {
            addMonitoradorHeader(document, monitorador);
            addMonitoradorInfo(document, monitorador);
            addEnderecos(document, monitorador);
            document.add(new Paragraph("----------------------------------------------------------------------------------------------------------------------------------"));
        }

        document.close();
        return baos.toByteArray();
    }

    private void addReportTitle(Document document) throws Exception {
        PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        Paragraph tituloRelatorio = new Paragraph("Relatório de Monitoradores Cadastrados")
                .setFont(font)
                .setFontSize(24)
                .setFontColor(ColorConstants.WHITE)
                .setBackgroundColor(new DeviceRgb(0, 0, 0))
                .setTextAlignment(TextAlignment.CENTER);
        document.add(tituloRelatorio);
    }

    private void addMonitoradorHeader(Document document, Monitorador monitorador) throws Exception {
        PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        Paragraph header = new Paragraph("Monitorador " + monitorador.getId())
                .setFont(font)
                .setFontSize(14)
                .setFontColor(ColorConstants.WHITE)
                .setBackgroundColor(new DeviceRgb(0, 0, 0))
                .setTextAlignment(TextAlignment.CENTER);
        document.add(header);
    }

    private void addMonitoradorInfo(Document document, Monitorador monitorador) throws Exception {
        PdfFont bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        Table table = new Table(2); // Duas colunas
        table.setWidth(522);

        // Nome/Razão Social e ID
        table.addCell(new Cell().add(new Paragraph().add(new Text("Nome/Razão Social: ").setFont(bold)).add(monitorador.getTipoPessoa() == TipoPessoa.PF ? monitorador.getNome() : monitorador.getRazaoSocial())).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(new Paragraph().add(new Text("ID: ").setFont(bold)).add(String.valueOf(monitorador.getId()))).setBorder(Border.NO_BORDER));

        // CPF/CNPJ e RG/I.E
        String cpfOuCnpj = monitorador.getTipoPessoa() == TipoPessoa.PF ? monitorador.getCpf() : monitorador.getCnpj();
        String rgOuIe = monitorador.getTipoPessoa() == TipoPessoa.PF ? monitorador.getRg() : monitorador.getInscricaoEstadual();
        table.addCell(new Cell().add(new Paragraph().add(new Text("CPF/CNPJ: ").setFont(bold)).add(cpfOuCnpj)).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(new Paragraph().add(new Text("RG/I.E: ").setFont(bold)).add(rgOuIe)).setBorder(Border.NO_BORDER));

        // Telefone e Email
        table.addCell(new Cell().add(new Paragraph().add(new Text("Telefone: ").setFont(bold)).add(monitorador.getTelefone())).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(new Paragraph().add(new Text("Email: ").setFont(bold)).add(monitorador.getEmail())).setBorder(Border.NO_BORDER));

        // Tipo Pessoa e Status
        table.addCell(new Cell().add(new Paragraph().add(new Text("Tipo Pessoa: ").setFont(bold)).add(monitorador.getTipoPessoa().getTipoPessoa())).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(new Paragraph().add(new Text("Status: ").setFont(bold)).add(monitorador.getStatus().getStatus())).setBorder(Border.NO_BORDER));

        document.add(table);
    }

    private void addEnderecos(Document document, Monitorador monitorador) throws Exception {
        PdfFont bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        Table enderecoTable = new Table(2); // Duas colunas
        enderecoTable.setWidth(522);
        document.add(new Paragraph("--------------------------------------------------------- Endereços --------------------------------------------------------").setFont(bold));
        for (Endereco endereco : monitorador.getEnderecos()) {
            // Logradouro e Número
            enderecoTable.addCell(new Cell().add(new Paragraph().add(new Text("Logradouro: ").setFont(bold)).add(endereco.getLogradouro())).setBorder(Border.NO_BORDER));
            enderecoTable.addCell(new Cell().add(new Paragraph().add(new Text("Número: ").setFont(bold)).add(String.valueOf(endereco.getNumero()))).setBorder(Border.NO_BORDER));

            // Bairro e Cidade
            enderecoTable.addCell(new Cell().add(new Paragraph().add(new Text("Bairro: ").setFont(bold)).add(endereco.getBairro())).setBorder(Border.NO_BORDER));
            enderecoTable.addCell(new Cell().add(new Paragraph().add(new Text("Cidade: ").setFont(bold)).add(endereco.getCidade())).setBorder(Border.NO_BORDER));

            // Estado e CEP (se aplicável)
            enderecoTable.addCell(new Cell().add(new Paragraph().add(new Text("Estado: ").setFont(bold)).add(endereco.getEstado().getNome())).setBorder(Border.NO_BORDER));
            enderecoTable.addCell(new Cell().add(new Paragraph().add(new Text("CEP: ").setFont(bold)).add(endereco.getCep())).setBorder(Border.NO_BORDER));

            // Separador para o próximo endereço (se houver mais de um endereço)
            enderecoTable.addCell(new Cell(2, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER));
        }

        document.add(enderecoTable);
    }

}
