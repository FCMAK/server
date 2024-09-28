package Component;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

import Servisofts.SConfig;
import Servisofts.SUtil;

public class Kolping {
    public static final String COMPONENT = "kolping";

    public static String getToken() {
        try {

            String url_ = SConfig.getJSON("kolping").getString("url");
            String usr = SConfig.getJSON("kolping").getString("usr");
            String pass = SConfig.getJSON("kolping").getString("pass");

            JSONObject message = new JSONObject();
            message.put("Username", usr);
            message.put("Password", pass);

            URL url = new URL(url_+"authenticate");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");

            con.setRequestProperty("Content-Length", Integer.toString(message.toString().getBytes().length));
            con.setRequestProperty("Content-Type", "application/json");
            
            con.setUseCaches(false);
            con.setDoOutput(true);

            DataOutputStream out = new DataOutputStream(con.getOutputStream());
            out.write(message.toString().getBytes());
            out.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();
            JSONObject resp = new JSONObject(content.toString());
            return resp.getString("token");
            // System.out.println(content.toString());
            // return true;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JSONArray get(String token, String service) {
        try {

            String url_ = SConfig.getJSON("kolping").getString("url");

            URL url = new URL(url_+service);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            con.setRequestProperty("Authorization", "Bearer "+token);
            
            con.setUseCaches(false);
            con.setDoOutput(true);

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();
            //System.out.println(content.toString());
            JSONObject resp = new JSONObject(content.toString());
            return resp.getJSONArray("Result");
            // System.out.println(content.toString());
            // return true;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JSONArray post(String token, String service, JSONObject data) {
        try {

            String url_ = SConfig.getJSON("kolping").getString("url");

            URL url = new URL(url_+service);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");

            con.setRequestProperty("Authorization", "Bearer "+token);
            con.setRequestProperty("Content-Type", "application/json");
            
            con.setUseCaches(false);
            con.setDoOutput(true);

             String jsonInputString = data.toString();

            // Escribir el cuerpo de la petición
            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Obtener la respuesta
            int responseCode = con.getResponseCode();
            //System.out.println("Response Code: " + responseCode);

            JSONObject resp;

            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                resp = new JSONObject(response.toString());
            }

            Object result = resp.get("Result");

            if (result instanceof JSONArray) {
                
                return resp.getJSONArray("Result");
                
            } else{
                return new JSONArray().put(resp.getJSONObject("Result"));
            } 
            
            
            // System.out.println(content.toString());
            // return true;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static void main(String[] args) {
        String token = Kolping.getToken();
        System.out.println("Token: "+token);

        JSONArray sucursales = Kolping.get(token, "Sucursales");
        System.out.println("");
        System.out.println("***********sucursales: ***********");
        System.out.println(sucursales);

        String nrosuc = sucursales.getJSONObject(0).getString("NroSuc");
        JSONArray especialidades = Kolping.get(token, "Especialidades/"+nrosuc);
        System.out.println("");
        System.out.println("***********especialidades: ***********");
        System.out.println(especialidades);

        String codesp = especialidades.getJSONObject(0).getString("CodEsp");
        JSONArray medicos = Kolping.get(token, "Medicos/"+nrosuc+"/"+codesp);
        
        System.out.println("***********medicos: ***********");
        System.out.println(medicos);

        JSONObject medico = medicos.getJSONObject(0);
        

        System.out.println("***********turnos: ***********");
        JSONArray turnos = Kolping.get(token, "Turnos/"+nrosuc+"/"+medico.get("CodMed")+"/"+SUtil.now());
        System.out.println(turnos);

        JSONArray pacientes = Kolping.get(token, "VerificarRegistroPorDocumento/6340999");

        if(pacientes.length()==0){
            String aux = "{\n" + //
            "  \"TipDoc\": \"1\",\n" + //
            "  \"NroDoc\": \"97523424\",\n" + //
            "  \"ComDoc\": \"\",\n" + //
            "  \"NomPer\": \"NombrePersona\",\n" + //
            "  \"PriApe\": \"PrimerApellido\",\n" + //
            "  \"SegApe\": \"\",\n" + //
            "  \"GenPer\": \"F\",\n" + //
            "  \"FecNac\": \"2000-01-01T00:00:00Z\",\n" + //
            "  \"TdoFac\": \"1\",\n" + //
            "  \"NdoFac\": \"\",\n" + //
            "  \"DcoFac\": \"\",\n" + //
            "  \"NomFac\": \"\",\n" + //
            "  \"MedCon\": []\n" + //
            "}";

            pacientes = Kolping.post(token, "RegistrarPaciente", new JSONObject(aux));
        }

        
        System.out.println("***********paciente: ***********");
        JSONObject paciente = pacientes.getJSONObject(0);
        System.out.println(paciente);


        


    }
}
