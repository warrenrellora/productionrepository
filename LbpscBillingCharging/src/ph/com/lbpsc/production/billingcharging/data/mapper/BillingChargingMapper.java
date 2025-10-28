package ph.com.lbpsc.production.billingcharging.data.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import ph.com.lbpsc.production.accountingsignatory.data.IDAO;
import ph.com.lbpsc.production.billingcharging.model.BillingCharging;
import ph.com.lbpsc.production.client.model.Client;
import ph.com.lbpsc.production.clientgroup.model.ClientGroup;
import ph.com.lbpsc.production.payroll.model.Payroll;

public interface BillingChargingMapper extends IDAO<BillingCharging> {
	public List<BillingCharging> getAllBillingCharging();

	public BillingCharging getBillingChargingById(int primaryKey);

	public int createBillingCharging(BillingCharging billingCharging);

	public int updateBillingCharging(BillingCharging billingCharging);

	public int deleteBillingCharging(BillingCharging billingCharging);

	public List<BillingCharging> getBillingChargingByBatchDate(Date batchDate);

	public List<BillingCharging> getBillingChargingByClientAndBatchDate(String clientCode, Date batchDate);

	public List<BillingCharging> getAllBillingChargingByPayroll(Payroll payroll);

	public List<BillingCharging> getAllPendingBillingChargingByClient(@Param("listClient") List<Client> listClient);

	public List<BillingCharging> getAllPendingBillingChargingByClientBatchDate(
			@Param("listClient") List<Client> listClient, @Param("batchDate") Date batchDate);

	public List<BillingCharging> getAllBillingChargingByClientGroupAndBatchDate(int prikeyGroup, Date batchDate);

	public List<BillingCharging> getAllBillingChargingByClientOrClientGroupAndBatchDate(@Param("client") Client client,
			@Param("clientGroup") ClientGroup clientGroup, @Param("batchDate") Date batchDate);

	public List<BillingCharging> getBillingChargingByClientCodeAndPayFrom(String clientCode, Date payFromA,
			Date payFromB);

	public List<BillingCharging> getAllBillingChargingByPayrollPrikey(Integer prikeyPayroll);

	public List<BillingCharging> getAllExtendedDutyBillingChargingByClientCdBatchDate(Date batchDate,
			String clientCode);

	public List<BillingCharging> getAllBillingChargingBatchDateClientOrGroupCoverUpOrExtendedDuty(
			@Param("client") Client client, @Param("clientGroup") ClientGroup clientGroup,
			@Param("batchDate") Date batchDate, @Param("isExtendedDuty") Integer isExtendedDuty);
}
