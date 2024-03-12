package servidor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {

    // defino el puerto en el que el servidor escuchara conexiones entrantes
    private static final int SERVER_PORT = 3555;

    public static void main(String[] args) {
        // intento crear un servidor en el puerto que seleccioné.
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            System.out.println("Servidor esperando conexiones...");

            // Bucle infinito para aceptar conexiones de clientes de manera continua
            while (true) {
                try (
                        // Acepto la conexión del cliente
                        Socket clientSocket = serverSocket.accept();
                        // Creo un flujo de entrada para leer los datos del cliente
                        DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
                        // Creo un flujo de salida para enviar datos al cliente
                        DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream())
                ) {

                    // Leer la operación enviada por el cliente como una cadena de texto
                    String operation = dis.readUTF();
                    System.out.println("Operacion que recibo del cliente: " + operation);

                    // Procesamos la operación y obtenemos el resultado
                    String result = procesarOperacion(operation);

                    // Enviamos el resultado de vuelta al cliente
                    dos.writeUTF(result);
                    System.out.println("Resultado de la operacion enviado al cliente: " + result);
                } catch (IOException e) {
                    // Manejo de cualquier error de E/S durante la comunicación con el cliente
                    System.err.println("Error de E/S con el cliente: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            // Manejo de error en caso de no poder iniciar el servidor
            System.err.println("Ha habido un error al iniciar el servidor: " + e.getMessage());
        }
    }

    private static String procesarOperacion(String operation) {
        // Divido la operación recibida en operandos y operador usando un espacio como delimitador
        String[] tokens = operation.split("\\s+");
        // verifico que la operacion tenga bien la estructura (operando operador operando)
        if (tokens.length != 3) {
            return "Operación no valida";
        }

        // Convertimos los operandos a números y extraemos el operador
        double operand1 = Double.parseDouble(tokens[0]);
        double operand2 = Double.parseDouble(tokens[2]);
        String operator = tokens[1];

        // Evaluamos el operador y realizamos la operación correspondiente
        switch (operator) {
            case "+":
                return String.valueOf(operand1 + operand2);
            case "-":
                return String.valueOf(operand1 - operand2);
            case "*":
                return String.valueOf(operand1 * operand2);
            case "/":
                // Para evitar la división por cero
                if (operand2 != 0) {
                    return String.valueOf(operand1 / operand2);
                } else {
                    return "Division por 0";
                }
            default:
                // En caso de que un operador no se reconozca, devuelvo un error
                return "Operador no valido";
        }
    }
}
