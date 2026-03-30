package local.alejandrogb.metricsservers.api.services.servidor;

import local.alejandrogb.metricsservers.exceptions.ProbeException;
import local.alejandrogb.metricsservers.models.servidor.ServidorInfo;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;

/**
 * Servicio encargado de obtener información de un servidor remoto mediante
 * conexión SSH.
 *
 * <p>
 * Este componente se utiliza durante el proceso de registro de nuevos
 * servidores en el sistema para enriquecer los datos proporcionados por el
 * cliente con información real obtenida directamente desde el sistema operativo
 * del servidor.
 * </p>
 *
 * <p>
 * La comunicación se realiza utilizando la librería {@code SSHJ}, estableciendo
 * una conexión SSH al host indicado y ejecutando una serie de comandos del
 * sistema.
 * </p>
 *
 * <p>
 * La información recopilada incluye:
 * </p>
 *
 * <ul>
 * <li>Nombre del host del sistema.</li>
 * <li>Nombre del sistema operativo.</li>
 * <li>Arquitectura de CPU.</li>
 * <li>Versión del kernel.</li>
 * </ul>
 *
 * <p>
 * Los datos obtenidos se encapsulan en un objeto {@link ServidorInfo} que
 * posteriormente será almacenado junto con el servidor en la base de datos.
 * </p>
 */
public class ServidorProbeService {

	/**
	 * Realiza una conexión SSH a un servidor remoto y obtiene información básica
	 * del sistema.
	 *
	 * <p>
	 * Durante la conexión se ejecutan varios comandos del sistema para recuperar
	 * información relevante del servidor.
	 * </p>
	 *
	 * <p>
	 * Se aplican límites de tiempo para evitar bloqueos prolongados en caso de
	 * problemas de red o servidores inaccesibles.
	 * </p>
	 *
	 * @param host dirección DNS o IP del servidor
	 * @return objeto {@link ServidorInfo} con la información recopilada
	 * @throws ProbeException si ocurre un error durante la conexión SSH o la
	 *                        ejecución de comandos remotos
	 */
	public ServidorInfo askServer(String host) {

		try (SSHClient ssh = new SSHClient()) {

			ssh.addHostKeyVerifier(new PromiscuousVerifier());

			ssh.setConnectTimeout(5000);
			ssh.setTimeout(5000);

			ssh.connect(host);
			ssh.authPassword(System.getenv().getOrDefault("SSH_PROBE_USER", "root"),
					System.getenv("SSH_PROBE_PASSWORD"));

			String hostname = exec(ssh, "hostname");
			String os = exec(ssh, "cat /etc/os-release | grep PRETTY_NAME | cut -d= -f2 | tr -d '\"'");
			String arch = exec(ssh, "uname -m");
			String kernel = exec(ssh, "uname -r");

			return new ServidorInfo(hostname, os, arch, kernel);

		} catch (Exception e) {
			throw new ProbeException("SSH falló", e);
		}
	}

	/**
	 * Ejecuta un comando en el servidor remoto mediante una sesión SSH.
	 *
	 * <p>
	 * El método abre una sesión temporal, ejecuta el comando indicado y devuelve la
	 * salida estándar producida por el sistema.
	 * </p>
	 *
	 * <p>
	 * Si el comando devuelve un código de salida distinto de cero, se considera que
	 * la ejecución ha fallado y se lanza una excepción.
	 * </p>
	 *
	 * @param ssh cliente SSH ya conectado al servidor
	 * @param cmd comando del sistema a ejecutar
	 * @return salida estándar del comando ejecutado
	 * @throws Exception si ocurre un error durante la ejecución
	 */
	private String exec(SSHClient ssh, String cmd) throws Exception {

		try (Session session = ssh.startSession()) {

			Session.Command command = session.exec(cmd);

			command.join();

			Integer exit = command.getExitStatus();

			if (exit != null && exit != 0)
				throw new RuntimeException("Comando falló: " + cmd);

			return new String(command.getInputStream().readAllBytes()).trim();
		}
	}
}