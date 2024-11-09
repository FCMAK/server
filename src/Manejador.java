import Component.Dbf;
import Component.Turno;
import Component.novedades;
import Component.usuario;
import Component.Medico;
import Component.Notificacion;
import Component.OrdenCompra;
import Component.Paciente;
import Component.PacienteUsuario;
import Component.ServicioKolping;
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
import Component.Ficha;
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
            case usuario.COMPONENT : usuario.onMessage(obj, session); break;
            case ServicioKolping.COMPONENT: ServicioKolping.onMessage(obj, session); break;
            case Ficha.COMPONENT: Ficha.onMessage(obj, session); break;
            case Paciente.COMPONENT: Paciente.onMessage(obj, session); break;
            case PacienteUsuario.COMPONENT: PacienteUsuario.onMessage(obj, session); break;
            case novedades.COMPONENT: novedades.onMessage(obj, session); break;
            case OrdenCompra.COMPONENT: OrdenCompra.onMessage(obj, session); break;
        }
    }
}
