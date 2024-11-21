package Controllers;

import org.json.JSONObject;

import Component.OrdenCompra;
import Servisofts.SConsole;
import Servisofts.http.Status;
import Servisofts.http.Exception.*;
import Servisofts.http.annotation.*;

@RestController
@RequestMapping("/kolping")
public class KolpingController {

    @GetMapping("/payment")
    public String callback(@RequestParam("qrid") String qrid) throws HttpException {
        try {
            SConsole.log("Entro al callback payment: "+qrid);
            JSONObject ordenCompra = OrdenCompra.getByQrId(qrid);
            ordenCompra = ordenCompra.getJSONObject(JSONObject.getNames(ordenCompra)[0]);

            JSONObject resp = OrdenCompra.verificarPago(ordenCompra.getString("key"));


            return resp.toString();
        } catch (Exception e) {
            SConsole.error("Error en el callback de kolping", e.getMessage());
            throw new HttpException(Status.BAD_REQUEST, e.getLocalizedMessage());
        }

        // DOC-> Tengo que retornar el status 200;
        // return "exito";
    }

}