package com.demo.myblueteeth;

/**
 * @Author： Victory
 * @Time： 2018/9/13
 * @QQ： 949021037
 * @Explain： com.demo.myblueteeth
 */
public class BLEBean {
    private String address;
    private String name;
    private int type;
    private boolean isMatching;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isMatching() {
        return isMatching;
    }

    public void setMatching(boolean matching) {
        isMatching = matching;
    }
}
