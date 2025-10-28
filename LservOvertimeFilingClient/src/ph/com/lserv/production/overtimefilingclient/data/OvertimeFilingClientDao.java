package ph.com.lserv.production.overtimefilingclient.data;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import ph.com.lserv.production.overtimefilingclient.data.mapper.OvertimeFilingClientMapper;
import ph.com.lserv.production.overtimefilingclient.model.OvertimeFilingClient;

public class OvertimeFilingClientDao implements IDAO<OvertimeFilingClient> {
	OvertimeFilingClientMapper mapper;
	SqlSessionFactory sqlSessionFactory;
	List<OvertimeFilingClient> listOvertimeFilingClient;
	OvertimeFilingClient overtimeFilingClient;

	public OvertimeFilingClientDao(SqlSessionFactory sqlSessionFactory) {
		super();
		this.sqlSessionFactory = sqlSessionFactory;
	}

	@Override
	public List<OvertimeFilingClient> getAllData() {
		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(OvertimeFilingClientMapper.class);
			listOvertimeFilingClient = mapper.getAllData();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlsession != null) {
				sqlsession.close();
			}
		}

		return listOvertimeFilingClient;
	}

	@Override
	public int createData(OvertimeFilingClient overtimeFilingClient) {
		int returnValue = 0;

		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(OvertimeFilingClientMapper.class);
			returnValue = mapper.createData(overtimeFilingClient);
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
	public int updateData(OvertimeFilingClient overtimeFilingClient) {
		int returnValue = 0;

		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(OvertimeFilingClientMapper.class);
			returnValue = mapper.updateData(overtimeFilingClient);
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
	public int deleteData(OvertimeFilingClient overtimeFilingClient) {
		int returnValue = 0;

		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(OvertimeFilingClientMapper.class);
			returnValue = mapper.deleteData(overtimeFilingClient);
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
	public OvertimeFilingClient getDataById(int id) {
		SqlSession sqlSession = this.sqlSessionFactory.openSession();

		try {
			this.mapper = sqlSession.getMapper(OvertimeFilingClientMapper.class);
			overtimeFilingClient = this.mapper.getDataById(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}

		return overtimeFilingClient;
	}

	public List<OvertimeFilingClient> getDataByEmpIdAndOvertimeDateFrom(int employeeCode, Date overtimeDateStart) {
		SqlSession sqlSession = this.sqlSessionFactory.openSession();

		try {
			this.mapper = sqlSession.getMapper(OvertimeFilingClientMapper.class);
			listOvertimeFilingClient = this.mapper.getDataByEmpIdAndOvertimeDateFrom(employeeCode, overtimeDateStart);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}

		return listOvertimeFilingClient;
	}
	
	public List<OvertimeFilingClient> getDataByEmpIdAndOvertimeDateTo(int employeeCode, Date overtimeDateEnd){
		SqlSession sqlSession = this.sqlSessionFactory.openSession();

		try {
			this.mapper = sqlSession.getMapper(OvertimeFilingClientMapper.class);
			listOvertimeFilingClient = this.mapper.getDataByEmpIdAndOvertimeDateTo(employeeCode, overtimeDateEnd);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}

		return listOvertimeFilingClient;
	}

}
