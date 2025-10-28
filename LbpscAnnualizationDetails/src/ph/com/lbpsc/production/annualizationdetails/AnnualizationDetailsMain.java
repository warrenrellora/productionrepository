package ph.com.lbpsc.production.annualizationdetails;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.ibatis.exceptions.PersistenceException;
import org.xml.sax.SAXException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import ph.com.lbpsc.production.annualization.model.Annualization;
import ph.com.lbpsc.production.annualizationbreakdown.model.AnnualizationBreakdown;
import ph.com.lbpsc.production.annualizationdetails.data.AnnualizationDetailsDao;
import ph.com.lbpsc.production.annualizationdetails.model.AnnualizationDetails;
import ph.com.lbpsc.production.annualizationdetails.view.BrowseAnnualizationDetailsController;
import ph.com.lbpsc.production.masterclass.MasterMain;
import ph.com.lbpsc.production.util.AlertUtil;
import ph.com.lbpsc.production.util.ErrorLog;
import ph.com.lbpsc.production.util.FxmlUtil;
import ph.com.lbpsc.production.util.ModificationType;

public class AnnualizationDetailsMain extends MasterMain<AnnualizationDetails> {
	private ObservableList<AnnualizationDetails> observableListAnnualization;

	public AnnualizationDetailsMain()
			throws ParserConfigurationException, SAXException, IOException, TransformerException {
		super(AnnualizationDetails.class);
	}

	@Override
	public boolean createMainObject(AnnualizationDetails annualizationDetails) {
		return this.createAnnualizationDetails(annualizationDetails);
	}

	@Override
	public boolean updateMainObject(AnnualizationDetails annualizationDetails) {
		return this.updateAnnualizationDetails(annualizationDetails);
	}

	@Override
	public boolean deleteMainObject(AnnualizationDetails annualizationDetails) {
		return this.deleteAnnualizationDetails(annualizationDetails);
	}

	@Override
	public AnchorPane getRootLayout() throws IOException {
		return this.getRootLayoutAnnualizationDetails();
	}

	public AnchorPane getRootLayoutAnnualizationDetails() throws IOException {
		this.initializeObjects();
		FxmlUtil<BrowseAnnualizationDetailsController> fxmlUtil = new FxmlUtil<>(
				this.getClass().getResource("view/BrowseAnnualizationDetails.fxml"));
		AnchorPane rootLayout = fxmlUtil.getFxmlPane();
		BrowseAnnualizationDetailsController rootController = fxmlUtil.getController();
		rootController.setMainApplication(this);
		return rootLayout;
	}

	public boolean showBrowseAnnualizationUpload(Stage stage) {
		try {
			FxmlUtil<BrowseAnnualizationDetailsController> fxmlUtil = new FxmlUtil<>(
					this.getClass().getResource("view/BrowseAnnualizationDetails.fxml"));
			AnchorPane browseAnnualizationDetailsLayout = fxmlUtil.getFxmlPane();
			BrowseAnnualizationDetailsController browseAnnualizationDetailsController = fxmlUtil.getController();
			browseAnnualizationDetailsController.setMainApplication(this, ModificationType.ADD);
			return browseAnnualizationDetailsController.showEditDialogStage("Annualization Details", stage,
					browseAnnualizationDetailsLayout, null);
		} catch (IOException e) {
			AlertUtil.showExceptionAlert(e, stage);
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public void initializeObjects() {
		this.observableListAnnualization = FXCollections.observableArrayList();
	}

	public List<AnnualizationDetails> getAllAnnualizationDetails() {
		AnnualizationDetailsDao annualizationDetailsDao = new AnnualizationDetailsDao(sqlSessionFactory);
		return annualizationDetailsDao.getAllData();
	}

	public AnnualizationDetails getAnnualizationDetailsByKey(Integer key) {
		AnnualizationDetailsDao annualizationDetailsDao = new AnnualizationDetailsDao(sqlSessionFactory);
		return annualizationDetailsDao.getDataByKey(key);
	}

	public boolean createAnnualizationDetails(AnnualizationDetails annualizationDetails) {
		try {
			return (new AnnualizationDetailsDao(sqlSessionFactory).createData(annualizationDetails) > 0) ? true : false;
		} catch (PersistenceException e) {
			ErrorLog.showErrorLog(e, this.primaryStage);
		}
		return false;
	}

	public boolean updateAnnualizationDetails(AnnualizationDetails annualizationDetails) {
		try {
			return (new AnnualizationDetailsDao(sqlSessionFactory).updateData(annualizationDetails) > 0) ? true : false;
		} catch (PersistenceException e) {
			ErrorLog.showErrorLog(e, this.primaryStage);
		}
		return false;
	}

	public boolean deleteAnnualizationDetails(AnnualizationDetails annualizationDetails) {
		try {
			return (new AnnualizationDetailsDao(sqlSessionFactory).deleteData(annualizationDetails) > 0) ? true : false;
		} catch (PersistenceException e) {
			ErrorLog.showErrorLog(e, this.primaryStage);
		}
		return false;
	}
	
	public boolean deleteAnnualizationDetailsList(List<AnnualizationDetails> annualizationDetailsList) {
		try {
			return (new AnnualizationDetailsDao(sqlSessionFactory)
					.deleteAnnualizationDetailsList(annualizationDetailsList) > 0) ? true : false;
		} catch (PersistenceException e) {
			ErrorLog.showErrorLog(e, this.primaryStage);
		}
		return false;
	}

	public List<AnnualizationDetails> getAnnualizationDetailsByBreakdown(Annualization annualization,
			AnnualizationBreakdown annualizationBreakdown) {
		AnnualizationDetailsDao annualizationDetailsDao = new AnnualizationDetailsDao(sqlSessionFactory);
		return annualizationDetailsDao.getAnnualizationDetailsByBreakdown(annualization, annualizationBreakdown);
	}

	public void populateAnnulizationDetails(Annualization annualization, AnnualizationBreakdown annualizationBreakdown,
			List<AnnualizationDetails> annualizationDetailsList) {
		this.observableListAnnualization.clear();
		if (annualization != null && annualizationBreakdown != null && annualizationDetailsList != null
				&& !annualizationDetailsList.isEmpty()) {
			List<AnnualizationDetails> showAnnualizationDetailsList = annualizationDetailsList.stream().filter(
					P -> P.getAnnualizationBreakdown().getPrimaryKey().equals(annualizationBreakdown.getPrimaryKey()))
					.collect(Collectors.toList());

			if (showAnnualizationDetailsList != null && !showAnnualizationDetailsList.isEmpty()) {
				this.observableListAnnualization.addAll(showAnnualizationDetailsList);
			}
		}
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		super.start(primaryStage);
		this.primaryStage.setTitle("Annualization Details");
	}

	public static void main(String[] args) {
		launch(args);
	}

	public ObservableList<AnnualizationDetails> getObservableListAnnualization() {
		return observableListAnnualization;
	}
}
