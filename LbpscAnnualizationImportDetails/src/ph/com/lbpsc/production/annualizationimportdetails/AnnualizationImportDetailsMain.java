package ph.com.lbpsc.production.annualizationimportdetails;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.ibatis.exceptions.PersistenceException;
import org.supercsv.cellprocessor.FmtDate;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvMapReader;
import org.supercsv.io.CsvMapWriter;
import org.supercsv.io.ICsvMapReader;
import org.supercsv.io.ICsvMapWriter;
import org.supercsv.prefs.CsvPreference;
import org.xml.sax.SAXException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import ph.com.lbpsc.production.annualizationimportdetails.data.AnnualizationImportDetailsDao;
import ph.com.lbpsc.production.annualizationimportdetails.model.AnnualizationImportDetails;
import ph.com.lbpsc.production.annualizationimportdetails.view.BrowseAnnualizationImportDetailsController;
import ph.com.lbpsc.production.annualizationimportdetails.view.EditAnnualizationImportDetailsController;
import ph.com.lbpsc.production.annualizationimportdetails.view.UploadAnnualizationImportDetailsController;
import ph.com.lbpsc.production.annualizationitems.AnnualizationItemsMain;
import ph.com.lbpsc.production.annualizationitems.model.AnnualizationItems;
import ph.com.lbpsc.production.classcomparator.ClassComparator;
import ph.com.lbpsc.production.employee.EmployeeMain;
import ph.com.lbpsc.production.employee.model.Employee;
import ph.com.lbpsc.production.importexportfile.ImportExportFileMain;
import ph.com.lbpsc.production.masterclass.MasterMain;
import ph.com.lbpsc.production.searchv2.Search;
import ph.com.lbpsc.production.user.UserMain;
import ph.com.lbpsc.production.util.AlertUtil;
import ph.com.lbpsc.production.util.ErrorLog;
import ph.com.lbpsc.production.util.FxmlUtil;
import ph.com.lbpsc.production.util.ModificationType;
import ph.com.lbpsc.production.util.ProcessingMessage;

public class AnnualizationImportDetailsMain extends MasterMain<AnnualizationImportDetails> {
	private ObservableList<Map.Entry<AnnualizationImportDetails, Boolean>> observableListAnnualizationImportDetails;
	private ObservableList<AnnualizationItems> observableListAnnualizationItems;
	private ObservableList<Employee> observableListEmployee;
	private List<AnnualizationImportDetails> annualizationImportDetailsList;
	private List<AnnualizationImportDetails> importList;

	private EmployeeMain employeeMain;
	private ImportExportFileMain importExportFileMain;

	public AnnualizationImportDetailsMain()
			throws ParserConfigurationException, SAXException, IOException, TransformerException {
		super(AnnualizationImportDetails.class);
	}

	@Override
	public boolean createMainObject(AnnualizationImportDetails annualizationImportDetails) {
		return this.createAnnualizationImportDetails(annualizationImportDetails);
	}

	@Override
	public boolean updateMainObject(AnnualizationImportDetails annualizationImportDetails) {
		return this.updateAnnualizationImportDetails(annualizationImportDetails);
	}

	@Override
	public boolean deleteMainObject(AnnualizationImportDetails annualizationImportDetails) {
		return this.deleteAnnualizationImportDetails(annualizationImportDetails);
	}

	@Override
	public AnchorPane getRootLayout() throws IOException {
		return this.getRootLayoutAnnualizationImportDetails();
	}

