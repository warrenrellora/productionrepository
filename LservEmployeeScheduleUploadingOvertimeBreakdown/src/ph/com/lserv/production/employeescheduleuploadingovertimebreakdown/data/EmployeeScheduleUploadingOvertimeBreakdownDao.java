package ph.com.lserv.production.employeescheduleuploadingovertimebreakdown.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import ph.com.lserv.production.employeescheduleuploadingovertimebreakdown.data.mapper.EmployeeScheduleUploadingOvertimeBreakdownMapper;
import ph.com.lserv.production.employeescheduleuploadingovertimebreakdown.model.EmployeeScheduleUploadingOvertimeBreakdown;

public class EmployeeScheduleUploadingOvertimeBreakdownDao implements IDAO<EmployeeScheduleUploadingOvertimeBreakdown> {
	EmployeeScheduleUploadingOvertimeBreakdownMapper mapper;
	SqlSessionFactory sqlSessionFactory;
	List<EmployeeScheduleUploadingOvertimeBreakdown> employeeScheduleUploadingOvertimeBreakdownList = new ArrayList<>();
	EmployeeScheduleUploadingOvertimeBreakdown employeeScheduleUploadingOvertimeBreakdown;
	List<Integer> integerList = new ArrayList<>();

	public EmployeeScheduleUploadingOvertimeBreakdownDao(SqlSessionFactory sqlSessionFactory) {
		this.sqlSessionFactory = sqlSessionFactory;
	}

	public List<Integer> getAllPrikeyUploadingDistinctByPayFromPayTo(Date payFrom, Date payTo) {
		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(EmployeeScheduleUploadingOvertimeBreakdownMapper.class);
			integerList = mapper.getAllPrikeyUploadingDistinctByPayFromPayTo(payFrom, payTo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlsession != null) {
				sqlsession.close();
			}
		}

		return integerList;
	}

	public List<Integer> getAllOvertimeTypeDistinctByPayFromPayToClientCd(Date payFrom, Date payTo, String clientCode) {
		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(EmployeeScheduleUploadingOvertimeBreakdownMapper.class);
			integerList = mapper.getAllOvertimeTypeDistinctByPayFromPayToClientCd(payFrom, payTo, clientCode);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlsession != null) {
				sqlsession.close();
			}
		}

		return integerList;
	}

	@Override
	public List<EmployeeScheduleUploadingOvertimeBreakdown> getAllData() {
		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(EmployeeScheduleUploadingOvertimeBreakdownMapper.class);
			employeeScheduleUploadingOvertimeBreakdownList = mapper.getAllData();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlsession != null) {
				sqlsession.close();
			}
		}

		return employeeScheduleUploadingOvertimeBreakdownList;
	}

	@Override
	public int createData(EmployeeScheduleUploadingOvertimeBreakdown employeeScheduleUploadingOvertimeBreakdown) {
		int returnValue = 0;

		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(EmployeeScheduleUploadingOvertimeBreakdownMapper.class);
			returnValue = mapper.createData(employeeScheduleUploadingOvertimeBreakdown);
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
	public int updateData(EmployeeScheduleUploadingOvertimeBreakdown employeeScheduleUploadingOvertimeBreakdown) {
		int returnValue = 0;

		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(EmployeeScheduleUploadingOvertimeBreakdownMapper.class);
			returnValue = mapper.updateData(employeeScheduleUploadingOvertimeBreakdown);
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
	public int deleteData(EmployeeScheduleUploadingOvertimeBreakdown employeeScheduleUploadingOvertimeBreakdown) {
		int returnValue = 0;

		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(EmployeeScheduleUploadingOvertimeBreakdownMapper.class);
			returnValue = mapper.deleteData(employeeScheduleUploadingOvertimeBreakdown);
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

	public List<EmployeeScheduleUploadingOvertimeBreakdown> getDataByPrikeyEmployeeScheduleUploading(
			Integer employeeScheduleUploadingPrikey) {
		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(EmployeeScheduleUploadingOvertimeBreakdownMapper.class);
			employeeScheduleUploadingOvertimeBreakdownList = mapper
					.getDataByPrikeyEmployeeScheduleUploading(employeeScheduleUploadingPrikey);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlsession != null) {
				sqlsession.close();
			}
		}

		return employeeScheduleUploadingOvertimeBreakdownList;
	}

	public List<EmployeeScheduleUploadingOvertimeBreakdown> getAllDataByEmployeeCodePayFromPayTo(Integer employeeCode,
			Date payFrom, Date payTo) {
		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(EmployeeScheduleUploadingOvertimeBreakdownMapper.class);
			employeeScheduleUploadingOvertimeBreakdownList = mapper.getAllDataByEmployeeCodePayFromPayTo(employeeCode,
					payFrom, payTo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlsession != null) {
				sqlsession.close();
			}
		}

		return employeeScheduleUploadingOvertimeBreakdownList;
	}

	@Override
	public EmployeeScheduleUploadingOvertimeBreakdown getDataById(int id) {
		SqlSession sqlSession = this.sqlSessionFactory.openSession();

		try {
			this.mapper = sqlSession.getMapper(EmployeeScheduleUploadingOvertimeBreakdownMapper.class);
			employeeScheduleUploadingOvertimeBreakdown = this.mapper.getDataById(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}

		return employeeScheduleUploadingOvertimeBreakdown;
	}

}
