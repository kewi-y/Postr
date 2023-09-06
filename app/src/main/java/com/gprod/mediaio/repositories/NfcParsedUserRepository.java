package com.gprod.mediaio.repositories;

public class NfcParsedUserRepository {
    private static NfcParsedUserRepository instance;
    private String userId;
    public static NfcParsedUserRepository getInstance() {
        if(instance == null){
            instance = new NfcParsedUserRepository();
        }
        return instance;
    }
    private NfcParsedUserRepository(){

    }
    public void setUserId(String id){
        userId = id;
    }
    public void clearUserId(){
        userId = null;
    }
    public String getUserId(){
        return userId;
    }

}
