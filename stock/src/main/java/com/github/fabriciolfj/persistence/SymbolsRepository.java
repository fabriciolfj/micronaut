package com.github.fabriciolfj.persistence;

import com.github.fabriciolfj.entity.SymbolEntity;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;

@Repository
public interface SymbolsRepository extends CrudRepository<SymbolEntity, String> {

    @NonNull
    @Override
    List<SymbolEntity> findAll();
}
