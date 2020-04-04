package com.ttsea.jlibrary.mail;

/**
 * 发送者信息，邮件地址、账号、密码 <br>
 * <p>
 * <b>more:</b>更多请点 <a href="http://www.ttsea.com" target="_blank">这里</a> <br>
 * <b>date:</b> 2017/4/12 18:10 <br>
 * <b>author:</b> Jason <br>
 * <b>version:</b> 1.0 <br>
 */
class SenderInfo extends ServerInfo {
    // 邮件发送者的地址
    private String fromAddress;
    // 登陆邮件发送服务器的用户名和密码
    private String userName;
    private String password;

    SenderInfo() {
        super();
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "SenderInfo{" +
                "fromAddress='" + fromAddress + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", " + super.toString() + '\'' +
                '}';
    }
}
