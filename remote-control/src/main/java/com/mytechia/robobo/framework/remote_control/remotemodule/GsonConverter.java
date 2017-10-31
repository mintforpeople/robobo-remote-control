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
package com.mytechia.robobo.framework.remote_control.remotemodule;

import com.google.gson.Gson;
import com.mytechia.robobo.framework.remote_control.remotemodule.Command;
import com.mytechia.robobo.framework.remote_control.remotemodule.Response;
import com.mytechia.robobo.framework.remote_control.remotemodule.Status;


/**
 * Implementation of the json converter using Google GSON library
 */
public class GsonConverter {



    public static String commandToJson(Command com){
        Gson gson = new Gson();
        return gson.toJson(com);

    };
    public static Command jsonToCommand(String json){
        Gson gson = new Gson();
        return gson.fromJson(json,Command.class);

    };
    public static String responseToJson(Response r){
        Gson gson = new Gson();

        return gson.toJson(r);
    };
    public static Response jsonToResponse(String json){

        Gson gson = new Gson();
        return gson.fromJson(json,Response.class);

    };
    public static String statusToJson(Status st){
        Gson gson = new Gson();
        return gson.toJson(st);
    };
    public static Status jsonToStatus(String json){
        Gson gson = new Gson();
        return gson.fromJson(json,Status.class);
    };
}
