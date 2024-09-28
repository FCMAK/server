package Component;

import org.json.JSONArray;
import org.json.JSONObject;
import Server.SSSAbstract.SSServerAbstract;
import Server.SSSAbstract.SSSessionAbstract;

public class Especialidad {
    public static final String COMPONENT = "especialidad";

    public static void onMessage(JSONObject obj, SSSessionAbstract session) {
        switch (obj.getString("type")) {
            case "getAll": getAll(obj, session); break;
            default: resend(obj, session);
        }
    }

    public static void getAll(JSONObject obj, SSSessionAbstract session) {
        try {
            String token = Kolping.getToken();
            
            JSONArray data = Kolping.get(token, "Especialidades/"+obj.getString("nrosuc"));
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            e.printStackTrace();
        }
    }

    public static JSONArray getAll(String nrosuc) {
        try {
            String token = Kolping.getToken();
            
            return Kolping.get(token, "Especialidades/"+nrosuc);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void resend(JSONObject obj, SSSessionAbstract session) {
        try {
            if (!obj.has("id_session_client")) {
                obj.put("id_session_client", session.getIdSession());
                Integrador.send(obj);
                obj.put("estado", "cargando");
                obj.put("data", new JSONArray());
                return;
            }
            SSServerAbstract.getSession(obj.getString("id_session_client")).send(obj.toString());
            obj.put("noSend", true);
        } catch (Exception e) {
            obj.put("estado", "error");
            e.printStackTrace();
        }
    }

}
