package areda.model;

public class Application {
    private User user;
    private Job job;

    public Application() {
    }

    public Application(User user, Job job) {
        this.user = user;
        this.job = job;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }
}
