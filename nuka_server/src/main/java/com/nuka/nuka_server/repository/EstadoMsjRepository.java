package com.nuka.nuka_server.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.nuka.nuka_server.models.EstadoMsj;

import com.nuka.nuka_server.models.TipoMsjEstado;

@Repository
public interface EstadoMsjRepository extends MongoRepository<EstadoMsj, String> {

    @Query("{ 'visible' : true, 'usuariosId': ?0, 'tipoMsjEstado': { $in: ?1 }}")
    List<EstadoMsj> findByUsuarioIdAndTipoMsjEstadoInList(String usuarioId, List<TipoMsjEstado> tipoMsjEstado);

    @Query("{ 'visible' : true, 'usuariosId': ?0, 'tipoMsjEstado': ?1 }")
    List<EstadoMsj> findByUsuarioIdAndTipoMsjEstado(String usuarioId, TipoMsjEstado tipoMsjEstado);

    @Query("{ 'visible': true, 'usuariosId': ?0, 'tipoMsjEstado': ?1, 'fecha': { $gte: ?2, $lt: ?3 } }")
    List<EstadoMsj> findByUsuarioIdAndTipoMsjEstadoAndFechaBetween(String usuarioId, TipoMsjEstado tipoMsjEstado,
	    long startDate, long endDate);

    @Query("{ 'visible' : true, 'tipoMsjEstado': ?0, 'fecha': { $gte: ?1, $lt: ?2 } }")
    List<EstadoMsj> findByTipoMsjEstadoAndFechaBetween(TipoMsjEstado tipoMsjEstado, long startDate, long endDate);
}
