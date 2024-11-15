package app.model.childModel;

import app.model.QueryObject;
import lombok.Data;

@Data
public class TopicModel extends QueryObject {

    private String topicNum;

    private String title;

    private String getDescription;

    private String narrative;

    private boolean relevant;

    @Override
    public String toString() {
        return getDescription + " " + narrative;
    }
}
