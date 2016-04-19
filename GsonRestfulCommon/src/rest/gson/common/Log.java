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
public class Log {
    
    private final String codeKey, codeStr, levelStr, logId, userMsg;
    private final int levelInt;
    
    private Log(LogBuilder builder){
        this.codeKey = builder.codeKey;
        this.codeStr = builder.codeStr;
        this.levelStr = builder.levelStr;
        this.logId = builder.logId;
        this.userMsg = builder.userMsg;
        this.levelInt = builder.levelInt;
    }
    
    public static class LogBuilder{
        private String codeKey, codeStr, levelStr, logId, userMsg;
        private int levelInt;
        
        public LogBuilder(LogLevel level){
            this.levelInt = level.getValue();
            this.levelStr = level.getName();
        }

        public LogBuilder codeKey(String codeKey) {
            this.codeKey = codeKey;
            return this;
        }

        public LogBuilder codeStr(String codeStr) {
            this.codeStr = codeStr;
            return this;
        }

        public LogBuilder logId(String logId) {
            this.logId = logId;
            return this;
        }

        public LogBuilder userMsg(String userMsg) {
            this.userMsg = userMsg;
            return this;
        }
        
        public Log build(){
            return new Log(this);
        }
        
    }

    public String getCodeKey() {
        return codeKey;
    }

    public String getCodeStr() {
        return codeStr;
    }

    public String getLevelStr() {
        return levelStr;
    }

    public String getLogId() {
        return logId;
    }

    public String getUserMsg() {
        return userMsg;
    }

    public int getLevelInt() {
        return levelInt;
    }
    
    
}


