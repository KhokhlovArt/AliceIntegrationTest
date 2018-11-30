package com.mks.aliceintegrationtest;

public class Contact {

    private String _name = "";
    private String _phone = "";
    private int _my_id;

    public void setName(String name) {
        _name = name;
    }

    public void setPhone(String phone) {
        _phone = phone;
    }


    public String getName() {
        return _name;
    }

    public String getPhone() {
        return _phone;
    }

    public void setMyId(int my_id) {
        _my_id = my_id;
    }

    @Override
    public String toString() {
        return "" + _name + "|" + _phone;
    }
}
