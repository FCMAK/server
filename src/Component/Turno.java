package Component;

import org.json.JSONArray;
import org.json.JSONObject;
import Server.SSSAbstract.SSSessionAbstract;

public class Turno {
    public static final String COMPONENT = "turno";

    public static void onMessage(JSONObject obj, SSSessionAbstract session) {
        switch (obj.getString("type")) {
            case "getAll":
                getAll(obj, session);
                break;
        }
    }

    public static void getAll(JSONObject obj, SSSessionAbstract session) {
        try {
            String token = Kolping.getToken();
            
            JSONArray data = null;
            if(obj.has("codmed") && !obj.isNull("codmed")){
                data = Kolping.get(token, "Turnos"+"/"+obj.getString("nrosuc")+"/"+obj.get("codmed")+"/"+obj.getString("fectur"));
            }else{
                data = Kolping.get(token, "Turnos"+"/"+obj.getString("nrosuc")+"/*/"+obj.getString("fectur"));
            }
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            e.printStackTrace();
        }
    }

    public static JSONArray getAll(String nrosuc, String codmed, String fecTur) {
        try {
            String token = Kolping.getToken();
            
            //String consulta = "select get_all('" + COMPONENT + "') as json";
            return Kolping.get(token, "Turnos"+"/"+nrosuc+"/"+codmed+"/"+fecTur);
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

   

}
