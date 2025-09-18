package Controllers;

import org.json.JSONArray;
import org.json.JSONObject;

import Component.Kolping;
import Component.OrdenCompra;
import Component.Turno;
import Servisofts.SConsole;
import Servisofts.http.Status;
import Servisofts.http.Exception.*;
import Servisofts.http.annotation.*;

@RestController
@RequestMapping("/kolping")
public class KolpingController {

    @GetMapping("/payment")
    public String callback(@RequestParam("qrid") String qrid, @RequestParam("voucher") String voucher, @RequestParam("action") String action) throws HttpException {
        try {
            SConsole.log("Entro al callback payment: "+qrid);

            SConsole.warning("qrid"+ qrid+" voucher"+voucher+" action"+action);

            JSONObject ordenCompra = OrdenCompra.getByQrId(qrid);
            ordenCompra = ordenCompra.getJSONObject(JSONObject.getNames(ordenCompra)[0]);

            // ANU = Anulacion
            if(action.equals("ANU")){
                ordenCompra.put("estado",0);
                OrdenCompra.editar(ordenCompra);

                String token = Kolping.getToken();

                JSONArray send = new JSONArray();
                JSONObject obj = new JSONObject();
                obj.put("CodMed",ordenCompra.getJSONObject("data").get("codmed"));
                obj.put("FecTur",ordenCompra.getJSONObject("data").get("fecha"));
                obj.put("CodTur",ordenCompra.getJSONObject("data").get("codtur"));
                obj.put("ComTur",ordenCompra.getJSONObject("data").get("comtur"));
                send.put(obj);

                JSONObject data = Kolping.put(token, "LiberarTurnos"+"/"+ordenCompra.getJSONObject("data").getString("nrosuc"), send);

                System.out.println(data);


                return new JSONObject().put("estado", "exito").toString();
            }

            JSONObject resp = OrdenCompra.verificarPagoV2(ordenCompra.getString("key"));

            return resp.toString();
        } catch (Exception e) {
            SConsole.error("Error en el callback de kolping", e.getMessage());
            throw new HttpException(Status.BAD_REQUEST, e.getLocalizedMessage());
        }
    }

}