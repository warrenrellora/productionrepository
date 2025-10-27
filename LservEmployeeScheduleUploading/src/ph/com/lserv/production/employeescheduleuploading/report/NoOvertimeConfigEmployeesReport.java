package ph.com.lserv.production.employeescheduleuploading.report;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import ph.com.lbpsc.production.accountsreceivablereport.report.GenerateArReport;
import ph.com.lbpsc.production.client.model.Client;
import ph.com.lbpsc.production.department.model.Department;
import ph.com.lbpsc.production.employmenthistory.model.EmploymentHistory;
import ph.com.lbpsc.production.util.ProcessingMessage;
import ph.com.lserv.production.employeescheduleuploading.EmployeeScheduleUploadingMain;

public class NoOvertimeConfigEmployeesReport extends GenerateArReport<EmployeeScheduleUploadingMain> {
	List<String> headerList = new ArrayList<>();

	public NoOvertimeConfigEmployeesReport(EmployeeScheduleUploadingMain mainApplication) {
		super(mainApplication);
		// TODO Auto-generated constructor stub
	}

	public void generateWorkbook(XSSFWorkbook workbook) {
		XSSFSheet sheet = workbook.createSheet("No Overtime Config Employees");
		sheet.setColumnWidth(0, 30);

		this.initializeStyle(workbook, 10, "Courier New");

		headerCenterStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		headerCenterStyle.setBorderBottom((short) 1);
		headerCenterStyle.setBorderTop((short) 1);
		headerCenterStyle.setBorderLeft((short) 1);
		headerCenterStyle.setBorderRight((short) 1);

		defaultStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		defaultStyle.setBorderBottom((short) 1);
		defaultStyle.setBorderTop((short) 1);
		defaultStyle.setBorderLeft((short) 1);
		defaultStyle.setBorderRight((short) 1);

		int rowCount = 0;
		int columnCount = 0;

		this.createCell(sheet.createRow(rowCount), columnCount++, "Employees without overtime config",
				headerCenterStyle);
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));
		rowCount++;
		columnCount = 0;

		this.headerList.add("Employee ID");
		this.headerList.add("Full Name");
		this.headerList.add("Client");
		this.headerList.add("Department");
		this.headerList.add("Overtime Type");

		this.createHeader(headerList, columnCount, sheet.createRow(rowCount), sheet, rowCount);
		rowCount++;
		columnCount = 0;

		for (Map.Entry<Integer, HashSet<String>> entry : this.mainApplication.getEmployeesWithoutOvertimeConfigHashMap()
				.entrySet()) {

			EmploymentHistory employmentHistory = this.mainApplication.getEmploymentHistoryMain()
					.getEmploymentHistoryMaxByEmployeeCode(entry.getKey());

			for (String overtimeTypeNotEncoded : entry.getValue()) {
				this.createDetails(employmentHistory, overtimeTypeNotEncoded, columnCount, sheet.createRow(rowCount++));
			}
		}

		for (int i = 0; i < headerList.size(); i++) {
			sheet.autoSizeColumn(i, true);
		}

	}

	public void createHeader(List<String> headerList, int columnCount, Row rowDetails, XSSFSheet sheet, int rowCount) {
		for (String header : headerList) {
			this.createCell(rowDetails, columnCount++, header, headerCenterStyle);
		}
	}

	public void createDetails(EmploymentHistory employmentHistory, String overtimeTypeNotEncoded, int columnCount,
			Row rowDetails) {

		Department department = employmentHistory.getDepartment();
		Client client = employmentHistory.getClient();

		this.createCell(rowDetails, columnCount++, employmentHistory.getEmployee().getEmployeeCode(), defaultStyle);
		this.createCell(rowDetails, columnCount++, employmentHistory.getEmployee().getEmployeeFullName(), defaultStyle);
		this.createCell(rowDetails, columnCount++, client.getClientName(), defaultStyle);
		this.createCell(rowDetails, columnCount++, department == null ? "" : department.getDepartmentName(),
				defaultStyle);
		this.createCell(rowDetails, columnCount++, overtimeTypeNotEncoded, defaultStyle);
	}

	@Override
	public void generateExcel(XSSFWorkbook workbook) throws Exception {
		// TODO Auto-generated method stub
		ProcessingMessage.showProcessingMessage(() -> this.generateWorkbook(workbook),
				this.mainApplication.getPrimaryStage(), "Generating excel file.");
	}

}
