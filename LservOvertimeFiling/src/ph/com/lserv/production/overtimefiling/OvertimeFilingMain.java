package ph.com.lserv.production.overtimefiling;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvMapWriter;
import org.supercsv.io.ICsvMapWriter;
import org.supercsv.prefs.CsvPreference;
import org.xml.sax.SAXException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ph.com.lbpsc.production.approval.ApprovalMain;
import ph.com.lbpsc.production.emailaddress.EmailAddressMain;
import ph.com.lbpsc.production.emailaddress.model.EmailAddress;
import ph.com.lbpsc.production.employee.EmployeeMain;
import ph.com.lbpsc.production.employee.model.Employee;
import ph.com.lbpsc.production.employmenthistory.EmploymentHistoryMain;
import ph.com.lbpsc.production.importexportfile.ImportExportFileMain;
import ph.com.lbpsc.production.masterclass.MasterMain;
import ph.com.lbpsc.production.organizationalstructure.OrganizationalStructureMain;
import ph.com.lbpsc.production.organizationalstructure.model.OrganizationalStructure;
import ph.com.lbpsc.production.user.model.User;
import ph.com.lbpsc.production.util.AlertUtil;
import ph.com.lbpsc.production.util.DateUtil;
import ph.com.lbpsc.production.util.ErrorLog;
import ph.com.lbpsc.production.util.FxmlUtil;
import ph.com.lbpsc.production.util.ModificationType;
import ph.com.lbpsc.production.util.NotificationUtil;
import ph.com.lbpsc.production.util.NotificationUtil.NotificationType;
import ph.com.lbpsc.production.util.ProcessingMessage;
import ph.com.lbpsc.production.util.StringToFileUtil;
import ph.com.lserv.production.overtimefiling.data.OvertimeFilingDao;
import ph.com.lserv.production.overtimefiling.model.OvertimeFiling;
import ph.com.lserv.production.overtimefiling.view.BrowseOvertimeFilingController;
import ph.com.lserv.production.overtimefiling.view.EditOvertimeFilingController;
import ph.com.lserv.production.overtimefiling.view.PrintOvertimeFilingController;
import ph.com.lserv.production.overtimefilingtrail.OvertimeFilingTrailMain;
import ph.com.lserv.production.overtimefilingtrail.model.OvertimeFilingTrail;

public class OvertimeFilingMain extends MasterMain<OvertimeFiling> {
	EmploymentHistoryMain employmentHistoryMain;
	ObservableList<OvertimeFiling> observableListTimeFiling;
	List<OvertimeFiling> listOvertimeFiling;
	OvertimeFiling overtimeFiling;
	BrowseOvertimeFilingController rootController;
	OvertimeFilingTrailMain overtimeFilingTrailMain;
	OrganizationalStructureMain organizationStructureMain;
	EmailAddressMain emailAddressMain;
	List<OrganizationalStructure> userParentList;
	OvertimeFiling origValue;
	EmployeeMain employeeMain;

	ApprovalMain<OvertimeFilingTrail> approvalMain;
	List<OvertimeFilingTrail> trailToApproveList;
	OvertimeFilingTrail selectedOvertimeTrail;

	Runnable remarksEntryRunnable;

	public OvertimeFilingMain() throws ParserConfigurationException, SAXException, IOException, TransformerException {
		super(OvertimeFiling.class);
	}

	public static void main(String[] args) {
		launch(args);
	}

	public OvertimeFilingMain(Class<OvertimeFiling> classType, Runnable remarksEntryRunnable)
			throws ParserConfigurationException, SAXException, IOException, TransformerException {
		super(classType);
		this.remarksEntryRunnable = remarksEntryRunnable;
	}

	@Override
	public boolean createMainObject(OvertimeFiling overtimeFiling) {
		return new OvertimeFilingDao(sqlSessionFactory).createData(overtimeFiling) > 0;
	}

