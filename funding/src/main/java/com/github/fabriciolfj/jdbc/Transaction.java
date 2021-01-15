package com.github.fabriciolfj.jdbc;

import java.math.BigDecimal;

import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.model.naming.NamingStrategies;

//MAppedEntity e o mesmo da @Entity mas tem algums recursos a mais
@MappedEntity(value = "transactions", namingStrategy = NamingStrategies.UnderScoreSeparatedLowerCase.class) //forma de converter nomes das propriedades em colunas
//nas tabelas, exemplo FooBar transforma em foo_bar
public class Transaction {

  @Id
  private long transactionId;
  private final String user;
  private final String symbol;
  private final BigDecimal modification;

  public Transaction(final String user, final String symbol, final BigDecimal modification) {
    this.user = user;
    this.symbol = symbol;
    this.modification = modification;
  }

  public Transaction(final long transactionId, final String user, final String symbol,
      final BigDecimal modification) {
    this.transactionId = transactionId;
    this.user = user;
    this.symbol = symbol;
    this.modification = modification;
  }

  public String getSymbol() {
    return symbol;
  }

  public BigDecimal getModification() {
    return modification;
  }

  public long getTransactionId() {
    return transactionId;
  }

  public String getUser() {
    return user;
  }

  @Override
  public String toString() {
    return "Transaction{" +
        "transactionId=" + transactionId +
        ", user='" + user + '\'' +
        ", symbol='" + symbol + '\'' +
        ", modification=" + modification +
        '}';
  }
}
