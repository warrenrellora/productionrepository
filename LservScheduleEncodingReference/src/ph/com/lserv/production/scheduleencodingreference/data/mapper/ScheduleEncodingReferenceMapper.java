package ph.com.lserv.production.scheduleencodingreference.data.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import ph.com.lbpsc.production.client.model.Client;
import ph.com.lserv.production.scheduleencodingreference.data.IDAO;
import ph.com.lserv.production.scheduleencodingreference.model.ScheduleEncodingReference;

public interface ScheduleEncodingReferenceMapper extends IDAO<ScheduleEncodingReference> {
	public ScheduleEncodingReference getScheduleByScheduleNameAndClientCode(String schedName,
			@Param("client") Client client);

	public List<ScheduleEncodingReference> getAllScheduleByClientCode(@Param("client") Client client);
	
	int deleteDataByPrikeyReferenceClient(Integer PrikeyReferenceClient);
}
