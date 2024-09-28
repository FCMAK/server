package Component;

import java.util.Date;
import java.util.UUID;
import org.json.JSONArray;
import org.json.JSONObject;
import Servisofts.SPGConect;
import Servisofts.SUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import Server.SSSAbstract.SSSessionAbstract;

public class Ficha {
    public static final String COMPONENT = "ficha";

    public static void onMessage(JSONObject obj, SSSessionAbstract session) {
        switch (obj.getString("type")) {
            case "editar":
                editar(obj, session);
                break;
        }
    }

    public static JSONArray registro(String NroSuc, String CodPac, String GenPer) {
        try {
            String token = Kolping.getToken();
            
            JSONObject ficha = new JSONObject();

            ficha.put("NroSuc", "0");
            ficha.put("CodPac", CodPac);
            ficha.put("GenPer", "M");
            ficha.put("PreSol", 60);
            ficha.put("UsuReg", "jtapia");
            ficha.put("DatNdo", "9751245");
            ficha.put("DatTdo", "1");
            ficha.put("DatNom", "JOSE MANUEL TAPIA MARTINEZ");
            ficha.put("DatMai", "jose.tapia@example.com");

            JSONArray SolSer = new JSONArray();
            JSONObject laSol = new JSONObject();
            laSol.put("NroSuc", "0");
            laSol.put("CodEsp", "1");
            laSol.put("CodPro", "CC");
            laSol.put("GruPro", "C");
            laSol.put("ClaPro", "1010000");
            laSol.put("CodMed", 329);
            laSol.put("CodTur", "10A");
            laSol.put("PrePro", 60);
            laSol.put("FecSol", "2024-09-20T22:44:42.659Z");
            SolSer.put(laSol);
            ficha.put("SolSer", SolSer);
            
            JSONObject ForPag = new JSONObject();
            ForPag.put("ImpTra", 60);
            ForPag.put("ImpQrc", 60);
            ForPag.put("QrcAut", "ABC12345");
            ficha.put("ForPag", ForPag);
              

            return Kolping.post(token, "Comprar", ficha);
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    

    public static JSONArray getAll() {
        try {
            String token = Kolping.getToken();
            
            //String consulta = "select get_all('" + COMPONENT + "') as json";
            return Kolping.get(token, "Sucursales");
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static void editar(JSONObject obj, SSSessionAbstract session) {
        try {
            JSONObject data = obj.getJSONObject("data");
            SPGConect.editObject("sucursal", data);
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            e.printStackTrace();
        }
    }

}
