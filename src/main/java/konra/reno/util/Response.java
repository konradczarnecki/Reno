package konra.reno.util;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class Response<T> {

    @Getter @Setter private String status;
    @Getter @Setter private String message;
    @Getter @Setter private T content;

    private Response(String status){
        this.status = status;
    }

    public static Response success(){

        return new Response<>("success");
    }

    public static Response failure(){
        return new Response("failure");
    }
}
