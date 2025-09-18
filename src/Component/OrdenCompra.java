package Component;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import Servisofts.Server.SSSAbstract.SSSessionAbstract;
import Servisofts.SConfig;
import Servisofts.SConsole;
import Servisofts.SPGConect;
import Servisofts.SUtil;

public class OrdenCompra {
    public static final String COMPONENT = "orden_compra";

    public static void onMessage(JSONObject obj, SSSessionAbstract session) {
        switch (obj.getString("type")) {
            case "getAll":
                getAll(obj, session);
                break;
            case "getByKey":
                getByKey(obj, session);
                break;
            case "registro":
                registro(obj, session);
                break;
            case "editar":
                editar(obj, session);
                break;
            case "dispensar":
                Dispensar(obj, session);
                break;
            case "solicitarQr":
                solicitarQr(obj, session);
                break;
            case "getActivas":
                getActivas(obj, session);
                break;
            case "verificarPago":
                verificarPago(obj, session);
                break;
            case "verificarPagoV2":
                verificarPagoV2(obj, session);
                break;
            case "confirmar":
                try {
                    confirmar(obj.getString("key"));
                } catch (Exception e) {
                    obj.put("error", e.getMessage());
                    obj.put("estado", "error");
                }
                break;
            case "registrarPreOrden":
                registrarPreOrden(obj, session);
                break;
            case "verificarPreOrden":
                verificarPreOrden(obj, session);
                break;
            case "historyPreOrden":
                historyPreOrden(obj, session);
                break;
             case "getActivasKolping":
                getActivasKolping(obj, session);
                break;
             case "getPreOrden":
                getPreOrden(obj, session);
                break;
        }
    }

    public static JSONObject confirmar(String key) throws Exception {
        JSONObject ordenCompra = OrdenCompra.getByKey(key);

        if (ordenCompra == null) {
            return null;
        }

        JSONObject data = ordenCompra.getJSONObject("data");

        JSONObject ficha = new JSONObject();
        ficha.put("NroSuc", data.getString("nrosuc"));
        ficha.put("CodPac", ordenCompra.get("codpac"));
        ficha.put("UsuReg", "Servisofts");

        // Facturacion
        ficha.put("DatNdo", ordenCompra.optString("nit", ""));
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
            laSol.put("CodEsp", det.getString("CodEsp"));
            laSol.put("CodPro", det.getString("CodPro"));
            laSol.put("GruPro", det.getString("GruPro"));
            laSol.put("ClaPro", det.getString("ClaPro"));
            laSol.put("CodMed", det.get("CodMed"));
            laSol.put("CodTur", data.getString("codtur") + data.getString("comtur"));
            laSol.put("PrePro", det.getDouble("PreV01"));
            // laSol.put("FecSol", SUtil.now().substring(0, 23) + "Z"); // Aca no deberia
            // ser la fecha actual sino la fecha para la que compra
            laSol.put("FecSol", data.getString("fecha") + "T00:00:00"); // Aca no deberia ser la
                                                                        // fecha actual sino la
                                                                        // fecha para la que
                                                                        // compramos.

            precio += det.getDouble("PreV01");
            SolSer.put(laSol);
        }

        ficha.put("SolSer", SolSer);
        ficha.put("PreSol", precio);

        String token = Kolping.getToken();

        String qrid = ordenCompra.get("qrid") + "";
        String voucher = ordenCompra.get("voucher") + "";

        if (qrid.length() <= 0) {
            SConsole.log("Hay error no viene qrid", voucher);
        }
        if (voucher.length() <= 0) {
            SConsole.log("Hay error no viene voucher", voucher);
        }

        JSONObject ForPag = new JSONObject();
        ForPag.put("ImpTra", precio);// siempre el monto
        ForPag.put("ImpQrc", precio);// siempre el monto
        ForPag.put("IdeQrc", Integer.parseInt(qrid)); // Qr id
        ForPag.put("QrcAut", voucher);// codigo del boucher

