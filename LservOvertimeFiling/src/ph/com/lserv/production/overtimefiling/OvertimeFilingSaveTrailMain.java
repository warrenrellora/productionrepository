package ph.com.lserv.production.overtimefiling;

import java.io.IOException;
import java.util.Date;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import javafx.scene.layout.AnchorPane;
import ph.com.lbpsc.production.recordprocedure.RecordProcedureMain;
import ph.com.lbpsc.production.recordstatus.RecordStatusMain;
import ph.com.lbpsc.production.recordstatusdetails.RecordStatusDetailsMain;
import ph.com.lbpsc.production.recordstatusdetails.model.RecordStatusDetails;
import ph.com.lbpsc.production.util.ErrorLog;
import ph.com.lbpsc.production.util.FxmlUtil;
import ph.com.lbpsc.production.util.ModificationType;
import ph.com.lbpsc.production.util.ObjectCopyUtil;
import ph.com.lserv.production.overtimefiling.model.OvertimeFiling;
import ph.com.lserv.production.overtimefiling.view.EditOvertimeFilingController;
import ph.com.lserv.production.overtimefilingtrail.OvertimeFilingTrailMain;
import ph.com.lserv.production.overtimefilingtrail.model.OvertimeFilingTrail;

public class OvertimeFilingSaveTrailMain extends OvertimeFilingMain {
	RecordStatusDetailsMain recordStatusDetailsMain;
	RecordStatusMain recordStatusMain;
	RecordProcedureMain recordProcedureMain;
	OvertimeFilingTrailMain overtimeFilingTrailMain;
	Integer overtimeFilingTrailCodePreset;
	EditOvertimeFilingController editController;
	
	public OvertimeFilingSaveTrailMain()
			throws ParserConfigurationException, SAXException, IOException, TransformerException {
		super();
	}

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public boolean createMainObject(OvertimeFiling overtimeFiling) {
		return this.createTrailRecord(ModificationType.ADD, overtimeFiling);
	}

	@Override
	public boolean updateMainObject(OvertimeFiling overtimeFiling) {
		return this.createTrailRecord(ModificationType.EDIT, overtimeFiling);
	}

	@Override
	public boolean deleteMainObject(OvertimeFiling overtimeFiling) {
		return this.createTrailRecord(ModificationType.DELETE, overtimeFiling);
	}

	@Override
	public void initializeObjects() {
		super.initializeObjects();
		try {
			this.overtimeFilingTrailMain = new OvertimeFilingTrailMain();
			this.recordStatusDetailsMain = new RecordStatusDetailsMain();
			this.recordStatusMain = new RecordStatusMain();
			this.recordProcedureMain = new RecordProcedureMain();
		} catch (ParserConfigurationException | SAXException | IOException | TransformerException e) {
			ErrorLog.showErrorLog(e, this.primaryStage);
			e.printStackTrace();
		}
	}

	public AnchorPane getEditLayoutOvertimeFiling() throws IOException {
		FxmlUtil<EditOvertimeFilingController> fxmlUtil = new FxmlUtil<>(
				this.getClass().getResource("view/EditOvertimeFiling.fxml"));
		AnchorPane rootlayout = fxmlUtil.getFxmlPane();
		this.editController = fxmlUtil.getController();
		editController.setMainApplication(this);
		return rootlayout;
	}
	
	public boolean createTrailRecord(ModificationType modificationType, OvertimeFiling overtimeFiling) {
		OvertimeFilingTrail overtimeFilingTrail = new OvertimeFilingTrail();
		RecordStatusDetails recordStatusDetails = this.recordStatusDetailsMain.getRecordStatusDetailsPendingTrail(1,
				"overtime_filing_trail");

		if (this.recordStatusDetailsMain.createRecordStatusDetails(recordStatusDetails)) {
			ObjectCopyUtil.copyProperties(overtimeFiling, overtimeFilingTrail, OvertimeFiling.class);

			if (overtimeFiling.getPrimaryKey() == null) {
				modificationType = ModificationType.ADD;
			}

			switch (modificationType) {
			case ADD:
				recordStatusDetails.setSecondRecordStatus(this.recordStatusMain.getRecordStatusByID(0));
				
//				overtimeFilingTrail.setRecordProcedure(this.recordProcedureMain.getRecordProcedureByID(1));
				if (this.recordStatusDetailsMain.updateRecordStatusDetails(recordStatusDetails)) {
					overtimeFilingTrail.setChangedByUser(this.getUser().getUserName());
					overtimeFilingTrail.setChangedOnDate(new Date());
					overtimeFilingTrail.setChangedInComputer(this.getComputerName());
					overtimeFilingTrail.setUser(this.getUser());
					overtimeFilingTrail.setRecordProcedure(this.recordProcedureMain.getRecordProcedureByID(1));
				}
				
				
				break;
			case EDIT:
				overtimeFilingTrail.setRecordProcedure(this.recordProcedureMain.getRecordProcedureByID(2));
				break;
			case DELETE:
				recordStatusDetails.setSecondRecordStatus(this.recordStatusMain.getRecordStatusByID(0));
				if (this.recordStatusDetailsMain.updateRecordStatusDetails(recordStatusDetails)) {
					overtimeFilingTrail.setChangedByUser(this.getUser().getUserName());
					overtimeFilingTrail.setChangedOnDate(new Date());
					overtimeFilingTrail.setChangedInComputer(this.getComputerName());
					overtimeFilingTrail.setUser(this.getUser());
					overtimeFilingTrail.setRecordProcedure(this.recordProcedureMain.getRecordProcedureByID(3));
				}
				break;

			default:
				break;
			}

			overtimeFilingTrail.setRecordStatus(this.recordStatusMain.getRecordStatusByID(0));
			overtimeFilingTrail.setRecordStatusDetails(recordStatusDetails);
			overtimeFilingTrail.setOvertimeFilingTrailCodePreset(this.overtimeFilingTrailCodePreset);
			
			
			if (this.overtimeFilingTrailMain.createMainObject(overtimeFilingTrail)) {
				return true;
			}
		}
		return false;
	}

	public Integer getOvertimeFilingTrailCodePreset() {
		return overtimeFilingTrailCodePreset;
	}

	public void setOvertimeFilingTrailCodePreset(Integer overtimeFilingTrailCodePreset) {
		this.overtimeFilingTrailCodePreset = overtimeFilingTrailCodePreset;
	}

	public EditOvertimeFilingController getEditController() {
		return editController;
	}

	public void setEditController(EditOvertimeFilingController editController) {
		this.editController = editController;
	}

	public RecordStatusDetailsMain getRecordStatusDetailsMain() {
		return recordStatusDetailsMain;
	}

	public void setRecordStatusDetailsMain(RecordStatusDetailsMain recordStatusDetailsMain) {
		this.recordStatusDetailsMain = recordStatusDetailsMain;
	}

	public RecordStatusMain getRecordStatusMain() {
		return recordStatusMain;
	}

	public void setRecordStatusMain(RecordStatusMain recordStatusMain) {
		this.recordStatusMain = recordStatusMain;
	}

	public RecordProcedureMain getRecordProcedureMain() {
		return recordProcedureMain;
	}

	public void setRecordProcedureMain(RecordProcedureMain recordProcedureMain) {
		this.recordProcedureMain = recordProcedureMain;
	}

	public OvertimeFilingTrailMain getOvertimeFilingTrailMain() {
		return overtimeFilingTrailMain;
	}

	public void setOvertimeFilingTrailMain(OvertimeFilingTrailMain overtimeFilingTrailMain) {
		this.overtimeFilingTrailMain = overtimeFilingTrailMain;
	}

}