	@Override
	public void initializeObjects() {
		try {
			AnnualizationItemsMain annualizationItemsMain = new AnnualizationItemsMain();
			this.observableListAnnualizationImportDetails = FXCollections.observableArrayList();
			this.observableListAnnualizationItems = FXCollections
					.observableArrayList(annualizationItemsMain.getAllAnnualizationItems());
			this.observableListEmployee = FXCollections.observableArrayList();
			this.annualizationImportDetailsList = new ArrayList<>();
			this.importList = new ArrayList<>();

			this.importExportFileMain = new ImportExportFileMain();
			this.employeeMain = new EmployeeMain();
			UserMain userMain = new UserMain();
			this.setUser(userMain.getUserByUserName("JEMAR"));
		} catch (ParserConfigurationException | SAXException | IOException | TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void populateAnnualizationImportDetails(AnnualizationImportDetails annualizationImportDetails) {
		this.getObservableListAnnualizationImportDetails().clear();

		List<AnnualizationImportDetails> annualizationImportDetailsList = this
				.getAnnualizationImportDetailsByDate(annualizationImportDetails);
		if (annualizationImportDetailsList != null && !annualizationImportDetailsList.isEmpty()) {
			LinkedHashMap<AnnualizationImportDetails, Boolean> hashMap = new LinkedHashMap<>();
			for (AnnualizationImportDetails getAnnualizationImportDetails : annualizationImportDetailsList) {
				hashMap.put(getAnnualizationImportDetails, false);
			}
			this.getObservableListAnnualizationImportDetails().addAll(hashMap.entrySet());
		}
	}

	public List<AnnualizationImportDetails> getAllAnnualizationImportDetails() {
		AnnualizationImportDetailsDao annualizationImportDetailsDao = new AnnualizationImportDetailsDao(
				sqlSessionFactory);
		return annualizationImportDetailsDao.getAllData();
	}

	public List<AnnualizationImportDetails> getAnnualizationImportDetails(
			AnnualizationImportDetails annualizationImportDetails) {
		AnnualizationImportDetailsDao annualizationImportDetailsDao = new AnnualizationImportDetailsDao(
				sqlSessionFactory);
		return annualizationImportDetailsDao.getAnnualizationImportDetails(annualizationImportDetails);
	}

	public List<AnnualizationImportDetails> getAnnualizationImportDetailsByDate(
			AnnualizationImportDetails annualizationImportDetails) {
		AnnualizationImportDetailsDao annualizationImportDetailsDao = new AnnualizationImportDetailsDao(
				sqlSessionFactory);
		return annualizationImportDetailsDao.getAnnualizationImportDetailsByDate(annualizationImportDetails);
	}

	public AnnualizationImportDetails getAnnualizationImportDetailsByKey(Integer key) {
		AnnualizationImportDetailsDao annualizationImportDetailsDao = new AnnualizationImportDetailsDao(
				sqlSessionFactory);
		return annualizationImportDetailsDao.getDataByKey(key);
	}

	public boolean createAnnualizationImportDetails(AnnualizationImportDetails annualizationImportDetails) {
		try {
			return (new AnnualizationImportDetailsDao(sqlSessionFactory).createData(annualizationImportDetails) > 0)
					? true : false;
		} catch (PersistenceException e) {
			ErrorLog.showErrorLog(e, this.primaryStage);
		}
		return false;
	}

	public boolean updateAnnualizationImportDetails(AnnualizationImportDetails annualizationImportDetails) {
		try {
			return (new AnnualizationImportDetailsDao(sqlSessionFactory).updateData(annualizationImportDetails) > 0)
					? true : false;
		} catch (PersistenceException e) {
			ErrorLog.showErrorLog(e, this.primaryStage);
		}
		return false;
	}

	public boolean deleteAnnualizationImportDetails(AnnualizationImportDetails annualizationImportDetails) {
		try {
			return (new AnnualizationImportDetailsDao(sqlSessionFactory).deleteData(annualizationImportDetails) > 0)
					? true : false;
		} catch (PersistenceException e) {
			ErrorLog.showErrorLog(e, this.primaryStage);
		}
		return false;
	}

	public boolean deleteMultipleAnnualizationImportDetails(
			List<AnnualizationImportDetails> annualizationImportDetailsList) {
		boolean returnValue = false;
		try {
			AnnualizationImportDetailsDao annualizationDao = new AnnualizationImportDetailsDao(sqlSessionFactory);
			returnValue = (annualizationDao.deleteMultipleData(annualizationImportDetailsList) > 0) ? true : false;
		} catch (PersistenceException e) {
			ErrorLog.showErrorLog(e, this.primaryStage);
		}
		return returnValue;
	}

	public boolean createMultipleAnnualizationImportDetails(
			List<AnnualizationImportDetails> annualizationImportDetailsList) {
		boolean returnValue = false;
		try {
			AnnualizationImportDetailsDao annualizationDao = new AnnualizationImportDetailsDao(sqlSessionFactory);
			returnValue = (annualizationDao.createMultipleData(annualizationImportDetailsList) > 0) ? true : false;
		} catch (PersistenceException e) {
			ErrorLog.showErrorLog(e, this.primaryStage);
		}
		return returnValue;
	}

	public AnchorPane getRootLayoutAnnualizationImportDetails() throws IOException {
		this.initializeObjects();
		FxmlUtil<BrowseAnnualizationImportDetailsController> fxmlUtil = new FxmlUtil<>(
				this.getClass().getResource("view/BrowseAnnualizationImportDetails.fxml"));
		AnchorPane rootLayout = fxmlUtil.getFxmlPane();
		BrowseAnnualizationImportDetailsController rootController = fxmlUtil.getController();
		rootController.setMainApplication(this);
		return rootLayout;
	}

	public boolean showEditAnnualizationImportDetails(AnnualizationImportDetails annualizationImportDetails,
			ModificationType modificationType) {
		try {
			FxmlUtil<EditAnnualizationImportDetailsController> fxmlUtil = new FxmlUtil<>(
					this.getClass().getResource("view/EditAnnualizationImportDetails.fxml"));
			AnchorPane editAnnualizationImportDetailsLayout = fxmlUtil.getFxmlPane();
			EditAnnualizationImportDetailsController editAnnualizationImportDetailsController = fxmlUtil
					.getController();
			editAnnualizationImportDetailsController.setMainApplication(this, modificationType);
			return editAnnualizationImportDetailsController.showEditDialogStage("Edit Annualization Import Details",
					this.primaryStage, editAnnualizationImportDetailsLayout, annualizationImportDetails);
		} catch (IOException e) {
			AlertUtil.showExceptionAlert(e, this.primaryStage);
			e.printStackTrace();
		}
		return false;
	}

	public boolean showUploadAnnualizationImportDetails() {
		try {
			FxmlUtil<UploadAnnualizationImportDetailsController> fxmlUtil = new FxmlUtil<>(
					this.getClass().getResource("view/UploadAnnualizationImportDetails.fxml"));
			AnchorPane uploadAnnualizationDetailsLayout = fxmlUtil.getFxmlPane();
			UploadAnnualizationImportDetailsController editAnnualizationImportDetailsController = fxmlUtil
					.getController();
			editAnnualizationImportDetailsController.setMainApplication(this, ModificationType.ADD);
			return editAnnualizationImportDetailsController.showEditDialogStage("Upload Annualization Import Details",
					this.primaryStage, uploadAnnualizationDetailsLayout, null);
		} catch (IOException e) {
			AlertUtil.showExceptionAlert(e, this.primaryStage);
			e.printStackTrace();
		}
		return false;
	}

	public void getSelectedAnnualizationImportDetails() {
		this.annualizationImportDetailsList.clear();
		if (this.getObservableListAnnualizationImportDetails() != null
				&& !this.getObservableListAnnualizationImportDetails().isEmpty()) {
			for (Entry<AnnualizationImportDetails, Boolean> annualizationImportDetails : this
					.getObservableListAnnualizationImportDetails()) {
				if (annualizationImportDetails.getValue()) {
					this.annualizationImportDetailsList.add(annualizationImportDetails.getKey());
				}
			}
		}
	}

	public boolean isAlreadyProcessed() {
		if (this.annualizationImportDetailsList != null && !this.annualizationImportDetailsList.isEmpty()) {
			for (AnnualizationImportDetails annualizationImportDetails : this.annualizationImportDetailsList) {
				if (annualizationImportDetails.getAnnualization() != null
						&& annualizationImportDetails.getAnnualization().getPrimaryKey() != null) {
					return true;
				}
			}
		}
		return false;
	}

	public Employee showSearchEmployee(ObservableList<Employee> observableListEmploymentHistory, Stage stage) {
		Search<Employee> search = new Search<>();

		search.setObservableListObject(observableListEmploymentHistory);
		search.addTableColumn("Employee ID", "employeeCode");
		search.addTableColumn("Surname", "surname");
		search.addTableColumn("First Name", "firstName");
		search.addTableColumn("Middle Name", "middleName");

		search.addSearchCriteria("Employee ID", "employeeCode", "######");
		search.addSearchCriteria("Surname", "surname", "");
		search.addSearchCriteria("First Name", "firstName", "");
		search.addSearchCriteria("Middle Name", "middleName", "");

		return search.showSearchDialog("Search Employee", stage);
	}

	public void populateEmployee(Date payFrom, Date payTo) {
		this.observableListEmployee.clear();

		List<Employee> employeeList = this.employeeMain.getAllActiveEmployee(payFrom, payTo);
		if (employeeList != null && !employeeList.isEmpty()) {
			this.observableListEmployee.addAll(employeeList);
		}
	}

	public boolean isDuplicate(AnnualizationImportDetails annualizationImportDetails) {
		if (annualizationImportDetails != null) {
			List<AnnualizationImportDetails> annualizationImportDetailsList = this
					.getAnnualizationImportDetails(annualizationImportDetails);
			if (annualizationImportDetailsList != null && !annualizationImportDetailsList.isEmpty()) {
				return true;
			}
		}
		return false;
	}

	public void exportAnnualizationImportDetailsTemplate(Stage stage) {
		try {
			String fileName = System.getProperty("user.home") + "\\Desktop\\AnnualizationImportDetails.csv";
			ICsvMapWriter mapWriter = null;
			try {
				mapWriter = new CsvMapWriter(new FileWriter(fileName), CsvPreference.STANDARD_PREFERENCE);

				final CellProcessor[] processors = new CellProcessor[] { new NotNull(), new FmtDate("MM/dd/yyyy"),
						new FmtDate("MM/dd/yyyy"), new NotNull(), new NotNull() };

				String[] header = new String[] { "EMP_ID", "PAY_FROM", "PAY_TO", "AMOUNT", "DAYS" };

				mapWriter.writeHeader(header);
				Map<String, Object> annualizationUploadMap = new HashMap<String, Object>();
				annualizationUploadMap.put(header[0], "0");
				annualizationUploadMap.put(header[1], new Date());
				annualizationUploadMap.put(header[2], new Date());
				annualizationUploadMap.put(header[3], "0");
				annualizationUploadMap.put(header[4], "0");

				mapWriter.write(annualizationUploadMap, header, processors);
			} finally {
				if (mapWriter != null) {
					mapWriter.close();
					AlertUtil.showAlert("", "", "File exported in " + fileName, AlertType.INFORMATION, stage);
				}
			}
		} catch (IOException e) {
			AlertUtil.showErrorAlert("No path found or File is open.", stage);
		}
	}

	public void uploadAnnualizationImportDetails(AnnualizationItems annualizationItems, Stage stage) {
		ICsvMapReader mapReader = null;
		HashMap<Integer, String> importErrorList = new HashMap<>();
		this.importList.clear();
		try {
			if (this.importExportFileMain.getRootLayoutImportExportFile(false, "csv", stage)) {
				String fileName = this.importExportFileMain.getFileLocation() + "\\"
						+ importExportFileMain.getFileName() + ".csv";

				mapReader = new CsvMapReader(new FileReader(fileName), CsvPreference.STANDARD_PREFERENCE);
				mapReader.getHeader(true);

				String[] header = new String[] { "EMP_ID", "PAY_FROM", "PAY_TO", "AMOUNT", "DAYS" };

				final CellProcessor[] processor = new CellProcessor[] { new NotNull(), new NotNull(), new NotNull(),
						new NotNull(), new NotNull() };

				Map<String, Object> annualizationImportDetailsMap;
				ProcessingMessage.showProcessingMessage(stage);

				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
				while ((annualizationImportDetailsMap = mapReader.read(header, processor)) != null) {
					AnnualizationImportDetails annualizationImportDetails = new AnnualizationImportDetails();
					Employee employee = this.employeeMain.getEmployeeByID(
							Integer.parseInt(annualizationImportDetailsMap.get(header[0]).toString().replace(",", "")));

					if (employee != null) {
						Date payFrom = simpleDateFormat.parse(annualizationImportDetailsMap.get(header[1]).toString());
						Date payTo = simpleDateFormat.parse(annualizationImportDetailsMap.get(header[2]).toString());
						BigDecimal amount = new BigDecimal(annualizationImportDetailsMap.get(header[3]).toString()
								.replace(",", "").replace(" ", ""));
						BigDecimal days = new BigDecimal(annualizationImportDetailsMap.get(header[4]).toString()
								.replace(",", "").replace(" ", ""));

						annualizationImportDetails.setEmployee(employee);
						annualizationImportDetails.setPayFrom(payFrom);
						annualizationImportDetails.setPayTo(payTo);
						annualizationImportDetails.setAmount(amount);
						annualizationImportDetails.setDays(days);
						annualizationImportDetails.setAnnualizationItems(annualizationItems);
						annualizationImportDetails.setUser(this.getUser());
						annualizationImportDetails.setChangedInComputer(this.getComputerName());
						annualizationImportDetails.setChangedOnDate(this.getDateNow());

						if (!this.isDuplicate(annualizationImportDetails)) {
							this.importList.add(annualizationImportDetails);
						} else {
							importErrorList.put(employee.getEmployeeCode(), "Duplicate record");
						}
					} else {
						importErrorList.put(Integer.parseInt(annualizationImportDetailsMap.get(header[0]).toString()),
								"Invalid employee ID");
					}
				}
				ProcessingMessage.closeProcessingMessage();
				boolean isUpload = true;

				if (importErrorList != null && !importErrorList.isEmpty()) {
					this.exportErrorUploadingList(importErrorList, stage);
					if (!this.importList.isEmpty()) {
						if (!AlertUtil.showQuestionAlertBoolean(
								"Found records with error and will be excluded in the import. \r\n Do you want to continue uploading?",
								stage)) {
							isUpload = false;
						}
					}
				}

				if (isUpload) {
					if (!this.importList.isEmpty()) {
						if (this.createMultipleAnnualizationImportDetails(this.importList)) {
							AlertUtil.showSuccessfullAlert("Record/s successfully saved.", stage);
						}
					}
				}
			}
		} catch (IOException | ParseException e) {
			ErrorLog.showErrorLog(e, stage);
			e.printStackTrace();
		}
	}

	public void exportErrorUploadingList(HashMap<Integer, String> importErrorList, Stage stage) {
		try {
			String exportFileName = System.getProperty("user.home") + "\\Desktop\\ListOfInvalidEmployee.csv";
			ICsvMapWriter mapWriter = null;

			mapWriter = new CsvMapWriter(new FileWriter(exportFileName), CsvPreference.STANDARD_PREFERENCE);

			final CellProcessor[] exportProcessors = new CellProcessor[] { new NotNull(), new NotNull() };
			String[] exportHeader = new String[] { "Employee ID", "Remarks" };
			mapWriter.writeHeader(exportHeader);

			for (Map.Entry<Integer, String> mapError : importErrorList.entrySet()) {
				Map<String, Object> adjustmentMap = new HashMap<String, Object>();
				adjustmentMap.put(exportHeader[0], mapError.getKey() == null ? "" : mapError.getKey().toString());
				adjustmentMap.put(exportHeader[1], mapError.getValue() == null ? "" : mapError.getValue());

				mapWriter.write(adjustmentMap, exportHeader, exportProcessors);
			}

			if (mapWriter != null) {
				mapWriter.close();
				AlertUtil.showAlert("", "", "List of employees with error was exported in : " + exportFileName,
						AlertType.WARNING, stage);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean withChangesEmployee(AnnualizationImportDetails firstAnnualizationImportDetails,
			AnnualizationImportDetails secondAnnualizationImportDetails) {
		if (!ClassComparator.compare(firstAnnualizationImportDetails, secondAnnualizationImportDetails,
				"employee.employeeCode")) {
			return true;
		}

		if (!ClassComparator.compare(firstAnnualizationImportDetails, secondAnnualizationImportDetails, "payFrom")) {
			return true;
		}

		if (!ClassComparator.compare(firstAnnualizationImportDetails, secondAnnualizationImportDetails, "payTo")) {
			return true;
		}

		if (!ClassComparator.compare(firstAnnualizationImportDetails, secondAnnualizationImportDetails, "amount")) {
			return true;
		}

		if (!ClassComparator.compare(firstAnnualizationImportDetails, secondAnnualizationImportDetails, "days")) {
			return true;
		}

		return false;
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		super.start(primaryStage);
		this.primaryStage.setTitle("Annualization Import Details");
	}

	public static void main(String[] args) {
		launch(args);
	}

	public ObservableList<Map.Entry<AnnualizationImportDetails, Boolean>> getObservableListAnnualizationImportDetails() {
		return observableListAnnualizationImportDetails;
	}

	public ObservableList<AnnualizationItems> getObservableListAnnualizationItems() {
		return observableListAnnualizationItems;
	}

	public List<AnnualizationImportDetails> getAnnualizationImportDetailsList() {
		return annualizationImportDetailsList;
	}

	public ObservableList<Employee> getObservableListEmployee() {
		return observableListEmployee;
	}
}
