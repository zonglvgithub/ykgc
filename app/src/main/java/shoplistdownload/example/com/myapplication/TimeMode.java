package shoplistdownload.example.com.myapplication;

/**
 * Created by wangxuan on 17/3/8.
 */

public class TimeMode {

    private String time;
    private boolean isSelected;
    private boolean validTime; //f:有效时间段 t:无效时间段 显示灰色

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isValidTime() {
        return validTime;
    }

    public void setValidTime(boolean validTime) {
        this.validTime = validTime;
    }


    @Override
    public String toString() {
        return "TimeMode{" +
                "time='" + time + '\'' +
                ", isSelected=" + isSelected +
                ", validTime=" + validTime +
                '}';
    }
}
