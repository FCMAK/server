package Component;

import org.json.JSONArray;
import org.json.JSONObject;
import Server.SSSAbstract.SSSessionAbstract;
import Servisofts.SConfig;
import Servisofts.SPGConect;
import Servisofts.SUtil;

public class OrdenCompra {
    public static final String COMPONENT = "orden_compra";

    public static void onMessage(JSONObject obj, SSSessionAbstract session) {
        switch (obj.getString("type")) {
            case "getAll": getAll(obj, session); break;
            case "getByKey": getByKey(obj, session); break;
            case "registro": registro(obj, session); break;
            case "editar": editar(obj, session); break;
            case "solicitarQr": solicitarQr(obj, session); break;
            case "getActivas": getActivas(obj, session); break;
            case "verificarPago": verificarPago(obj, session); break;
        }
    }

    public static JSONObject confirmar(String key) {
        try {
            
            JSONObject ordenCompra = OrdenCompra.getByKey(key);

            if(ordenCompra==null){
                return null;
            }

            JSONObject data = ordenCompra.getJSONObject("data");            
            

            JSONObject ficha = new JSONObject();
            ficha.put("NroSuc", data.getString("nrosuc"));
            ficha.put("CodPac", ordenCompra.get("codpac"));
            ficha.put("UsuReg", "Servisofts");
            
            // Facturacion
            ficha.put("DatNdo",ordenCompra.optString("nit", ""));
            ficha.put("DatTdo", "1");
            ficha.put("DatNom", ordenCompra.optString("razon_social", ""));
            ficha.put("DatMai", ordenCompra.optString("email_factura", ""));

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

              
            String token = Kolping.getToken();

            String qrid = ordenCompra.get("qrid")+"";
            String voucher = ordenCompra.get("voucher")+"";
            
            JSONObject ForPag = new JSONObject();
            ForPag.put("ImpTra", precio);//siempre el monto
            ForPag.put("ImpQrc", precio);//siempre el monto
            ForPag.put("IdeQrc", Integer.parseInt(qrid)); // Qr id
            ForPag.put("QrcAut", voucher);// codigo del boucher

            ficha.put("ForPag", ForPag);
            System.out.println(ficha);
            JSONArray data_ = Kolping.post(token, "Comprar", ficha);
            
            JSONObject obj = new JSONObject();
            obj.put("key",key).put("confirmacion", data_.getJSONObject(0)).put("estado_pago", "pagado");

            OrdenCompra.editar(obj);

            return obj;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
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

    public static JSONObject getByQrId(String qrid) {
        try {
            String consulta = "select get_orden_compra('"+qrid+"') as json";
            return SPGConect.ejecutarConsultaObject(consulta);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void getActivas(JSONObject obj, SSSessionAbstract session) {
        try {
            String consulta = "select get_mis_ordenes('"+obj.getString("key_usuario")+"') as json";
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

    public static void solicitarQr(JSONObject obj, SSSessionAbstract session) {
        try {

            JSONObject ordenCompra = OrdenCompra.getByKey(obj.getString("key"));

            if(ordenCompra.has("confirmacion") && !ordenCompra.isNull("confirmacion")){
                obj.put("data", ordenCompra.get("confirmacion"));
                obj.put("estado", "exito");
                return ;
            }

            JSONArray detalle = ordenCompra.getJSONObject("data").getJSONArray("detalle");

            double total = 0;
            String glosa="";
            for (int i = 0; i < detalle.length(); i++) {
                total+=detalle.getJSONObject(i).getDouble("PreV01");
                glosa=detalle.getJSONObject(i).getString("NomPro")+"... ";
            }

            String token = Kolping.getToken();

            JSONObject sendQr = new JSONObject();
            sendQr.put("gloss", glosa);
            sendQr.put("amount", total);
            sendQr.put("additionalData", SConfig.getJSON().optString("url_callback")+"/rest/kolping/payment");
            //sendQr.put("additionalData", "sdfsdf");
            sendQr.put("transactionId", "12345");

            JSONObject qr = Kolping.post_(token, "ObtenerQr", sendQr);

            JSONObject ordenCompra_ = new JSONObject();
            ordenCompra_.put("key", obj.getString("key"));
            ordenCompra_.put("confirmacion", qr);
            ordenCompra_.put("estado_pago", "esperando_pago");


            if(qr.has("id") && !qr.isNull("id")){
                ordenCompra_.put("qrid", qr.get("id")+"");
            }
            OrdenCompra.editar(ordenCompra_);


            obj.put("data", qr);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            e.printStackTrace();
        }
    }

    public static void verificarPago(JSONObject obj, SSSessionAbstract session) {
        obj.put("data", verificarPago(obj.getString("key")));
        obj.put("estado", "exito");
    }

    public static JSONObject verificarPago(String key) {
        try {


            JSONObject ordenCompra = OrdenCompra.getByKey(key);

            if(ordenCompra.getString("estado_pago").equals("pagado")){
                return ordenCompra;
            }

            String token = Kolping.getToken();
            JSONObject sendQr = new JSONObject();

            String qrid = ordenCompra.getString("qrid");

            sendQr.put("qrid", qrid);
            JSONObject qr = Kolping.post_(token, "VerificarQr", sendQr);


            if(qr.has("status") && qr.getBoolean("status")==false){
                JSONObject send = new JSONObject();
                send.put("component", "orden_compra");
                send.put("type", "verificar_pago");
                send.put("estado", "error");
                send.put("error", "Qr ya pagado");
                return send;
            }

            if(qr.getInt("statusId")==2){
                
                    ordenCompra.put("voucher", qr.get("voucherId")+"");
                    ordenCompra.put("estado_pago", "pagado");
                    ordenCompra.put("qrid", qrid);
                    OrdenCompra.editar(ordenCompra);
                    confirmar(ordenCompra.getString("key"));
            }
            
            return OrdenCompra.getByKey(key);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JSONObject getByKey(String key) {
        try {
            String consulta = "select get_by_key_orden_compra('"+key+"') as json";
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
