package ph.com.lserv.production.scheduleencoding.view;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvException;
import org.supercsv.io.CsvMapWriter;
import org.supercsv.io.ICsvMapWriter;
import org.supercsv.prefs.CsvPreference;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import ph.com.lbpsc.production.client.model.Client;
import ph.com.lbpsc.production.custom.controls.autofillcombobox.AutoFillComboBox;
import ph.com.lbpsc.production.custom.controls.validatedtextfield.ValidatedTextField;
import ph.com.lbpsc.production.employee.model.Employee;
import ph.com.lbpsc.production.employmenthistory.model.EmploymentHistory;
import ph.com.lbpsc.production.importexportfile.ImportExportFileMain;
import ph.com.lbpsc.production.masterclass.MasterBrowseController;
import ph.com.lbpsc.production.overtimetype.model.OvertimeType;
import ph.com.lbpsc.production.searchv2.Search;
import ph.com.lbpsc.production.util.AlertUtil;
import ph.com.lbpsc.production.util.DateUtil;
import ph.com.lbpsc.production.util.ModificationType;
import ph.com.lbpsc.production.util.ObjectCopyUtil;
import ph.com.lbpsc.production.util.ProcessingMessage;
import ph.com.lbpsc.production.util.ProgressUtil;
import ph.com.lbpsc.production.util.TableColumnUtil;
import ph.com.lbpsc.production.util.TableViewUtil;
import ph.com.lserv.production.employeescheduleencoding.model.EmployeeScheduleEncoding;
import ph.com.lserv.production.employeescheduleencodingirregular.model.EmployeeScheduleEncodingIrregular;
import ph.com.lserv.production.employeescheduleencodingovertime.model.EmployeeScheduleEncodingOvertime;
import ph.com.lserv.production.scheduleencoding.ScheduleEncodingMain;
import ph.com.lserv.production.scheduleencoding.model.ScheduleEncoding;
import ph.com.lserv.production.scheduleencodingreference.model.ScheduleEncodingReference;

public class BrowseScheduleEncodingController extends MasterBrowseController<ScheduleEncoding, ScheduleEncodingMain> {
	EditScheduleEncodingController editController;
	List<ValidatedTextField> textFieldList = new ArrayList<>();
	List<ValidatedTextField> textFieldHeaderList = new ArrayList<>();
	List<EmployeeScheduleEncodingOvertime> listEmployeeScheduleOvertime = new ArrayList<>();
	List<EmployeeScheduleEncodingOvertime> listEmployeeScheduleOvertimeADD = new ArrayList<>();
	List<EmployeeScheduleEncodingOvertime> listEmployeeScheduleOvertimeUPDATE = new ArrayList<>();
	List<EmployeeScheduleEncodingOvertime> listEmployeeScheduleOvertimeDELETE = new ArrayList<>();
	List<ScheduleEncoding> listScheduleEncoding = new ArrayList<>();
	List<ScheduleEncoding> listScheduleEncodingADD = new ArrayList<>();
	List<ScheduleEncoding> listScheduleEncodingUPDATE = new ArrayList<>();
	List<ScheduleEncoding> listScheduleEncodingDELETE = new ArrayList<>();
	List<EmployeeScheduleEncodingIrregular> listEmployeeScheduleIrregular = new ArrayList<>();
	List<EmployeeScheduleEncodingIrregular> listEmployeeScheduleIrregularADD = new ArrayList<>();
	List<EmployeeScheduleEncodingIrregular> listEmployeeScheduleIrregularUPDATE = new ArrayList<>();
	List<EmployeeScheduleEncodingIrregular> listEmployeeScheduleIrregularDELETE = new ArrayList<>();

	boolean isImportError = false;
	boolean isRegularSchedule = true;
	boolean isSaveSuccessSched = true;
	boolean isSaveSuccessOTSched = true;

	@Override
	public void configureAccess() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAdd() {
		this.mainApplication.getEnabledValidatedTextFieldList().clear();
		if (this.comboBoxClient.getValue() != null && !this.comboBoxClient.getValue().isEmpty()) {
			this.modifyDetails(new ScheduleEncoding(), ModificationType.ADD);
		} else {
			AlertUtil.showInformationAlert("Please select a client.", this.getParentStage());
		}
	}

	@Override
	public void onEdit() {
		this.mainApplication.getEnabledValidatedTextFieldList().clear();
		ScheduleEncoding scheduleEncoding = this.tableViewSchedule.getSelectionModel().getSelectedItem();

		if (scheduleEncoding == null) {
			AlertUtil.showNoDataAlert(this.getParentStage());
			return;
		}

		this.modifyDetails(scheduleEncoding, ModificationType.EDIT);
	}

	@Override
	public void onDelete() {
		ScheduleEncoding scheduleEncoding = this.tableViewSchedule.getSelectionModel().getSelectedItem();
		boolean successSave = false;
		if (scheduleEncoding == null) {
			AlertUtil.showNoDataAlert(this.getParentStage());
			return;
		}

		if (AlertUtil.showDeleteQuestionAlertBoolean(this.getParentStage())) {

			ScheduleEncodingReference scheduleEncodingReference = this.mainApplication
					.getScheduleEncodingReferenceMain().getScheduleByScheduleNameAndClientCode(
							scheduleEncoding.getScheduleName(), this.comboBoxClient.getValueObject());

			// List<ScheduleEncoding> scheduleEncodingList = this.mainApplication
			// .getAllScheduleByPrikeyReferenceClient(scheduleEncodingReference.getPrimaryKeyReference());

			List<EmployeeScheduleEncoding> employeeScheduleEncodingList = this.mainApplication
					.getEmployeeScheduleEncodingMain()
					.getAllEmployeeScheduleByPrikeyReferenceClient(scheduleEncodingReference.getPrimaryKeyReference());

			if (employeeScheduleEncodingList.isEmpty()) {
				if (this.mainApplication.deleteDataByPrikeyReferenceClient(
						scheduleEncoding.getScheduleEncodingReference().getPrimaryKeyReference())) {
					successSave = this.mainApplication.getScheduleEncodingReferenceMain()
							.deleteDataByPrikeyReferenceClient(
									scheduleEncoding.getScheduleEncodingReference().getPrimaryKeyReference());
				}
			} else {
				AlertUtil.showErrorAlert("Delete failed. This schedule is being use.", this.getParentStage());
				return;
			}

			if (successSave) {
				AlertUtil.showSuccessDeleteAlert(this.getParentStage());
			} else {
				AlertUtil.showInformationAlert("Data not deleted.", this.getParentStage());
			}
		}
		this.setTableViewItems();
		this.clearFields();
	}

	@Override
	public void onSearch() {
		ObservableList<ScheduleEncoding> observableListScheduleEncoding = FXCollections.observableArrayList();
		observableListScheduleEncoding.setAll(this.mainApplication.getAllData());

		if (this.mainApplication.getObservableListScheduleEncoding().isEmpty()
				|| this.mainApplication.getObservableListScheduleEncoding() == null) {
			AlertUtil.showInformationAlert("No data found.", this.getParentStage());
			return;
		}

		Search<ScheduleEncoding> searchDialog = new Search<>();
		searchDialog.setObservableListObject(this.mainApplication.getObservableListScheduleEncoding());

		searchDialog.addTableColumn("Schedule Name", "scheduleName");

		searchDialog.addSearchCriteria("Schedule Name", "scheduleName", "");

		ScheduleEncoding scheduleEncoding = searchDialog.showSearchDialog("Search Schedule", this.getParentStage());

		if (scheduleEncoding != null) {
			this.tableViewSchedule.requestFocus();
			this.tableViewSchedule.scrollTo(scheduleEncoding);
			this.tableViewSchedule.getSelectionModel().select(scheduleEncoding);
		}

	}

	public ScheduleEncodingReference setScheduleReferenceObject() {
		ScheduleEncodingReference scheduleEncodingReference = new ScheduleEncodingReference();

		scheduleEncodingReference.setScheduleName(this.mainApplication.getSchedName());
		scheduleEncodingReference.setClient(this.mainApplication.getClient());
		scheduleEncodingReference.setUser(this.mainApplication.getUser());
		scheduleEncodingReference.setChangedOnDate(this.mainApplication.getDateNow());
		scheduleEncodingReference.setChangedInComputer(this.mainApplication.getComputerName());

		return scheduleEncodingReference;
	}

	@Override
	public void modifyDetails(ScheduleEncoding scheduleEncoding, ModificationType modificationType) {
		if (this.mainApplication.showEditScheduleEncoding(scheduleEncoding, modificationType)) {

			boolean successSave = false;

			if (modificationType.equals(ModificationType.ADD)) {
				ScheduleEncodingReference scheduleEncodingReferenceSaved = new ScheduleEncodingReference();

				ScheduleEncodingReference scheduleEncodingReference = this.setScheduleReferenceObject();

				if (this.mainApplication.getScheduleEncodingReferenceMain()
						.createMainObject(scheduleEncodingReference)) {

					scheduleEncodingReferenceSaved = this.mainApplication.getScheduleEncodingReferenceMain()
							.getScheduleByScheduleNameAndClientCode(scheduleEncodingReference.getScheduleName(),
									scheduleEncodingReference.getClient());

					for (ScheduleEncoding scheduleEncodingFromMain : this.mainApplication
							.getObjectToModifySelectedObjectList()) {

						scheduleEncodingFromMain.setScheduleEncodingReference(scheduleEncodingReferenceSaved);
					}

					successSave = this.mainApplication
							.createMultipleMainObject(this.mainApplication.getObjectToModifySelectedObjectList());
				}

			}

			if (modificationType.equals(ModificationType.EDIT)) {

				List<ScheduleEncoding> scheduleEncodingFromMain = this.mainApplication
						.getObjectToModifySelectedObjectList();

				for (ScheduleEncoding objectToModify : scheduleEncodingFromMain) {
					ScheduleEncoding scheduleEncodingExisting = new ScheduleEncoding();

					ScheduleEncodingReference scheduleEncodingReference = this.mainApplication
							.getScheduleEncodingReferenceMain().getScheduleByScheduleNameAndClientCode(
									scheduleEncoding.getScheduleName(), this.mainApplication.getClient());

					scheduleEncodingExisting = this.mainApplication.getAllScheduleByPrikeyReferenceClientAndScheduleDay(
							scheduleEncodingReference.getPrimaryKeyReference(), objectToModify.getScheduleDay());

					if (scheduleEncodingExisting != null) {
						objectToModify.setPrimaryKey(scheduleEncodingExisting.getPrimaryKey());

						successSave = this.mainApplication.updateMainObject(objectToModify);

					} else {
						successSave = this.mainApplication.createMainObject(objectToModify);

					}

					this.mainApplication.getScheduleEncodingToDeleteList().forEach(p -> {
						this.mainApplication.deleteDataByPrikeyReferenceClientAndSchedDay(
								p.getScheduleEncodingReference().getPrimaryKeyReference(), p.getScheduleDay());

					});
				}
			}

			if (successSave) {
				AlertUtil.showSuccessSaveAlert(this.getParentStage());
				this.setTableViewItems();
				this.setShowDetails();
				TableViewUtil.refreshTableView(this.tableViewSchedule);
			} else {
				AlertUtil.showErrorAlert("Data not saved.", this.getParentStage());
			}

			this.mainApplication.getObjectToModifySelectedObjectList().clear();
			this.mainApplication.getObjectToModifySelectedList().clear();
			this.mainApplication.getScheduleEncodingToDeleteList().clear();
			this.mainApplication.setIsBreakExempted(false);
		}
	}

