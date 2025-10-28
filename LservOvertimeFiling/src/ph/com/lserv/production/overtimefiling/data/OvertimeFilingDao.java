package ph.com.lserv.production.overtimefiling.data;

import java.sql.Timestamp;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import ph.com.lbpsc.production.organizationalstructure.model.OrganizationalStructure;
import ph.com.lserv.production.overtimefiling.data.mapper.OvertimeFilingMapper;
import ph.com.lserv.production.overtimefiling.model.OvertimeFiling;

public class OvertimeFilingDao implements IDAO<OvertimeFiling> {
	SqlSessionFactory sqlSessionFactory;
	OvertimeFilingMapper mapper;
	List<OvertimeFiling> listOvertimeFiling;
	List<Timestamp> overtimeFilingDateTimeList;
	List<OrganizationalStructure> organizationalStructureParentList;
	OvertimeFiling overtimeFiling = new OvertimeFiling();
	String result;

	public OvertimeFilingDao(SqlSessionFactory sqlSessionFactory) {
		super();
		this.sqlSessionFactory = sqlSessionFactory;
	}

	public List<OvertimeFiling> getOvertimeByCutoffDate(String dateTimeFrom, String dateTimeTo) {
		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(OvertimeFilingMapper.class);
			listOvertimeFiling = mapper.getOvertimeByCutoffDate(dateTimeFrom, dateTimeTo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlsession != null) {
				sqlsession.close();
			}
		}

		return listOvertimeFiling;
	}

	public List<OvertimeFiling> getAllOvertimeByOvertimeFromAndPrikeyUser(String dateFrom,
			Integer prikeyUser) {
		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(OvertimeFilingMapper.class);
			listOvertimeFiling = mapper.getAllOvertimeByOvertimeFromAndPrikeyUser(dateFrom, prikeyUser);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlsession != null) {
				sqlsession.close();
			}
		}

		return listOvertimeFiling;
	}
	
	public List<OvertimeFiling> getAllOvertimeByOvertimeToAndPrikeyUser(String dateTo, Integer prikeyUser){
		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(OvertimeFilingMapper.class);
			listOvertimeFiling = mapper.getAllOvertimeByOvertimeToAndPrikeyUser(dateTo, prikeyUser);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlsession != null) {
				sqlsession.close();
			}
		}

		return listOvertimeFiling;
	}

	@Override
	public List<OvertimeFiling> getAllData() {
		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(OvertimeFilingMapper.class);
			listOvertimeFiling = mapper.getAllData();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlsession != null) {
				sqlsession.close();
			}
		}

		return listOvertimeFiling;
	}

	public List<OvertimeFiling> getTimeDifference(int id) {
		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(OvertimeFilingMapper.class);
			listOvertimeFiling = mapper.getTimeDifference(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlsession != null) {
				sqlsession.close();
			}
		}

		return listOvertimeFiling;
	}

	@Override
	public int createData(OvertimeFiling overtimeFiling) {
		int returnValue = 0;

		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(OvertimeFilingMapper.class);
			returnValue = mapper.createData(overtimeFiling);
			sqlsession.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlsession != null) {
				sqlsession.close();
			}
		}

		return returnValue;
	}

	@Override
	public int updateData(OvertimeFiling overtimeFiling) {
		int returnValue = 0;

		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(OvertimeFilingMapper.class);
			returnValue = mapper.updateData(overtimeFiling);
			sqlsession.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlsession != null) {
				sqlsession.close();
			}
		}

		return returnValue;
	}

	@Override
	public int deleteData(OvertimeFiling overtimeFiling) {
		int returnValue = 0;

		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(OvertimeFilingMapper.class);
			returnValue = mapper.deleteData(overtimeFiling);
			sqlsession.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlsession != null) {
				sqlsession.close();
			}
		}

		return returnValue;
	}

	@Override
	public OvertimeFiling getDataById(int id) {
		SqlSession sqlSession = this.sqlSessionFactory.openSession();

		try {
			this.mapper = sqlSession.getMapper(OvertimeFilingMapper.class);
			overtimeFiling = this.mapper.getDataById(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}

		return overtimeFiling;
	}

	public OvertimeFiling getMaxFiledOvertime() {
		SqlSession sqlSession = this.sqlSessionFactory.openSession();

		try {
			this.mapper = sqlSession.getMapper(OvertimeFilingMapper.class);
			overtimeFiling = this.mapper.getMaxFiledOvertime();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}

		return overtimeFiling;
	}

	public List<OvertimeFiling> getUserData(int id) {
		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(OvertimeFilingMapper.class);
			listOvertimeFiling = mapper.getUserData(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlsession != null) {
				sqlsession.close();
			}
		}

		return listOvertimeFiling;
	}

	public List<OvertimeFiling> getAllOvertimeSortedAscending() {
		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(OvertimeFilingMapper.class);
			listOvertimeFiling = mapper.getAllOvertimeSortedAscending();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlsession != null) {
				sqlsession.close();
			}
		}
		return listOvertimeFiling;
	}

}
