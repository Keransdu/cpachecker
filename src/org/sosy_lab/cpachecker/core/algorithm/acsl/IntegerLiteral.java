// This file is part of CPAchecker,
// a tool for configurable software verification:
// https://cpachecker.sosy-lab.org
//
// SPDX-FileCopyrightText: 2020 Dirk Beyer <https://www.sosy-lab.org>
//
// SPDX-License-Identifier: Apache-2.0

package org.sosy_lab.cpachecker.core.algorithm.acsl;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import org.sosy_lab.cpachecker.cfa.ast.c.CExpression;
import org.sosy_lab.cpachecker.exceptions.UnrecognizedCodeException;

public class IntegerLiteral implements ACSLTerm {

  private final Integer literal;

  public IntegerLiteral(Integer i) {
    literal = i;
  }

  @Override
  public String toString() {
    return String.valueOf(literal);
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof IntegerLiteral) {
      IntegerLiteral other = (IntegerLiteral) o;
      return literal.equals(other.literal);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return 31 * literal.hashCode() * literal.hashCode() + 31;
  }

  public Integer getLiteral() {
    return literal;
  }

  @Override
  public CExpression accept(ACSLTermToCExpressionVisitor visitor) throws UnrecognizedCodeException {
    return visitor.visit(this);
  }

  @Override
  public boolean isAllowedIn(Class<?> clauseType) {
    return true;
  }

  @Override
  public Set<ACSLBuiltin> getUsedBuiltins() {
    return ImmutableSet.of();
  }
}
