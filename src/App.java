import Controllers.KolpingController;
import Servisofts.Servisofts;
import Servisofts.http.Rest;

public class App {
    public static void main(String[] args) {
        try {
            Servisofts.ManejadorCliente = ManejadorCliente::onMessage;
            Servisofts.Manejador = Manejador::onMessage;
            Rest.addController(KolpingController.class);    
            Servisofts.initialize();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}