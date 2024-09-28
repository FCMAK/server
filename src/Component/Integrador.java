package Component;

import org.json.JSONObject;
import Server.SSSAbstract.SSSessionAbstract;;

public class Integrador {
    public static final String COMPONENT = "integrador";
    public static SSSessionAbstract session;

    public static boolean send(JSONObject obj) {
        if (session != null) {
            session.send(obj.toString());
            return true;
        }
        return false;
    }

    public static void onMessage(JSONObject obj, SSSessionAbstract session) {
        switch (obj.getString("type")) {
            case "identificarme":
                identificarme(obj, session);
                break;
        }
    }

    public static void identificarme(JSONObject obj, SSSessionAbstract session) {
        try {
            Integrador.session = session;
            JSONObject data = new JSONObject();
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            e.printStackTrace();
        }
    }

}
