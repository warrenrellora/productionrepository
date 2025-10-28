package ph.com.lserv.production.employeescheduleencoding.data;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import ph.com.lserv.production.employeescheduleencoding.data.mapper.EmployeeScheduleEncodingMapper;
import ph.com.lserv.production.employeescheduleencoding.model.EmployeeScheduleEncoding;

public class EmployeeScheduleEncodingDao implements IDAO<EmployeeScheduleEncoding> {
	SqlSessionFactory sqlSessionFactory;
	EmployeeScheduleEncodingMapper mapper;
	List<EmployeeScheduleEncoding> employeeScheduleEncodingList = new ArrayList<>();
	EmployeeScheduleEncoding employeeScheduleEncoding = new EmployeeScheduleEncoding();

	public EmployeeScheduleEncodingDao(SqlSessionFactory sqlSessionFactory) {
		this.sqlSessionFactory = sqlSessionFactory;
	}

	@Override
	public List<EmployeeScheduleEncoding> getAllData() {
		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(EmployeeScheduleEncodingMapper.class);
			employeeScheduleEncodingList = mapper.getAllData();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlsession != null) {
				sqlsession.close();
			}
		}

		return employeeScheduleEncodingList;
	}
	
	public List<EmployeeScheduleEncoding> getAllEmployeeScheduleByPrikeyReferenceClient(Integer prikeyReferenceClient){
		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(EmployeeScheduleEncodingMapper.class);
			employeeScheduleEncodingList = mapper.getAllEmployeeScheduleByPrikeyReferenceClient(prikeyReferenceClient);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlsession != null) {
				sqlsession.close();
			}
		}

		return employeeScheduleEncodingList;
	}
	
	public List<EmployeeScheduleEncoding> getAllEmployeeScheduleByEmpId(Integer employeeID){
		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(EmployeeScheduleEncodingMapper.class);
			employeeScheduleEncodingList = mapper.getAllEmployeeScheduleByEmpId(employeeID);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlsession != null) {
				sqlsession.close();
			}
		}

		return employeeScheduleEncodingList;
	}

	@Override
	public int createData(EmployeeScheduleEncoding employeeScheduleEncoding) {
		int returnValue = 0;

		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(EmployeeScheduleEncodingMapper.class);
			returnValue = mapper.createData(employeeScheduleEncoding);
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
	
	public int createMultipleData(List<EmployeeScheduleEncoding> selectedEmployeesToBeSaveList) {
		int returnValue = 0;

		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(EmployeeScheduleEncodingMapper.class);
			returnValue = mapper.createMultipleData(selectedEmployeesToBeSaveList);
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
	public int updateData(EmployeeScheduleEncoding employeeScheduleEncoding) {
		int returnValue = 0;

		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(EmployeeScheduleEncodingMapper.class);
			returnValue = mapper.updateData(employeeScheduleEncoding);
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
	public int deleteData(EmployeeScheduleEncoding employeeScheduleEncoding) {
		// TODO Auto-generated method stub
		int returnValue = 0;

		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(EmployeeScheduleEncodingMapper.class);
			returnValue = mapper.deleteData(employeeScheduleEncoding);
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
	public EmployeeScheduleEncoding getDataById(int id) {
		// TODO Auto-generated method stub
		SqlSession sqlSession = this.sqlSessionFactory.openSession();

		try {
			this.mapper = sqlSession.getMapper(EmployeeScheduleEncodingMapper.class);
			employeeScheduleEncoding = this.mapper.getDataById(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}

		return employeeScheduleEncoding;
	}
	
	public EmployeeScheduleEncoding getEmployeeScheduleByEmployeeID(Integer employeeID) {
				SqlSession sqlSession = this.sqlSessionFactory.openSession();

				try {
					this.mapper = sqlSession.getMapper(EmployeeScheduleEncodingMapper.class);
					employeeScheduleEncoding = this.mapper.getEmployeeScheduleByEmployeeID(employeeID);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (sqlSession != null) {
						sqlSession.close();
					}
				}

				return employeeScheduleEncoding;
	}

}
