package com.example.encs5150_project.model.repository.api;

import com.example.encs5150_project.model.entity.*;
import org.json.*;
import java.time.*;
import java.util.*;

public class EventJsonParser {
    public static List<Event> ParseEvent(String jsonFormattedString){
        List<Event> eventList;
        try{
            JSONArray jsonArray=new JSONArray(jsonFormattedString);
            eventList=new ArrayList<>();
            for(int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                eventList.add(new Event(jsonObject.getLong(EventFields.FIELD_ID),jsonObject.getString(EventFields.FIELD_TITLE),jsonObject.getString(EventFields.FIELD_DESCRIPTION),jsonObject.getString(EventFields.FIELD_CATEGORY), LocalDate.parse(jsonObject.getString(EventFields.FIELD_DATE)), LocalTime.parse(jsonObject.getString(EventFields.FIELD_TIME)),jsonObject.getString(EventFields.FIELD_LOCATION),jsonObject.getInt(EventFields.FIELD_TOTAL_SEATS),jsonObject.getString(EventFields.FIELD_IMAGE_PATH), EntityStatus.ENABLED));
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return eventList;
    }
}
