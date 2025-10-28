package ph.com.lbpsc.production.annualizationdetails.data.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import ph.com.lbpsc.production.annualization.model.Annualization;
import ph.com.lbpsc.production.annualizationbreakdown.model.AnnualizationBreakdown;
import ph.com.lbpsc.production.annualizationdetails.model.AnnualizationDetails;

public interface AnnualizationDetailsMapper {
	public List<AnnualizationDetails> getAllAnnualizationDetails();

	public AnnualizationDetails getAnnualizationDetailsById(Integer iD);

	public int createAnnualizationDetails(AnnualizationDetails annualizationDetails);

	public int updateAnnualizationDetails(AnnualizationDetails annualizationDetails);

	public int deleteAnnualizationDetails(AnnualizationDetails annualizationDetails);

	public List<AnnualizationDetails> getAnnualizationDetailsByBreakdown(
			@Param("annualization") Annualization annualization,
			@Param("annualizationBreakdown") AnnualizationBreakdown annualizationBreakdown);

	public int deleteAnnualizationDetailsList(@Param("annualizationDetailsList") List<AnnualizationDetails> annualizationDetailsList);

}
