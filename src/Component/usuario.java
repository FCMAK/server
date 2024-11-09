package Component;

import org.json.JSONObject;
import Server.SSSAbstract.SSSessionAbstract;
import SocketCliente.SocketCliente;

public class usuario {
    public static final String COMPONENT = "usuario";

    public static void onMessage(JSONObject obj, SSSessionAbstract session) {
        switch (obj.getString("type")) {
            case "identificacion":
                identificacion(obj, session);
                break;
        }
    }

    public static void identificacion(JSONObject obj, SSSessionAbstract session) {
        JSONObject send = new JSONObject();
        send.put("component", "firebase_token");
        send.put("type", "registro");
        if (obj.has("firebase")) {
            send.put("firebase", obj.getJSONObject("firebase"));
            send.put("estado", "cargando");
            SocketCliente.send("notification", send.toString());
        }

       
    }

}
