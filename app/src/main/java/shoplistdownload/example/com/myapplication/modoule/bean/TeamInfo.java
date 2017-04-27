package shoplistdownload.example.com.myapplication.modoule.bean;

/**
 * Created by zhanghuawei on 2017/4/26.
 *
 */

public class TeamInfo {

    private String name;
    private String id;
    private int startPosition;
    private int endPosition;

    public int getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(int startPosition) {
        this.startPosition = startPosition;
    }

    public int getEndPosition() {
        return endPosition;
    }

    public void setEndPosition(int endPosition) {
        this.endPosition = endPosition;
    }

    public TeamInfo() {
    }

    public TeamInfo(String name, String id, int startPosition, int endPosition) {
        this.name = name;
        this.id = id;
        this.startPosition = startPosition;
        this.endPosition = endPosition;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }



    @Override
    public String toString() {
        return "TeamInfo{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", startPosition=" + startPosition +
                ", endPosition=" + endPosition +
                '}';
    }
}
