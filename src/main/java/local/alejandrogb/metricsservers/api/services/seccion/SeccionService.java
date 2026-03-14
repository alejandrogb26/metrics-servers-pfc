package local.alejandrogb.metricsservers.api.services.seccion;

import java.util.List;
import java.util.Map;

import jakarta.ws.rs.NotFoundException;
import local.alejandrogb.metricsservers.dao.DaoApi;
import local.alejandrogb.metricsservers.models.Seccion;

public class SeccionService {

	private final DaoApi dao = DaoApi.getInstance();

	public Seccion findById(int id) {
		Seccion seccion = dao.findSeccionById(id);
		if (seccion == null)
			throw new NotFoundException("Sección no encontrada");
		return seccion;
	}

	public List<Seccion> findAll() {
		return dao.findAllSeccion();
	}

	public int insert(Seccion seccion) {
		return dao.insertSeccion(seccion);
	}

	public void update(int id, Map<String, Object> fields) {
		if (!dao.updateSeccion(id, fields)) {
			throw new NotFoundException("Sección no encontrada para actualizar");
		}
	}

	public boolean delete(int id) {
		// Nota: Si la BD lanza error por FK (RESTRICT), DaoApi lanzará una DaoException
		// que debería ser capturada por un ExceptionMapper global.
		return dao.deleteSeccion(id);
	}
}