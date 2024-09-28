package Component;

import org.json.JSONArray;
import org.json.JSONObject;
import Server.SSSAbstract.SSSessionAbstract;
import Servisofts.SPGConect;
import Servisofts.SUtil;

public class OrdenCompra {
    public static final String COMPONENT = "orden_compra";

    public static void onMessage(JSONObject obj, SSSessionAbstract session) {
        switch (obj.getString("type")) {
            case "getAll": getAll(obj, session); break;
            case "getByKey": getByKey(obj, session); break;
            case "confirmar": confirmar(obj, session); break;
            case "registro": registro(obj, session); break;
            case "editar": editar(obj, session); break;
        }
    }

    public static void confirmar(JSONObject obj, SSSessionAbstract session) {
        try {
            
            JSONObject ordenCompra = OrdenCompra.getByKey(obj.getString("key"));

            if(ordenCompra==null){
                obj.put("estado", "error");
                obj.put("error", "No existe la orden de compra");
                return;
            }

            JSONObject data = ordenCompra.getJSONObject("data");            
            

            JSONObject ficha = new JSONObject();
            ficha.put("NroSuc", data.getString("nrosuc"));
            ficha.put("CodPac", Integer.parseInt(ordenCompra.getString("codpac")));
            ficha.put("UsuReg", "rpaz");
            
            // Facturacion
            ficha.put("DatNdo", "166224026");
            ficha.put("DatTdo", "1");
            ficha.put("DatNom", "Servisofts Srl.");
            ficha.put("DatMai", "servisofts.srl@gmail.com");

            JSONArray SolSer = new JSONArray();

            double precio = 0;

            JSONObject laSol;
            JSONObject det;
            for (int i = 0; i < data.getJSONArray("detalle").length(); i++) {
                det = data.getJSONArray("detalle").getJSONObject(i);
                laSol = new JSONObject();
                laSol.put("NroSuc", data.getString("nrosuc"));
                laSol.put("CodEsp", data.getString("nrosuc"));
                laSol.put("CodPro", det.getString("CodPro"));
                laSol.put("GruPro", det.getString("GruPro"));
                laSol.put("ClaPro", det.getString("ClaPro"));
                laSol.put("CodMed", det.get("CodMed"));
                laSol.put("CodTur", data.getString("codtur")+data.getString("comtur"));
                laSol.put("PrePro", det.getDouble("PreV01"));
                laSol.put("FecSol", SUtil.now().substring(0, 23)+"Z");
                
                precio+=det.getDouble("PreV01");
                SolSer.put(laSol);
            }
            
            ficha.put("SolSer", SolSer);
            ficha.put("PreSol", precio);

            JSONObject ForPag = new JSONObject();
            ForPag.put("ImpTra", precio);
            ForPag.put("ImpQrc", precio);
            ForPag.put("QrcAut", "ABC12345");
            ficha.put("ForPag", ForPag);
              
            String token = Kolping.getToken();
            JSONArray data_ = Kolping.post(token, "Comprar", ficha);
            
            OrdenCompra.editar(new JSONObject().put("key", obj.getString("key")).put("confirmacion", data_).put("estado_pago", "esperando_confirmacion"));

            obj.put("data", data_);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            e.printStackTrace();
        }
    }

    public static void getAll(JSONObject obj, SSSessionAbstract session) {
        try {
            String consulta = "select get_all('" + COMPONENT + "', 'key_usuario', '"+obj.getString("key_usuario")+"') as json";
            JSONObject data = SPGConect.ejecutarConsultaObject(consulta);
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            e.printStackTrace();
        }
    }

    public static void getByKey(JSONObject obj, SSSessionAbstract session) {
        try {
            String consulta = "select get_by_key('" + COMPONENT + "', '"+obj.getString("key")+"') as json";
            JSONObject data = SPGConect.ejecutarConsultaObject(consulta);
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            e.printStackTrace();
        }
    }

    public static JSONObject getByKey(String key) {
        try {
            String consulta = "select get_by_key('" + COMPONENT + "', '"+key+"') as json";
            return SPGConect.ejecutarConsultaObject(consulta);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void registro(JSONObject obj, SSSessionAbstract session) {
        try {
            JSONObject data_ = new JSONObject();
            data_.put("key", SUtil.uuid());
            data_.put("estado", 1);
            data_.put("estado_pago", "pendiente");
            data_.put("fecha_on", SUtil.now());
            data_.put("data", obj.getJSONObject("data"));
            data_.put("key_usuario", obj.getString("key_usuario"));
            SPGConect.insertObject(COMPONENT, data_);
            obj.put("data", data_);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            e.printStackTrace();
        }
    }

    public static void editar(JSONObject obj, SSSessionAbstract session) {
        try {
            JSONObject data = obj.getJSONObject("data");
            SPGConect.editObject(COMPONENT, data);
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            e.printStackTrace();
        }
    }
    public static boolean editar(JSONObject data) {
        try {
            SPGConect.editObject(COMPONENT, data);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
