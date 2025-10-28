package ph.com.lbpsc.production.billingcharging.view;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import ph.com.lbpsc.production.ReportMain;
import ph.com.lbpsc.production.billing.model.Billing;
import ph.com.lbpsc.production.billingcharging.BillingChargingMain;
import ph.com.lbpsc.production.billingcharging.model.BillingCharging;
import ph.com.lbpsc.production.billingclientconfiguration.model.BillingClientConfiguration;
import ph.com.lbpsc.production.billingrate.model.BillingRate;
import ph.com.lbpsc.production.billingregisterprocessing.printbilling.PrintBillingStandard;
import ph.com.lbpsc.production.billingreportclass.model.BillingReportClass;
import ph.com.lbpsc.production.billingsoaconfiguration.model.BillingSoaConfiguration;
import ph.com.lbpsc.production.client.model.Client;
import ph.com.lbpsc.production.clientgroup.model.ClientGroup;
import ph.com.lbpsc.production.custom.controls.autofillcombobox.AutoFillComboBox;
import ph.com.lbpsc.production.custom.controls.formatteddatepicker.FormattedDatePicker;
import ph.com.lbpsc.production.custom.controls.validatedtextfield.ValidatedTextField;
import ph.com.lbpsc.production.employee.model.Employee;
import ph.com.lbpsc.production.employmentconfiguration.model.EmploymentConfiguration;
import ph.com.lbpsc.production.masterclass.MasterController;
import ph.com.lbpsc.production.overtime.model.Overtime;
import ph.com.lbpsc.production.signatory.model.Signatory;
import ph.com.lbpsc.production.statementofaccount.model.StatementOfAccount;
import ph.com.lbpsc.production.util.AlertUtil;
import ph.com.lbpsc.production.util.DateFormatter;
import ph.com.lbpsc.production.util.DateUtil;
import ph.com.lbpsc.production.util.FieldValidator;
import ph.com.lbpsc.production.util.ObjectCopyUtil;
import ph.com.lbpsc.production.util.ObservableListUtil;
import ph.com.lbpsc.production.util.ProcessingMessage;
import ph.com.lbpsc.production.util.ReportUtil;
import ph.com.lbpsc.production.util.TableColumnUtil;
import ph.com.lserv.production.soasuppliesbreakdown.model.SuppliesBreakdown;

public class PrintBillingChargingController extends MasterController<BillingChargingMain> {
	Stage stage;
	Client client;
	ClientGroup clientGroup;
	Date payFrom;
	Date payTo;
	Date batchDate;
	ObservableList<Client> observableListClient = FXCollections.observableArrayList();
	BillingClientConfiguration billingClientConfiguration = new BillingClientConfiguration();
	BillingSoaConfiguration billingSoaConfiguration = new BillingSoaConfiguration();
	ObservableList<Billing> observableListBillingPrint = FXCollections.observableArrayList();
	List<SuppliesBreakdown> listSuppliesBreakdownToPrint = new ArrayList<>();
	ObservableList<StatementOfAccount> statementOfAccountRegularObservableList = FXCollections.observableArrayList();

	BillingCharging billingCharging = new BillingCharging();
	List<BillingCharging> listBillingCharging = new ArrayList<>();

	// private BillingChargingMain mainApplication;
	// private Stage dialogStage;
	// private boolean isOkClicked = false;

