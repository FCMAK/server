import org.json.JSONArray;
import org.json.JSONObject;

public class Test {
    public static void main(String[] args) {
        try {
            JSONObject mailConfig = new JSONObject();
            mailConfig.put("subject", "Recuperar contraseña!");
            mailConfig.put("path", "mail/recuperar_pass.html");

            JSONObject params = new JSONObject();
            params.put("codigo", "7732732");

            new Email(new JSONArray().put("rickypazd@hotmail.com"), mailConfig, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}   
