import org.json.JSONArray;
import org.json.JSONObject;

import Component.Especialidad;
import Component.Ficha;
import Component.Medico;
import Component.Paciente;
import Component.Sucursal;
import Component.Turno;

public class TestApi {
    public static void main(String[] args) {
        try {
            JSONArray sucursales = Sucursal.getAll();
            System.out.println("*** Sucursales: "+sucursales.length());
            JSONArray especialidades = Especialidad.getAll("999");
            System.out.println("*** Especialidades: "+especialidades.length());
            JSONArray medicos = Medico.getAll("999", "999");
            System.out.println("*** Medicos: "+medicos.length());

            JSONObject paciente = new JSONObject();
            paciente.put("TipDoc","1");
            paciente.put("NroDoc","6392496");
            paciente.put("ComDoc", "");
            paciente.put("NomPer", "Ricardo");
            paciente.put("PriApe", "Paz");
            paciente.put("SegApe", "Demiquel");
            paciente.put("GenPer", "M");
            paciente.put("FecNac", "1997-03-14T00:00:00Z");
            paciente.put("TdoFac", "1");
            paciente.put("NdoFac", "0");
            paciente.put("DcoFac", "");
            paciente.put("NomFac", "0");

            JSONArray MedCon = new JSONArray();
            MedCon.put(new JSONObject().put("TipMed", "1").put("ValMed", "carlos_3312@tmail.com"));
            MedCon.put(new JSONObject().put("TipMed", "2").put("ValMed", "78006991"));
            MedCon.put(new JSONObject().put("TipMed", "3").put("ValMed", "av/ san roque 3312").put("DatMed", ""));

            paciente.put("MedCon", MedCon);

            JSONArray pacientes = Paciente.registro(paciente);
            
            System.out.println("*** Pacientes: "+pacientes.length());

            System.out.println(""+pacientes);
            System.out.println("");

            JSONObject paciente_ = pacientes.getJSONObject(0);

            //JSONArray turnos = Turno.getAll("0", "3", "2024-09-02T04:00:00.000Z");
            //System.out.println("*** Turnos: "+turnos.length());

            JSONArray ficha = Ficha.registro("0", paciente_.get("CodPer")+"", paciente_.getString("GenPer"));
            System.out.println("*** Ficha: "+ficha);
            System.out.println("");
            System.out.println("");

            

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}   
