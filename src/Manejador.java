import Component.Dbf;
import Component.Turno;
import Component.Medico;
import Component.Notificacion;
import Component.Sucursal;
import Component.Farmacia;
import Servisofts.SConsole;
import org.json.JSONObject;
import Component.Integrador;
import Component.Especialidad;
import Component.CategoriaFarmacia;
import Component.CotizacionFarmacia;
import Component.ServicioDomicilio;
import Component.FarmaciaCategoriaFarmacia;
import Server.SSSAbstract.SSSessionAbstract;

public class Manejador {
    public static void onMessage(JSONObject obj, SSSessionAbstract session) {
        if (session != null) {
            SConsole.log(session.getIdSession(), "\t|\t", obj.getString("component"), obj.getString("type"));
        }
        if (obj.isNull("component")) {
            return;
        }
        switch (obj.getString("component")) {
            case Sucursal.COMPONENT: Sucursal.onMessage(obj, session); break;
            case Dbf.COMPONENT: Dbf.onMessage(obj, session); break;
            case Integrador.COMPONENT: Integrador.onMessage(obj, session); break;
            case Medico.COMPONENT: Medico.onMessage(obj, session); break;
            case Especialidad.COMPONENT: Especialidad.onMessage(obj, session); break;
            case Farmacia.COMPONENT: Farmacia.onMessage(obj, session); break;
            case CategoriaFarmacia.COMPONENT: CategoriaFarmacia.onMessage(obj, session); break;
            case Turno.COMPONENT: Turno.onMessage(obj, session); break;
            case FarmaciaCategoriaFarmacia.COMPONENT: FarmaciaCategoriaFarmacia.onMessage(obj, session); break;
            case ServicioDomicilio.COMPONENT: ServicioDomicilio.onMessage(obj, session); break;
            case CotizacionFarmacia.COMPONENT: CotizacionFarmacia.onMessage(obj, session); break;
            case Notificacion.COMPONENT: Notificacion.onMessage(obj, session); break;
        }
    }
}
