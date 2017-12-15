package konra.reno;

public class Response<T> {

    private String status;
    private String message;
    private T content;

    public Response(){
    }

    private Response(String status){
        this.status = status;
    }

    public static Response success(){
        return new Response<>("success");
    }

    public static Response failure(){
        return new Response("failure");
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }
}
