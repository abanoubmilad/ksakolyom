package abanoubm.ksakolyom;

public class StoryList {
    private String id;
    private String photo;
    private String content;
    private String date;

    public StoryList(String id, String photo, String content, String date) {
        this.id = id;
        this.photo = photo;
        this.content = content;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }




    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


}
