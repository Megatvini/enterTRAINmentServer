package ge.edu.freeuni.android.entertrainment.server.services.music.data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement
public class Music  implements Cloneable{
    public Music(){}

    @XmlElement
    private String id;
    @XmlElement
    private String name;
    @XmlElement
    private int rating;
    @XmlElement
    private String imagePath;
    @XmlElement
    private String voted = "null";
    private int duration;

    @XmlTransient
    private  String filePath;


    public Music(int duration, String id , String name, int rating, String imagePath) {
        this.id = id;
        this.duration = duration;
        this.name = name;
        this.rating = rating;
        this.imagePath = imagePath;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @XmlTransient
    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVoted() {
        return voted;
    }

    public void setVoted(String voted) {
        this.voted = voted;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Music && ((Music) obj).getId().equals(this.getId());
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public Music clone() throws CloneNotSupportedException {
        Music clone = (Music) super.clone();
        clone.setRating(rating);
        clone.setName(name);
        clone.setId(id);
        clone.setFilePath(filePath);
        clone.setImagePath(imagePath);
        return clone;
    }


    public String toJson(){
        return String.format("{\"name\":\"%s\",\"id\":\"%s\",\"rating\":%d,\"imagePath\":\"%s\",\"voted\":\"%s\"}",

                name,
                id,
                rating,
                imagePath,
                voted
        );
    }



}
