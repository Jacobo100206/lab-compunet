package co.icesi.buscaminas.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.google.gson.Gson;

import co.icesi.buscaminas.client.dtos.Request;
import co.icesi.buscaminas.client.dtos.Response;
import co.icesi.buscaminas.client.model.Cell;

public class MainClient {

    private static BuscaminasTCPClient client = new BuscaminasTCPClient();

    private static Gson gson = new Gson();

    private static String host;

    private static int port;

    public static void main(String[] args) {
        host = args.length > 0 ? args[0] : "localhost";
        port = args.length > 1 ? Integer.parseInt(args[1]) : 12345;

        Scanner scanner = new Scanner(System.in);
        int option = 0;
        while (option != 6) {
        
            System.out.println("      BUSCAMINAS DISTRIBUIDO - CLIENTE TCP");
            
            System.out.println("[1] Iniciar nueva partida (Filas, Columnas, Minas)");
            System.out.println("[2] Destapar celda (Fila, Columna)");
            System.out.println("[3] Marcar / Desmarcar bandera (Fila, Columna)");
            System.out.println("[4] Consultar estado actual del tablero");
            System.out.println("[5] Rendirse y revelar tablero completo");
            System.out.println("[6] Salir");
            System.out.print("Seleccione una opcion: ");
            option = scanner.nextInt();
            try {
                Map<String, String> data = new HashMap<>();
                Response response = null;
                switch (option) {
                    case 1:
                        System.out.print("Filas: ");
                        data.put("n", scanner.next());
                        System.out.print("Columnas: ");
                        data.put("m", scanner.next());
                        System.out.print("Minas: ");
                        data.put("minas", scanner.next());
                        response = send("INIT_GAME", data);
                        break;
                    case 2:
                        System.out.print("Fila: ");
                        data.put("i", scanner.next());
                        System.out.print("Columna: ");
                        data.put("j", scanner.next());
                        response = send("SELECT_CELL", data);
                        break;
                    case 3:
                        System.out.print("Fila: ");
                        data.put("i", scanner.next());
                        System.out.print("Columna: ");
                        data.put("j", scanner.next());
                        response = send("MARK_CELL", data);
                        break;
                    case 4:
                        response = send("GET_BOARD", data);
                        break;
                    case 5:
                        response = send("SOW_ALL", data);
                        break;
                    case 6:
                        break;
                    default:
                        System.out.println("Opcion no valida");
                        break;
                }
                if (response != null) {
                    showResponse(response);
                }
            } catch (Exception e) {
                System.out.println("Error de comunicacion con el servidor: " + e.getMessage());
            }
        }
        scanner.close();
    }

    private static Response send(String action, Map<String, String> data) throws Exception {
        Request request = new Request();
        request.action = action;
        request.data = data;
        return client.sendRequest(host, port, request);
    }

    private static void showResponse(Response response) throws Exception {
        if (response.data.get("message") != null) {
            System.out.println(response.status + ": " + response.data.get("message"));
        }
        Cell[][] board = gson.fromJson(gson.toJsonTree(response.data.get("board")), Cell[][].class);
        BoardRenderer.printBoard(board);

        if (Boolean.TRUE.equals(response.data.get("gameEnd"))) {
            if (Boolean.TRUE.equals(response.data.get("win"))) {
                System.out.println("\u001B[32m*** FELICITACIONES, GANASTE LA PARTIDA ***\u001B[0m");
            } else {
                System.out.println("\u001B[31m*** BOOM! Pisaste una mina. DERROTA ***\u001B[0m");
                Response all = send("SOW_ALL", new HashMap<>());
                board = gson.fromJson(gson.toJsonTree(all.data.get("board")), Cell[][].class);
                BoardRenderer.printBoard(board);
            }
        }
    }
}
