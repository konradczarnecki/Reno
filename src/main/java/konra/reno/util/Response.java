package konra.reno.util;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter
public class Response<T> {

    private String status;
    private String message;
    private T content;

    public Response(String status){
        this.status = status;
    }

    public static Response success(){
        return new Response<>("success");
    }

    public static Response failure(){
        return new Response<>("failure");
    }
}
