package br.com.axxiom.core.web.view;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.beans.BeanUtils;
import org.springframework.web.servlet.view.document.AbstractExcelView;

public class ExcelView extends AbstractExcelView {

    @Override
    protected void buildExcelDocument(Map<String, Object> model, HSSFWorkbook workbook, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        for (Entry<String, Object> item : model.entrySet()) {
            Object val = item.getValue();
            if (val instanceof Iterable) {
                String sheetName = item.getKey();
                gerarAba(workbook, sheetName, (Iterable<?>) val);
            }
        }
    }

    public void gerarAba(HSSFWorkbook wb, String name, Iterable<?> lista) throws IllegalArgumentException, SecurityException,
            IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        HSSFSheet sheet = wb.createSheet(name);
        int linha = 0;
        boolean headerReady = false;
        for (Object mu : lista) {
            int celula = 0;

            if (!headerReady) {
                PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(mu.getClass());
                HSSFRow linhaRegistro = sheet.createRow(linha++);
                for (PropertyDescriptor pd : pds) {
                    if (pd.getName().equals("class"))
                        continue;
                    HSSFCell cell = linhaRegistro.createCell(celula++);
                    setCell(cell, pd.getName());
                }
                headerReady = true;
            }
            celula = 0;
            HSSFRow linhaRegistro = sheet.createRow(linha++);
            PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(mu.getClass());
            for (PropertyDescriptor pd : pds) {
                if (pd.getName().equals("class"))
                    continue;
                HSSFCell cell = linhaRegistro.createCell(celula++);
                Object val = pd.getReadMethod().invoke(mu);
                setCell(cell, val);
            }
        }
    }

    private void setCell(Cell celula, Object valor) {

        if (valor == null) {
            celula.setCellValue("");
            return;
        }

        if (valor instanceof Integer) {
            celula.setCellValue((((Integer) valor)).doubleValue());

        } else if (valor instanceof Long) {
            celula.setCellValue(((Long) valor).doubleValue());

        } else if (valor instanceof Boolean) {
            celula.setCellValue((Boolean) valor);

        } else if (valor instanceof Float) {
            celula.setCellValue(((Float) valor).doubleValue());

        } else if (valor instanceof BigDecimal) {
            celula.setCellValue(((BigDecimal) valor).doubleValue());

        } else {
            celula.setCellValue(valor.toString());
        }
    }
}