	@Override
	public boolean updateMainObject(OvertimeFiling overtimeFiling) {
		return new OvertimeFilingDao(sqlSessionFactory).updateData(overtimeFiling) > 0;
	}

	@Override
	public boolean deleteMainObject(OvertimeFiling overtimeFiling) {
		return new OvertimeFilingDao(sqlSessionFactory).deleteData(overtimeFiling) > 0;
	}

	public List<OvertimeFiling> getAllData() {
		return new OvertimeFilingDao(sqlSessionFactory).getAllData();
	}

	public List<OvertimeFiling> getUserData(int id) {
		return new OvertimeFilingDao(sqlSessionFactory).getUserData(id);
	}

	public List<OvertimeFiling> getAllOvertimeSortedAscending() {
		return new OvertimeFilingDao(sqlSessionFactory).getAllOvertimeSortedAscending();
	}

	public OvertimeFiling getDataById(int id) {
		return new OvertimeFilingDao(sqlSessionFactory).getDataById(id);
	}

	public OvertimeFiling getMaxFiledOvertime() {
		return new OvertimeFilingDao(sqlSessionFactory).getMaxFiledOvertime();
	}

	public boolean isWithPendingOvertimeFilingTrail(OvertimeFiling overtimeFiling) {
		return false;
	}

	public List<OvertimeFiling> getOvertimeByCutoffDate(String dateTimeFrom, String dateTimeTo) {
		return new OvertimeFilingDao(sqlSessionFactory).getOvertimeByCutoffDate(dateTimeFrom, dateTimeTo);
	}

	public List<OvertimeFiling> getAllOvertimeByOvertimeFromAndPrikeyUser(String dateFrom, Integer prikeyUser) {
		return new OvertimeFilingDao(sqlSessionFactory).getAllOvertimeByOvertimeFromAndPrikeyUser(dateFrom, prikeyUser);
	}
	
	public List<OvertimeFiling> getAllOvertimeByOvertimeToAndPrikeyUser(String dateTo, Integer prikeyUser){
		return new OvertimeFilingDao(sqlSessionFactory).getAllOvertimeByOvertimeToAndPrikeyUser(dateTo, prikeyUser);
	}

	public void onRemarks() {
		// this.overtimeFilingApprovalMain.setObjectSelectedForRemarks();

		if (this.getSelectedOvertimeTrail() != null) {
			System.out.println(this.getSelectedOvertimeTrail().toString());

			Stage stage = new Stage();
			stage.initStyle(StageStyle.UTILITY);
			stage.initModality(Modality.WINDOW_MODAL);
			stage.setTitle("Add remarks - Overtime Filing");
			stage.initOwner(this.getPrimaryStage());
			stage.setResizable(false);

			if (this.getApprovalMain().showRemarksDialog(stage)) {
				System.out.println("pasok");
				this.remarksEntry();
			}
		} else {
			System.out.println("null");
		}

	}

	public boolean isSeniorAssociateUser(Employee employee) {
		List<Employee> seniorAssociateEmployeeList = this.getEmployeeMain().getAllSeniorAssociateEmployees();

		if (employee != null) {
			// "approval records user"
			for (Employee employeeList : seniorAssociateEmployeeList) {
				if (employeeList.getEmployeeCode().compareTo(employee.getEmployeeCode()) == 0) {
					return true;
				}
			}
		} else if (employee == null) {
			// "system user"
			for (Employee employeeList : seniorAssociateEmployeeList) {

				if (employeeList.getEmployeeCode()
						.compareTo(this.getUser().getEmploymentHistory().getEmployee().getEmployeeCode()) == 0) {
					return true;
				}
			}
		}

		return false;
	}

