package com.github.fabriciolfj.store;

import com.github.fabriciolfj.model.Symbol;
import lombok.Getter;

import javax.inject.Singleton;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Singleton
public class InMemoryStore {

    private final List<Symbol> symbols;

    public InMemoryStore() {
        symbols = Stream.of("Caderno", "Lapis", "Caneta")
                .map(Symbol::new)
                .collect(Collectors.toList());
    }
}
