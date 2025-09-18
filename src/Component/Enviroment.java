package Component;

import Servisofts.SPGConect;
import Servisofts.Server.SSSAbstract.SSSessionAbstract;

import org.json.JSONObject;

public class Enviroment
{
    public static final String COMPONENT = "enviroment";

    public Enviroment(JSONObject obj, SSSessionAbstract session) {
        switch (obj.getString("type")) {
            case "getVersion":
                getVersion(obj);
                break;
            case "getByKey":
                getByKey(obj);
                break;
            default:
                break;
        }
    }
    
    public void getByKey(JSONObject obj){
        try{
            String consulta =  "select get_by_key('"+COMPONENT+"','"+obj.getString("key")+"') as json";
            JSONObject data = SPGConect.ejecutarConsultaObject(consulta);
            obj.put("data", data);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public void getVersion(JSONObject obj){
        try{
            String consulta =  "select get_by_key('"+COMPONENT+"','version') as json";
            JSONObject data = SPGConect.ejecutarConsultaObject(consulta);
            obj.put("data", data.getJSONObject("version").getString("data"));
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static String getVersion(){
        try{
            String consulta =  "select get_by_key('"+COMPONENT+"','version') as json";
            JSONObject data = SPGConect.ejecutarConsultaObject(consulta);
            return data.getJSONObject("version").getString("data");
        }catch(Exception e){
            e.printStackTrace();
            return "0";
        }
    }
}
