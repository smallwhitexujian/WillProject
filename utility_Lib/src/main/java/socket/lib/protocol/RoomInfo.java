package socket.lib.protocol;

/**
 * Created by xujian on 15/9/10.
 */
public class RoomInfo {
    public int BarId = 0;
    public int UserId = 0;
    public RoomInfo(int mBarId, int mUserId) {
        super();
        this.BarId = mBarId;
        this.UserId = mUserId;
    }
    public int getBarId() {
        return BarId;
    }

    public void setBarId(int barId) {
        BarId = barId;
    }

    public int getUserId() {
        return UserId;
    }

    public void setUserId(int userId) {
        UserId = userId;
    }

    @Override
    public String toString() {
        return "RoomInfo{" +
                "BarId=" + BarId +
                ", UserId=" + UserId +
                '}';
    }
}
