package local.alejandrogb.metricsservers.api.services.servidor;

import local.alejandrogb.metricsservers.exceptions.ProbeException;
import local.alejandrogb.metricsservers.models.servidor.ServidorInfo;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;

public class ServidorProbeService {

	public ServidorInfo askServer(String host) {

		try (SSHClient ssh = new SSHClient()) {

			ssh.addHostKeyVerifier(new PromiscuousVerifier());

			ssh.setConnectTimeout(5000);
			ssh.setTimeout(5000);

			ssh.connect(host);
			ssh.authPassword("root", "abc123.");

			String hostname = exec(ssh, "hostname");
			String os = exec(ssh, "cat /etc/os-release | grep PRETTY_NAME | cut -d= -f2 | tr -d '\"'");
			String arch = exec(ssh, "uname -m");
			String kernel = exec(ssh, "uname -r");

			return new ServidorInfo(hostname, os, arch, kernel);

		} catch (Exception e) {
			throw new ProbeException("SSH falló", e);
		}
	}

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
