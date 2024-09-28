package Component;

import org.json.JSONArray;
import org.json.JSONObject;
import Server.SSSAbstract.SSServerAbstract;
import Server.SSSAbstract.SSSessionAbstract;

public class Paciente {
    public static final String COMPONENT = "paciente";

    public static void onMessage(JSONObject obj, SSSessionAbstract session) {
        switch (obj.getString("type")) {
            case "getAll": getAll(obj, session); break;
            case "getByCi": getByCi(obj, session); break;
            case "registro": registro(obj, session); break;
            default: resend(obj, session);
        }
    }

    public static void getAll(JSONObject obj, SSSessionAbstract session) {
        try {
            String token = Kolping.getToken();
            
            JSONArray data = Kolping.get(token, "Paciente/"+obj.getString("nrosuc")+"/"+  obj.getString("codesp"));
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            e.printStackTrace();
        }
    }

    public static void getByCi(JSONObject obj, SSSessionAbstract session) {
        try {
            String token = Kolping.getToken();
            
            JSONArray data = Kolping.get(token, "VerificarRegistroPorDocumento/"+obj.getString("ci"));
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            e.printStackTrace();
        }
    }

    public static JSONArray getByCi(String ci) {
        try {
            String token = Kolping.getToken();
            
            return Kolping.get(token, "VerificarRegistroPorDocumento/"+ci);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void registro(JSONObject obj, SSSessionAbstract session) {
        try {
            String token = Kolping.getToken();
            
            JSONArray data = Kolping.get(token, "VerificarRegistroPorDocumento/"+obj.getString("ci"));
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            e.printStackTrace();
        }
    }



    public static JSONArray registro(JSONObject paciente) {
        try {

            String token = Kolping.getToken();
            return Kolping.post(token, "RegistrarPaciente", paciente);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JSONArray getAll(String nrosuc, String codesp) {
        try {
            String token = Kolping.getToken();
            return Kolping.get(token, "Paciente/"+nrosuc+"/"+  codesp);
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