	public BigDecimal getUsedOtHours(Timestamp timeFrom, Timestamp timeTo, User user) {
		List<OvertimeFiling> userApprovedOvertimeList = new ArrayList<>();
		BigDecimal usedOtHours = BigDecimal.ZERO;

		if (user != null) {
			userApprovedOvertimeList = this.getUserData(user.getPrimaryKey());
		} else {
			userApprovedOvertimeList = this.getUserData(this.getUser().getPrimaryKey());
		}

		String latestFiledOvertimeMonth = DateUtil.getMonth(timeFrom);
		Integer latestFiledOvertimeYear = DateUtil.getYear(timeFrom);

		for (OvertimeFiling overtimeFiling : userApprovedOvertimeList) {
			String monthFrom = DateUtil.getMonth(overtimeFiling.getDateTimeFrom());
			String monthTo = DateUtil.getMonth(overtimeFiling.getDateTimeTo());
			Integer yearFrom = DateUtil.getYear(overtimeFiling.getDateTimeFrom());
			Integer yearTo = DateUtil.getYear(overtimeFiling.getDateTimeTo());

			if ((monthFrom.equals(latestFiledOvertimeMonth) && monthTo.equals(latestFiledOvertimeMonth))
					&& (yearFrom.equals(latestFiledOvertimeYear) && yearTo.equals(latestFiledOvertimeYear))) {
				usedOtHours = usedOtHours.add(overtimeFiling.getTotalOtHours());
			}
		}
		return usedOtHours;
	}

	public BigDecimal[] computeExceededHrAndMin(BigDecimal diffOtHoursInMillis) {
		BigDecimal results[] = new BigDecimal[3];
		results[0] = BigDecimal.ZERO;
		results[1] = BigDecimal.ZERO;
		results[2] = BigDecimal.ZERO;
		BigDecimal exceededOtHours = BigDecimal.ZERO;

		BigDecimal diffOtHoursInSeconds = diffOtHoursInMillis.divide(new BigDecimal(1000), 4, RoundingMode.HALF_UP);
		BigDecimal diffOtHours = diffOtHoursInSeconds.divide(new BigDecimal(3600), 4, RoundingMode.HALF_UP);
		exceededOtHours = diffOtHours;

		BigDecimal getHr = exceededOtHours.divide(new BigDecimal(1), 0, RoundingMode.FLOOR);
		Double getMinToDouble = exceededOtHours.doubleValue();
		Double computeMin = 60 * (getMinToDouble % 1); // only computes the fractional decimal part
		BigDecimal getMin = BigDecimal.valueOf(computeMin);
		getMin = getMin.setScale(0, RoundingMode.HALF_UP);
		results[0] = getHr;
		results[1] = getMin;
		results[2] = exceededOtHours;

		return results;
	}

	public BigDecimal[] getExceededOtHours(Timestamp timeFrom, Timestamp timeTo, User user) {
		BigDecimal usedOtHours = BigDecimal.ZERO;
		BigDecimal results[] = new BigDecimal[3];
		results[0] = BigDecimal.ZERO;
		results[1] = BigDecimal.ZERO;
		results[2] = BigDecimal.ZERO;

		BigDecimal inputTotalHours = this.getTotalNumberOfHours(timeFrom, timeTo);

		if (user != null) {
			usedOtHours = this.getUsedOtHours(timeFrom, timeTo, user);
		} else {
			usedOtHours = this.getUsedOtHours(timeFrom, timeTo, null);
		}

		BigDecimal sumOtHours = inputTotalHours.add(usedOtHours);

		if (usedOtHours.compareTo(new BigDecimal(40)) < 0) {
			if (sumOtHours.compareTo(new BigDecimal(40)) > 0) {
				BigDecimal sumOtHoursToMillis = sumOtHours.multiply(new BigDecimal(3600000));
				BigDecimal diffOtHoursInMillis = sumOtHoursToMillis.subtract(new BigDecimal(144000000));

				results = this.computeExceededHrAndMin(diffOtHoursInMillis);

				return results;
			}
		} else {
			if (sumOtHours.compareTo(new BigDecimal(40)) > 0) {
				BigDecimal sumOtHoursToMillis = sumOtHours.multiply(new BigDecimal(3600000));
				BigDecimal usedOtHoursToMillis = usedOtHours.multiply(new BigDecimal(3600000));
				BigDecimal diffOtHoursInMillis = sumOtHoursToMillis.subtract(usedOtHoursToMillis);

				results = this.computeExceededHrAndMin(diffOtHoursInMillis);

				return results;
			}
		}

		return results;
	}

