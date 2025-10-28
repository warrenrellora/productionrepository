package ph.com.lbpsc.production.annualizationimportdetails.data.mapper;

import java.util.List;

import ph.com.lbpsc.production.annualizationimportdetails.model.AnnualizationImportDetails;

public interface AnnualizationImportDetailsMapper {
	public List<AnnualizationImportDetails> getAllAnnualizationImportDetails();

	public AnnualizationImportDetails getAnnualizationImportDetailsById(Integer iD);

	public int createAnnualizationImportDetails(AnnualizationImportDetails annualizationImportDetails);

	public int updateAnnualizationImportDetails(AnnualizationImportDetails annualizationImportDetails);

	public int deleteAnnualizationImportDetails(AnnualizationImportDetails annualizationImportDetails);

	public List<AnnualizationImportDetails> getAnnualizationImportDetails(
			AnnualizationImportDetails annualizationImportDetails);

	public List<AnnualizationImportDetails> getAnnualizationImportDetailsByDate(
			AnnualizationImportDetails annualizationImportDetails);
}
