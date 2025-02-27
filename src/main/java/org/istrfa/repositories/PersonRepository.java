package org.istrfa.repositories;

import org.istrfa.models.PersonEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PersonRepository extends JpaRepository<PersonEntity, UUID> {

    @Query("SELECT pers FROM PersonEntity pers WHERE pers.numerodoc = ?1 ")
    PersonEntity findByNumdoc(String numerodoc);

}