	@Override
	public void configureAccess() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSetMainApplication() {
		// TODO Auto-generated method stub
		this.filterStatementOfAccountRegular();

		ObservableList<Signatory> observableListSignatory = FXCollections.observableArrayList();
		observableListSignatory.setAll(this.mainApplication.getSignatoryMain().getAllSignatory());

		this.autoFillComboBoxCertifiedCorrect.setItems(observableListSignatory, P -> P.getEmployeeName());
		this.autoFillComboBoxApprovedBy.setItems(observableListSignatory, P -> P.getEmployeeName());
		this.autoFillComboBoxCheckedBySOA.setItems(observableListSignatory, p -> p.getEmployeeName());
		this.autoFillComboBoxApprovedBySOA.setItems(observableListSignatory, p -> p.getEmployeeName());
		this.autoFillComboBoxCheckedBySOAOT.setItems(observableListSignatory, p -> p.getEmployeeName());
		this.autoFillComboBoxApprovedBySOAOT.setItems(observableListSignatory, p -> p.getEmployeeName());
		this.labelClient.setText(this.getClient() != null ? this.getClient().getClientName() : "");
		this.labelClientGroup.setText(this.getClientGroup() != null ? this.getClientGroup().getGroupName() : "");
		// this.autoFillComboBoxClient.setItems(this.observableListClient, p ->
		// p.getClientName());
		//
		// if (this.client != null) {
		// this.autoFillComboBoxClient.getSelectionModel().select(this.client.getClientName());
		// }

		if (this.payFrom != null) {
			this.formattedDatePickerPayPeriodFrom
					.setValue(DateFormatter.toLocalDate(DateUtil.getCutOffDate(this.payFrom, 0, false)));
		}

		if (this.payTo != null) {
			this.formattedDatePickerPayPeriodTo
					.setValue(DateFormatter.toLocalDate(DateUtil.getCutOffDateOfPayTo(this.payTo)));
		}

		this.tableViewSOA.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue != null) {
				billingSoaConfiguration = this.getClientAddress(this.billingClientConfiguration, newValue);
				if (billingSoaConfiguration != null) {
					this.autoFillComboBoxCheckedBySOA.setValue(this.billingSoaConfiguration.getChecked() != null
							? this.billingSoaConfiguration.getChecked().getEmployeeName()
							: "");
					this.autoFillComboBoxApprovedBySOA.setValue(this.billingSoaConfiguration.getApproved() != null
							? this.billingSoaConfiguration.getApproved().getEmployeeName()
							: "");
				} else {
					this.autoFillComboBoxCheckedBySOAOT.setValue(null);
					this.autoFillComboBoxApprovedBySOAOT.setValue(null);
				}
			}
		});

		this.tableViewSOAOT.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue != null) {
				billingSoaConfiguration = this.getClientAddress(this.billingClientConfiguration, newValue);
				if (billingSoaConfiguration != null) {
					this.autoFillComboBoxCheckedBySOAOT.setValue(this.billingSoaConfiguration.getChecked() != null
							? this.billingSoaConfiguration.getChecked().getEmployeeName()
							: "");
					this.autoFillComboBoxApprovedBySOAOT.setValue(this.billingSoaConfiguration.getApproved() != null
							? this.billingSoaConfiguration.getApproved().getEmployeeName()
							: "");
				} else {
					this.autoFillComboBoxApprovedBySOAOT.setValue(null);
					this.autoFillComboBoxCheckedBySOAOT.setValue(null);
				}
			}
		});

	}

	// public void setFieldListeners() {
	// StatementOfAccount statementOfAccount =
	// this.tableViewSOA.getSelectionModel().getSelectedItem();
	//
	// if (statementOfAccount != null) {
	// this.autoFillComboBoxCheckedBySOA.setValue(statementOfAccount.getby);
	// }
	// }

	public void printBillingCharging(StatementOfAccount statementOfAccount) {
		System.out.println("printBillingCharging");
		ProcessingMessage.showProcessingMessage(this.getStage());

		// String clientCode =
		// this.autoFillComboBoxClient.getValueObject().getClientCode();
		Date payFromA = DateFormatter.toDate(this.formattedDatePickerPayPeriodFrom.getValue());
		Date payToA = DateUtil.getCutOffDateOfPayTo(payFromA);

		// Date payFrom2 = DateUtil
		// .getCutOffDateOfPayTo(DateFormatter.toDate(this.formattedDatePickerPayPeriodFrom.getValue()));
		Date payFromB = DateUtil.addDayOrMonthToDate(payFromA, 0, 15);
		Date payToB = DateUtil.getCutOffDateOfPayTo(payFromB);

		String periodA = new SimpleDateFormat("MMMM dd-").format(payFromA)
				.concat(new SimpleDateFormat("dd").format(payToA));
		String periodB = new SimpleDateFormat("MMMM dd-").format(payFromB)
				.concat(new SimpleDateFormat("dd").format(payToB));

		if (this.client != null) {
			listBillingCharging.addAll(
					this.mainApplication.getAllBillingChargingBatchDateClientOrGroupCoverUpOrExtendedDuty(this.client,
							null, this.batchDate, 1));
			// listBillingCharging.addAll(this.mainApplication
			// .getAllExtendedDutyBillingChargingByClientCdBatchDate(this.batchDate,
			// this.client.getClientCode()));
		} else {
			listBillingCharging
					.addAll(this.mainApplication.getAllBillingChargingBatchDateClientOrGroupCoverUpOrExtendedDuty(null,
							this.clientGroup, this.batchDate, 1));
			// listBillingCharging.addAll(this.mainApplication
			// .getAllBillingChargingByClientGroupAndBatchDate(this.clientGroup.getPrimaryKey(),
			// this.batchDate));
		}
		System.out.println("listBillingCharging: " + listBillingCharging.size());

		if (listBillingCharging.size() == 0 || listBillingCharging == null || listBillingCharging.isEmpty()) {
			return;
		}

		ReportMain reportMain = new ReportMain();
		Map<String, Object> parameters = new HashMap<String, Object>();

		// if (this.autoFillComboBoxClient.getValueObject() == null
		// && this.formattedDatePickerPayPeriodFrom.getValue() == null
		// && this.formattedDatePickerPayPeriodTo.getValue() == null) {
		// AlertUtil.showInformationAlert("Select client & pay period first.",
		// this.getStage());
		// return;
		// }
		System.out.println("pass");

		String imagePath = System.getenv("LOCALAPPDATA").concat(
				File.separator + "IFMIS" + File.separator + "app" + File.separator + "resources" + File.separator);
		parameters.put("imagePath", imagePath);
		parameters.put("client_name",
				this.labelClient.getText().isEmpty() ? this.labelClientGroup.getText() : this.labelClient.getText());
		parameters.put("clientCode", this.client.getClientCode());
		parameters.put("pay_from", DateFormatter.toDate(this.formattedDatePickerPayPeriodFrom.getValue()));
		parameters.put("pay_to", DateFormatter.toDate(this.formattedDatePickerPayPeriodTo.getValue()));

		parameters.put("checkedBy", this.autoFillComboBoxCertifiedCorrect.getValueObject().getEmployeeName());
		parameters.put("checkerPosition", this.autoFillComboBoxCertifiedCorrect.getValueObject().getPosition());
		parameters.put("approvedBy", this.autoFillComboBoxApprovedBy.getValueObject().getEmployeeName());
		parameters.put("approverPosition", this.autoFillComboBoxApprovedBy.getValueObject().getPosition());
		parameters.put("preparedBy",
				this.mainApplication.getUser().getEmploymentHistory().getEmployee().getEmployeeFullName());
		parameters.put("preparedByPosition",
				this.mainApplication.getUser().getEmploymentHistory().getPosition().getPositionName());
		parameters.put("periodA", periodA);
		parameters.put("periodB", periodB);
		parameters.put("payFromA", payFromA);
		parameters.put("payFromB", payFromB);

		// Payroll payroll = new Payroll();
		// payroll.getEmploymentHistory().getEmployee().getEmployeeFullName();
		//
		// BillingCharging billingCharging = new BillingCharging();
		// billingCharging.getEmploymentConfiguration().getAmountOfPayRate();
		List<Employee> employeeList = listBillingCharging.stream()
				.map(p -> p.getPayroll().getEmploymentHistory().getEmployee()).distinct().collect(Collectors.toList());
		List<BillingCharging> billingChargingFinalList = new ArrayList<>();

		for (Employee employee : employeeList) {
			List<BillingCharging> billingChargingByEmployeeList = new ArrayList<>();
			billingChargingByEmployeeList.addAll(listBillingCharging.stream().filter(p -> p.getPayroll()
					.getEmploymentHistory().getEmployee().getEmployeeCode().compareTo(employee.getEmployeeCode()) == 0)
					.collect(Collectors.toList()));
			billingChargingByEmployeeList.forEach(p -> p.setEmployee(employee));

			List<EmploymentConfiguration> employmentConfigList = new ArrayList<>();
			employmentConfigList.addAll(billingChargingByEmployeeList.stream().map(p -> p.getEmploymentConfiguration())
					.distinct().collect(Collectors.toList()));

			for (EmploymentConfiguration employmentConfig : employmentConfigList) {
				System.out.println("loop");
				System.out.println(employmentConfig.getPosition().getPositionName());
				BigDecimal periodAWorkHrs = BigDecimal.ZERO;
				BigDecimal periodBWorkHrs = BigDecimal.ZERO;
				BigDecimal periodATotal = BigDecimal.ZERO;
				BigDecimal periodBTotal = BigDecimal.ZERO;

				BillingCharging billingChargingFinal = new BillingCharging();
				// BigDecimal periodAWorkDays = BigDecimal.ZERO;
				// BigDecimal periodBWorkDays = BigDecimal.ZERO;

				for (BillingCharging billingCharging : billingChargingByEmployeeList) {

					if (billingCharging.getEmploymentConfiguration().getPosition().getPositionCode()
							.compareTo(employmentConfig.getPosition().getPositionCode()) == 0) {

						for (Overtime overtimeBillingCharging : billingCharging.getOvertimeList()) {
							if (overtimeBillingCharging.getIsExtendedDuty()) {
								BigDecimal minutesToHrs = BigDecimal.ZERO;
								BigDecimal totalHrs = BigDecimal.ZERO;
								BigDecimal totalAmount = BigDecimal.ZERO;

								minutesToHrs = minutesToHrs
										.add(new BigDecimal(overtimeBillingCharging.getMinutesOfOvertime())
												.divide(new BigDecimal("60"), RoundingMode.HALF_UP));
								totalHrs = totalHrs.add(
										minutesToHrs.add(new BigDecimal(overtimeBillingCharging.getHoursOfOvertime())));
								totalAmount = totalAmount.add(overtimeBillingCharging.getAmountOfOvertime());

								if (billingCharging.getPayFrom().compareTo(payFromA) == 0) {
									periodAWorkHrs = periodAWorkHrs.add(totalHrs);
									periodATotal = periodATotal.add(totalAmount);
								}
								if (billingCharging.getPayFrom().compareTo(payFromB) == 0) {
									periodBWorkHrs = periodBWorkHrs.add(totalHrs);
									periodBTotal = periodBTotal.add(totalAmount);
								}
							}
						}
						ObjectCopyUtil.copyProperties(billingCharging, billingChargingFinal, BillingCharging.class);
					}

					// if
					// (billingCharging.getEmploymentConfiguration().getPosition().getPositionCode()
					// .compareTo(employmentConfig.getPosition().getPositionCode()) == 0) {
					//
					// if (billingCharging.getPayFrom().compareTo(payFromA) == 0) {
					// periodAWorkHrs = periodAWorkHrs.add(billingCharging.getWorkHours() == null ?
					// BigDecimal.ZERO
					// : billingCharging.getWorkHours());
					// }
					// if (billingCharging.getPayFrom().compareTo(payFromB) == 0) {
					// periodBWorkHrs = periodBWorkHrs.add(billingCharging.getWorkHours() == null ?
					// BigDecimal.ZERO
					// : billingCharging.getWorkHours());
					// }
					//
					// // if (billingCharging.getPayFrom().compareTo(payFromA) == 0) {
					// // periodAWorkDays = periodAWorkDays
					// // .add(billingCharging.getRegularDays() == null ? BigDecimal.ZERO
					// // : new BigDecimal(billingCharging.getRegularDays()));
					// // }
					// // if (billingCharging.getPayFrom().compareTo(payFromB) == 0) {
					// // periodBWorkDays = periodBWorkDays
					// // .add(billingCharging.getRegularDays() == null ? BigDecimal.ZERO
					// // : new BigDecimal(billingCharging.getRegularDays()));
					// // }
					//
					// ObjectCopyUtil.copyProperties(billingCharging, billingChargingFinal,
					// BillingCharging.class);
					//
					// }

				}
				billingChargingFinal.setPeriodAWorkHrs(periodAWorkHrs);
				billingChargingFinal.setPeriodBWorkHrs(periodBWorkHrs);
				billingChargingFinal.setPeriodATotal(periodATotal);
				billingChargingFinal.setPeriodBTotal(periodBTotal);

				BillingRate billingRateOvertime = new BillingRate();
				billingRateOvertime = this.mainApplication.getBillingRate(billingChargingFinal.getPayroll(), true,
						employmentConfig);

				if (billingRateOvertime != null) {
					billingChargingFinal.setBillingRate(billingRateOvertime);
				}

				// finalization for this billingCharging by emp config
				boolean isEmployeeAndPositionExists = billingChargingFinalList.stream()
						.filter(p -> p.getEmployee().getEmployeeCode().compareTo(employee.getEmployeeCode()) == 0
								&& p.getEmploymentConfiguration().getPrimaryKey()
										.compareTo(employmentConfig.getPrimaryKey()) == 0)
						.findFirst().isPresent();

				if (!isEmployeeAndPositionExists) {
					billingChargingFinalList.add(billingChargingFinal);
				}
			}
		}

		System.out.println("billingChargingFinalList: " + billingChargingFinalList.size());

		parameters.put(JRParameter.REPORT_CONNECTION, new ReportMain().getDatabaseConnection());

		JRBeanCollectionDataSource jRBeanCollectionDataSource = new JRBeanCollectionDataSource(
				billingChargingFinalList);
		InputStream inputStream = reportMain.getClass().getResourceAsStream("reports/BillingCharging.jasper");
		ReportUtil.showReportBeanCollection(inputStream, parameters, jRBeanCollectionDataSource);

		ProcessingMessage.closeProcessingMessage();

	}

	public void filterStatementOfAccountRegular() {
		// if
		// (this.mainApplication.getBillingRegisterProcessingMain().showFilterBilling())
		// {
		ObservableList<StatementOfAccount> soaRegularObservableList = FXCollections.observableArrayList();
		Integer regularBillingPrikey = 1;

		soaRegularObservableList.setAll(this.mainApplication.getObservableListStatementOfAccount().stream()
				.filter(p -> p.getBillingType().getPrimaryKey().equals(regularBillingPrikey))
				.collect(Collectors.toList()));

		this.tableViewSOA.setItems(soaRegularObservableList);
		TableColumnUtil.setColumn(this.tableColumnSOA, p -> p.getStatementOfAccountCode());
		TableColumnUtil.setColumn(this.tableColumnClient,
				p -> p.getClient() != null ? p.getClient().getClientCode() : "");
		TableColumnUtil.setColumn(this.tableColumnDepartment,
				p -> p.getDepartment() != null ? p.getDepartment().getDepartmentCode() : "");
		TableColumnUtil.setColumn(this.tableColumnAmountDue, p -> p.getAmountOfNet() != null ? p.getAmountOfNet() : "");
		// }
	}

	public boolean isValidChecker(StatementOfAccount statementOfAccount) {
		System.out.println("isValidChecker");
		// List<AutoFillComboBox<?>> autoFillComboBoxList = new ArrayList<>();
		// autoFillComboBoxList.add(this.autoFillComboBoxApprovedBySOA);
		// autoFillComboBoxList.add(this.autoFillComboBoxCheckedBySOA);

		// boolean isEmpty =
		// FieldValidator.autoFillComboBoxValidate(autoFillComboBoxList);
		billingClientConfiguration = this.mainApplication.getBillingRegisterProcessingMain()
				.getBillingClientConfigurationClientAndGroup(statementOfAccount.getClient(),
						statementOfAccount.getClientGroup());
		billingSoaConfiguration = this.getClientAddress(billingClientConfiguration, statementOfAccount);

		System.out.println(this.billingClientConfiguration == null);
		System.out.println(this.billingSoaConfiguration == null);

		if (billingSoaConfiguration == null) {
			AlertUtil.showAlert("Client Address Details", "No Client Address", "Please add client address details",
					AlertType.INFORMATION, this.mainApplication.getPrimaryStage());
			return false;
		} else {
			// if (!isEmpty) {
			// this.autoFillComboBoxCheckedBySOA.setValue(billingSoaConfiguration.getChecked()
			// != null
			// ? billingSoaConfiguration.getChecked().getEmployeeName()
			// : "");
			// this.autoFillComboBoxApprovedBySOA.setValue(billingSoaConfiguration.getApproved()
			// != null
			// ? billingSoaConfiguration.getApproved().getEmployeeName()
			// : "");
			// }
		}

		// if (FieldValidator.autoFillComboBoxValidate(autoFillComboBoxList) == false) {
		// AlertUtil.showIncompleteDataAlert(this.mainApplication.getPrimaryStage());
		// return false;
		// }

		return true;
	}

	public BillingSoaConfiguration getClientAddress(BillingClientConfiguration billingClientConfiguration,
			StatementOfAccount statementOfAccount) {
		BillingSoaConfiguration clientAddress = new BillingSoaConfiguration();
		if (statementOfAccount.getClient() != null) {
			if (billingClientConfiguration.getIsBillingPerDepartment()) {
				clientAddress = this.mainApplication.getBillingRegisterProcessingMain().getBillingSoaConfigurationMain()
						.getBillingSoaConfigurationByClient(statementOfAccount.getClient(),
								statementOfAccount.getDepartment());
				if (clientAddress == null) {
					clientAddress = this.mainApplication.getBillingRegisterProcessingMain()
							.getBillingSoaConfigurationMain()
							.getBillingSoaConfigurationByClient(statementOfAccount.getClient(), null);
				}
			} else {
				clientAddress = this.mainApplication.getBillingRegisterProcessingMain().getBillingSoaConfigurationMain()
						.getBillingSoaConfigurationByClient(statementOfAccount.getClient(),
								statementOfAccount.getDepartment());
			}

		} else {
			clientAddress = this.mainApplication.getBillingRegisterProcessingMain().getBillingSoaConfigurationMain()
					.getBillingSoaConfigurationByGroup(statementOfAccount.getClientGroup());
		}
		return clientAddress;
	}

	public void setParameterMap() {
		this.mainApplication.getParameterMapPrinting().put("approvedBy",
				this.autoFillComboBoxApprovedBySOA.getValueObject().getEmployeeName());
		this.mainApplication.getParameterMapPrinting().put("approverPosition",
				this.autoFillComboBoxApprovedBySOA.getValueObject().getPosition());
		this.mainApplication.getParameterMapPrinting().put("checkedBy",
				this.autoFillComboBoxCheckedBySOA.getValueObject().getEmployeeName());
		this.mainApplication.getParameterMapPrinting().put("checkerPosition",
				this.autoFillComboBoxCheckedBySOA.getValueObject().getPosition());
		this.mainApplication.getParameterMapPrinting().put("BIRNumberEncode",
				this.textFieldBIRNo.getText() == null ? "" : this.textFieldBIRNo.getText());

		String firstName = this.mainApplication.getUser().getEmploymentHistory().getEmployee().getFirstName();

		String middleInitial = !this.mainApplication.getUser().getEmploymentHistory().getEmployee().getMiddleName()
				.isEmpty()
						? this.mainApplication.getUser().getEmploymentHistory().getEmployee().getMiddleName()
								.substring(0, 1) + ". "
						: "";
		String surname = this.mainApplication.getUser().getEmploymentHistory().getEmployee().getSurname();

		String fullName = firstName + " " + middleInitial + surname;

		this.mainApplication.getParameterMapPrinting().put("preparedBy", fullName);

		Signatory signatory = ObservableListUtil.getObject(this.mainApplication.getSignatoryMain().getAllSignatory(),
				P -> P.getEmployee().getEmployeeCode().compareTo(
						this.mainApplication.getUser().getEmploymentHistory().getEmployee().getEmployeeCode()) == 0);

		this.mainApplication.getParameterMapPrinting().put("preparedByPosition",
				signatory != null ? signatory.getPosition() : "");

	}

	public boolean printBillingReport(StatementOfAccount statementOfAccount) {
		System.out.println("printBillingReport");
		System.out.println("statementOfAccount: ");
		System.out.println(statementOfAccount == null);
		try {
			if (statementOfAccount != null) {

				System.out.println("\nsoaPriKey: " + statementOfAccount.getPrimaryKey());

				if (this.isValidChecker(statementOfAccount)) {

					if (statementOfAccount.getBillingType().getPrimaryKey().equals(1)) {
						this.mainApplication.getBillingRegisterProcessingMain()
								.showBrowseBillingAttachment(this.getStage());
						if (this.mainApplication.getBillingRegisterProcessingMain().getIsOkay()) {
							this.executePrintBillingRegister(statementOfAccount);
							return true;
						}
					} else {
						this.executePrintBillingRegister(statementOfAccount);
						return true;
					}

				}
			} else {
				AlertUtil.showAlert("", "", "Please select Statement of Account to print.", AlertType.INFORMATION,
						this.getStage());
				return false;
			}
		} catch (Exception e) {
			AlertUtil.showExceptionAlert(e, this.mainApplication.getPrimaryStage());
			// this.mainApplication.closeProcessingMessage();
		}
		// this.autoFillComboBoxCheckedBySOA.setValue("");
		// this.autoFillComboBoxApprovedBySOA.setValue("");
		return false;
	}

	public void executePrintBillingRegister(StatementOfAccount statementOfAccount) {

		if (statementOfAccount.getBillingType().getPrimaryKey().equals(10)) {
			System.out.println("\nSOA OT");
			System.out.println("soa prikey ot: " + statementOfAccount.getPrimaryKey());
		}

		this.mainApplication.getParameterMapPrinting().clear();

		ProcessingMessage.showProcessingMessage(this.mainApplication.getPrimaryStage(), "Printing reports.");
		PrintBillingStandard printBillingStandard = this.getPrintBillingReport(statementOfAccount);

		this.observableListBillingPrint.clear();
		this.observableListBillingPrint.addAll(this.mainApplication.getBillingRegisterProcessingMain().getBillingMain()
				.getBillingBySoa(statementOfAccount.getPrimaryKey()));

		System.out.println("observableListBillingPrint: " + this.observableListBillingPrint.size());

		this.listSuppliesBreakdownToPrint.clear();
		this.listSuppliesBreakdownToPrint.addAll(this.mainApplication.getBillingRegisterProcessingMain()
				.getSuppliesBreakdownMain().getAllSuppliesBreakdownBySoaKey(statementOfAccount.getPrimaryKey()));
		System.out.println("listSuppliesBreakdownToPrint:" + this.listSuppliesBreakdownToPrint.size());

		this.initializePrintBillingStandardProperties(printBillingStandard, statementOfAccount);
		this.setParameterMap();
		this.setParameterOriginal(statementOfAccount);

		try {
			printBillingStandard.printBilling(statementOfAccount, this.mainApplication.getParameterMapPrinting(),
					this.observableListBillingPrint);
		} catch (ParserConfigurationException | SAXException | IOException | TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ProcessingMessage.closeProcessingMessage();
	}

	public void setParameterOriginal(StatementOfAccount statementOfAccount) {
		StatementOfAccount statementOfAccountOriginal = this.mainApplication.getStatementOfAccountMain()
				.getOriginalStatementOfAccount(statementOfAccount.getOriginalStatementOfAccountCode());
		if (statementOfAccountOriginal != null) {
			this.mainApplication.getParameterMapPrinting().put("soaCodeOriginal",
					statementOfAccountOriginal.getStatementOfAccountCode() == null ? ""
							: statementOfAccountOriginal.getStatementOfAccountCode());
			this.mainApplication.getParameterMapPrinting().put("BIRNumberOriginal",
					statementOfAccountOriginal.getBirNumber());
			this.mainApplication.getParameterMapPrinting().put("seqNumber", statementOfAccount.getSequenceNumber());

			String concatMessage = "";

			if (statementOfAccountOriginal.getBillingInvoiceNumber() != null) {
				concatMessage = " with BI# " + statementOfAccountOriginal.getBillingInvoiceNumber();
			} else {
				concatMessage = " with BIR# " + statementOfAccountOriginal.getBirNumber();
			}

			this.mainApplication.getParameterMapPrinting().put("cancelRemarks",
					"* This cancels SOA# ina" + statementOfAccountOriginal.getStatementOfAccountCode() + concatMessage);

		}
	}

	public void initializePrintBillingStandardProperties(PrintBillingStandard printBillingStandard,
			StatementOfAccount statementOfAccount) {
		printBillingStandard.setReportMain(this.mainApplication.getBillingRegisterProcessingMain().getReportMain());
		printBillingStandard.setBillingClientConfiguration(billingClientConfiguration);
		printBillingStandard.setBillingSoaConfiguration(billingSoaConfiguration);
		printBillingStandard.setObservableListClientContractBreakdown(
				this.mainApplication.getBillingRegisterProcessingMain().getObservableListClientContractBreakdown());
		printBillingStandard.setObservableListAttachment(
				this.mainApplication.getBillingRegisterProcessingMain().getObservableListAttachment());
		printBillingStandard.setBillingPayrollDetailsMain(
				this.mainApplication.getBillingRegisterProcessingMain().getBillingPayrollDetailsMain());
		printBillingStandard.setListBilling(this.observableListBillingPrint);
		printBillingStandard.setListSuppliesBreakdown(this.listSuppliesBreakdownToPrint);
		if (statementOfAccount.getClient() != null) {
			printBillingStandard.setPayrollClientConfiguration(this.mainApplication.getBillingRegisterProcessingMain()
					.getPayrollClientConfiguration(statementOfAccount.getClient(), null));
		} else if (statementOfAccount.getClientGroup() != null) {
			printBillingStandard.setPayrollClientConfiguration(this.mainApplication.getBillingRegisterProcessingMain()
					.getPayrollClientConfiguration(null, statementOfAccount.getClientGroup()));
		}
		printBillingStandard.setSoaBreakdownDetailsMain(
				this.mainApplication.getBillingRegisterProcessingMain().getSoaBreakdownDetailsMain());
		printBillingStandard.setUser(this.mainApplication.getUser());
		printBillingStandard.setCompanyMain(this.mainApplication.getCompanyMain());

		printBillingStandard.setStage(this.mainApplication.getPrimaryStage());
		printBillingStandard.setComputerName(this.mainApplication.getComputerName());
		printBillingStandard.setComputerNumber(this.mainApplication.getComputerNumber());
		printBillingStandard.setPayrollMain(this.mainApplication.getBillingRegisterProcessingMain().getPayrollMain());
		printBillingStandard.setStatementOfAccountMain(this.mainApplication.getStatementOfAccountMain());
		printBillingStandard.setBillingSoaConfigurationMain(
				this.mainApplication.getBillingRegisterProcessingMain().getBillingSoaConfigurationMain());
		printBillingStandard.setReportMain(this.mainApplication.getBillingRegisterProcessingMain().getReportMain());
		printBillingStandard.setMiscellaneousAdjustmentDetailsMain(
				this.mainApplication.getBillingRegisterProcessingMain().getMiscellaneousAdjustmentDetailsMain());
		printBillingStandard.setMiscellaneousAdjustmentMain(
				this.mainApplication.getBillingRegisterProcessingMain().getMiscellaneousAdjustmentMain());
		printBillingStandard.setMandatoryContributionMain(
				this.mainApplication.getBillingRegisterProcessingMain().getMandatoryContributionMain());
		printBillingStandard.setReferenceDocumentCodeMain(
				this.mainApplication.getBillingRegisterProcessingMain().getReferenceDocumentCodeMain());
		printBillingStandard
				.setAllowanceMain(this.mainApplication.getBillingRegisterProcessingMain().getAllowanceMain());
	}

	public PrintBillingStandard getPrintBillingReport(StatementOfAccount statementOfAccount) {
		try {
			BillingReportClass billingReportClass = this.mainApplication.getBillingReportClassMain()
					.getBillingReportClass(statementOfAccount);
			if (billingReportClass != null) {
				System.out.println(billingReportClass.getClassName());
				Class<?> c = Class.forName("ph.com.lbpsc.production.billingregisterprocessing.printbilling."
						+ billingReportClass.getClassName());
				System.out.println(c);
				return (PrintBillingStandard) c.newInstance();
			}
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return new PrintBillingStandard();
	}

	public void handlePrint() {
		System.out.println("PRINT");

		StatementOfAccount statementOfAccount = this.tableViewSOA.getSelectionModel().getSelectedItem();
		StatementOfAccount statementOfAccountOvertime = this.tableViewSOAOT.getSelectionModel().getSelectedItem();

		List<AutoFillComboBox<?>> autoFillComboBoxList = new ArrayList<>();

		autoFillComboBoxList.add(this.autoFillComboBoxApprovedBy);
		autoFillComboBoxList.add(this.autoFillComboBoxApprovedBySOA);
		autoFillComboBoxList.add(this.autoFillComboBoxCertifiedCorrect);
		autoFillComboBoxList.add(this.autoFillComboBoxCheckedBySOA);

		System.out.println(FieldValidator.autoFillComboBoxValidate(autoFillComboBoxList));

		if (statementOfAccount != null
				&& (!this.labelClient.getText().isEmpty() || !this.labelClientGroup.getText().isEmpty())
				&& FieldValidator.autoFillComboBoxValidate(autoFillComboBoxList)) {
			System.out.println("pass");
			System.out.println("soa prikey:" + statementOfAccount.getPrimaryKey());

			// this.printBillingCharging();
			// this.printBillingRegister(statementOfAccount);

			if (this.printBillingReport(statementOfAccount)) {
				System.out.println("pass2");
				System.out.println(statementOfAccountOvertime == null);

				if (statementOfAccountOvertime != null) {
					this.printBillingReport(statementOfAccountOvertime);
				}

				this.printBillingCharging(statementOfAccount);
				this.printBillingChargingSummary(statementOfAccount, listBillingCharging);
				// ProcessingMessage.showProcessingMessage(this.stage);
				// this.printBillingChargingSummary(statementOfAccount);
				// ProcessingMessage.closeProcessingMessage();
			}

		} else {
			AlertUtil.showInformationAlert("Complete all necessary fields", this.getStage());
			return;
		}
	}

	public void printBillingChargingSummary(StatementOfAccount statementOfAccount,
			List<BillingCharging> listBillingCharging) {
		ReportMain reportMain = new ReportMain();
		Map<String, Object> parameters = new HashMap<String, Object>();

		if (listBillingCharging.isEmpty() || listBillingCharging == null) {
			return;
		}

		String imagePath = System.getenv("LOCALAPPDATA").concat(
				File.separator + "IFMIS" + File.separator + "app" + File.separator + "resources" + File.separator);

		Date payFromA = DateFormatter.toDate(this.formattedDatePickerPayPeriodFrom.getValue());
		Date payToA = DateUtil.getCutOffDateOfPayTo(payFromA);
		Date payFromB = DateUtil.addDayOrMonthToDate(payFromA, 0, 15);
		Date payToB = DateUtil.getCutOffDateOfPayTo(payFromB);

		String periodA = new SimpleDateFormat("MMMM dd-").format(payFromA)
				.concat(new SimpleDateFormat("dd").format(payToA));
		String periodB = new SimpleDateFormat("MMMM dd-").format(payFromB)
				.concat(new SimpleDateFormat("dd").format(payToB));

		parameters.put("imagePath", imagePath);
		parameters.put("client_name",
				this.labelClient.getText().isEmpty() ? this.labelClientGroup.getText() : this.labelClient.getText());
		parameters.put("clientCode", this.client.getClientCode());
		parameters.put("pay_from", DateFormatter.toDate(this.formattedDatePickerPayPeriodFrom.getValue()));
		parameters.put("pay_to", DateFormatter.toDate(this.formattedDatePickerPayPeriodTo.getValue()));

		parameters.put("checkedBy", this.autoFillComboBoxCertifiedCorrect.getValueObject().getEmployeeName());
		parameters.put("checkerPosition", this.autoFillComboBoxCertifiedCorrect.getValueObject().getPosition());
		parameters.put("approvedBy", this.autoFillComboBoxApprovedBy.getValueObject().getEmployeeName());
		parameters.put("approverPosition", this.autoFillComboBoxApprovedBy.getValueObject().getPosition());
		parameters.put("preparedBy",
				this.mainApplication.getUser().getEmploymentHistory().getEmployee().getEmployeeFullName());
		parameters.put("preparedByPosition",
				this.mainApplication.getUser().getEmploymentHistory().getPosition().getPositionName());
		parameters.put("periodA", periodA);
		parameters.put("periodB", periodB);
		parameters.put("payFromA", payFromA);
		parameters.put("payFromB", payFromB);

		List<BillingCharging> billingChargingFinalList = new ArrayList<>();
		List<Billing> billingRegisterList = new ArrayList<>();
		// List<Overtime> billingChargingOvertimeList = new ArrayList<>();
		// billingChargingOvertimeList.addAll(this.mainApplication.getOvertimeMain()
		// .getBillingChargingOvertimeByBillingChargingPrikey(this.billingCharging.getPrimaryKey()));

		// BigDecimal amountExtendedDuty = BigDecimal.ZERO;

		// for (Overtime overtime : billingChargingOvertimeList) {
		// amountExtendedDuty = amountExtendedDuty.add(overtime.getAmountOfOvertime());
		// }

		BigDecimal totalAmountExtendedDuty = BigDecimal.ZERO;

		for (BillingCharging billingCharging : listBillingCharging) {

			// totalAmount =
			// billingCharging.getBillingRate().getAmountOfDailyBillingRate().divide(new
			// BigDecimal("8"))
			// .multiply(billingCharging.getWorkHours()).setScale(2, RoundingMode.HALF_UP);
			if (billingCharging.getOvertimeList() != null || !billingCharging.getOvertimeList().isEmpty()) {
				for (Overtime overtime : billingCharging.getOvertimeList()) {
					if (overtime.getIsExtendedDuty()) {
						totalAmountExtendedDuty = totalAmountExtendedDuty.add(overtime.getAmountOfOvertime());
					}
				}
			}

			// totalAmount = billingCharging.getOvertimeList()).setScale(2,
			// RoundingMode.HALF_UP);
		}

		// totalAmountExtendedDuty = this.billingCharging.get

		// assuming na regular billing lang to
		Integer billingType = statementOfAccount.getBillingType().getPrimaryKey();
		Integer billType = 0;
		if (billingType.equals(1)) {
			billType = 1;
		}

		if (!billType.equals(1)) {
			System.out.println("not reg bill");
			return;
		}

		Integer billingConfig = this.billingClientConfiguration.getIsBillingRegisterSeparateReliever() == true ? 1 : 0;

		BigDecimal amountRegularBilling = BigDecimal.ZERO;
		billingRegisterList.addAll(this.mainApplication.getBillingMain().getBillingReportBySproc(
				statementOfAccount.getBatchDate(), statementOfAccount.getPrimaryKey(), billType, billingConfig));

		System.out.println("billingRegisterList: " + billingRegisterList.size());

		for (Billing billing : billingRegisterList) {
			if (billing.getAmountOfGrossPay() != null) {
				amountRegularBilling = amountRegularBilling.add(billing.getAmountOfGrossPay());
			}
		}

		String clientRegularBilling = statementOfAccount.getClient().getClientName();
		String clientExtendedDuty = this.labelClient.getText().isEmpty() ? this.labelClientGroup.getText()
				: this.labelClient.getText();

		BigDecimal totalBilling = BigDecimal.ZERO;
		totalBilling = totalAmountExtendedDuty.add(amountRegularBilling);

		parameters.put("amountExtendedDuty", totalAmountExtendedDuty);
		parameters.put("amountRegularBilling", amountRegularBilling);
		parameters.put("clientRegularBilling", clientRegularBilling);
		parameters.put("clientExtendedDuty", clientExtendedDuty);
		parameters.put("totalBilling", totalBilling);

		parameters.put(JRParameter.REPORT_CONNECTION, new ReportMain().getDatabaseConnection());
		JRBeanCollectionDataSource jRBeanCollectionDataSource = new JRBeanCollectionDataSource(
				billingChargingFinalList);
		InputStream inputStream = reportMain.getClass().getResourceAsStream("reports/BillingChargingSummary.jasper");
		ReportUtil.showReportBeanCollection(inputStream, parameters, jRBeanCollectionDataSource);

	}

	public void handleRetrieveOvertimeSOA() {
		System.out.println("handleRetrieveOvertimeSOA");
		if (this.mainApplication.showFilterBilling(true)) {
			// TODO
			// FILTER THIS NA DAPAT SOA NA FOR OT LANG NASA LIST NA TO
			// this.statementOfAccountRegularList.setAll(this.mainApplication.getObservableListStatementOfAccount());
			ObservableList<StatementOfAccount> soaOvertimeObservableList = FXCollections.observableArrayList();
			Integer regularOvertimePrikey = 10;

			soaOvertimeObservableList.setAll(this.mainApplication.getObservableListStatementOfAccount().stream()
					.filter(p -> p.getBillingType().getPrimaryKey().equals(regularOvertimePrikey)).distinct()
					.collect(Collectors.toList()));

			// this.mainApplication.getObservableListStatementOfAccount().forEach(p -> {
			// System.out.println(p.getBillingType().getBillingTypeCode());
			// if (p.getBillingType().getPrimaryKey().compareTo(10) == 0) {
			// if (!this.statementOfAccountRegularList.contains(p)) {
			// this.statementOfAccountRegularList.add(p);
			// }
			// }
			// });

			this.tableViewSOAOT.setItems(soaOvertimeObservableList);
			TableColumnUtil.setColumn(this.tableColumnSOAOT, p -> p.getStatementOfAccountCode());
			TableColumnUtil.setColumn(this.tableColumnClientSOAOT,

					p -> p.getClient() != null ? p.getClient().getClientCode() : "");
			TableColumnUtil.setColumn(this.tableColumnDepartmentSOAOT,
					p -> p.getDepartment() != null ? p.getDepartment().getDepartmentCode() : "");
			TableColumnUtil.setColumn(this.tableColumnAmountDueSOAOT,
					p -> p.getAmountOfNet() != null ? p.getAmountOfNet() : "");
		}
	}

	// public void setMainApplication(BillingChargingMain mainApplication, Stage
	// dialogStage) {
	// this.mainApplication = mainApplication;
	// this.dialogStage = dialogStage;
	//
	// this.buttonOk.setDisable(true);
	// }
	//
	// public void closeDialogStage() {
	// this.dialogStage.close();
	// }

	public void handleCancel() {
		this.stage.close();
	}

	public Stage getStage() {
		return stage;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public Date getPayFrom() {
		return payFrom;
	}

	public void setPayFrom(Date payFrom) {
		this.payFrom = payFrom;
	}

	public Date getPayTo() {
		return payTo;
	}

	public void setPayTo(Date payTo) {
		this.payTo = payTo;
	}

	public ObservableList<Client> getObservableListClient() {
		return observableListClient;
	}

	public void setObservableListClient(ObservableList<Client> observableListClient) {
		this.observableListClient = observableListClient;
	}

	// public boolean isOkClicked() {
	// return isOkClicked;
	// }
	//
	// public void setOkClicked(boolean isOkClicked) {
	// this.isOkClicked = isOkClicked;
	// }

	public BillingCharging getBillingCharging() {
		return billingCharging;
	}

	public void setBillingCharging(BillingCharging billingCharging) {
		this.billingCharging = billingCharging;
	}

	public ClientGroup getClientGroup() {
		return clientGroup;
	}

	public void setClientGroup(ClientGroup clientGroup) {
		this.clientGroup = clientGroup;
	}

	public Date getBatchDate() {
		return batchDate;
	}

	public void setBatchDate(Date batchDate) {
		this.batchDate = batchDate;
	}

	@FXML
	private Button buttonOk;
	@FXML
	private Button buttonCancel;
	@FXML
	private Button buttonRetrieveSOAOT;
	// @FXML
	// private AutoFillComboBox<Client> autoFillComboBoxClient;
	@FXML
	private FormattedDatePicker formattedDatePickerPayPeriodFrom;
	@FXML
	private FormattedDatePicker formattedDatePickerPayPeriodTo;
	@FXML
	private AutoFillComboBox<Signatory> autoFillComboBoxCertifiedCorrect;
	@FXML
	private AutoFillComboBox<Signatory> autoFillComboBoxApprovedBy;
	@FXML
	private AutoFillComboBox<Signatory> autoFillComboBoxCheckedBySOA;
	@FXML
	private AutoFillComboBox<Signatory> autoFillComboBoxApprovedBySOA;
	@FXML
	private AutoFillComboBox<Signatory> autoFillComboBoxCheckedBySOAOT;
	@FXML
	private AutoFillComboBox<Signatory> autoFillComboBoxApprovedBySOAOT;
	@FXML
	private ValidatedTextField textFieldBIRNo;
	@FXML
	private ValidatedTextField textFieldBIRNoSOAOT;
	@FXML
	private TableView<StatementOfAccount> tableViewSOA;
	@FXML
	private TableColumn<StatementOfAccount, String> tableColumnSOA;
	@FXML
	private TableColumn<StatementOfAccount, String> tableColumnClient;
	@FXML
	private TableColumn<StatementOfAccount, String> tableColumnDepartment;
	@FXML
	private TableColumn<StatementOfAccount, String> tableColumnAmountDue;
	@FXML
	private TableView<StatementOfAccount> tableViewSOAOT;
	@FXML
	private TableColumn<StatementOfAccount, String> tableColumnSOAOT;
	@FXML
	private TableColumn<StatementOfAccount, String> tableColumnClientSOAOT;
	@FXML
	private TableColumn<StatementOfAccount, String> tableColumnDepartmentSOAOT;
	@FXML
	private TableColumn<StatementOfAccount, String> tableColumnAmountDueSOAOT;
	@FXML
	private Label labelClient;
	@FXML
	private Label labelClientGroup;
}
