package ph.com.lserv.production.scheduleencoding.data;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import ph.com.lbpsc.production.client.model.Client;
import ph.com.lserv.production.scheduleencoding.data.mapper.ScheduleEncodingMapper;
import ph.com.lserv.production.scheduleencoding.model.ScheduleEncoding;

public class ScheduleEncodingDao implements IDAO<ScheduleEncoding> {
	SqlSessionFactory sqlSessionFactory;
	ScheduleEncodingMapper mapper;
	ScheduleEncoding scheduleEncoding = new ScheduleEncoding();
	List<ScheduleEncoding> scheduleEncodingList = new ArrayList<>();
	List<String> scheduleEncodingNameList = new ArrayList<>();

	public ScheduleEncodingDao(SqlSessionFactory sqlSessionFactory) {
		super();
		this.sqlSessionFactory = sqlSessionFactory;
	}

	@Override
	public List<ScheduleEncoding> getAllData() {
		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(ScheduleEncodingMapper.class);
			scheduleEncodingList = mapper.getAllData();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlsession != null) {
				sqlsession.close();
			}
		}

		return scheduleEncodingList;
	}

	@Override
	public int createData(ScheduleEncoding scheduleEncoding) {
		int returnValue = 0;

		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(ScheduleEncodingMapper.class);
			returnValue = mapper.createData(scheduleEncoding);
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

	public int createMultipleData(List<ScheduleEncoding> scheduleEncodingList) {
		int returnValue = 0;

		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(ScheduleEncodingMapper.class);
			returnValue = mapper.createMultipleData(scheduleEncodingList);
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
	public int updateData(ScheduleEncoding scheduleEncoding) {
		int returnValue = 0;

		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(ScheduleEncodingMapper.class);
			returnValue = mapper.updateData(scheduleEncoding);
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

	public int updateMultipleData(List<ScheduleEncoding> scheduleEncodingList) {
		int returnValue = 0;
		SqlSession sqlsession = this.sqlSessionFactory.openSession(ExecutorType.BATCH);
		try {
			mapper = sqlsession.getMapper(ScheduleEncodingMapper.class);
			int index = 0;
			while (index < scheduleEncodingList.size()) {
				mapper.updateData(scheduleEncodingList.get(index));
				index++;
			}
			returnValue = 1;
			// returnValue = mapper.updateMultipleData(scheduleEncodingList);

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

	public int deleteMultipleData(List<ScheduleEncoding> scheduleEncodingList) {
		int returnValue = 0;
		SqlSession sqlsession = this.sqlSessionFactory.openSession(ExecutorType.BATCH);
		try {
			mapper = sqlsession.getMapper(ScheduleEncodingMapper.class);
			int index = 0;
			while (index < scheduleEncodingList.size()) {
				mapper.deleteData(scheduleEncodingList.get(index));
				index++;
			}
			returnValue = 1;
			// returnValue = mapper.updateMultipleData(scheduleEncodingList);

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
	public int deleteData(ScheduleEncoding scheduleEncoding) {
		int returnValue = 0;

		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(ScheduleEncodingMapper.class);
			returnValue = mapper.deleteData(scheduleEncoding);
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

	public int deleteDataByClientNameAndSchedName(String clientName, String schedName) {
		int returnValue = 0;

		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(ScheduleEncodingMapper.class);
			returnValue = mapper.deleteDataByClientNameAndSchedName(clientName, schedName);
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

	public int deleteDataByClientNameAndSchedNameAndDay(String schedName, String schedDay, String clientName) {
		int returnValue = 0;

		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(ScheduleEncodingMapper.class);
			returnValue = mapper.deleteDataByClientNameAndSchedNameAndDay(schedName, schedDay, clientName);
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

	public int deleteDataByPrikeyReferenceClient(Integer prikeyReferenceClient) {
		int returnValue = 0;

		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(ScheduleEncodingMapper.class);
			returnValue = mapper.deleteDataByPrikeyReferenceClient(prikeyReferenceClient);
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

	public int deleteDataByPrikeyReferenceClientAndSchedDay(Integer prikeyReferenceClient, String schedDay) {
		int returnValue = 0;

		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(ScheduleEncodingMapper.class);
			returnValue = mapper.deleteDataByPrikeyReferenceClientAndSchedDay(prikeyReferenceClient, schedDay);
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
	public ScheduleEncoding getDataById(int id) {
		SqlSession sqlSession = this.sqlSessionFactory.openSession();

		try {
			this.mapper = sqlSession.getMapper(ScheduleEncodingMapper.class);
			scheduleEncoding = this.mapper.getDataById(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}

		return scheduleEncoding;
	}

	public ScheduleEncoding getSchedBySchedNameAndDay(String schedName, String day) {
		SqlSession sqlSession = this.sqlSessionFactory.openSession();

		try {
			this.mapper = sqlSession.getMapper(ScheduleEncodingMapper.class);
			scheduleEncoding = this.mapper.getSchedBySchedNameAndDay(schedName, day);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}

		return scheduleEncoding;
	}

	public ScheduleEncoding getScheduleImportedByEmpIdAndDay(Integer employeeID, String dayOfDate) {
		SqlSession sqlSession = this.sqlSessionFactory.openSession();

		try {
			this.mapper = sqlSession.getMapper(ScheduleEncodingMapper.class);
			scheduleEncoding = this.mapper.getScheduleImportedByEmpIdAndDay(employeeID, dayOfDate);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}

		return scheduleEncoding;
	}

	public List<ScheduleEncoding> getAllDistinctScheduleEncodingByClientName(String clientName) {
		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(ScheduleEncodingMapper.class);
			scheduleEncodingList = mapper.getAllDistinctScheduleEncodingByClientName(clientName);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlsession != null) {
				sqlsession.close();
			}
		}

		return scheduleEncodingList;
	}

	public List<ScheduleEncoding> getAllScheduleEncodingByEncodingName(String scheduleName) {
		SqlSession sqlSession = this.sqlSessionFactory.openSession();

		try {
			this.mapper = sqlSession.getMapper(ScheduleEncodingMapper.class);
			scheduleEncodingList = this.mapper.getAllScheduleEncodingByEncodingName(scheduleName);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}

		return scheduleEncodingList;
	}

	public List<ScheduleEncoding> getAllScheduleEncodingByClientName(String clientName) {
		SqlSession sqlSession = this.sqlSessionFactory.openSession();

		try {
			this.mapper = sqlSession.getMapper(ScheduleEncodingMapper.class);
			scheduleEncodingList = this.mapper.getAllScheduleEncodingByClientName(clientName);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}

		return scheduleEncodingList;
	}

	public List<ScheduleEncoding> getAllScheduleEncodingByClientNameAndScheduleName(String clientName,
			String scheduleName) {
		SqlSession sqlSession = this.sqlSessionFactory.openSession();

		try {
			this.mapper = sqlSession.getMapper(ScheduleEncodingMapper.class);
			scheduleEncodingList = this.mapper.getAllScheduleEncodingByClientNameAndScheduleName(clientName,
					scheduleName);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}

		return scheduleEncodingList;
	}

	public List<ScheduleEncoding> getAllScheduleByClientCodeDistinct(Client client) {
		SqlSession sqlSession = this.sqlSessionFactory.openSession();

		try {
			this.mapper = sqlSession.getMapper(ScheduleEncodingMapper.class);
			scheduleEncodingList = this.mapper.getAllScheduleByClientCodeDistinct(client);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}

		return scheduleEncodingList;
	}

	public List<ScheduleEncoding> getAllScheduleByClientCode(Client client) {
		SqlSession sqlSession = this.sqlSessionFactory.openSession();

		try {
			this.mapper = sqlSession.getMapper(ScheduleEncodingMapper.class);
			scheduleEncodingList = this.mapper.getAllScheduleByClientCode(client);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}

		return scheduleEncodingList;
	}

	public List<ScheduleEncoding> getAllScheduleByPrikeyReferenceClient(Integer prikeyReferenceClient) {
		SqlSession sqlSession = this.sqlSessionFactory.openSession();

		try {
			this.mapper = sqlSession.getMapper(ScheduleEncodingMapper.class);
			scheduleEncodingList = this.mapper.getAllScheduleByPrikeyReferenceClient(prikeyReferenceClient);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}

		return scheduleEncodingList;
	}

	public List<ScheduleEncoding> getAllEmployeeScheduleByEmpId(Integer employeeID) {
		SqlSession sqlSession = this.sqlSessionFactory.openSession();

		try {
			this.mapper = sqlSession.getMapper(ScheduleEncodingMapper.class);
			scheduleEncodingList = this.mapper.getAllEmployeeScheduleByEmpId(employeeID);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}

		return scheduleEncodingList;
	}

	public List<ScheduleEncoding> getAllScheduleUploadedByEmployeeId(Integer employeeID) {
		SqlSession sqlSession = this.sqlSessionFactory.openSession();

		try {
			this.mapper = sqlSession.getMapper(ScheduleEncodingMapper.class);
			scheduleEncodingList = this.mapper.getAllScheduleUploadedByEmployeeId(employeeID);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}

		return scheduleEncodingList;
	}

	public ScheduleEncoding getAllScheduleByPrikeyReferenceClientAndScheduleDay(Integer prikeyReferenceClient,
			String schedDay) {
		SqlSession sqlSession = this.sqlSessionFactory.openSession();

		try {
			this.mapper = sqlSession.getMapper(ScheduleEncodingMapper.class);
			scheduleEncoding = this.mapper.getAllScheduleByPrikeyReferenceClientAndScheduleDay(prikeyReferenceClient,
					schedDay);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}

		return scheduleEncoding;
	}

	public ScheduleEncoding getScheduleEncodingByClientNameAndScheduleNameAndScheduleDay(String clientName,
			String scheduleName, String scheduleDay) {
		SqlSession sqlSession = this.sqlSessionFactory.openSession();

		try {
			this.mapper = sqlSession.getMapper(ScheduleEncodingMapper.class);
			scheduleEncoding = this.mapper.getScheduleEncodingByClientNameAndScheduleNameAndScheduleDay(clientName,
					scheduleName, scheduleDay);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}

		return scheduleEncoding;
	}

}
