package local.alejandrogb.metricsservers.api.services.ambito;

import java.util.List;

import jakarta.ws.rs.NotFoundException;
import local.alejandrogb.metricsservers.dao.DaoApi;
import local.alejandrogb.metricsservers.models.Ambito;

public class AmbitoService {

	private final DaoApi dao = DaoApi.getInstance();

	public List<Ambito> getAll() {
		return dao.findAllAmbito();
	}

	public Ambito getById(int id) {
		Ambito a = dao.findAmbitoById(id);
		if (a == null)
			throw new NotFoundException("Ámbito no encontrado");
		return a;
	}
}
