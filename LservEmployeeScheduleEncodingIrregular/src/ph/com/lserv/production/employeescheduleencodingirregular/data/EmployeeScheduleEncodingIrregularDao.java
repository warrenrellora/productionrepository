package ph.com.lserv.production.employeescheduleencodingirregular.data;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import ph.com.lserv.production.employeescheduleencodingirregular.data.mapper.EmployeeScheduleEncodingIrregularMapper;
import ph.com.lserv.production.employeescheduleencodingirregular.model.EmployeeScheduleEncodingIrregular;

public class EmployeeScheduleEncodingIrregularDao implements IDAO<EmployeeScheduleEncodingIrregular> {
	SqlSessionFactory sqlSessionFactory;
	EmployeeScheduleEncodingIrregularMapper mapper;
	List<EmployeeScheduleEncodingIrregular> employeeScheduleEncodingIrregularList = new ArrayList<>();
	EmployeeScheduleEncodingIrregular employeeScheduleEncodingIrregular = new EmployeeScheduleEncodingIrregular();

	public EmployeeScheduleEncodingIrregularDao(SqlSessionFactory sqlSessionFactory) {
		super();
		this.sqlSessionFactory = sqlSessionFactory;
	}

	public List<EmployeeScheduleEncodingIrregular> getAllEmployeeIrregularScheduleByEmployeeID(Integer employeeID) {
		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(EmployeeScheduleEncodingIrregularMapper.class);
			employeeScheduleEncodingIrregularList = mapper.getAllEmployeeIrregularScheduleByEmployeeID(employeeID);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlsession != null) {
				sqlsession.close();
			}
		}

		return employeeScheduleEncodingIrregularList;
	}

	@Override
	public List<EmployeeScheduleEncodingIrregular> getAllData() {
		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(EmployeeScheduleEncodingIrregularMapper.class);
			employeeScheduleEncodingIrregularList = mapper.getAllData();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlsession != null) {
				sqlsession.close();
			}
		}

		return employeeScheduleEncodingIrregularList;
	}

	@Override
	public int createData(EmployeeScheduleEncodingIrregular employeeScheduleEncodingIrregular) {
		int returnValue = 0;

		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(EmployeeScheduleEncodingIrregularMapper.class);
			returnValue = mapper.createData(employeeScheduleEncodingIrregular);
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

	public int createMultipleData(List<EmployeeScheduleEncodingIrregular> createdObjectsToBeSavedList) {
		int returnValue = 0;

		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(EmployeeScheduleEncodingIrregularMapper.class);
			returnValue = mapper.createMultipleData(createdObjectsToBeSavedList);
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
	public int updateData(EmployeeScheduleEncodingIrregular employeeScheduleEncodingIrregular) {
		int returnValue = 0;

		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(EmployeeScheduleEncodingIrregularMapper.class);
			returnValue = mapper.updateData(employeeScheduleEncodingIrregular);
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
	public int deleteData(EmployeeScheduleEncodingIrregular employeeScheduleEncodingIrregular) {
		int returnValue = 0;

		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(EmployeeScheduleEncodingIrregularMapper.class);
			returnValue = mapper.deleteData(employeeScheduleEncodingIrregular);
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
	public EmployeeScheduleEncodingIrregular getDataById(int id) {
		SqlSession sqlSession = this.sqlSessionFactory.openSession();

		try {
			this.mapper = sqlSession.getMapper(EmployeeScheduleEncodingIrregularMapper.class);
			employeeScheduleEncodingIrregular = this.mapper.getDataById(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}

		return employeeScheduleEncodingIrregular;
	}

	public EmployeeScheduleEncodingIrregular getEmployeeIrregularScheduleByPrikey(Integer prikey) {
		SqlSession sqlSession = this.sqlSessionFactory.openSession();

		try {
			this.mapper = sqlSession.getMapper(EmployeeScheduleEncodingIrregularMapper.class);
			employeeScheduleEncodingIrregular = this.mapper.getEmployeeIrregularScheduleByPrikey(prikey);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}

		return employeeScheduleEncodingIrregular;
	}

	public EmployeeScheduleEncodingIrregular getEmployeeIrregularScheduleByDateAndEmployeeID(String dateSchedule,
			Integer employeeID) {
		SqlSession sqlSession = this.sqlSessionFactory.openSession();

		try {
			this.mapper = sqlSession.getMapper(EmployeeScheduleEncodingIrregularMapper.class);
			employeeScheduleEncodingIrregular = this.mapper
					.getEmployeeIrregularScheduleByDateAndEmployeeID(dateSchedule, employeeID);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}

		return employeeScheduleEncodingIrregular;
	}

}
