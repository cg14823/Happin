package mosaic.happin;

public class Comments {
    private Long timestamp;
    private String user;
    private String comment;

    public Comments() {
    }

    public Comments(String comment, String user, Long timestamp) {
        this.comment = comment;
        this.user = user;
        this.timestamp = timestamp;
    }
    public String getComment(){
        return comment;
    }
    public String getUser(){
        return user;
    }
    public Long getTimestamp(){
        return timestamp;
    }
}
