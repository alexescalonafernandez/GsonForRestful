/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.gson.common;

/**
 *
 * @author alexander.escalona
 */
public class LogLevel {
    
    private final int value;
    private final String name;

    public LogLevel(int value, String name) {
        this.value = value;
        this.name = name;
    }
    
    public static final LogLevel FATAL =  new LogLevel(0, "FATAL");
    public static final LogLevel ERROR =  new LogLevel(1, "ERROR");
    public static final LogLevel WARNING =  new LogLevel(2, "WARNING");
    public static final LogLevel INFO =  new LogLevel(3, "INFO");

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }
    
    
}