package app.model.childModel;


import lombok.*;

@Data
@Getter
@Setter
@ToString
@NoArgsConstructor
public class   FTModel {
    @NonNull
    private String docNo;
    @NonNull
    private String date;
    @NonNull
    private String title;
    @NonNull
    private String content;
    @NonNull
    private String author;

    }
