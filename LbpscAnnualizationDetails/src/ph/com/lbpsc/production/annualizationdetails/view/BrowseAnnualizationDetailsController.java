package ph.com.lbpsc.production.annualizationdetails.view;

import java.text.DecimalFormat;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import ph.com.lbpsc.production.annualizationdetails.AnnualizationDetailsMain;
import ph.com.lbpsc.production.annualizationdetails.model.AnnualizationDetails;
import ph.com.lbpsc.production.masterclass.MasterEditController;

public class BrowseAnnualizationDetailsController
		extends MasterEditController<AnnualizationDetails, AnnualizationDetailsMain> {

	@Override
	public boolean isValid() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onSave() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onShowEditDialogStage() {
		// TODO Auto-generated method stub

	}

	@Override
	public void configureAccess() {
		// TODO Auto-generated method stub

	}

	@Override
	public void showDetails(AnnualizationDetails annualizationDetails) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSetMainApplication() {
		this.tableViewAnnualizationDetails.setItems(this.mainApplication.getObservableListAnnualization());

		this.tableColumnAnnualizationItems.setCellValueFactory(P -> {
			SimpleStringProperty property = new SimpleStringProperty();
			if (P.getValue() != null && P.getValue().getAnnualizationItems() != null) {
				property.setValue(P.getValue().getAnnualizationItems().getItemsName());
			}
			return property;
		});

		this.tableColumnAnnualizationAmount.setCellValueFactory(P -> {
			SimpleStringProperty property = new SimpleStringProperty();
			DecimalFormat decimalFormat = new DecimalFormat("###,###,##0.00");
			if (P.getValue() != null && P.getValue().getAmount() != null) {
				property.setValue(decimalFormat.format(P.getValue().getAmount()));
			}
			return property;
		});

		this.tableColumnAnnualizationDays.setCellValueFactory(P -> {
			SimpleStringProperty property = new SimpleStringProperty();
			DecimalFormat decimalFormat = new DecimalFormat("###,###,##0.0000");
			if (P.getValue() != null && P.getValue().getDays() != null) {
				property.setValue(decimalFormat.format(P.getValue().getDays()));
			}
			return property;
		});
	}

	@FXML
	private TableView<AnnualizationDetails> tableViewAnnualizationDetails;

	@FXML
	private TableColumn<AnnualizationDetails, String> tableColumnAnnualizationItems;

	@FXML
	private TableColumn<AnnualizationDetails, String> tableColumnAnnualizationAmount;

	@FXML
	private TableColumn<AnnualizationDetails, String> tableColumnAnnualizationDays;

}
