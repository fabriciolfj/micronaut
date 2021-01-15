package com.github.fabriciolfj.persistence;

import com.github.fabriciolfj.dto.QuoteDTO;
import com.github.fabriciolfj.entity.QuoteEntity;
import com.github.fabriciolfj.entity.SymbolEntity;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.Slice;
import io.micronaut.data.repository.CrudRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface QuotesRepository extends CrudRepository<QuoteEntity, Long> {

    @NonNull
    @Override
    List<QuoteEntity> findAll();

    Optional<QuoteEntity> findBySymbol(SymbolEntity symbol);

    List<QuoteDTO> listOrderByVolumeDesc();

    List<QuoteDTO> findByVolumeGreaterThanOrderByVolumeDesc(BigDecimal value);

    List<QuoteDTO> findByVolumeGreaterThan(BigDecimal value, Pageable page);

    Slice<QuoteDTO> list(Pageable pageable);
}
