package ph.com.lbpsc.production.annualization.data.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import ph.com.lbpsc.production.annualization.model.Annualization;
import ph.com.lbpsc.production.client.model.Client;
import ph.com.lbpsc.production.employee.model.Employee;

public interface AnnualizationMapper {
	public List<Annualization> getAllAnnualization();

	public Annualization getAnnualizationById(int iD);

	public int createAnnualization(Annualization annualization);

	public int updateAnnualization(Annualization annualization);

	public int deleteAnnualization(Annualization annualization);

	public Annualization getEmployeePayrollByBatchDate(Employee employee, Date payFrom, Date payTo);

	public Annualization getAnnualizationByEmployeeId(Employee employee, Integer annnualizationYear,
			boolean isPrevious);

	public List<Annualization> getAnnualizationByClient(Client client, Integer annnualizationYear);

	public List<Annualization> getAnnualizationByAnnualizationYear(@Param("clientList") List<Client> clientList,
			@Param("annualizationYear") Integer annualizationYear);

	public Annualization getAnnualizationPayrollByBatchDate(@Param("employee") Employee employee,
			@Param("batchDateFrom") Date batchDateFrom, @Param("batchDateTo") Date batchDateTo);

	public Annualization getAnnualizationByQuitclaim(Employee employee, Integer annnualizationYear,
			Integer prikeyQuitclaim);
}
