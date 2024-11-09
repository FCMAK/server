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
    public String callback(@RequestParam("qrid") String qrid, @RequestParam("boucherId") String boucherId) throws HttpException {
        try {
            SConsole.log("Entro al callback payment: "+qrid);
            JSONObject obj = OrdenCompra.getByQrId(qrid);
            obj = obj.getJSONObject(obj.getNames(obj)[0]);
            SConsole.log(obj);
            SConsole.log("voucherId: "+boucherId);
        
            //SConsole.log("QRID= "+obj.getString("qrid"));
            // throw new HttpException(Status.BAD_REQUEST, "error");
            // throw new HttpException(Status.CONFLICT, "conflicto");
            // throw new HttpException(Status.ACCEPTED, "acecpted");

            //JSONObject solicitudCompra = PaqueteVentaUsuario.getSolicitudCompraPaquete(obj.getString("qrid"));
            //PaqueteVentaUsuario.aprobarSolicitudCompra(solicitudCompra);

            return "{}";
        } catch (Exception e) {
            SConsole.error("Error en el callback de kolping", e.getMessage());
            throw new HttpException(Status.BAD_REQUEST, e.getLocalizedMessage());
        }

        // DOC-> Tengo que retornar el status 200;
        // return "exito";
    }

}