        ficha.put("ForPag", ForPag);
        System.out.println(ficha);
        JSONArray data_ = Kolping.post(token, "Comprar", ficha);

        JSONObject compra = data_.getJSONObject(0);

        SConsole.warning("Esta es la respuesta de el endpoint comprar", data_);
        JSONObject obj = new JSONObject();
        // obj.put("key", key).put("confirmacion", compra.put("estado_pago", "pagado"));
        // OrdenCompra.editar(obj.put("estado_pago", "pagado"));
        // compra.keys().forEachRemaining(k -> {
        // ordenCompra.getJSONObject("data").put(k, compra.get(k));
        // });
        ordenCompra.put("confirmacion", compra);
        ordenCompra.getJSONObject("data").put("codtur", compra.get("CodTur"));
        ordenCompra.getJSONObject("data").put("fecha",
                compra.optString("FecTur", ordenCompra.getJSONObject("data").getString("fecha")));

        ordenCompra.put("estado_pago", "pagado");
        OrdenCompra.editar(ordenCompra);

        return obj;
    }

    public static JSONObject confirmarV2(String key) throws Exception {
        JSONObject ordenCompra = OrdenCompra.getByKey(key);

        if (ordenCompra == null) {
            return null;
        }

        JSONObject data = ordenCompra.getJSONObject("data");

        JSONObject ficha = new JSONObject();
        ficha.put("NroSuc", data.getString("nrosuc"));
        ficha.put("CodPac", ordenCompra.get("codpac"));
        ficha.put("UsuReg", "Servisofts");

        // Facturacion
        ficha.put("DatNdo", ordenCompra.optString("nit", ""));
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
            laSol.put("CodEsp", det.getString("CodEsp"));
            laSol.put("CodPro", det.getString("CodPro"));
            laSol.put("GruPro", det.getString("GruPro"));
            laSol.put("ClaPro", det.getString("ClaPro"));
            laSol.put("CodMed", det.get("CodMed"));
            laSol.put("CodTur", data.getString("codtur") + data.getString("comtur"));
            laSol.put("PrePro", det.getDouble("PreV01"));
            // laSol.put("FecSol", SUtil.now().substring(0, 23) + "Z"); // Aca no deberia
            // ser la fecha actual sino la fecha para la que compra
            laSol.put("FecSol", data.getString("fecha") + "T00:00:00"); // Aca no deberia ser la
                                                                        // fecha actual sino la
                                                                        // fecha para la que
                                                                        // compramos.

            precio += det.getDouble("PreV01");
            SolSer.put(laSol);
        }

        ficha.put("SolSer", SolSer);
        ficha.put("PreSol", precio);

        String token = Kolping.getToken();

        String qrid = ordenCompra.get("qrid") + "";
        String voucher = ordenCompra.get("voucher") + "";

        if (qrid.length() <= 0) {
            SConsole.log("Hay error no viene qrid", voucher);
        }
        if (voucher.length() <= 0) {
            SConsole.log("Hay error no viene voucher", voucher);
        }

        JSONObject ForPag = new JSONObject();
        ForPag.put("ImpTra", precio);// siempre el monto
        ForPag.put("ImpQrc", precio);// siempre el monto
        ForPag.put("IdeQrc", Integer.parseInt(qrid)); // Qr id
        ForPag.put("QrcAut", voucher);// codigo del boucher

        ficha.put("ForPag", ForPag);
        System.out.println(ficha);
        JSONArray data_ = Kolping.post(token, "ComprarV2", ficha);

        if (data_.length() == 0) {
            throw new Exception("No se pudo confirmar la compra");
        }
        if (data_.getJSONObject(0).has("Status")) {
            if (data_.getJSONObject(0).optBoolean("Status") == false) {
                System.out.println(data_.getJSONObject(0).optString("Message"));
                // throw new Exception(data_.getJSONObject(0).optString("Message"));
                throw new Exception("Ocurrio un problema al momento de confirmar la compra");
            }

        }

        JSONObject compra = data_.getJSONObject(0);
        compra.put("version", "2");

        SConsole.warning("Esta es la respuesta de el endpoint comprar", data_);
        JSONObject obj = new JSONObject();
        // obj.put("key", key).put("confirmacion", compra.put("estado_pago", "pagado"));
        // OrdenCompra.editar(obj.put("estado_pago", "pagado"));
        // compra.keys().forEachRemaining(k -> {
        // ordenCompra.getJSONObject("data").put(k, compra.get(k));
        // });
        ordenCompra.put("confirmacion", compra);
        ordenCompra.getJSONObject("data").put("codtur", compra.get("CodTur"));
        ordenCompra.getJSONObject("data").put("fecha",
                compra.optString("FecTur", ordenCompra.getJSONObject("data").getString("fecha")));

        ordenCompra.put("estado_pago", "pagado");
        OrdenCompra.editar(ordenCompra);

        return obj;
    }

    public static void getAll(JSONObject obj, SSSessionAbstract session) {
        try {
            String consulta = "select get_all_all('" + COMPONENT + "', 'key_usuario', '"
                    + obj.getString("key_usuario") + "') as json";
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
            String consulta = "select get_orden_compra('" + qrid + "') as json";
            return SPGConect.ejecutarConsultaObject(consulta);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static void getActivasKolping(JSONObject obj, SSSessionAbstract session) {
        try {
            String token = Kolping.getToken();
            JSONObject resp = Kolping.get_(token, "HistorialPreOrdenes/" + obj.getString("key_usuario") + "?codEst=PEN|PAG");
            System.out.println(resp);
            obj.put("data", resp.getJSONArray("result"));
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            e.printStackTrace();
        }
    }
    
    public static void getActivas(JSONObject obj, SSSessionAbstract session) {
        try {
            String consulta = "select get_mis_ordenes('" + obj.getString("key_usuario") + "') as json";
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

            String consulta = "select get_by_key('" + COMPONENT + "', '" + obj.getString("key")
                    + "') as json";
            JSONObject data = SPGConect.ejecutarConsultaObject(consulta);
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            e.printStackTrace();
        }
    }

    public static void Dispensar(JSONObject obj, SSSessionAbstract session) {
        try {
            if (!SConfig.getJSON().getBoolean("realizar_venta")) {

                JSONObject whiteList = SPGConect
                        .ejecutarConsultaObject("select get_all('user_tester','key_usuario', '"
                                + obj.getString("key_usuario") + "') as json");
                if (whiteList.isEmpty()) {
                    obj.put("estado", "error");
                    obj.put("error", SConfig.getJSON().getString("realizar_venta_error_message"));
                    return;
                }
            }
            JSONObject ordenCompra = OrdenCompra.getByKey(obj.getString("key"));

            if (ordenCompra.has("data")) {
                if (ordenCompra.getJSONObject("data").has("dispensar")) {
                    obj.put("data", ordenCompra.getJSONObject("data").getJSONObject("dispensar"));
                    obj.put("estado", "exito");
                    return;
                }
            }
            ordenCompra.remove("confirmacion");
            String token = Kolping.getToken();

            JSONObject turno = new JSONObject();
            turno.put("CodMed", ordenCompra.getJSONObject("data").get("codmed"));
            turno.put("FecTur", ordenCompra.getJSONObject("data").get("fecha") + "T00:00:00");
            turno.put("CodTur", ordenCompra.getJSONObject("data").get("codtur"));
            turno.put("ComTur", ordenCompra.getJSONObject("data").get("comtur"));

            JSONObject dispensar = Kolping.put(token,
                    "ReservarTurnos/" + ordenCompra.getJSONObject("data").get("nrosuc"),
                    new JSONArray().put(turno));

            if (dispensar.getBoolean("status") == true) {
                ordenCompra.getJSONObject("data").put("dispensar", dispensar);
                OrdenCompra.editar(ordenCompra);
            }
            System.out.println(dispensar);

            obj.put("data", dispensar);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public static void solicitarQr(JSONObject obj, SSSessionAbstract session) {
        try {

            JSONObject ordenCompra = OrdenCompra.getByKey(obj.getString("key"));

            if (ordenCompra.has("confirmacion") && !ordenCompra.isNull("confirmacion")) {
                obj.put("data", ordenCompra.get("confirmacion"));
                obj.put("estado", "exito");
                return;
            }

            JSONArray detalle = ordenCompra.getJSONObject("data").getJSONArray("detalle");

            double total = 0;
            String glosa = "";
            for (int i = 0; i < detalle.length(); i++) {
                total += detalle.getJSONObject(i).getDouble("PreV01");
                glosa = detalle.getJSONObject(i).getString("NomPro") + "... ";
            }

            String token = Kolping.getToken();

            JSONObject sendQr = new JSONObject();
            sendQr.put("gloss", glosa);
            sendQr.put("amount", total);
            sendQr.put("additionalData",
                    SConfig.getJSON().optString("url_callback") + "/rest/kolping/payment");
            // sendQr.put("additionalData", "sdfsdf");
            sendQr.put("transactionId", "12345");

            JSONObject qr = Kolping.post_(token, "ObtenerQr", sendQr);

            JSONObject ordenCompra_ = new JSONObject();
            ordenCompra_.put("key", obj.getString("key"));
            ordenCompra_.put("confirmacion", qr);
            ordenCompra_.put("estado_pago", "esperando_pago");

            if (qr.has("id") && !qr.isNull("id")) {
                ordenCompra_.put("qrid", qr.get("id") + "");
            }
            OrdenCompra.editar(ordenCompra_);

            obj.put("data", qr);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());

        }
    }

    public static void verificarPago(JSONObject obj, SSSessionAbstract session) {
        try {
            obj.put("data", verificarPago(obj.getString("key")));
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("error", e.getLocalizedMessage());
            obj.put("estado", "error");
        }

    }

    public static void verificarPagoV2(JSONObject obj, SSSessionAbstract session) {
        try {
            obj.put("data", verificarPagoV2(obj.getString("key")));
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("error", e.getLocalizedMessage());
            obj.put("estado", "error");
        }

    }

    public static JSONObject verificarPagoV2(String key) throws Exception {

        JSONObject ordenCompra = OrdenCompra.getByKey(key);

        if (ordenCompra.getString("estado_pago").equals("pagado")) {
            return ordenCompra;
        }

        String token = Kolping.getToken();
        JSONObject sendQr = new JSONObject();

        if (ordenCompra.has("voucher") && !ordenCompra.isNull("voucher")) {
            System.out.println("voucher");
            confirmarV2(ordenCompra.getString("key"));
            return OrdenCompra.getByKey(key);
        }
        String qrid = ordenCompra.getString("qrid");

        sendQr.put("qrid", qrid);
        JSONObject qr = Kolping.post_(token, "VerificarQr", sendQr);

        if (qr.has("status") && qr.getBoolean("status") == false) {
            JSONObject send = new JSONObject();
            send.put("component", "orden_compra");
            send.put("type", "verificar_pago");
            send.put("estado", "error");
            send.put("error", "Qr ya pagado");
            return send;
        }

        if (qr.getInt("statusId") == 2) {

            // new Notification().send(
            // "Compra exitosa.",
            // "Se realizo el pago con exito.",
            // ordenCompra.getString("key_usuario")
            // );

            ordenCompra.put("voucher", qr.get("voucherId") + "");
            // ordenCompra.put("qrid", qrid);
            OrdenCompra.editar(ordenCompra);
            confirmarV2(ordenCompra.getString("key"));
        }

        return OrdenCompra.getByKey(key);

    }

    public static JSONObject verificarPago(String key) throws Exception {

        JSONObject ordenCompra = OrdenCompra.getByKey(key);

        if (ordenCompra.getString("estado_pago").equals("pagado")) {
            return ordenCompra;
        }

        String token = Kolping.getToken();
        JSONObject sendQr = new JSONObject();

        if (ordenCompra.has("voucher") && !ordenCompra.isNull("voucher")) {
            System.out.println("voucher");
            confirmar(ordenCompra.getString("key"));
            return OrdenCompra.getByKey(key);
        }
        String qrid = ordenCompra.getString("qrid");

        sendQr.put("qrid", qrid);
        JSONObject qr = Kolping.post_(token, "VerificarQr", sendQr);

        if (qr.has("status") && qr.getBoolean("status") == false) {
            JSONObject send = new JSONObject();
            send.put("component", "orden_compra");
            send.put("type", "verificar_pago");
            send.put("estado", "error");
            send.put("error", "Qr ya pagado");
            return send;
        }

        if (qr.getInt("statusId") == 2) {

            // new Notification().send(
            // "Compra exitosa.",
            // "Se realizo el pago con exito.",
            // ordenCompra.getString("key_usuario")
            // );

            ordenCompra.put("voucher", qr.get("voucherId") + "");
            // ordenCompra.put("qrid", qrid);
            OrdenCompra.editar(ordenCompra);
            confirmar(ordenCompra.getString("key"));
        }

        return OrdenCompra.getByKey(key);

    }

    public static JSONObject getByKey(String key) {
        try {
            String consulta = "select get_by_key_orden_compra('" + key + "') as json";
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

    public static void registrarPreOrden(JSONObject obj, SSSessionAbstract session) {
        try {
            String key = obj.getString("key");
            JSONObject orden = OrdenCompra.getByKey(key);
            // System.out.println("imprmir " + orden);
            if (orden.has("confirmacion") && !orden.isNull("confirmacion")) {
                obj.put("data", new JSONObject().put("status", true));
                obj.put("estado", "exito");
                return;
            }

            double PreSol = 0;

            JSONArray detalle = orden.getJSONObject("data").getJSONArray("detalle");

            JSONArray solSerArray = new JSONArray();

            for (int i = 0; i < detalle.length(); i++) {
                JSONObject det = detalle.getJSONObject(i);

                JSONObject servicio = new JSONObject();
                servicio.put("NroSuc", orden.getJSONObject("data").getString("nrosuc"));
                servicio.put("CodEsp", det.optString("CodEsp"));
                servicio.put("CodPro", det.getString("CodPro"));
                servicio.put("GruPro", det.getString("GruPro"));
                servicio.put("ClaPro", det.getString("ClaPro"));
                servicio.put("CodMed", det.getInt("CodMed"));
                // comtur
                servicio.put("CodTur", orden.getJSONObject("data").getString("codtur")
                        + orden.getJSONObject("data").getString("comtur"));
                servicio.put("HorTur", orden.getJSONObject("data").getString("hortur"));

                servicio.put("PrePro", det.getDouble("PreV01"));

                PreSol += det.getDouble("PreV01");
                // servicio.put("FecSol", "2024-06-24T16:31:42.659Z"); // *****************
                // servicio.put("FecSol", "2024-06-24T16:31:42.659Z"); // *****************
                servicio.put("FecSol", orden.getJSONObject("data").getString("fecha"));
                solSerArray.put(servicio);
            }

            JSONObject request = new JSONObject();
            request.put("IdeApp", obj.getString("key_usuario")); // *****************
            request.put("NroSuc", orden.getJSONObject("data").getString("nrosuc"));
            request.put("CodPac", Integer.parseInt(orden.getString("codpac")));
            request.put("PreSol", PreSol);
            request.put("UsuReg", "userappk"); // *****************
            request.put("DatNdo", orden.getString("nit"));
            request.put("DatTdo", "1"); // *****************
            request.put("DatNom", orden.getString("razon_social"));
            request.put("DatMai", orden.getString("email_factura"));
            // Crear array SolSer con un solo servicio

            request.put("SolSer", solSerArray);

            // Crear objeto ForPag
            JSONObject forPag = new JSONObject();
            forPag.put("ImpTra", PreSol);
            forPag.put("IdeQrc", JSONObject.NULL); // Usa JSONObject.NULL en lugar de null crudo
            forPag.put("ImpQrc", PreSol);
            forPag.put("QrcAut", "ABC12345");

            request.put("ForPag", forPag);

            // Imprimir el request final
            // System.out.println("Request JSON final: " + request.toString(2)); //
            // toString(2) para
            // formato bonito

            System.out.println(request);

            String token = Kolping.getToken();
            JSONObject resp = Kolping.post_2(token, "CrearPreOrden", request);

            System.out.println("respuesta " + resp);
            // obj.put("data", data);
            if (!resp.has("status") || resp.getBoolean("status") == false) {
                obj.put("estado", "error");
                obj.put("error", resp.optString("message", "Error al crear la preorden"));
                return;
            }
            JSONObject result = resp.getJSONObject("result");
            JSONObject pago = new JSONObject().put("id", result.getInt("idePag")).put("qr", result.getString("datPag"));
            JSONObject ordenCompra_ = new JSONObject();

            int nroOrder = result.getInt("nroOrd");
            ordenCompra_.put("key", orden.getString("key"));
            ordenCompra_.put("nro_ord", nroOrder);
            ordenCompra_.put("confirmacion", pago);
            ordenCompra_.put("qrid", pago.getInt("id") + "");
            ordenCompra_.put("estado_pago", "esperando_pago");
            OrdenCompra.editar(ordenCompra_);
            obj.put("data", resp);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            e.printStackTrace();
        }
    }

    public static void verificarPreOrden(JSONObject obj, SSSessionAbstract session) {
        try {
            String key = obj.getString("key");
            JSONObject orden = OrdenCompra.getByKey(key);
            if (orden == null) {
                obj.put("estado", "error");
                obj.put("error", "Orden no encontrada");
                return;
            }

            int nro_ord = orden.getInt("nro_ord");
            System.out.println("verificando preorden nro_ord: " + nro_ord);
            String token = Kolping.getToken();
            JSONObject resp = Kolping.get_(token, "ObtenerPreOrden/" + nro_ord);
            JSONObject result = resp.getJSONObject("result");
            String codEst = result.getString("codEst");
            if (codEst.equals("PAG")) {
                // Si el estado es PAG, significa que la preorden ya fue pagada
                // Confirmamos la orden de compra
                JSONObject confirmacion = new JSONObject();
                confirmacion.put("OdaPdf", result.getString("odaImg"));
                confirmacion.put("version", 2);
                confirmacion.put("FacUrl", result.getString("facUrl"));
                confirmacion.put("NroGrl",result.optInt("nroGrl"));
                orden.put("confirmacion", confirmacion);

                orden.getJSONObject("data").put("dispensar", new JSONObject());

                orden.put("estado_pago", "pagado");
                OrdenCompra.editar(orden);

            }
            // System.out.println(codEst);
            obj.put("data", orden);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            e.printStackTrace();
        }
    }

    public static void historyPreOrden(JSONObject obj, SSSessionAbstract session) {
        try {
            String token = Kolping.getToken();
            JSONObject resp = Kolping.get_(token, "HistorialPreOrdenes/" + obj.getString("key_usuario") + "?codEst=*");
            System.out.println(resp);
            obj.put("data", resp.getJSONArray("result"));
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            e.printStackTrace();
        }
    }

    public static void getPreOrden(JSONObject obj, SSSessionAbstract session) {
        try {
            String token = Kolping.getToken();

            JSONObject resp = Kolping.get_(token, "ObtenerPreOrden/" + obj.optString("key"));
            obj.put("data", resp.getJSONObject("result"));
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            e.printStackTrace();
        }
    }
}
