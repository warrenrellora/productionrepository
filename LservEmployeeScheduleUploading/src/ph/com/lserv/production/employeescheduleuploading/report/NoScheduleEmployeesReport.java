package ph.com.lserv.production.employeescheduleuploading.report;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import ph.com.lbpsc.production.accountsreceivablereport.report.GenerateArReport;
import ph.com.lbpsc.production.client.model.Client;
import ph.com.lbpsc.production.department.model.Department;
import ph.com.lbpsc.production.employee.model.Employee;
import ph.com.lbpsc.production.util.ProcessingMessage;
import ph.com.lserv.production.employeescheduleuploading.EmployeeScheduleUploadingMain;

public class NoScheduleEmployeesReport extends GenerateArReport<EmployeeScheduleUploadingMain> {
	List<String> headerList = new ArrayList<>();

	public NoScheduleEmployeesReport(EmployeeScheduleUploadingMain mainApplication) {
		super(mainApplication);
	}

	public void generateWorkbook(XSSFWorkbook workbook) {
		XSSFSheet sheet = workbook.createSheet("No Schedule Employees");
		sheet.setColumnWidth(0, 30);

		// DataFormat dataFormat = workbook.createDataFormat();
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

		this.createCell(sheet.createRow(rowCount), columnCount++, "Employees without schedules", headerCenterStyle);
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));
		rowCount++;
		columnCount = 0;

		this.headerList.add("Employee ID");
		this.headerList.add("Full Name");
		this.headerList.add("Client");
		this.headerList.add("Department");

		this.createHeader(headerList, columnCount, sheet.createRow(rowCount), sheet, rowCount);
		rowCount++;
		columnCount = 0;

		for (Integer employeeId : this.mainApplication.getListEmpIdNoSched()) {

			Employee employee = mainApplication.getEmployeeMain().getEmployeeByID(employeeId);

			createDetails(employee, columnCount, sheet.createRow(rowCount++));
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

	public void createDetails(Employee employeeScheduleUploading, int columnCount, Row rowDetails) {

		Department department = this.mainApplication.getDepartmentMain()
				.getDepartmentByCode(employeeScheduleUploading.getDepartment());
		Client client = this.mainApplication.getClientMain().getClientByCode(employeeScheduleUploading.getClient());

		this.createCell(rowDetails, columnCount++, employeeScheduleUploading.getEmployeeCode(), defaultStyle);
		this.createCell(rowDetails, columnCount++, employeeScheduleUploading.getEmployeeFullName(), defaultStyle);
		this.createCell(rowDetails, columnCount++, client.getClientName(), defaultStyle);
		this.createCell(rowDetails, columnCount++, department == null ? "" : department.getDepartmentName(),
				defaultStyle);
	}

	@Override
	public void generateExcel(XSSFWorkbook workbook) throws Exception {
		// TODO Auto-generated method stub
		ProcessingMessage.showProcessingMessage(() -> this.generateWorkbook(workbook),
				this.mainApplication.getPrimaryStage(), "Generating excel file.");
	}

}
