package cliente;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import javax.swing.JOptionPane;

public class Cliente {

    // Defino la dirección del servidor y el puerto para la conexión
    private static final String SERVER_ADDRESS = "192.168.43.141";
    private static final int SERVER_PORT = 3555;

    public static void main(String[] args) {
        // Intento establecer una conexión con el servidor
        try (Socket clienteSocket = new Socket()) {
            System.out.println("Estableciendo conexion");

            // Creo una dirección de socket para conectarme al servidor
            InetSocketAddress addr = new InetSocketAddress(SERVER_ADDRESS, SERVER_PORT);
            clienteSocket.connect(addr);

            // Inicializo los flujos de entrada y salida para comunicarme con el servidor
            DataInputStream dis = new DataInputStream(clienteSocket.getInputStream());
            DataOutputStream dos = new DataOutputStream(clienteSocket.getOutputStream());

            // Utilizo un bucle para mantener la comunicación con el servidor
            boolean continuation = true;
            while (continuation) {
                // Solicito al usuario que introduzca una operación
                String operation = solicitarOperacion();
                // Envío la operación al servidor
                enviarOperacion(dos, operation);

                // Recibo y proceso la respuesta del servidor, decidiendo si continuo o no
                continuation = recibirYProcesarResultado(dis);

                System.out.println(continuation);
            }
        } catch (IOException e) {
            // Muestro un mensaje de error si ocurre alguna excepción de E/S
            System.err.println("Se produjo un error de E/S: " + e.getMessage());
        }
    }

    // Método para solicitar la operación al usuario mediante una ventana emergente
    private static String solicitarOperacion() {
        return JOptionPane.showInputDialog("Introduzca la operacion");
    }

    // Método para enviar la operación al servidor
    private static void enviarOperacion(DataOutputStream dos, String operation) throws IOException {
        byte[] sendMessageOperation = operation.getBytes();
        dos.writeInt(sendMessageOperation.length);
        dos.write(sendMessageOperation);
    }

    // Método para recibir y procesar la respuesta del servidor
    private static boolean recibirYProcesarResultado(DataInputStream dis) throws IOException {
        int messageLength = dis.readInt();
        byte[] mensajeBytes = new byte[messageLength];
        dis.readFully(mensajeBytes);
        String receivedString = new String(mensajeBytes);
        String[] receivedArray = receivedString.split("_");

        // Verifico si la operación es válida
        if (receivedArray[0].equals("null")) {
            // Muestro un mensaje de error si la operación no es válida
            JOptionPane.showMessageDialog(null, "Tu operacion no es válida, introduce una apta.");
            return true; // Continúo permitiendo al usuario introducir otra operación
        } else {
            // Proceso la respuesta y decido si continuar o no
            return enviarContinuacion(receivedArray);
        }
    }

    // Método para procesar la respuesta del servidor y decidir si continúo o no
    private static boolean enviarContinuacion(String[] array) {
        // Muestro una ventana emergente con el resultado y opciones para continuar o no
        String[] options = new String[]{array[1], array[2]};
        int choice = JOptionPane.showOptionDialog(
                null,
                "Tu operación es de\n" + array[0],
                "Ventana cliente", 0, 0,
                null,
                options,
                options[1]
        );

        // Devuelvo true o false dependiendo de la elección del usuario
        return choice == 0;
    }
}
