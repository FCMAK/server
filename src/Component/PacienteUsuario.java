package Component;

import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;
import Server.SSSAbstract.SSServerAbstract;
import Server.SSSAbstract.SSSessionAbstract;
import Servisofts.SPGConect;
import Servisofts.SUtil;

public class PacienteUsuario {
    public static final String COMPONENT = "paciente_usuario";

    public static void onMessage(JSONObject obj, SSSessionAbstract session) {
        switch (obj.getString("type")) {
            case "getAll": getAll(obj, session); break;
            case "getByCi": getByCi(obj, session); break;
            case "getByKey": getByKey(obj, session); break;
            case "registro": registro(obj, session); break;
            case "registro2": registro2(obj, session); break;
            case "editar": editar(obj, session); break;
            default: resend(obj, session);
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

    public static void getAll(JSONObject obj, SSSessionAbstract session) {
        try {
            JSONObject data = SPGConect.ejecutarConsultaObject("select get_all('"+COMPONENT+"', 'key_usuario', '"+obj.getString("key_usuario")+"') as json");
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            e.printStackTrace();
        }
    }

    public static void getByCi(JSONObject obj, SSSessionAbstract session) {
        try {
            String token = Kolping.getToken();
            
            JSONArray data = Kolping.get(token, "VerificarRegistroPorDocumento/"+obj.getString("ci"));
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            e.printStackTrace();
        }
    }

    public static JSONObject getByKey(String codper, String key_usuario) {
        try {
            String consulta = "select get_by('"+COMPONENT+"', 'codper','"+codper+"','key_usuario','"+key_usuario+"') as json";
            return SPGConect.ejecutarConsultaObject(consulta);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void getByKey(JSONObject obj, SSSessionAbstract session) {
        try {
            String consulta = "select get_by('"+COMPONENT+"', 'codper','"+obj.getString("codper")+"','key_usuario','"+obj.getString("key_usuario")+"') as json";
            JSONObject data = SPGConect.ejecutarConsultaObject(consulta);
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            e.printStackTrace();
        }
    }

    public static JSONArray getByCi(String ci) {
        try {
            String token = Kolping.getToken();
            
            return Kolping.get(token, "VerificarRegistroPorDocumento/"+ci);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JSONObject getByKeyUsuarioCi(String key_usuario, String ci) {
        try {
            String consulta = "select get_by('"+COMPONENT+"','key_usuario','"+key_usuario+"','ci','"+ci+"') as json";
            
            return SPGConect.ejecutarConsultaObject(consulta);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void registro(JSONObject obj, SSSessionAbstract session) {
        try {
            
            JSONObject data = obj.getJSONObject("data");
            JSONArray pacientes = Paciente.registro(data);
            JSONObject paciente = null;
            if(pacientes!=null){
                paciente = pacientes.getJSONObject(0);
            }
            
            String codPer = "";
            if(paciente.has("CodPer")){
                codPer = paciente.get("CodPer")+"";
            }
            if(paciente.has("CodCli")){
                codPer = paciente.get("CodCli")+"";
            }
            
            JSONObject pacienteDb = getByKey(codPer, obj.getString("key_usuario"));

            if(pacienteDb!=null && !pacienteDb.isEmpty()){
                obj.put("estado", "error");
                obj.put("error", "YA existe el paciente");
                return;
            }

            JSONObject dataSend = new JSONObject();
            dataSend.put("key", UUID.randomUUID().toString());
            dataSend.put("estado", 1);
            dataSend.put("fecha_on", SUtil.now());
            dataSend.put("key_usuario", obj.getString("key_usuario"));
            dataSend.put("ci", data.get("NroDoc"));
            dataSend.put("alias", data.getString("PriApe")+" "+data.getString("SegApe")+" "+data.getString("NomPer"));
            dataSend.put("codper", codPer);
            SPGConect.insertObject(COMPONENT, dataSend);

            obj.put("data", paciente);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getMessage());

            e.printStackTrace();
        }
    }

    public static void registro2(JSONObject obj, SSSessionAbstract session) {
        try {
            
            JSONObject data = obj.getJSONObject("data");

            if(!data.has("NroDoc") || data.isNull("NroDoc")){
                obj.put("estado", "error");
                obj.put("error", "Se esperava NroDoc.");
                return;
            }

            JSONObject pacienteUsuario = getByKeyUsuarioCi(obj.getString("key_usuario"), data.getString("NroDoc"));

            if(pacienteUsuario!=null && !pacienteUsuario.isEmpty()){
                obj.put("estado", "error");
                obj.put("error", "Ya existe el paciente en su lista.");
                return;
            }

            JSONArray pacientes = Paciente.getByCi(data.getString("NroDoc"));

            JSONObject paciente = null;
            for (int i = 0; i < pacientes.length(); i++) {
                if(pacientes.getJSONObject(i).getInt("CodPer")==data.getInt("CodPer")){
                    paciente = pacientes.getJSONObject(i);
                }
            }
            
            
            

            JSONObject dataSend = new JSONObject();
            dataSend.put("key", UUID.randomUUID().toString());
            dataSend.put("estado", 1);
            dataSend.put("fecha_on", SUtil.now());
            dataSend.put("key_usuario", obj.getString("key_usuario"));
            dataSend.put("ci", paciente.getString("NroDoc"));
            dataSend.put("alias", paciente.getString("NomCom"));
            dataSend.put("codper", paciente.get("CodPer"));
            SPGConect.insertObject(COMPONENT, dataSend);

            obj.put("data", paciente);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getMessage());
            e.printStackTrace();
        }
    }


    public static JSONArray registro(JSONObject paciente) {
        try {

            String token = Kolping.getToken();
            return Kolping.post(token, "RegistrarPaciente", paciente);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JSONArray getAll(String nrosuc, String codesp) {
        try {
            String token = Kolping.getToken();
            return Kolping.get(token, "Paciente/"+nrosuc+"/"+  codesp);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void resend(JSONObject obj, SSSessionAbstract session) {
        try {
            if (!obj.has("id_session_client")) {
                obj.put("id_session_client", session.getIdSession());
                Integrador.send(obj);
                obj.put("estado", "cargando");
                obj.put("data", new JSONArray());
                return;
            }
            SSServerAbstract.getSession(obj.getString("id_session_client")).send(obj.toString());
            obj.put("noSend", true);
        } catch (Exception e) {
            obj.put("estado", "error");
            e.printStackTrace();
        }
    }

}
