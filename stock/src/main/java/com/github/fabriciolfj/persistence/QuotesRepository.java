package com.github.fabriciolfj.persistence;

import com.github.fabriciolfj.entity.QuoteEntity;
import com.github.fabriciolfj.entity.SymbolEntity;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuotesRepository extends CrudRepository<QuoteEntity, Long> {

    @NonNull
    @Override
    List<QuoteEntity> findAll();

    Optional<QuoteEntity> findBySymbol(SymbolEntity symbol);
}
