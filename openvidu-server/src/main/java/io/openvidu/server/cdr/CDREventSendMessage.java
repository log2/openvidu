package io.openvidu.server.cdr;


import com.google.gson.JsonObject;

import io.openvidu.server.core.Participant;

public class CDREventSendMessage extends CDREvent {

	private Participant participant;
	private JsonObject message;
	
	public CDREventSendMessage(Participant participant, JsonObject message, Long timeStamp) {
		super(CDREventName.messageSent, participant.getSessionId(), participant.getUniqueSessionId(), timeStamp);
		this.participant = participant;
		this.message = message;
	}

	@Override
	public JsonObject toJson() {
		JsonObject json = super.toJson();
		json.addProperty("participantId", this.participant.getParticipantPublicId());
		json.addProperty("data", message.get("data").getAsString());
		json.addProperty("type", message.get("type").getAsString());
		return json;
	}
}
