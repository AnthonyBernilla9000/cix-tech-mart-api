package org.istrfa.repositories;

import org.istrfa.models.TypeMarcaModeloEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface TypeMarcaModeloRepository extends JpaRepository<TypeMarcaModeloEntity, UUID> {

    @Query("SELECT tmm FROM TypeMarcaModeloEntity tmm WHERE tmm.typeregist = 3 AND tmm.active = 1 ")
    List<TypeMarcaModeloEntity> getAllTypes();

    @Query("SELECT tmm FROM TypeMarcaModeloEntity tmm WHERE tmm.parentid = ?1 " +
            "AND tmm.typeregist = 2 AND tmm.active = 1 ")
    List<TypeMarcaModeloEntity> getMarcaByType(UUID idparent);

    @Query("SELECT tmm FROM TypeMarcaModeloEntity tmm WHERE tmm.parentid = ?1 " +
            "AND tmm.typeregist = 1 AND tmm.active = 1 ")
    List<TypeMarcaModeloEntity> getModeloByMarca(UUID idparent);

}
