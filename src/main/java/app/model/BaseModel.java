package app.model;


import lombok.*;

@Data
@Getter
@Setter
@ToString
@NoArgsConstructor
public class   BaseModel {
    public String docNo;
    public String date;
    public String title;
    public String content;


    public BaseModel(String docNo, String date, String title, String content) {
        this.docNo = docNo;
        this.date = date;
        this.title = title;
        this.content = content;
    }



}
