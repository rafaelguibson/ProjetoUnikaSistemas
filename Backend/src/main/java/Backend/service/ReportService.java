package Backend.service;

import Backend.entitie.Monitorador;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class ReportService {

    private Map<String, Object> params = new HashMap<>();
    @Autowired
    private DataSource dataSource;


    public byte[] exportReport(List<Monitorador> monitoradores){
        byte[] bytes = null;



        try {
            File file = ResourceUtils.getFile("src/main/resources/relatorio-monitorador.jasper");
            JasperPrint jasperPrint;
            try {
                jasperPrint = JasperFillManager.fillReport(file.getAbsolutePath(), null, dataSource.getConnection());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            bytes = JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (JRException e) {
            throw new RuntimeException(e);
        }
        return bytes;
    }
}
