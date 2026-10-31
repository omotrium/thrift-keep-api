package thrift_keep.model;

public class RegisterPushTokenRequest {

    private String token;

    private String platform;

    private String deviceName;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(
            String platform
    ) {
        this.platform = platform;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(
            String deviceName
    ) {
        this.deviceName = deviceName;
    }
}
