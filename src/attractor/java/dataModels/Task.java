package attractor.java.dataModels;

public class Task {
    private int idTask;
    private String name;
    private int type;
    private String desc;
    private int idDay;

    public Task(int idTask, String name, int type, String desc, int idDay) {
        this.idTask = idTask;
        this.name = name;
        this.type = type;
        this.desc = desc;
        this.idDay = idDay;
    }

    public int getIdTask() {
        return idTask;
    }

    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }

    public int getIdDay() {
        return idDay;
    }

    public void setIdTask(int idTask) {
        this.idTask = idTask;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setIdDay(int idDay) {
        this.idDay = idDay;
    }
}