	public void setTableViewItems() {
		this.mainApplication.populateScheduleEncoding();

		this.tableViewSchedule.setItems(this.mainApplication.getObservableListScheduleEncoding());
		TableColumnUtil.setColumn(this.tableColumnScheduleName, p -> p.getScheduleName());

		this.labelRecordsCount.setText(String.valueOf(this.mainApplication.getObservableListScheduleEncoding().size()));

	}

	@Override
	public void showDetails(ScheduleEncoding scheduleEncoding) {

	}

	public void showDetailsSchedule(ScheduleEncodingReference scheduleEncodingReference,
			List<ScheduleEncoding> scheduleEncodingList) {
		boolean isBreakExempted = false;

		if (scheduleEncodingList.size() > 0) {
			for (ScheduleEncoding scheduleEncoding : scheduleEncodingList) {

				isBreakExempted = scheduleEncoding.getIsBreakExempted() == null ? false
						: scheduleEncoding.getIsBreakExempted() == 1 ? true : false;
				if (isBreakExempted) {
					this.textFieldBreakExempted.setText("Yes");
				} else {
					this.textFieldBreakExempted.setText("No");
				}

				this.textFieldScheduleName.setText(scheduleEncodingReference.getScheduleName());
				this.textFieldOffset.setText(String.valueOf(scheduleEncoding.getOffsetAllowed()));

				switch (scheduleEncoding.getScheduleDay()) {
				case "Monday":
					this.textFieldMonday.setText(
							scheduleEncoding.getTimeIn().toString() + " - " + scheduleEncoding.getTimeOut().toString());
					if (!isBreakExempted) {
						this.textFieldMondayLunch.setText(scheduleEncoding.getLunchOut().toString() + " - "
								+ scheduleEncoding.getLunchIn().toString());
					} else {
						this.textFieldMondayLunch.clear();
					}
					this.textFieldTotalMinMon.setText(String.valueOf(scheduleEncoding.getTotalMinPerDay()));
					break;
				case "Tuesday":
					this.textFieldTuesday.setText(
							scheduleEncoding.getTimeIn().toString() + " - " + scheduleEncoding.getTimeOut().toString());
					if (!isBreakExempted) {
						this.textFieldTuesdayLunch.setText(scheduleEncoding.getLunchOut().toString() + " - "
								+ scheduleEncoding.getLunchIn().toString());
					} else {
						this.textFieldTuesdayLunch.clear();
					}
					this.textFieldTotalMinTues.setText(String.valueOf(scheduleEncoding.getTotalMinPerDay()));
					break;
				case "Wednesday":
					this.textFieldWednesday.setText(
							scheduleEncoding.getTimeIn().toString() + " - " + scheduleEncoding.getTimeOut().toString());
					if (!isBreakExempted) {
						this.textFieldWednesdayLunch.setText(scheduleEncoding.getLunchOut().toString() + " - "
								+ scheduleEncoding.getLunchIn().toString());
					} else {
						this.textFieldWednesdayLunch.clear();
					}
					this.textFieldTotalMinWed.setText(String.valueOf(scheduleEncoding.getTotalMinPerDay()));
					break;
				case "Thursday":
					this.textFieldThursday.setText(
							scheduleEncoding.getTimeIn().toString() + " - " + scheduleEncoding.getTimeOut().toString());
					if (!isBreakExempted) {
						this.textFieldThursdayLunch.setText(scheduleEncoding.getLunchOut().toString() + " - "
								+ scheduleEncoding.getLunchIn().toString());
					} else {
						this.textFieldThursdayLunch.clear();
					}
					this.textFieldTotalMinThurs.setText(String.valueOf(scheduleEncoding.getTotalMinPerDay()));
					break;
				case "Friday":
					this.textFieldFriday.setText(
							scheduleEncoding.getTimeIn().toString() + " - " + scheduleEncoding.getTimeOut().toString());
					if (!isBreakExempted) {
						this.textFieldFridayLunch.setText(scheduleEncoding.getLunchOut().toString() + " - "
								+ scheduleEncoding.getLunchIn().toString());
					} else {
						this.textFieldFridayLunch.clear();
					}
					this.textFieldTotalMinFri.setText(String.valueOf(scheduleEncoding.getTotalMinPerDay()));
					break;
				case "Saturday":
					this.textFieldSaturday.setText(
							scheduleEncoding.getTimeIn().toString() + " - " + scheduleEncoding.getTimeOut().toString());
					if (!isBreakExempted) {
						this.textFieldSaturdayLunch.setText(scheduleEncoding.getLunchOut().toString() + " - "
								+ scheduleEncoding.getLunchIn().toString());
					} else {
						this.textFieldSaturdayLunch.clear();
					}
					this.textFieldTotalMinSat.setText(String.valueOf(scheduleEncoding.getTotalMinPerDay()));
					break;
				case "Sunday":
					this.textFieldSunday.setText(
							scheduleEncoding.getTimeIn().toString() + " - " + scheduleEncoding.getTimeOut().toString());
					if (!isBreakExempted) {
						this.textFieldSundayLunch.setText(scheduleEncoding.getLunchOut().toString() + " - "
								+ scheduleEncoding.getLunchIn().toString());
					} else {
						this.textFieldSundayLunch.clear();
					}
					this.textFieldTotalMinSun.setText(String.valueOf(scheduleEncoding.getTotalMinPerDay()));
					break;
				default:
					this.clearFields();
					break;
				}
			}
		}
	}

	public void setClientComboBox() {
		this.mainApplication.populateComboBoxClient();
		this.comboBoxClient.setItems(this.mainApplication.getObservableListClient(), p -> p.getClientName());
	}

	public void setupTextField() {
		this.setTextFieldList();
		this.setFieldNonEditable();

		this.textFieldList.forEach(textField -> {
			textField.setStyle("-fx-alignment: BASELINE_CENTER;");
			textField.setStyle("-fx-text-box-border: black;");
		});

		this.textFieldHeaderList.forEach(textField -> {
			textField.setStyle("-fx-text-box-border: black;");
		});

		this.labelRecordsCount.setText("0");
	}

	public void setTextFieldList() {
		this.textFieldList.add(textFieldMonday);
		this.textFieldList.add(textFieldMondayLunch);
		this.textFieldList.add(textFieldTotalMinMon);
		this.textFieldList.add(textFieldTuesday);
		this.textFieldList.add(textFieldTuesdayLunch);
		this.textFieldList.add(textFieldTotalMinTues);
		this.textFieldList.add(textFieldWednesday);
		this.textFieldList.add(textFieldWednesdayLunch);
		this.textFieldList.add(textFieldTotalMinWed);
		this.textFieldList.add(textFieldThursday);
		this.textFieldList.add(textFieldThursdayLunch);
		this.textFieldList.add(textFieldTotalMinThurs);
		this.textFieldList.add(textFieldFriday);
		this.textFieldList.add(textFieldFridayLunch);
		this.textFieldList.add(textFieldTotalMinFri);
		this.textFieldList.add(textFieldSaturday);
		this.textFieldList.add(textFieldSaturdayLunch);
		this.textFieldList.add(textFieldTotalMinSat);
		this.textFieldList.add(textFieldSunday);
		this.textFieldList.add(textFieldSundayLunch);
		this.textFieldList.add(textFieldTotalMinSun);

		this.textFieldHeaderList.add(textFieldOffset);
		this.textFieldHeaderList.add(textFieldScheduleName);
		this.textFieldHeaderList.add(textFieldBreakExempted);
	}

	public void clearFields() {
		this.textFieldHeaderList.forEach(textField -> {
			textField.clear();
		});

		this.textFieldList.forEach(textField -> {
			textField.clear();
		});
	}

	public void setFieldNonEditable() {
		this.textFieldHeaderList.forEach(textField -> {
			textField.setEditable(false);
		});

		this.textFieldList.forEach(textField -> {
			textField.setEditable(false);
		});

	}

	@FXML
	public void handleDelete() {
		this.onDelete();
	}

	@FXML
	public void handleCollect() {

	}