	public boolean isWithRemainingOtHours(Timestamp timeFrom, Timestamp timeTo, User user) {
		BigDecimal[] exceededOtHours = new BigDecimal[3];
		Arrays.fill(exceededOtHours, new BigDecimal(0));

		if (user != null) {
			if (this.isSeniorAssociateUser(user.getEmploymentHistory().getEmployee())) {
				exceededOtHours = this.getExceededOtHours(timeFrom, timeTo, user);
			}
		} else {
			if (this.isSeniorAssociateUser(null)) {
				exceededOtHours = this.getExceededOtHours(timeFrom, timeTo, null);
			}
		}
		if (exceededOtHours[0].compareTo(new BigDecimal(0)) > 0
				|| exceededOtHours[1].compareTo(new BigDecimal(0)) > 0) {
			return false;
		}
		return true;
	}

	public BigDecimal getTotalNumberOfHours(Timestamp timeFrom, Timestamp timeTo) {

		long millisResult = timeTo.getTime() - timeFrom.getTime();
		BigDecimal seconds = BigDecimal.valueOf(millisResult).divide(new BigDecimal(1000), 4, RoundingMode.HALF_UP);
		BigDecimal hours = seconds.divide(new BigDecimal(3600), 4, RoundingMode.HALF_UP);

		// if ot exceeds 3hrs, must subtract 30mins to total ot hr
		if (hours.compareTo(new BigDecimal(3)) > 0) {
			BigDecimal newSeconds = seconds.subtract(new BigDecimal(1800));
			BigDecimal newHour = newSeconds.divide(new BigDecimal(3600), 4, RoundingMode.HALF_UP);
			return newHour;
		}

		return hours;
	}

	@Override
	public AnchorPane getRootLayout() throws IOException {
		return this.showBrowseOvertimeFiling();
	}

	public AnchorPane showBrowseOvertimeFiling() throws IOException {
		this.initializeObjects();
		FxmlUtil<BrowseOvertimeFilingController> fxmlUtil = new FxmlUtil<>(
				this.getClass().getResource("view/BrowseOvertimeFiling.fxml"));
		AnchorPane browseLayout = fxmlUtil.getFxmlPane();
		BrowseOvertimeFilingController controller = fxmlUtil.getController();
		controller.setMainApplication(this);
		this.rootController = fxmlUtil.getController();
		this.rootController.setMainApplication(this);

		return browseLayout;
	}

