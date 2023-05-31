public class UserLeaderboard {
    private String user;
    private long time;


    public UserLeaderboard(String user, long time) {
        this.user = user;
        this.time = time;
    }

    public String getUser(){
        return user;
    }

    public long getTime(){
        return time;
    }

    public void setTime(long time){
        this.time=time;
    }


    @Override
    public String toString() {
        return "{" +
            " user='" + getUser() + "'" +
            ", time='" + getTime() + "'" +
            "}";
    }

}
