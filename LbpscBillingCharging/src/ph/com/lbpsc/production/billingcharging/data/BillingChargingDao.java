package ph.com.lbpsc.production.billingcharging.data;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import ph.com.lbpsc.production.billingcharging.data.mapper.BillingChargingMapper;
import ph.com.lbpsc.production.billingcharging.model.BillingCharging;
import ph.com.lbpsc.production.client.model.Client;
import ph.com.lbpsc.production.clientgroup.model.ClientGroup;
import ph.com.lbpsc.production.payroll.model.Payroll;

public class BillingChargingDao implements IDAO<BillingCharging> {
	private SqlSessionFactory sqlSessionFactory = null;
	private BillingChargingMapper mapper;
	private List<BillingCharging> billingChargingList;

	public BillingChargingDao(SqlSessionFactory sqlSessionFactory) {
		this.sqlSessionFactory = sqlSessionFactory;
	}

	public SqlSessionFactory getSqlSessionFactory() {
		return this.sqlSessionFactory;
	}

	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		this.sqlSessionFactory = sqlSessionFactory;
	}

	@Override
	public List<BillingCharging> getAllData() {
		SqlSession sqlSession = this.sqlSessionFactory.openSession();
		try {
			mapper = sqlSession.getMapper(BillingChargingMapper.class);
			billingChargingList = mapper.getAllBillingCharging();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}
		return billingChargingList;
	}

	public List<BillingCharging> getBillingChargingByBatchDate(Date batchDate) {
		SqlSession sqlSession = this.sqlSessionFactory.openSession();
		try {
			mapper = sqlSession.getMapper(BillingChargingMapper.class);
			billingChargingList = mapper.getBillingChargingByBatchDate(batchDate);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}
		return billingChargingList;
	}

	@Override
	public int createData(BillingCharging billingCharging) {
		int cnt = 0;
		SqlSession sqlSession = this.sqlSessionFactory.openSession();
		try {
			mapper = sqlSession.getMapper(BillingChargingMapper.class);
			cnt = mapper.createBillingCharging(billingCharging);
			sqlSession.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}
		return cnt;
	}

	@Override
	public int updateData(BillingCharging billingCharging) {
		int cnt = 0;
		SqlSession sqlSession = this.sqlSessionFactory.openSession();
		try {
			mapper = sqlSession.getMapper(BillingChargingMapper.class);
			cnt = mapper.updateBillingCharging(billingCharging);
			sqlSession.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}
		return cnt;
	}

	@Override
	public int deleteData(BillingCharging billingCharging) {
		int cnt = 0;
		SqlSession sqlSession = this.sqlSessionFactory.openSession();
		try {
			mapper = sqlSession.getMapper(BillingChargingMapper.class);
			cnt = mapper.deleteBillingCharging(billingCharging);
			sqlSession.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}
		return cnt;
	}

	@Override
	public BillingCharging getDataByKey(Integer primaryKey) {
		BillingCharging billingCharging = null;
		SqlSession sqlSession = this.sqlSessionFactory.openSession();
		try {
			mapper = sqlSession.getMapper(BillingChargingMapper.class);
			billingCharging = mapper.getBillingChargingById(primaryKey);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}
		return billingCharging;
	}

	public List<BillingCharging> getBillingChargingByClientAndBatchDate(String clientCode, Date batchDate) {
		SqlSession sqlSession = this.sqlSessionFactory.openSession();
		try {
			mapper = sqlSession.getMapper(BillingChargingMapper.class);
			billingChargingList = mapper.getBillingChargingByClientAndBatchDate(clientCode, batchDate);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}
		return billingChargingList;
	}

	public List<BillingCharging> getAllBillingChargingByPayroll(Payroll payroll) {
		SqlSession sqlSession = this.sqlSessionFactory.openSession();
		try {
			mapper = sqlSession.getMapper(BillingChargingMapper.class);
			billingChargingList = mapper.getAllBillingChargingByPayroll(payroll);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}
		return billingChargingList;
	}

	public List<BillingCharging> getAllBillingChargingByPayrollPrikey(Integer prikeyPayroll) {
		SqlSession sqlSession = this.sqlSessionFactory.openSession();
		try {
			mapper = sqlSession.getMapper(BillingChargingMapper.class);
			billingChargingList = mapper.getAllBillingChargingByPayrollPrikey(prikeyPayroll);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}
		return billingChargingList;
	}

	public List<BillingCharging> getAllPendingBillingChargingByClient(List<Client> listClient) {
		SqlSession sqlSession = this.sqlSessionFactory.openSession();
		try {
			mapper = sqlSession.getMapper(BillingChargingMapper.class);
			billingChargingList = mapper.getAllPendingBillingChargingByClient(listClient);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}
		return billingChargingList;
	}

	public List<BillingCharging> getAllPendingBillingChargingByClientBatchDate(List<Client> listClient,
			Date batchDate) {
		SqlSession sqlSession = this.sqlSessionFactory.openSession();
		try {
			mapper = sqlSession.getMapper(BillingChargingMapper.class);
			billingChargingList = mapper.getAllPendingBillingChargingByClientBatchDate(listClient, batchDate);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}
		return billingChargingList;
	}

	public List<BillingCharging> getAllBillingChargingByClientGroupAndBatchDate(int prikeyGroup, Date batchDate) {
		SqlSession sqlSession = this.sqlSessionFactory.openSession();
		try {
			mapper = sqlSession.getMapper(BillingChargingMapper.class);
			billingChargingList = mapper.getAllBillingChargingByClientGroupAndBatchDate(prikeyGroup, batchDate);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}
		return billingChargingList;
	}

	public List<BillingCharging> getAllBillingChargingByClientOrClientGroupAndBatchDate(Client client,
			ClientGroup clientGroup, Date batchDate) {
		SqlSession sqlSession = this.sqlSessionFactory.openSession();
		try {
			mapper = sqlSession.getMapper(BillingChargingMapper.class);
			billingChargingList = mapper.getAllBillingChargingByClientOrClientGroupAndBatchDate(client, clientGroup,
					batchDate);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}
		return billingChargingList;
	}

	public List<BillingCharging> getBillingChargingByClientCodeAndPayFrom(String clientCode, Date payFromA,
			Date payFromB) {
		SqlSession sqlSession = this.sqlSessionFactory.openSession();
		try {
			mapper = sqlSession.getMapper(BillingChargingMapper.class);
			billingChargingList = mapper.getBillingChargingByClientCodeAndPayFrom(clientCode, payFromA, payFromB);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}
		return billingChargingList;
	}

	public List<BillingCharging> getAllExtendedDutyBillingChargingByClientCdBatchDate(Date batchDate,
			String clientCode) {
		SqlSession sqlSession = this.sqlSessionFactory.openSession();
		try {
			mapper = sqlSession.getMapper(BillingChargingMapper.class);
			billingChargingList = mapper.getAllExtendedDutyBillingChargingByClientCdBatchDate(batchDate, clientCode);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}
		return billingChargingList;
	}

	public List<BillingCharging> getAllBillingChargingBatchDateClientOrGroupCoverUpOrExtendedDuty(Client client,
			ClientGroup clientGroup, Date batchDate, Integer isExtendedDuty) {
		SqlSession sqlSession = this.sqlSessionFactory.openSession();
		try {
			mapper = sqlSession.getMapper(BillingChargingMapper.class);
			billingChargingList = mapper.getAllBillingChargingBatchDateClientOrGroupCoverUpOrExtendedDuty(client,
					clientGroup, batchDate, isExtendedDuty);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}
		return billingChargingList;
	}

}
