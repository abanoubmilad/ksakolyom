package abanoubm.ksakolyom;

public class Story extends StoryList {
    private String read;

    public Story(String id, String photo, String fullPhoto, String content, String date, String read) {
        super(id, photo, fullPhoto, content, date);
        this.read = read;
    }

    public Story(String id, String photo, String fullPhoto, String content, String date) {
        super(id, photo, fullPhoto, content, date);
        this.read = "0";
    }


    public String getRead() {
        return read;
    }


}
