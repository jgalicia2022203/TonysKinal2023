package org.juangalicia.report;

import java.io.InputStream;
import java.util.Map;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;
import org.juangalicia.db.Conexion;

public class GenerateReport {
    public static void showReport(String nameReport, String title, Map parameters){
        InputStream report = GenerateReport.class.getResourceAsStream(nameReport);
        try {
            JasperReport masterReport = (JasperReport)JRLoader.loadObject(report);
            JasperPrint printedReport = JasperFillManager.fillReport(masterReport, parameters, Conexion.getInsance().getConexion());
            JasperViewer viewer = new JasperViewer(printedReport, false);
            viewer.setTitle(title);
            viewer.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
