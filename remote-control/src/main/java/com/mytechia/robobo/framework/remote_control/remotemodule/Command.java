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

import java.util.HashMap;


/**
 * Represents a remote control command
 */
public class Command {
    private String name;
    private int id;
    private HashMap<String,String> parameters;

    /**
     * Constructor of the class
     * @param name Command Name
     * @param id Command Id
     * @param parameters Map of additional parameters
     */
    public Command(String name, int id, HashMap<String,String> parameters){
        this.id=id;
        this.name = name;
        this.parameters = parameters;
    }

    public String getName() {
        return name;
    }

    public Integer getId() {
        return id;
    }

    public HashMap<String, String> getParameters() {
        return parameters;
    }

    public Response createResponse(){
        return new Response(id);
    }
}
