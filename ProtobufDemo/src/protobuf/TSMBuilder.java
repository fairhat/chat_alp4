package protobuf;

import protobuf.TimeServerProtos.TimeServerMessage;

public class TSMBuilder {

	public static TimeServerMessage buildTSM(int id, String time, String name, String zone){
		
		TimeServerMessage.Builder builder = TimeServerMessage.newBuilder();
		
		builder.setRequests(id);
		builder.setName(name);
		builder.setTime(time);
		
		if(zone!=null)
			builder.setZone(zone);
		
		TimeServerMessage tsm = builder.build();
		
		return tsm;
		
	}
	
}