	public void handleEmailNotification(OvertimeFiling overtimeFiling) {
		List<OrganizationalStructure> parentList = this.getUserParentList();

		if (parentList != null && !parentList.isEmpty()) {
			try {
				SimpleDateFormat dateFormat = new SimpleDateFormat("MMddyyyyHHmmss");
				List<Employee> employeeNoEmailList = new ArrayList<>();

				String path = NotificationUtil.getNotificationPath(NotificationType.APPROVAL_NOTIFICATION)
						+ "OvertimeApprovalPayroll_" + dateFormat.format(new Date());
				if (this.generateEmailConfiguration(parentList, path, employeeNoEmailList, overtimeFiling)) {
					AlertUtil.showSuccessfullAlert("Approval sent successfully.", this.getPrimaryStage());
				} else {
					AlertUtil.showErrorAlert("Error sending approval.", this.getPrimaryStage());
				}

				if (employeeNoEmailList != null && !employeeNoEmailList.isEmpty()) {
					if (AlertUtil.showQuestionAlertBoolean(
							"Employees without email address found. Do you want to export list?",
							this.getPrimaryStage())) {
						this.exportEmployeeNoEmailAddress(employeeNoEmailList);
					}
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			AlertUtil.showErrorAlert("Error sending approval. User has no approver.", this.getPrimaryStage());
		}

	}

	public boolean generateEmailConfiguration(List<OrganizationalStructure> userParentList, String fileLocation,
			Collection<Employee> employeeNoEmailList, OvertimeFiling overtimeFiling) {
		if (userParentList != null && !userParentList.isEmpty()) {

			if ((new File(fileLocation)).mkdirs()) {
				List<EmailAddress> emailAddressList = new ArrayList<>();
				List<String> emailConfigurationList = new ArrayList<>();
				List<Integer> employeeIdList = new ArrayList<>();

				userParentList.forEach(c -> {
					employeeIdList.add(c.getEmployee().getEmployeeCode());
				});

				emailAddressList.clear();
				emailAddressList = this.emailAddressMain.getAllEmailAddressByEmployeeIdList(employeeIdList);

				if (!emailAddressList.isEmpty() && emailAddressList != null) {
					emailAddressList.forEach(C -> {
						for (OrganizationalStructure parentList : userParentList) {

							if (C.getEmployee().getEmployeeCode()
									.compareTo(parentList.getEmployee().getEmployeeCode()) == 0) {
								if (C.getElectronicMailAddress() != null
										&& C.getElectronicMailAddress().compareTo("") != 0) {
									parentList.getEmployee().setEmailAddress(C.getElectronicMailAddress());
								}
							}
						}
					});
				}

				ProcessingMessage.showProcessingMessage(this.getPrimaryStage(), "Generating Overtime Filing Approval");
				for (OrganizationalStructure parentList : userParentList) {
					if (parentList.getEmployee().getEmailAddress() != null
							&& !parentList.getEmployee().getEmailAddress().trim().equals("")) {
						try {
							ObservableList<OrganizationalStructure> overtimeForEmailList = FXCollections
									.observableArrayList(userParentList.stream()
											.filter(P -> P.getEmployee().getEmployeeCode()
													.equals(parentList.getEmployee().getEmployeeCode()))
											.collect(Collectors.toList()));

							if (overtimeForEmailList != null && !overtimeForEmailList.isEmpty()) {
								String emailConfigurationRow = "";
								String emailSubject = "Request for Overtime Approval";
								SimpleDateFormat dateTimeFormat = new SimpleDateFormat("MMM. dd, yyyy HH:mm:ss");

								emailConfigurationRow = parentList.getEmployee().getEmailAddress().trim() + "#"
										+ emailSubject + "#" + "Overtime filing has been made by "
										+ this.getUser().getEmploymentHistory().getEmployee().getEmployeeFullName()
										+ " from IFMIS New System on " + new Date() + "#"
										+ " Overtime Date and Time of: "
										+ dateTimeFormat.format(overtimeFiling.getDateTimeFrom()) + " - "
										+ dateTimeFormat.format(overtimeFiling.getDateTimeTo()) + "#"
										+ " Accomplishments: " + overtimeFiling.getWorkDone();

								emailConfigurationList.add(emailConfigurationRow);
							}
						} catch (Exception e) {
							e.printStackTrace();
							return false;
						}
					} else {
						employeeNoEmailList.add(parentList.getEmployee());
					}
				}
				ProcessingMessage.closeProcessingMessage();

				if (emailConfigurationList != null & emailConfigurationList.size() > 0) {
					StringToFileUtil.writeListToFile(fileLocation + "/emailconfiguration.email",
							emailConfigurationList);
				}
			}
		}
		return true;
	}

	public void exportEmployeeNoEmailAddress(List<Employee> employeeNoEmailList) {
		ImportExportFileMain importExportFileMain = new ImportExportFileMain();
		try {
			if (importExportFileMain.getRootLayoutImportExportFile(true, "csv", this.getPrimaryStage())) {
				String fileName = importExportFileMain.getFileLocation() + "\\" + importExportFileMain.getFileName()
						+ ".csv";

				ICsvMapWriter mapWriter = null;
				boolean isSuccessfulExport = true;

				try {
					mapWriter = new CsvMapWriter(new FileWriter(fileName), CsvPreference.STANDARD_PREFERENCE);
					final CellProcessor[] processors = new CellProcessor[] { new NotNull(), new NotNull() };

					String[] header = new String[] { "Employee ID", "Employee Name" };
					mapWriter.writeHeader(header);
					Map<String, Object> employeeDetailsMap = new HashMap<String, Object>();

					ProcessingMessage.showProcessingMessage(this.getPrimaryStage());
					for (Employee employee : employeeNoEmailList) {
						if (employee != null) {
							employeeDetailsMap.put(header[0], employee.getEmployeeCode());
							employeeDetailsMap.put(header[1], employee.getEmployeeFullName());

							mapWriter.write(employeeDetailsMap, header, processors);
						}
					}
					ProcessingMessage.closeProcessingMessage();
				} catch (Exception e) {
					isSuccessfulExport = false;
					AlertUtil.showErrorAlert("Export not successful.", this.getPrimaryStage());
					ErrorLog.showErrorLog(e, this.getPrimaryStage());
				} finally {
					if (mapWriter != null) {
						mapWriter.close();
					}
				}

				if (isSuccessfulExport) {
					AlertUtil.showSuccessfullAlert("Export Complete.", this.getPrimaryStage());
				} else {
					File file = new File(fileName);
					if (file.exists()) {
						file.delete();
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean showEditOvertimeFiling(OvertimeFiling overtimeFiling, ModificationType modificationType) {
		try {
			FxmlUtil<EditOvertimeFilingController> fxmlUtil = new FxmlUtil<>(
					this.getClass().getResource("view/EditOvertimeFiling.fxml"));
			AnchorPane editLayout = fxmlUtil.getFxmlPane();
			EditOvertimeFilingController controller = fxmlUtil.getController();
			controller.setMainApplication(this, modificationType);
			return controller.showEditDialogStage(modificationType + " Overtime", this.primaryStage, editLayout,
					overtimeFiling);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void showPrintOvertimeFiling() {
		try {
			FxmlUtil<PrintOvertimeFilingController> fxmlUtil = new FxmlUtil<>(
					this.getClass().getResource("view/PrintOvertimeFiling.fxml"));
			AnchorPane printLayout = fxmlUtil.getFxmlPane();

			Stage dialogStage = new Stage();
			dialogStage.initStyle(StageStyle.UTILITY);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(this.primaryStage);
			dialogStage.setTitle("Generate Report - Overtime Filing");
			dialogStage.setResizable(false);

			PrintOvertimeFilingController controller = fxmlUtil.getController();
			dialogStage.setScene(new Scene(printLayout));

			controller.setMainApplication(this);
			controller.setStage(dialogStage);

			dialogStage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void initializeObjects() {
		try {
			this.employeeMain = new EmployeeMain();
			this.origValue = new OvertimeFiling();
			this.userParentList = new ArrayList<>();
			this.emailAddressMain = new EmailAddressMain();
			this.organizationStructureMain = new OrganizationalStructureMain();
			this.employmentHistoryMain = new EmploymentHistoryMain();
			this.observableListTimeFiling = FXCollections.observableArrayList();
			this.listOvertimeFiling = new ArrayList<>();
			this.overtimeFilingTrailMain = new OvertimeFilingTrailMain();
			this.approvalMain = new ApprovalMain<>();
			this.approvalMain.initializeObjects();
			this.trailToApproveList = new ArrayList<>();
			this.selectedOvertimeTrail = new OvertimeFilingTrail();
		} catch (ParserConfigurationException | SAXException | IOException | TransformerException e) {
			e.printStackTrace();
		}
	}

	public List<OrganizationalStructure> getUserParentList() {
		return this.userParentList = this.getOrganizationStructureMain()
				.getUserParentOrganizationalStructureByEmployeeCode(
						this.getUser().getEmploymentHistory().getEmployee().getEmployeeCode());
	}

	public void populateObservableList() {
		this.listOvertimeFiling = this.getUserData(this.getUser().getPrimaryKey());
		this.observableListTimeFiling.setAll(listOvertimeFiling);
	}

	public void remarksEntry() {
		System.out.println("remarksEntry");
		if (this.remarksEntryRunnable != null) {
			System.out.println("runnablenotnull");
			this.remarksEntryRunnable.run();
		}
	}

	public OvertimeFilingTrailMain getOvertimeFilingTrailMain() {
		return overtimeFilingTrailMain;
	}

	public void setOvertimeFilingTrailMain(OvertimeFilingTrailMain overtimeFilingTrailMain) {
		this.overtimeFilingTrailMain = overtimeFilingTrailMain;
	}

	public EmploymentHistoryMain getEmploymentHistoryMain() {
		return employmentHistoryMain;
	}

	public void setEmploymentHistoryMain(EmploymentHistoryMain employmentHistoryMain) {
		this.employmentHistoryMain = employmentHistoryMain;
	}

	public ObservableList<OvertimeFiling> getObservableListTimeFiling() {
		return observableListTimeFiling;
	}

	public void setObservableListTimeFiling(ObservableList<OvertimeFiling> observableListTimeFiling) {
		this.observableListTimeFiling = observableListTimeFiling;
	}

	public List<OvertimeFiling> getListOvertimeFiling() {
		return listOvertimeFiling;
	}

	public void setListOvertimeFiling(List<OvertimeFiling> listOvertimeFiling) {
		this.listOvertimeFiling = listOvertimeFiling;
	}

	public OvertimeFiling getOvertimeFiling() {
		return overtimeFiling;
	}

	public void setOvertimeFiling(OvertimeFiling overtimeFiling) {
		this.overtimeFiling = overtimeFiling;
	}

	public BrowseOvertimeFilingController getRootController() {
		return rootController;
	}

	public void setRootController(BrowseOvertimeFilingController rootController) {
		this.rootController = rootController;
	}

	public OrganizationalStructureMain getOrganizationStructureMain() {
		return organizationStructureMain;
	}

	public void setOrganizationStructureMain(OrganizationalStructureMain organizationStructureMain) {
		this.organizationStructureMain = organizationStructureMain;
	}

	public void setUserParentList(List<OrganizationalStructure> userParentList) {
		this.userParentList = userParentList;
	}

	public OvertimeFiling getOrigValue() {
		return origValue;
	}

	public void setOrigValue(OvertimeFiling origValue) {
		this.origValue = origValue;
	}

	public EmailAddressMain getEmailAddressMain() {
		return emailAddressMain;
	}

	public void setEmailAddressMain(EmailAddressMain emailAddressMain) {
		this.emailAddressMain = emailAddressMain;
	}

	public EmployeeMain getEmployeeMain() {
		return employeeMain;
	}

	public void setEmployeeMain(EmployeeMain employeeMain) {
		this.employeeMain = employeeMain;
	}

	public ApprovalMain<OvertimeFilingTrail> getApprovalMain() {
		return approvalMain;
	}

	public void setApprovalMain(ApprovalMain<OvertimeFilingTrail> approvalMain) {
		this.approvalMain = approvalMain;
	}

	public List<OvertimeFilingTrail> getTrailToApproveList() {
		return trailToApproveList;
	}

	public void setTrailToApproveList(List<OvertimeFilingTrail> trailToApproveList) {
		this.trailToApproveList = trailToApproveList;
	}

	public Runnable getRemarksEntryRunnable() {
		return remarksEntryRunnable;
	}

	public void setRemarksEntryRunnable(Runnable remarksEntryRunnable) {
		this.remarksEntryRunnable = remarksEntryRunnable;
	}

	public OvertimeFilingTrail getSelectedOvertimeTrail() {
		return selectedOvertimeTrail;
	}

	public void setSelectedOvertimeTrail(OvertimeFilingTrail selectedOvertimeTrail) {
		this.selectedOvertimeTrail = selectedOvertimeTrail;
	}

}
