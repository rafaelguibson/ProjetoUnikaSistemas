package backend.service;

import backend.entitie.Monitorador;
import net.sf.jasperreports.engine.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;


@Service
public class ReportService {

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