	public void handleExportRegularSchedule() {
		try {
			String defaultLocation = System.getProperty("user.home");
			String file = "RegularScheduleTemplate";
			String fileName = defaultLocation + "\\Desktop\\" + file + ".csv";
			ICsvMapWriter mapWriter = null;
			try {
				mapWriter = new CsvMapWriter(new FileWriter(fileName), CsvPreference.STANDARD_PREFERENCE);

				final CellProcessor[] processors = new CellProcessor[115];

				String[] header = new String[] { "EMPID", "TIMEINHR", "TIMEINMIN", "TIMEOUTHR", "TIMEOUTMIN",
						"LUNCHINHR", "LUNCHINMIN", "LUNCHOUTHR", "LUNCHOUTMIN", "OFFSETHR", "OFFSETMIN",
						"ISBREAKEXEMPTED", "MONRD", "TUERD", "WEDRD", "THURD", "FRIRD", "SATRD", "SUNRD", "OT10STARTHR",
						"OT10STARTMIN", "OT10ENDHR", "OT10ENDMIN", "OT25STARTHR", "OT25STARTMIN", "OT25ENDHR",
						"OT25ENDMIN", "OT30STARTHR", "OT30STARTMIN", "OT30ENDHR", "OT30ENDMIN", "OT50STARTHR",
						"OT50STARTMIN", "OT50ENDHR", "OT50ENDMIN", "OT100STARTHR", "OT100STARTMIN", "OT100ENDHR",
						"OT100ENDMIN", "OT125STARTHR", "OT125STARTMIN", "OT125ENDHR", "OT125ENDMIN", "OT130STARTHR",
						"OT130STARTMIN", "OT130ENDHR", "OT130ENDMIN", "OT150STARTHR", "OT150STARTMIN", "OT150ENDHR",
						"OT150ENDMIN", "OT160STARTHR", "OT160STARTMIN", "OT160ENDHR", "OT160ENDMIN", "OT169STARTHR",
						"OT169STARTMIN", "OT169ENDHR", "OT169ENDMIN", "OT195STARTHR", "OT195STARTMIN", "OT195ENDHR",
						"OT195ENDMIN", "OT200STARTHR", "OT200STARTMIN", "OT200ENDHR", "OT200ENDMIN", "OT230STARTHR",
						"OT230STARTMIN", "OT230ENDHR", "OT230ENDMIN", "OT260STARTHR", "OT260STARTMIN", "OT260ENDHR",
						"OT260ENDMIN", "OT338STARTHR", "OT338STARTMIN", "OT338ENDHR", "OT338ENDMIN", "OT12510STARTHR",
						"OT12510STARTMIN", "OT12510ENDHR", "OT12510ENDMIN", "OT13010STARTHR", "OT13010STARTMIN",
						"OT13010ENDHR", "OT13010ENDMIN", "OTND125STARTHR", "OTND125STARTMIN", "OTND125ENDHR",
						"OTND125ENDMIN", "OTND130STARTHR", "OTND130STARTMIN", "OTND130ENDHR", "OTND130ENDMIN",
						"OTND200STARTHR", "OTND200STARTMIN", "OTND200ENDHR", "OTND200ENDMIN", "OTXS130STARTHR",
						"OTXS130STARTMIN", "OTXS130ENDHR", "OTXS130ENDMIN", "OTXS150STARTHR", "OTXS150STARTMIN",
						"OTXS150ENDHR", "OTXS150ENDMIN", "OTXS200STARTHR", "OTXS200STARTMIN", "OTXS200ENDHR",
						"OTXS200ENDMIN", "OTXS260STARTHR", "OTXS260STARTMIN", "OTXS260ENDHR", "OTXS260ENDMIN" };

				mapWriter.writeHeader(header);
				Map<String, Object> resultMap = new HashMap<String, Object>();

				resultMap.put(header[0], "123456");
				resultMap.put(header[1], "8");
				resultMap.put(header[2], "0");
				resultMap.put(header[3], "17");
				resultMap.put(header[4], "0");
				resultMap.put(header[5], "13");
				resultMap.put(header[6], "0");
				resultMap.put(header[7], "12");
				resultMap.put(header[8], "0");
				resultMap.put(header[9], "1");
				resultMap.put(header[10], "0");
				resultMap.put(header[11], "1");
				resultMap.put(header[12], "0");
				resultMap.put(header[13], "0");
				resultMap.put(header[14], "0");
				resultMap.put(header[15], "0");
				resultMap.put(header[16], "0");
				resultMap.put(header[17], "1");
				resultMap.put(header[18], "1");

				int maxColumn = 115;
				for (int currentColumn = 19; currentColumn < maxColumn; currentColumn++) {
					resultMap.put(header[currentColumn], "0");
				}

				mapWriter.write(resultMap, header, processors);

			} catch (IOException e) {
				AlertUtil.showAlert("", "", "Invalid format or the file is being used by another process",
						AlertType.WARNING, this.mainApplication.getPrimaryStage());
			} finally {
				if (mapWriter != null) {
					mapWriter.close();
					AlertUtil.showAlert("", "", "Export Complete.\n\nExtracted Sample Location: " + fileName,
							AlertType.INFORMATION, this.mainApplication.getPrimaryStage());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void handleExportIrregularSchedule() {
		try {
			String defaultLocation = System.getProperty("user.home");
			String file = "IrregularScheduleTemplate";
			String fileName = defaultLocation + "\\Desktop\\" + file + ".csv";
			ICsvMapWriter mapWriter = null;
			try {
				mapWriter = new CsvMapWriter(new FileWriter(fileName), CsvPreference.STANDARD_PREFERENCE);

				final CellProcessor[] processors = new CellProcessor[109];

				String[] header = new String[] { "EMPID", "DATE", "TIMEINHR", "TIMEINMIN", "TIMEOUTHR", "TIMEOUTMIN",
						"LUNCHINHR", "LUNCHINMIN", "LUNCHOUTHR", "LUNCHOUTMIN", "OFFSETHR", "OFFSETMIN",
						"ISBREAKEXEMPTED", "OT10STARTHR", "OT10STARTMIN", "OT10ENDHR", "OT10ENDMIN", "OT25STARTHR",
						"OT25STARTMIN", "OT25ENDHR", "OT25ENDMIN", "OT30STARTHR", "OT30STARTMIN", "OT30ENDHR",
						"OT30ENDMIN", "OT50STARTHR", "OT50STARTMIN", "OT50ENDHR", "OT50ENDMIN", "OT100STARTHR",
						"OT100STARTMIN", "OT100ENDHR", "OT100ENDMIN", "OT125STARTHR", "OT125STARTMIN", "OT125ENDHR",
						"OT125ENDMIN", "OT130STARTHR", "OT130STARTMIN", "OT130ENDHR", "OT130ENDMIN", "OT150STARTHR",
						"OT150STARTMIN", "OT150ENDHR", "OT150ENDMIN", "OT160STARTHR", "OT160STARTMIN", "OT160ENDHR",
						"OT160ENDMIN", "OT169STARTHR", "OT169STARTMIN", "OT169ENDHR", "OT169ENDMIN", "OT195STARTHR",
						"OT195STARTMIN", "OT195ENDHR", "OT195ENDMIN", "OT200STARTHR", "OT200STARTMIN", "OT200ENDHR",
						"OT200ENDMIN", "OT230STARTHR", "OT230STARTMIN", "OT230ENDHR", "OT230ENDMIN", "OT260STARTHR",
						"OT260STARTMIN", "OT260ENDHR", "OT260ENDMIN", "OT338STARTHR", "OT338STARTMIN", "OT338ENDHR",
						"OT338ENDMIN", "OT12510STARTHR", "OT12510STARTMIN", "OT12510ENDHR", "OT12510ENDMIN",
						"OT13010STARTHR", "OT13010STARTMIN", "OT13010ENDHR", "OT13010ENDMIN", "OTND125STARTHR",
						"OTND125STARTMIN", "OTND125ENDHR", "OTND125ENDMIN", "OTND130STARTHR", "OTND130STARTMIN",
						"OTND130ENDHR", "OTND130ENDMIN", "OTND200STARTHR", "OTND200STARTMIN", "OTND200ENDHR",
						"OTND200ENDMIN", "OTXS130STARTHR", "OTXS130STARTMIN", "OTXS130ENDHR", "OTXS130ENDMIN",
						"OTXS150STARTHR", "OTXS150STARTMIN", "OTXS150ENDHR", "OTXS150ENDMIN", "OTXS200STARTHR",
						"OTXS200STARTMIN", "OTXS200ENDHR", "OTXS200ENDMIN", "OTXS260STARTHR", "OTXS260STARTMIN",
						"OTXS260ENDHR", "OTXS260ENDMIN" };

				mapWriter.writeHeader(header);
				Map<String, Object> resultMap = new HashMap<String, Object>();

				resultMap.put(header[0], "123456");
				resultMap.put(header[1], "01/01/2000");
				resultMap.put(header[2], "8");
				resultMap.put(header[3], "0");
				resultMap.put(header[4], "17");
				resultMap.put(header[5], "0");
				resultMap.put(header[6], "13");
				resultMap.put(header[7], "0");
				resultMap.put(header[8], "12");
				resultMap.put(header[9], "0");
				resultMap.put(header[10], "1");
				resultMap.put(header[11], "0");
				resultMap.put(header[12], "1");

				int maxColumn = 109;
				for (int currentColumn = 13; currentColumn < maxColumn; currentColumn++) {
					resultMap.put(header[currentColumn], "0");
				}

				mapWriter.write(resultMap, header, processors);

			} catch (IOException e) {
				AlertUtil.showAlert("", "", "Invalid format or the file is being used by another process",
						AlertType.WARNING, this.mainApplication.getPrimaryStage());
			} finally {
				if (mapWriter != null) {
					mapWriter.close();
					AlertUtil.showAlert("", "", "Export Complete.\n\nExtracted Sample Location: " + fileName,
							AlertType.INFORMATION, this.mainApplication.getPrimaryStage());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setFieldListener() {

		this.tableViewSchedule.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue != null) {
				this.mainApplication
						.setSchedName(this.tableViewSchedule.getSelectionModel().getSelectedItem().getScheduleName());
				this.setShowDetails();
			}
		});

		this.tableViewSchedule.setRowFactory(p -> {
			TableRow<ScheduleEncoding> row = new TableRow<>();
			row.setOnMouseClicked(event -> {

				if (event.getClickCount() == 2 && !row.isEmpty()) {
					this.onEdit();
				}
			});
			return row;
		});

		this.comboBoxClient.valueObjectProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue != null) {

				this.buttonSearch.setDisable(false);
				this.clearFields();
				this.mainApplication.setClientName(newValue.getClientName());
				this.mainApplication.setClient(newValue);

				this.setTableViewItems();

				TableViewUtil.refreshTableView(this.tableViewSchedule);

				// if (!newValue.isEmpty()) {
				// // this.handleCollect();
				// this.setTableViewItems();
				// TableViewUtil.refreshTableView(this.tableViewSchedule);
				// } else {
				// this.buttonSearch.setDisable(true);
				// this.mainApplication.getObservableListScheduleEncoding().clear();
				// this.labelRecordsCount.setText("0");
				// TableViewUtil.refreshTableView(this.tableViewSchedule);
				// }
			} else {
				this.buttonSearch.setDisable(true);
				this.mainApplication.getObservableListScheduleEncoding().clear();
				this.labelRecordsCount.setText("0");
				TableViewUtil.refreshTableView(this.tableViewSchedule);
			}
		});
	}

	public void setShowDetails() {
		ScheduleEncodingReference scheduleEncodingReference = new ScheduleEncodingReference();

		scheduleEncodingReference = this.mainApplication.getScheduleEncodingReferenceMain()
				.getScheduleByScheduleNameAndClientCode(this.mainApplication.getSchedName(),
						this.mainApplication.getClient());
		List<ScheduleEncoding> scheduleEncodingList = new ArrayList<>();

		// scheduleEncodingList =
		// this.mainApplication.getAllScheduleEncodingByClientNameAndScheduleName(
		// this.mainApplication.getClientName(), newValue.getScheduleName());

		scheduleEncodingList = this.mainApplication
				.getAllScheduleByPrikeyReferenceClient(scheduleEncodingReference.getPrimaryKeyReference());

		this.clearFields();
		this.showDetailsSchedule(scheduleEncodingReference, scheduleEncodingList);
	}

	@Override
	public void onSetMainApplication() {
		this.setupTextField();
		this.setClientComboBox();
		this.setFieldListener();

		this.buttonSearch.setDisable(true);
		this.buttonCollect.setVisible(false);
		this.buttonCollect.setManaged(false);

	}

	public void resetImportList() {
		this.isImportError = false;
		this.isRegularSchedule = true;
		this.listScheduleEncoding = new ArrayList<>();
		this.listScheduleEncodingADD = new ArrayList<>();
		this.listScheduleEncodingUPDATE = new ArrayList<>();
		this.listScheduleEncodingDELETE = new ArrayList<>();
		this.listEmployeeScheduleOvertime = new ArrayList<>();
		this.listEmployeeScheduleOvertimeADD = new ArrayList<>();
		this.listEmployeeScheduleOvertimeUPDATE = new ArrayList<>();
		this.listEmployeeScheduleOvertimeDELETE = new ArrayList<>();
		this.listEmployeeScheduleIrregular = new ArrayList<>();
		this.listEmployeeScheduleIrregularADD = new ArrayList<>();
		this.listEmployeeScheduleIrregularUPDATE = new ArrayList<>();
		this.listEmployeeScheduleIrregularDELETE = new ArrayList<>();
	}

	public void handleImportIrregularSchedule() {
		try {
			this.resetImportList();
			this.isRegularSchedule = false;

			ImportExportFileMain importExportFileMain = new ImportExportFileMain();
			if (importExportFileMain.getRootLayoutImportExportFile(false, "csv",
					this.mainApplication.getPrimaryStage())) {
				String fileName = importExportFileMain.getFileLocation() + "\\" + importExportFileMain.getFileName()
						+ ".csv";
				// ProcessingMessage.showProcessingMessage(this.mainApplication.getPrimaryStage());

				String line = null;
				BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
				List<String> importList = new ArrayList<>();

				while ((line = bufferedReader.readLine()) != null) {
					importList.add(line);
				}
				bufferedReader.close();

				String header = importList.get(0);
				if (header.compareTo(new ScheduleEncoding().getIrregularScheduleHeader()) != 0) {
					AlertUtil.showErrorAlert("Invalid columns in .csv file. \nPlease follow the template columns.",
							this.getParentStage());
					return;
				}
				
				importList.remove(0); // remove headers to read all data

				Task<Void> taskImportIrregSched = new Task<Void>() {
					@Override
					protected Void call() throws Exception {
						int counter = 0;
						for (String details : importList) {
							readDetailsIrregSchedList(details);
							counter += 1;
							updateProgress(ProgressUtil.getProgressValue(counter, importList.size()), 1D);
							updateMessage(ProgressUtil.getMessageValue("Saving...\nIrregular schedule\n", counter,
									importList.size()));
						}

						Platform.runLater(() -> {
							// ProcessingMessage.closeProcessingMessage();

							if (isImportError) {
								AlertUtil.showErrorAlert("Data not saved.", getParentStage());
								return;
							} else {
								AlertUtil.showSuccessSaveAlert(getParentStage());
							}
						});

						return null;
					}
				};
				ProgressUtil.showProcessInterface(this.getParentStage(), taskImportIrregSched, true);
			}

		} catch (SuperCsvException e) {
			ProcessingMessage.closeProcessingMessage();
			AlertUtil.showErrorAlert("Invalid columns in .csv file. \nPlease follow the template columns.",
					this.getParentStage());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void handleImportRegularSchedule() throws IOException {
		this.resetImportList();
		this.isRegularSchedule = true;

		ImportExportFileMain importExportFileMain = new ImportExportFileMain();
		boolean isImportFile = importExportFileMain.getRootLayoutImportExportFile(false, "csv",
				mainApplication.getPrimaryStage());

		Task<Void> taskImportRegSched = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				try {
					if (isImportFile) {
						String fileName = importExportFileMain.getFileLocation() + "\\"
								+ importExportFileMain.getFileName() + ".csv";

						String line = null;
						BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
						List<String> importList = new ArrayList<>();

						while ((line = bufferedReader.readLine()) != null) {
							importList.add(line);
						}

						// check header if correct column template
						String header = importList.get(0);
						if (header.compareTo(new ScheduleEncoding().getRegularScheduleHeader()) != 0) {
							Platform.runLater(() -> {
								AlertUtil.showErrorAlert(
										"Invalid columns in .csv file. \nPlease follow the template columns.",
										getParentStage());
							});
							bufferedReader.close();
							return null;
						}

						importList.remove(0); // remove headers to read all data

						int counter = 0;
						for (String details : importList) {
							readDetailsRegSchedList(details);

							counter += 1;
							updateProgress(ProgressUtil.getProgressValue(counter, importList.size()), 1D);
							updateMessage(ProgressUtil.getMessageValue("Reading...\nRegular schedule\n", counter,
									importList.size()));
						}

						bufferedReader.close();

						List<Integer> listEmployeeID = listScheduleEncoding.stream()
								.map(p -> p.getEmployee().getEmployeeCode()).distinct().collect(Collectors.toList());

						counter = 0;
						for (Integer employeeID : listEmployeeID) {
							setListToSaveSchedule(employeeID);
							setListToSaveOvertime(employeeID);

							counter += 1;
							updateProgress(ProgressUtil.getProgressValue(counter, listEmployeeID.size()), 1D);
							updateMessage(ProgressUtil.getMessageValue("Processing...\nRegular schedule\n", counter,
									listEmployeeID.size()));
						}

						try {

							if (!listScheduleEncodingDELETE.isEmpty()) {
								for (int ctr = 0; ctr < 1; ctr++) {
									if (!mainApplication.deleteMultipleMainObject(listScheduleEncodingDELETE)) {
										isSaveSuccessSched = false;
									}
									updateProgress(ProgressUtil.getProgressValue(ctr, 1), 1D);
									updateMessage(
											ProgressUtil.getMessageValue("Deleting...\nRegular schedule\n", ctr, 1));
								}
							}

							if (!listScheduleEncodingADD.isEmpty()) {
								for (int ctr = 0; ctr < 1; ctr++) {
									if (!mainApplication.createMultipleMainObject(listScheduleEncodingADD)) {
										isSaveSuccessSched = false;
									}
									updateProgress(ProgressUtil.getProgressValue(ctr, 1), 1D);
									updateMessage(
											ProgressUtil.getMessageValue("Saving...\nRegular schedule\n", ctr, 1));
								}
							}

							if (!listScheduleEncodingUPDATE.isEmpty()) {
								for (int ctr = 0; ctr < 1; ctr++) {
									if (!mainApplication.updateMultipleMainObject(listScheduleEncodingUPDATE)) {
										isSaveSuccessSched = false;
									}
									counter += 1;
									updateProgress(ProgressUtil.getProgressValue(ctr, 1), 1D);
									updateMessage(
											ProgressUtil.getMessageValue("Updating...\nRegular schedule\n", ctr, 1));
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

						if (!listEmployeeScheduleOvertimeDELETE.isEmpty()) {
							for (int ctr = 0; ctr < 1; ctr++) {
								if (!mainApplication.getEmployeeScheduleEncodingOvertimeMain()
										.deleteMultipleData(listEmployeeScheduleOvertimeDELETE)) {
									isSaveSuccessOTSched = false;
								}
								updateProgress(ProgressUtil.getProgressValue(ctr, 1), 1D);
								updateMessage(ProgressUtil.getMessageValue("Deleting...\nOvertime schedule\n", ctr, 1));
							}
						}

						if (!listEmployeeScheduleOvertimeADD.isEmpty()) {
							for (int ctr = 0; ctr < 1; ctr++) {
								if (!mainApplication.getEmployeeScheduleEncodingOvertimeMain()
										.createMultipleDataByBatch(listEmployeeScheduleOvertimeADD)) {
									isSaveSuccessOTSched = false;
								}
								updateProgress(ProgressUtil.getProgressValue(ctr, 1), 1D);
								updateMessage(ProgressUtil.getMessageValue("Saving...\nOvertime schedule\n", ctr, 1));
							}
						}

						if (!listEmployeeScheduleOvertimeUPDATE.isEmpty()) {
							for (int ctr = 0; ctr < 1; ctr++) {
								if (!mainApplication.getEmployeeScheduleEncodingOvertimeMain()
										.updateMultipleData(listEmployeeScheduleOvertimeUPDATE)) {
									isSaveSuccessOTSched = false;
								}
								updateProgress(ProgressUtil.getProgressValue(ctr, 1), 1D);
								updateMessage(ProgressUtil.getMessageValue("Updating...\nOvertime schedule\n", ctr, 1));
							}
						}

						counter = 0;
						Platform.runLater(() -> {
							if (isSaveSuccessSched && isSaveSuccessOTSched) {
								AlertUtil.showSuccessSaveAlert(getParentStage());
							} else if (!isSaveSuccessSched) {
								AlertUtil.showRecordNotSave("Record not saved - Regular Schedule", getParentStage());
							} else if (!isSaveSuccessOTSched) {
								AlertUtil.showRecordNotSave("Record not saved - Regular Overtime Schedule",
										getParentStage());
							}
						});
					}
				} catch (SuperCsvException e) {
					AlertUtil.showErrorAlert("Invalid columns in .csv file. \nPlease follow the template columns.",
							getParentStage());
				} catch (Exception e) {
					e.printStackTrace();
				}

				return null;
			}
		};
		ProgressUtil.showProcessInterface(this.getParentStage(), taskImportRegSched, true);
	}

	// public boolean saveImportedScheduleOvertime() {
	// boolean isSuccessSave = true;
	// try {
	//
	// if (!this.listEmployeeScheduleOvertimeDELETE.isEmpty()) {
	// for (EmployeeScheduleEncodingOvertime overtimeSchedule :
	// this.listEmployeeScheduleOvertimeDELETE) {
	// if (!this.mainApplication.getEmployeeScheduleEncodingOvertimeMain()
	// .deleteMainObject(overtimeSchedule)) {
	// isSuccessSave = false;
	// }
	// }
	// }
	//
	// if (!this.listEmployeeScheduleOvertimeADD.isEmpty()) {
	// for (EmployeeScheduleEncodingOvertime overtimeSchedule :
	// this.listEmployeeScheduleOvertimeADD) {
	// if (!this.mainApplication.getEmployeeScheduleEncodingOvertimeMain()
	// .createMainObject(overtimeSchedule)) {
	// isSuccessSave = false;
	// }
	// }
	// }
	//
	// if (!this.listEmployeeScheduleOvertimeUPDATE.isEmpty()) {
	// for (EmployeeScheduleEncodingOvertime overtimeSchedule :
	// this.listEmployeeScheduleOvertimeUPDATE) {
	// if (!this.mainApplication.getEmployeeScheduleEncodingOvertimeMain()
	// .updateMainObject(overtimeSchedule)) {
	// isSuccessSave = false;
	// }
	// }
	// }
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// return false;
	// }
	// return isSuccessSave;
	// }
	//
	// public boolean saveImportedSchedule() {
	// boolean isSuccessSave = true;
	//
	// try {
	//
	// if (!this.listScheduleEncodingDELETE.isEmpty()) {
	// for (ScheduleEncoding scheduleEncoding : this.listScheduleEncodingDELETE) {
	// if (!this.mainApplication.deleteMainObject(scheduleEncoding)) {
	// isSuccessSave = false;
	// }
	// }
	// }
	//
	// if (!this.listScheduleEncodingADD.isEmpty()) {
	// if
	// (!this.mainApplication.createMultipleMainObject(this.listScheduleEncodingADD))
	// {
	// isSuccessSave = false;
	// }
	// }
	//
	// if (!this.listScheduleEncodingUPDATE.isEmpty()) {
	// if
	// (!this.mainApplication.updateMultipleMainObject(this.listScheduleEncodingUPDATE))
	// {
	// isSuccessSave = false;
	// }
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// return false;
	// }
	// return isSuccessSave;
	// }

	public void setListToSaveOvertime(Integer employeeID) {
		List<EmployeeScheduleEncodingOvertime> existingOvertimeScheduleList = this.mainApplication
				.getEmployeeScheduleEncodingOvertimeMain().getAllOvertimeRegularByEmployeeId(employeeID);

		List<EmployeeScheduleEncodingOvertime> overtimeScheduleByEmpIDList = this.listEmployeeScheduleOvertime.stream()
				.filter(p -> p.getEmploymentHistory().getEmployee().getEmployeeCode().compareTo(employeeID) == 0)
				.collect(Collectors.toList());

		if (existingOvertimeScheduleList.isEmpty()) {
			this.listEmployeeScheduleOvertimeADD.addAll(overtimeScheduleByEmpIDList);
		} else {
			List<EmployeeScheduleEncodingOvertime> existingOvertimeScheduleListCOPY = new ArrayList<>();
			existingOvertimeScheduleListCOPY.addAll(existingOvertimeScheduleList);

			for (EmployeeScheduleEncodingOvertime overtimeSchedule : overtimeScheduleByEmpIDList) {
				EmployeeScheduleEncodingOvertime existingOvertimeSchedule = existingOvertimeScheduleList.stream()
						.filter(p -> p.getOvertimeType().getPrimaryKey()
								.equals(overtimeSchedule.getOvertimeType().getPrimaryKey()))
						.findAny().orElse(null);

				if (existingOvertimeSchedule != null) {
					existingOvertimeScheduleListCOPY.remove(existingOvertimeSchedule);
					overtimeSchedule.setPrimaryKey(existingOvertimeSchedule.getPrimaryKey());
					ObjectCopyUtil.copyProperties(overtimeSchedule, existingOvertimeSchedule,
							EmployeeScheduleEncodingOvertime.class);

					boolean isDuplicate = this.listEmployeeScheduleOvertimeUPDATE.stream()
							.filter(p -> p.getEmploymentHistory().getEmployee().getEmployeeCode().equals(employeeID)
									&& p.getOvertimeType().getPrimaryKey()
											.equals(existingOvertimeSchedule.getOvertimeType().getPrimaryKey()))
							.findFirst().isPresent();
					if (!isDuplicate) {
						this.listEmployeeScheduleOvertimeUPDATE.add(existingOvertimeSchedule);
					}
				} else {
					boolean isDuplicate = this.listEmployeeScheduleOvertimeADD.stream()
							.filter(p -> p.getEmploymentHistory().getEmployee().getEmployeeCode().equals(employeeID)
									&& p.getOvertimeType().getPrimaryKey()
											.equals(overtimeSchedule.getOvertimeType().getPrimaryKey()))
							.findAny().isPresent();
					if (!isDuplicate) {
						this.listEmployeeScheduleOvertimeADD.add(overtimeSchedule);
					}
				}
			}

			this.listEmployeeScheduleOvertimeADD.removeAll(this.listEmployeeScheduleOvertimeUPDATE);

			if (!existingOvertimeScheduleListCOPY.isEmpty()) {
				this.listEmployeeScheduleOvertimeDELETE.addAll(existingOvertimeScheduleListCOPY);
			}
		}
	}

	public void setListToSaveSchedule(Integer employeeID) {
		List<ScheduleEncoding> existingScheduleList = this.mainApplication
				.getAllScheduleUploadedByEmployeeId(employeeID);

		List<ScheduleEncoding> scheduleEncodingByEmpIDList = this.listScheduleEncoding.stream()
				.filter(p -> p.getEmployee().getEmployeeCode().equals(employeeID)).collect(Collectors.toList());

		if (existingScheduleList.isEmpty()) {
			this.listScheduleEncodingADD.addAll(scheduleEncodingByEmpIDList);
		} else {
			List<ScheduleEncoding> existingScheduleListCOPY = new ArrayList<>();
			existingScheduleListCOPY.addAll(existingScheduleList);

			for (ScheduleEncoding scheduleEncoding : scheduleEncodingByEmpIDList) {
				ScheduleEncoding existingScheduleEncoding = existingScheduleList.stream()
						.filter(p -> p.getScheduleDay().equals(scheduleEncoding.getScheduleDay())).findAny()
						.orElse(null);

				if (existingScheduleEncoding != null) {
					existingScheduleListCOPY.remove(existingScheduleEncoding);
					scheduleEncoding.setPrimaryKey(existingScheduleEncoding.getPrimaryKey());
					ObjectCopyUtil.copyProperties(scheduleEncoding, existingScheduleEncoding, ScheduleEncoding.class);

					boolean isDuplicate = this.listScheduleEncodingUPDATE.stream()
							.filter(p -> p.getEmployee().getEmployeeCode().equals(employeeID)
									&& p.getScheduleDay().equals(existingScheduleEncoding.getScheduleDay()))
							.findAny().isPresent();
					if (!isDuplicate) {
						this.listScheduleEncodingUPDATE.add(existingScheduleEncoding);
					}
				} else {
					boolean isDuplicate = this.listScheduleEncodingADD.stream()
							.filter(p -> p.getEmployee().getEmployeeCode().equals(employeeID)
									&& p.getScheduleDay().equals(scheduleEncoding.getScheduleDay()))
							.findAny().isPresent();
					if (!isDuplicate) {
						this.listScheduleEncodingADD.add(scheduleEncoding);
					}
				}
			}

			if (!existingScheduleListCOPY.isEmpty()) {
				this.listScheduleEncodingDELETE.addAll(existingScheduleListCOPY);
			}
		}
	}

	public synchronized boolean isValidImportSched(String[] detailsImport) {
		try {
			if (detailsImport.length == 0 || detailsImport.length < 5) {
				this.isImportError = true;
				return false;
			}

			if (Integer.valueOf(detailsImport[0]) == null) {
				this.isImportError = true;
				return false;
			}

			if (detailsImport[0].length() < 6) {
				AlertUtil.showErrorAlert("Invalid Employee ID: " + detailsImport[0] + "\nEmployee ID MUST be 6 digits.",
						this.getParentStage());
				this.isImportError = true;
				return false;
			}

			Integer employeeID = Integer.valueOf(detailsImport[0]);
			Employee employee = new Employee();
			employee = this.mainApplication.getEmployeeMain().getEmployeeByID(employeeID);
			if (employee == null) {
				AlertUtil.showErrorAlert("Invalid Employee ID: " + detailsImport[0], this.getParentStage());
				this.isImportError = true;
				return false;
			}

			if (this.isRegularSchedule) {
				int maxCol = detailsImport.length;
				for (int currentCol = 1; currentCol < maxCol; currentCol++) {
					if (currentCol == 11 || currentCol == 12 || currentCol == 13 || currentCol == 14 || currentCol == 15
							|| currentCol == 16 || currentCol == 17 || currentCol == 18) {
						if (detailsImport[currentCol].length() > 1) {
							AlertUtil.showErrorAlert(
									"Invalid data: " + detailsImport[currentCol] + "\nPlease follow the template.",
									this.getParentStage());
							this.isImportError = true;
							return false;
						}
					} else {
						if (detailsImport[currentCol].length() > 2) {
							AlertUtil.showErrorAlert(
									"Invalid data: " + detailsImport[currentCol] + "\nPlease follow the template.",
									this.getParentStage());
							this.isImportError = true;
							return false;
						}
					}
				}
			} else {

				if (detailsImport[1].length() < 10 || detailsImport[1].length() > 10) {
					AlertUtil.showErrorAlert("Invalid Date: " + detailsImport[1] + "\nPlease follow the template.",
							this.getParentStage());
					this.isImportError = true;
					return false;
				}

				DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
				Date dateIrregularSchedule = dateFormat.parse(detailsImport[1]);

				if (dateIrregularSchedule == null) {
					AlertUtil.showErrorAlert("Invalid Date: " + detailsImport[1] + "\nPlease follow the template.",
							this.getParentStage());
					this.isImportError = true;
					return false;
				}

				int maxCol = detailsImport.length;
				for (int currentCol = 3; currentCol < maxCol; currentCol++) {
					if (currentCol == 10) {
						if (detailsImport[currentCol].length() > 1) {
							AlertUtil.showErrorAlert(
									"Invalid data: " + detailsImport[currentCol] + "\nPlease follow the template.",
									this.getParentStage());
							this.isImportError = true;
							return false;
						}
					} else {
						if (detailsImport[currentCol].length() > 2) {
							AlertUtil.showErrorAlert(
									"Invalid data: " + detailsImport[currentCol] + "\nPlease follow the template.",
									this.getParentStage());
							this.isImportError = true;
							return false;
						}
					}
				}
			}
		} catch (ParseException e) {
			AlertUtil.showErrorAlert(
					"Invalid Date: " + detailsImport[1] + "\nPlease follow the date template: 'MM/dd/yyyy'",
					this.getParentStage());
			this.isImportError = true;
			return false;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	public void setSchedIrregularFromImport(String[] detailsImport, Integer isBreakExempted, Integer offsetMin,
			EmploymentHistory employmentHistory, Date dateSchedule) {
		EmployeeScheduleEncodingIrregular employeeScheduleEncodingIrregular = new EmployeeScheduleEncodingIrregular();
		Integer totalMinPerDay = 0;
		Timestamp timeInTS = null;
		Timestamp timeOutTS = null;
		Timestamp lunchOutTS = null;
		Timestamp lunchInTS = null;

		Integer timeInHr = Integer.valueOf(detailsImport[2]);
		Integer timeInMin = Integer.valueOf(detailsImport[3]);
		Time timeIn = this.convertToTime(timeInHr, timeInMin);
		timeInTS = this.entryToTimestampConverter(this.mainApplication.getDateNow(), timeIn);

		Integer timeOutHr = Integer.valueOf(detailsImport[4]);
		Integer timeOutMin = Integer.valueOf(detailsImport[5]);
		Time timeOut = this.convertToTime(timeOutHr, timeOutMin);
		timeOutTS = this.entryToTimestampConverter(this.mainApplication.getDateNow(), timeOut);

		if (isBreakExempted == 1) {
			totalMinPerDay = this.computeTotalMinsEntry(timeInTS, timeOutTS);
		} else {
			Integer lunchOutHr = Integer.valueOf(detailsImport[8]);
			Integer lunchOutMin = Integer.valueOf(detailsImport[9]);
			Time lunchOut = this.convertToTime(lunchOutHr, lunchOutMin);
			employeeScheduleEncodingIrregular.setLunchOut(lunchOut);
			lunchOutTS = this.entryToTimestampConverter(this.mainApplication.getDateNow(), lunchOut);

			Integer lunchInHr = Integer.valueOf(detailsImport[6]);
			Integer lunchInMin = Integer.valueOf(detailsImport[7]);
			Time lunchIn = this.convertToTime(lunchInHr, lunchInMin);
			employeeScheduleEncodingIrregular.setLunchIn(lunchIn);
			lunchInTS = this.entryToTimestampConverter(this.mainApplication.getDateNow(), lunchIn);

			totalMinPerDay = this.computeTotalMinsEntry(timeInTS, timeOutTS);
			Integer totalMinLunch = this.computeTotalMinsEntry(lunchOutTS, lunchInTS);
			totalMinPerDay = totalMinPerDay - totalMinLunch;
		}

		employeeScheduleEncodingIrregular.setEmploymentHistory(employmentHistory);
		employeeScheduleEncodingIrregular.setDateSchedule(dateSchedule);
		employeeScheduleEncodingIrregular.setTimeIn(timeIn);
		employeeScheduleEncodingIrregular.setTimeOut(timeOut);
		employeeScheduleEncodingIrregular.setIsBreakExempted(isBreakExempted);
		employeeScheduleEncodingIrregular.setOffsetAllowed(offsetMin);
		employeeScheduleEncodingIrregular.setTotalMinPerDay(totalMinPerDay);

		employeeScheduleEncodingIrregular.setUser(this.mainApplication.getUser());
		employeeScheduleEncodingIrregular.setChangedOnDate(this.mainApplication.getDateNow());
		employeeScheduleEncodingIrregular.setChangedInComputer(this.mainApplication.getComputerName());

		EmployeeScheduleEncodingIrregular existingIrregularSched = this.mainApplication
				.getEmployeeScheduleEncodingIrregularMain().getEmployeeIrregularScheduleByDateAndEmployeeID(
						new SimpleDateFormat("yyyy-MM-dd").format(dateSchedule),
						employmentHistory.getEmployee().getEmployeeCode());

		boolean isSuccessSave = true;

		if (existingIrregularSched != null) {
			employeeScheduleEncodingIrregular.setPrimaryKey(existingIrregularSched.getPrimaryKey());
			ObjectCopyUtil.copyProperties(employeeScheduleEncodingIrregular, existingIrregularSched,
					EmployeeScheduleEncodingIrregular.class);
			isSuccessSave = this.mainApplication.getEmployeeScheduleEncodingIrregularMain()
					.updateMainObject(existingIrregularSched);

		} else {
			isSuccessSave = this.mainApplication.getEmployeeScheduleEncodingIrregularMain()
					.createMainObject(employeeScheduleEncodingIrregular);
		}

		if (!isSuccessSave) {
			AlertUtil.showErrorAlert("Data not saved: " + employmentHistory.getEmployee().getEmployeeCode(),
					this.getParentStage());
			return;
		}

	}

	public Integer computeTotalOffsetMin(Integer offsetHrArg, Integer offsetMinArg) {
		Integer result = 0;
		Integer offsetHr = offsetHrArg == null ? 0 : offsetHrArg;
		Integer offsetMin = offsetMinArg == null ? 0 : offsetMinArg;
		Integer offsetHrIntoMin = offsetHr * 60;
		result = offsetHrIntoMin + offsetMin;
		return result;
	}

	public void readDetailsIrregSchedList(String details) {
		try {
			String[] detailsImport = details.split(",");
			for (String detailsSplit : detailsImport) {
				if (detailsSplit == null) {
					detailsSplit = "";
				}
			}

			if (this.isValidImportSched(detailsImport)) {
				EmploymentHistory employmentHistory = new EmploymentHistory();
				employmentHistory = this.mainApplication.getEmploymentHistoryMain()
						.getEmploymentHistoryMaxByEmployeeCode(Integer.valueOf(detailsImport[0]));
				if (employmentHistory != null) {
					Integer isBreakExempted = Integer.valueOf(detailsImport[12]);
					Integer offsetMin = this.computeTotalOffsetMin(Integer.valueOf(detailsImport[10]),
							Integer.valueOf(detailsImport[11]));
					DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
					Date dateSchedule = dateFormat.parse(detailsImport[1]);

					this.setSchedIrregularFromImport(detailsImport, isBreakExempted, offsetMin, employmentHistory,
							dateSchedule);

					int currentColumn = 13;
					for (int maxColumn = detailsImport.length; currentColumn < maxColumn; currentColumn++) {
						if (detailsImport[currentColumn] == null || detailsImport[currentColumn] == ""
								|| detailsImport[currentColumn].isEmpty()
								|| detailsImport[currentColumn].compareTo("0") == 0) {
							currentColumn = currentColumn + 3;
							// continue;
						} else {
							switch (currentColumn) {
							case 13:
								currentColumn = this.setOvertimeSchedFromImport(detailsImport, currentColumn, 1,
										employmentHistory, dateSchedule);
								break;
							case 17:
								currentColumn = this.setOvertimeSchedFromImport(detailsImport, currentColumn, 2,
										employmentHistory, dateSchedule);
								break;
							case 21:
								currentColumn = this.setOvertimeSchedFromImport(detailsImport, currentColumn, 3,
										employmentHistory, dateSchedule);
								break;
							case 25:
								currentColumn = this.setOvertimeSchedFromImport(detailsImport, currentColumn, 4,
										employmentHistory, dateSchedule);
								break;
							case 29:
								currentColumn = this.setOvertimeSchedFromImport(detailsImport, currentColumn, 5,
										employmentHistory, dateSchedule);
								break;
							case 33:
								currentColumn = this.setOvertimeSchedFromImport(detailsImport, currentColumn, 6,
										employmentHistory, dateSchedule);
								break;
							case 37:
								currentColumn = this.setOvertimeSchedFromImport(detailsImport, currentColumn, 7,
										employmentHistory, dateSchedule);
								break;
							case 41:
								currentColumn = this.setOvertimeSchedFromImport(detailsImport, currentColumn, 9,
										employmentHistory, dateSchedule);
								break;
							case 45:
								currentColumn = this.setOvertimeSchedFromImport(detailsImport, currentColumn, 10,
										employmentHistory, dateSchedule);
								break;
							case 49:
								currentColumn = this.setOvertimeSchedFromImport(detailsImport, currentColumn, 11,
										employmentHistory, dateSchedule);
								break;
							case 53:
								currentColumn = this.setOvertimeSchedFromImport(detailsImport, currentColumn, 14,
										employmentHistory, dateSchedule);
								break;
							case 57:
								currentColumn = this.setOvertimeSchedFromImport(detailsImport, currentColumn, 15,
										employmentHistory, dateSchedule);
								break;
							case 61:
								currentColumn = this.setOvertimeSchedFromImport(detailsImport, currentColumn, 16,
										employmentHistory, dateSchedule);
								break;
							case 65:
								currentColumn = this.setOvertimeSchedFromImport(detailsImport, currentColumn, 18,
										employmentHistory, dateSchedule);
								break;
							case 69:
								currentColumn = this.setOvertimeSchedFromImport(detailsImport, currentColumn, 19,
										employmentHistory, dateSchedule);
								break;
							case 73:
								currentColumn = this.setOvertimeSchedFromImport(detailsImport, currentColumn, 20,
										employmentHistory, dateSchedule);
								break;
							case 77:
								currentColumn = this.setOvertimeSchedFromImport(detailsImport, currentColumn, 21,
										employmentHistory, dateSchedule);
								break;
							case 81:
								currentColumn = this.setOvertimeSchedFromImport(detailsImport, currentColumn, 27,
										employmentHistory, dateSchedule);
								break;
							case 85:
								currentColumn = this.setOvertimeSchedFromImport(detailsImport, currentColumn, 28,
										employmentHistory, dateSchedule);
								break;
							case 89:
								currentColumn = this.setOvertimeSchedFromImport(detailsImport, currentColumn, 29,
										employmentHistory, dateSchedule);
								break;
							case 93:
								currentColumn = this.setOvertimeSchedFromImport(detailsImport, currentColumn, 41,
										employmentHistory, dateSchedule);
								break;
							case 97:
								currentColumn = this.setOvertimeSchedFromImport(detailsImport, currentColumn, 42,
										employmentHistory, dateSchedule);
								break;
							case 101:
								currentColumn = this.setOvertimeSchedFromImport(detailsImport, currentColumn, 43,
										employmentHistory, dateSchedule);
								break;
							case 105:
								currentColumn = this.setOvertimeSchedFromImport(detailsImport, currentColumn, 44,
										employmentHistory, dateSchedule);
								break;
							default:
								break;
							}
						}
					}
				}
			} else {
				return;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void readDetailsRegSchedList(String details) {
		try {
			String[] detailsImport = details.split(",");

			if (this.isValidImportSched(detailsImport)) {

				EmploymentHistory employmentHistory = new EmploymentHistory();
				employmentHistory = this.mainApplication.getEmploymentHistoryMain()
						.getEmploymentHistoryMaxByEmployeeCode(Integer.valueOf(detailsImport[0]));

				if (employmentHistory == null) {
					return;
				}

				Integer isBreakExempted = Integer.valueOf(detailsImport.length >= 11 ? detailsImport[11] : "0");
				Integer offsetMin = this.computeTotalOffsetMin(
						Integer.valueOf(detailsImport.length >= 9 ? detailsImport[9] : "0"),
						Integer.valueOf(detailsImport.length >= 10 ? detailsImport[10] : "0"));

				LinkedHashMap<String, Integer> daysMap = new LinkedHashMap<>();
				Integer monRD = Integer.valueOf(detailsImport[12]);
				Integer tueRD = Integer.valueOf(detailsImport[13]);
				Integer wedRD = Integer.valueOf(detailsImport[14]);
				Integer thuRD = Integer.valueOf(detailsImport[15]);
				Integer friRD = Integer.valueOf(detailsImport[16]);
				Integer satRD = Integer.valueOf(detailsImport[17]);
				Integer sunRD = Integer.valueOf(detailsImport[18]);
				daysMap.put("Monday", monRD);
				daysMap.put("Tuesday", tueRD);
				daysMap.put("Wednesday", wedRD);
				daysMap.put("Thursday", thuRD);
				daysMap.put("Friday", friRD);
				daysMap.put("Saturday", satRD);
				daysMap.put("Sunday", sunRD);

				for (Map.Entry<String, Integer> entry : daysMap.entrySet()) {
					if (entry.getValue() == 0) {
						this.setSchedFromImport(detailsImport, isBreakExempted, offsetMin, employmentHistory,
								entry.getKey());
					}
				}

				int currentColumn = 19;
				for (int maxColumn = detailsImport.length; currentColumn < maxColumn; currentColumn++) {
					if (detailsImport[currentColumn] == null || detailsImport[currentColumn] == ""
							|| detailsImport[currentColumn].isEmpty()
							|| detailsImport[currentColumn].compareTo("0") == 0) {
						currentColumn = currentColumn + 3;
						// continue;
					} else {
						switch (currentColumn) {
						case 19:
							currentColumn = this.setOvertimeSchedFromImport(detailsImport, currentColumn, 1,
									employmentHistory, null);
							break;
						case 23:
							currentColumn = this.setOvertimeSchedFromImport(detailsImport, currentColumn, 2,
									employmentHistory, null);
							break;
						case 27:
							currentColumn = this.setOvertimeSchedFromImport(detailsImport, currentColumn, 3,
									employmentHistory, null);
							break;
						case 31:
							currentColumn = this.setOvertimeSchedFromImport(detailsImport, currentColumn, 4,
									employmentHistory, null);
							break;
						case 35:
							currentColumn = this.setOvertimeSchedFromImport(detailsImport, currentColumn, 5,
									employmentHistory, null);
							break;
						case 39:
							currentColumn = this.setOvertimeSchedFromImport(detailsImport, currentColumn, 6,
									employmentHistory, null);
							break;
						case 43:
							currentColumn = this.setOvertimeSchedFromImport(detailsImport, currentColumn, 7,
									employmentHistory, null);
							break;
						case 47:
							currentColumn = this.setOvertimeSchedFromImport(detailsImport, currentColumn, 9,
									employmentHistory, null);
							break;
						case 51:
							currentColumn = this.setOvertimeSchedFromImport(detailsImport, currentColumn, 10,
									employmentHistory, null);
							break;
						case 55:
							currentColumn = this.setOvertimeSchedFromImport(detailsImport, currentColumn, 11,
									employmentHistory, null);
							break;
						case 59:
							currentColumn = this.setOvertimeSchedFromImport(detailsImport, currentColumn, 14,
									employmentHistory, null);
							break;
						case 63:
							currentColumn = this.setOvertimeSchedFromImport(detailsImport, currentColumn, 15,
									employmentHistory, null);
							break;
						case 67:
							currentColumn = this.setOvertimeSchedFromImport(detailsImport, currentColumn, 16,
									employmentHistory, null);
							break;
						case 71:
							currentColumn = this.setOvertimeSchedFromImport(detailsImport, currentColumn, 18,
									employmentHistory, null);
							break;
						case 75:
							currentColumn = this.setOvertimeSchedFromImport(detailsImport, currentColumn, 19,
									employmentHistory, null);
							break;
						case 79:
							currentColumn = this.setOvertimeSchedFromImport(detailsImport, currentColumn, 20,
									employmentHistory, null);
							break;
						case 83:
							currentColumn = this.setOvertimeSchedFromImport(detailsImport, currentColumn, 21,
									employmentHistory, null);
							break;
						case 87:
							currentColumn = this.setOvertimeSchedFromImport(detailsImport, currentColumn, 27,
									employmentHistory, null);
							break;
						case 91:
							currentColumn = this.setOvertimeSchedFromImport(detailsImport, currentColumn, 28,
									employmentHistory, null);
							break;
						case 95:
							currentColumn = this.setOvertimeSchedFromImport(detailsImport, currentColumn, 29,
									employmentHistory, null);
							break;
						case 99:
							currentColumn = this.setOvertimeSchedFromImport(detailsImport, currentColumn, 41,
									employmentHistory, null);
							break;
						case 103:
							currentColumn = this.setOvertimeSchedFromImport(detailsImport, currentColumn, 42,
									employmentHistory, null);
							break;
						case 107:
							currentColumn = this.setOvertimeSchedFromImport(detailsImport, currentColumn, 43,
									employmentHistory, null);
							break;
						case 111:
							currentColumn = this.setOvertimeSchedFromImport(detailsImport, currentColumn, 44,
									employmentHistory, null);
							break;
						default:
							break;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public int setOvertimeSchedFromImport(String[] detailsImport, int currentColumn, int otTypePrikey,
			EmploymentHistory employmentHistory, Date dateIrregular) {
		int startMinCol = currentColumn + 1;
		int endHrCol = currentColumn + 2;
		int endMinCol = currentColumn + 3;

		Integer otStartHr = Integer.valueOf(detailsImport[currentColumn]);
		Integer otStartMin = Integer.valueOf(detailsImport[startMinCol]);
		Integer otEndHr = Integer.valueOf(detailsImport[endHrCol]);
		Integer otEndMin = Integer.valueOf(detailsImport[endMinCol]);

		Time otStart = this.convertToTime(otStartHr, otStartMin);
		Time otEnd = this.convertToTime(otEndHr, otEndMin);

		this.setOvertimeSched(otStart, otEnd, otTypePrikey, employmentHistory, dateIrregular);
		return endMinCol;
	}

	public void setOvertimeSched(Time otStart, Time otEnd, int otTypePrikey, EmploymentHistory employmentHistory,
			Date dateIrregular) {
		EmployeeScheduleEncodingOvertime employeeScheduleEncodingOvertime = new EmployeeScheduleEncodingOvertime();
		OvertimeType overtimeType = new OvertimeType();

		overtimeType = this.mainApplication.getOvertimeTypeMain().getOvertimeTypeById(otTypePrikey);
		employeeScheduleEncodingOvertime.setOvertimeType(overtimeType);
		employeeScheduleEncodingOvertime.setEmploymentHistory(employmentHistory);
		employeeScheduleEncodingOvertime.setClient(employmentHistory.getClient());
		employeeScheduleEncodingOvertime.setIsRegularOvertime(this.isRegularSchedule ? 1 : 0);
		employeeScheduleEncodingOvertime.setTimeStart(otStart);
		employeeScheduleEncodingOvertime.setTimeEnd(otEnd);

		if (!this.isRegularSchedule) {
			EmployeeScheduleEncodingIrregular irregularSchedule = new EmployeeScheduleEncodingIrregular();
			irregularSchedule = this.mainApplication.getEmployeeScheduleEncodingIrregularMain()
					.getEmployeeIrregularScheduleByDateAndEmployeeID(
							new SimpleDateFormat("yyyy-MM-dd").format(dateIrregular),
							employmentHistory.getEmployee().getEmployeeCode());
			if (irregularSchedule != null) {
				employeeScheduleEncodingOvertime.setEmployeeScheduleEncodingIrregular(irregularSchedule);
			}
		}

		Date date = this.mainApplication.getDateNow();
		if (otStart.after(otEnd)) {
			date = DateUtil.addDayOrMonthToDate(date, 0, -1);
		}

		Timestamp start = this.entryToTimestampConverter(date, otStart);
		Timestamp end = this.entryToTimestampConverter(this.mainApplication.getDateNow(), otEnd);
		employeeScheduleEncodingOvertime.setTotalMin(this.computeTotalMinsEntry(start, end));

		employeeScheduleEncodingOvertime.setUser(this.mainApplication.getUser());
		employeeScheduleEncodingOvertime.setChangedOnDate(this.mainApplication.getDateNow());
		employeeScheduleEncodingOvertime.setChangedInComputer(this.mainApplication.getComputerName());

		boolean isDuplicate = this.listEmployeeScheduleOvertime.stream()
				.filter(p -> p.getEmploymentHistory().getEmployee().getEmployeeCode()
						.compareTo(employmentHistory.getEmployee().getEmployeeCode()) == 0
						&& p.getOvertimeType().getPrimaryKey().compareTo(otTypePrikey) == 0)
				.findFirst().isPresent();
		if (!isDuplicate) {
			List<OvertimeType> overtimeTypeList = new ArrayList<>();
			overtimeTypeList.addAll(this.mainApplication.getOvertimeTypeMain()
					.getOvertimeTypeByClientCode(employmentHistory.getClient().getClientCode()));

			boolean isOvertimeTypeEncodedInClient = overtimeTypeList.stream()
					.filter(p -> p.getPrimaryKey().equals(otTypePrikey)).findAny().isPresent();

			if (isOvertimeTypeEncodedInClient) {
				this.listEmployeeScheduleOvertime.add(employeeScheduleEncodingOvertime);
			}
		}

		if (!this.isRegularSchedule) {
			List<EmployeeScheduleEncodingOvertime> existingOvertimeScheduleList = this.mainApplication
					.getEmployeeScheduleEncodingOvertimeMain()
					.getAllOvertimeIrregularByEmployeeId(employmentHistory.getEmployee().getEmployeeCode());

			EmployeeScheduleEncodingOvertime existingOvertimeSchedule = existingOvertimeScheduleList.stream()
					.filter(p -> p.getEmployeeScheduleEncodingIrregular() != null
							&& p.getEmployeeScheduleEncodingIrregular().getDateSchedule().equals(dateIrregular))
					.findAny().orElse(null);

			boolean isSuccessSave = true;
			if (existingOvertimeSchedule == null) {
				isSuccessSave = this.mainApplication.getEmployeeScheduleEncodingOvertimeMain()
						.createMainObject(employeeScheduleEncodingOvertime);
			} else {
				employeeScheduleEncodingOvertime.setPrimaryKey(existingOvertimeSchedule.getPrimaryKey());
				ObjectCopyUtil.copyProperties(employeeScheduleEncodingOvertime, existingOvertimeSchedule,
						EmployeeScheduleEncodingOvertime.class);
				isSuccessSave = this.mainApplication.getEmployeeScheduleEncodingOvertimeMain()
						.updateMainObject(existingOvertimeSchedule);
			}

			if (!isSuccessSave) {
				this.isImportError = true;
				return;
			}

		}

	}

	public BinaryOperator<ScheduleEncoding> binaryOperatorSetScheduleEncodingProperties() {
		return (a, b) -> {
			ScheduleEncoding scheduleEncoding = new ScheduleEncoding();
			scheduleEncoding.setPrimaryKey(b.getPrimaryKey());
			return scheduleEncoding;
		};
	}

	public void setSchedFromImport(String[] detailsImport, Integer isBreakExempted, Integer offsetMin,
			EmploymentHistory employmentHistory, String scheduleDay) {

		ScheduleEncoding scheduleEncoding = new ScheduleEncoding();
		Integer totalMinPerDay = 0;
		Timestamp timeInTS = null;
		Timestamp timeOutTS = null;
		Timestamp lunchOutTS = null;
		Timestamp lunchInTS = null;

		Integer timeInHr = Integer.valueOf(detailsImport[1]);
		Integer timeInMin = Integer.valueOf(detailsImport[2]);
		Time timeIn = this.convertToTime(timeInHr, timeInMin);
		timeInTS = this.entryToTimestampConverter(this.mainApplication.getDateNow(), timeIn);

		Integer timeOutHr = Integer.valueOf(detailsImport[3]);
		Integer timeOutMin = Integer.valueOf(detailsImport[4]);
		Time timeOut = this.convertToTime(timeOutHr, timeOutMin);
		timeOutTS = this.entryToTimestampConverter(this.mainApplication.getDateNow(), timeOut);

		if (isBreakExempted == 1) {
			totalMinPerDay = this.computeTotalMinsEntry(timeInTS, timeOutTS);
		} else {
			Integer lunchOutHr = Integer.valueOf(detailsImport[7]);
			Integer lunchOutMin = Integer.valueOf(detailsImport[8]);
			Time lunchOut = this.convertToTime(lunchOutHr, lunchOutMin);
			scheduleEncoding.setLunchOut(lunchOut);
			lunchOutTS = this.entryToTimestampConverter(this.mainApplication.getDateNow(), lunchOut);

			Integer lunchInHr = Integer.valueOf(detailsImport[5]);
			Integer lunchInMin = Integer.valueOf(detailsImport[6]);
			Time lunchIn = this.convertToTime(lunchInHr, lunchInMin);
			scheduleEncoding.setLunchIn(lunchIn);
			lunchInTS = this.entryToTimestampConverter(this.mainApplication.getDateNow(), lunchIn);

			totalMinPerDay = this.computeTotalMinsEntry(timeInTS, timeOutTS);
			Integer totalMinLunch = this.computeTotalMinsEntry(lunchOutTS, lunchInTS);
			totalMinPerDay = totalMinPerDay - totalMinLunch;
		}

		scheduleEncoding.setScheduleDay(scheduleDay);
		scheduleEncoding.setEmployee(employmentHistory.getEmployee());
		scheduleEncoding.setTimeIn(timeIn);
		scheduleEncoding.setTimeOut(timeOut);
		scheduleEncoding.setIsBreakExempted(isBreakExempted);
		scheduleEncoding.setOffsetAllowed(offsetMin);
		scheduleEncoding.setTotalMinPerDay(totalMinPerDay);

		scheduleEncoding.setUser(this.mainApplication.getUser());
		scheduleEncoding.setChangedOnDate(this.mainApplication.getDateNow());
		scheduleEncoding.setChangedInComputer(this.mainApplication.getComputerName());

		boolean isDuplicate = this.listScheduleEncoding.stream()
				.filter(p -> p.getEmployee().getEmployeeCode().equals(employmentHistory.getEmployee().getEmployeeCode())
						&& p.getScheduleDay().equals(scheduleDay))
				.findAny().isPresent();

		if (!isDuplicate) {
			this.listScheduleEncoding.add(scheduleEncoding);
		}

	}

	public Timestamp entryToTimestampConverter(Date dateEntry, Time timeEntry) {
		Timestamp result = null;

		if (dateEntry != null && timeEntry != null) {
			String StringInput = new SimpleDateFormat("yyyy-MM-dd").format(dateEntry)
					.concat(" ".concat(new SimpleDateFormat("HH:mm:ss").format(timeEntry)));
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			LocalDateTime localDateTimeInput = LocalDateTime.from(formatter.parse(StringInput));
			result = Timestamp.valueOf(localDateTimeInput);
		}

		return result;
	}

	public Integer computeTotalMinsEntry(Timestamp timeFrom, Timestamp timeTo) {
		Integer totalMins = 0;

		long millisResult = timeTo.getTime() - timeFrom.getTime();
		BigDecimal seconds = BigDecimal.valueOf(millisResult).divide(new BigDecimal(1000), 4, RoundingMode.HALF_UP);
		BigDecimal minutes = seconds.divide(new BigDecimal(60), 4, RoundingMode.HALF_UP);

		totalMins = minutes.intValue();

		return totalMins;
	}

	public Time convertToTime(Integer timeHr, Integer timeMin) {
		String timeString = (timeHr < 10 ? "0" + timeHr.toString() : timeHr.toString()) + ":"
				+ (timeMin < 10 ? "0" + timeMin.toString() : timeMin.toString()) + ":00";
		return Time.valueOf(timeString);
	}

	@FXML
	private Button buttonAdd;
	@FXML
	private Button buttonDelete;
	@FXML
	private Button buttonSearch;
	@FXML
	private Button buttonCollect;
	@FXML
	private TableView<ScheduleEncoding> tableViewSchedule;
	@FXML
	private TableColumn<ScheduleEncoding, String> tableColumnScheduleName;
	@FXML
	private AutoFillComboBox<Client> comboBoxClient;
	@FXML
	private ValidatedTextField textFieldScheduleName;
	@FXML
	private ValidatedTextField textFieldMonday;
	@FXML
	private ValidatedTextField textFieldTuesday;
	@FXML
	private ValidatedTextField textFieldWednesday;
	@FXML
	private ValidatedTextField textFieldThursday;
	@FXML
	private ValidatedTextField textFieldFriday;
	@FXML
	private ValidatedTextField textFieldSaturday;
	@FXML
	private ValidatedTextField textFieldSunday;
	@FXML
	private ValidatedTextField textFieldMondayLunch;
	@FXML
	private ValidatedTextField textFieldTuesdayLunch;
	@FXML
	private ValidatedTextField textFieldWednesdayLunch;
	@FXML
	private ValidatedTextField textFieldThursdayLunch;
	@FXML
	private ValidatedTextField textFieldFridayLunch;
	@FXML
	private ValidatedTextField textFieldSaturdayLunch;
	@FXML
	private ValidatedTextField textFieldSundayLunch;
	@FXML
	private ValidatedTextField textFieldOffset;
	@FXML
	private ValidatedTextField textFieldTotalMinMon;
	@FXML
	private ValidatedTextField textFieldTotalMinTues;
	@FXML
	private ValidatedTextField textFieldTotalMinWed;
	@FXML
	private ValidatedTextField textFieldTotalMinThurs;
	@FXML
	private ValidatedTextField textFieldTotalMinFri;
	@FXML
	private ValidatedTextField textFieldTotalMinSat;
	@FXML
	private ValidatedTextField textFieldTotalMinSun;
	@FXML
	private Label labelRecordsCount;
	@FXML
	private ValidatedTextField textFieldBreakExempted;
	@FXML
	private MenuItem menuItemImportRegSched;
	@FXML
	private MenuItem menuItemImportIrregSched;
	@FXML
	private MenuItem menuItemExportRegSched;
	@FXML
	private MenuItem menuItemExportIrregSched;

}
