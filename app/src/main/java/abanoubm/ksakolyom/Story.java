package abanoubm.ksakolyom;

public class Story extends StoryList {
    private String read;
    private String fullPhoto;

    public Story(String id, String read, String photo, String fullPhoto, String content, String date) {
        super(id, photo, content, date);
        this.read = read;
        this.fullPhoto = fullPhoto;
    }

    public Story(String id, String photo, String fullPhoto, String content, String date) {
        super(id, photo, content, date);
        this.read = "0";
        this.fullPhoto = fullPhoto;
    }


    public String getRead() {
        return read;
    }

    public void setRead(String read) {
        this.read = read;
    }

    public String getFullPhoto() {
        return fullPhoto;
    }

    public void setFullPhoto(String fullPhoto) {
        this.fullPhoto = fullPhoto;
    }
}
