package com.mytechia.robobo.framework.remote_control;

import java.util.Map;

/*******************************************************************************
 * Copyright 2016 Mytech Ingenieria Aplicada <http://www.mytechia.com>
 * Copyright 2016 Luis Llamas <luis.llamas@mytechia.com>
 * <p>
 * This file is part of Robobo Remote Control Module.
 * <p>
 * Robobo Remote Control Module is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * Robobo Remote Control Module is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public License
 * along with Robobo Remote Control Module.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
public final class JsonConverter {
    public static String commandToJson(Command com){
        /*
        {
          "type:command",
          "name:name",
          "id:id",
          "parameters:
            [
            {"parameter1","value1"},
            {"parameter2","value2"}
           ]"
        }



         */
        String output = "{\"type:command\",";
        output =output+"\"name:"+com.getName()+"\"," +
                "\"id:"+com.getId()+"\""+
        "\"parameters:[";

        for(Map.Entry<String, String> entry : com.getParameters().entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            output= output+"{\""+key+":"+value+"\"},";

            // do what you have to do here
            // In your case, an other loop.
        }
        output = output.substring(0, output.length()-1);
        output = output+"]\"}";


        return output;
    }
    public static Command jsonToCommand(String json){
        return null;
    }
    public static String responseToJson(Response r){
        String output = "{\"type:response\",";
        output =output+
                "\"commandID:"+r.getCommandId()+"\""+
                "\"value:[";

        for(Map.Entry<String, String> entry : r.getValue().entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            output= output+"{\""+key+":"+value+"\"},";

            // do what you have to do here
            // In your case, an other loop.
        }
        output = output.substring(0, output.length()-1);
        output = output+"]\"}";


        return output;
    }
    public static Response jsonToResponse(String json){
        return null;
    }
    public static String statusToJson(Status st){
        String output = "{\"type:status\",";
        output =output+
                "\"name:"+st.getName()+"\""+
                "\"value:[";

        for(Map.Entry<String, String> entry : st.getValue().entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            output= output+"{\""+key+":"+value+"\"},";

            // do what you have to do here
            // In your case, an other loop.
        }
        output = output.substring(0, output.length()-1);
        output = output+"]\"}";


        return output;
    }
    public static Status jsonToStatus(String json){
        return null;
    }


}
