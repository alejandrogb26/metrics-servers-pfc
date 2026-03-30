package local.alejandrogb.metricsservers.api.services.permiso;

import java.util.List;

import jakarta.ws.rs.NotFoundException;
import local.alejandrogb.metricsservers.dao.DaoApi;
import local.alejandrogb.metricsservers.models.Permiso;

public class PermisoService {

	private final DaoApi dao = DaoApi.getInstance();

	public List<Permiso> getAll() {
		return dao.findAllPermisos();
	}

	public Permiso getById(int id) {
		Permiso p = dao.findPermisoById(id);
		if (p == null)
			throw new NotFoundException("Permiso no encontrado");
		return p;
	}
}
