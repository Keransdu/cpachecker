/*
 *  CPAchecker is a tool for configurable software verification.
 *  This file is part of CPAchecker.
 *
 *  Copyright (C) 2007-2014  Dirk Beyer
 *  All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 *  CPAchecker web page:
 *    http://cpachecker.sosy-lab.org
 */
package org.sosy_lab.cpachecker.cpa.value.type;

import java.io.Serializable;
import java.util.Optional;
import org.sosy_lab.cpachecker.cfa.types.c.CType;
import org.sosy_lab.cpachecker.cpa.interval.NumberInterface;

/**
 * This class represents a boolean value.
 * It may store the values <code>false</code> and <code>true</code>.
 */
public class BooleanValue implements NumberInterface, Serializable {

  private static final long serialVersionUID = -35132790150256304L;

  // static objects for singleton pattern
  private static final BooleanValue TRUE_VALUE = new BooleanValue(true);
  private static final BooleanValue FALSE_VALUE = new BooleanValue(false);

  private final boolean value;

  private BooleanValue(boolean value) {
    this.value = value;
  }

  /**
   * Returns an instance of a <code>BooleanValue</code> object
   * with the specified value.
   *
   * @param value the value the returned object should hold
   * @return an instance of <code>BooleanValue</code> with the specified value
   */
  public static BooleanValue valueOf(boolean value) {
    if (value) {
      return TRUE_VALUE;
    } else {
      return FALSE_VALUE;
    }
  }

  /**
   * Returns an instance of a <code>BooleanValue</code> object representing the
   * boolean meaning of the given value, if one exists.
   * If none exists, an <code>Optional</code> with no contained reference is returned.
   *
   * @param pValue the {@link Value} whose boolean meaning should be returned
   * @return an <code>Optional</code> instance containing a reference to the
   * <code>BooleanValue</code> object representing the boolean meaning of the given value,
   * if one exists. An empty <code>Optional</code> instance, otherwise.
   */
  public static Optional<BooleanValue> valueOf(NumberInterface pValue) {
    if (pValue.isUnknown()) {
      return Optional.empty();

    } else if (pValue.isNumericValue()) {
      return valueOf((NumericValue) pValue);

    } else if (pValue instanceof BooleanValue) {
      return Optional.of((BooleanValue) pValue);

    } else {
      return Optional.empty();
    }
  }

  private static Optional<BooleanValue> valueOf(NumericValue pValue) {
    if (pValue.equals(new NumericValue(0L))) {
      return Optional.of(valueOf(false));
    } else if (pValue.equals(new NumericValue(1L))) {
      return Optional.of(valueOf(true));
    } else {
      return Optional.empty();
    }
  }

  /**
   * Returns whether this object represents the boolean value
   * <code>true</code>.
   *
   * @return <code>true</code> if this object represents <code>true</code>,
   *         false otherwise
   */
  public boolean isTrue() {
    return value;
  }

  /**
   * Returns the negation of this <code>BooleanValue</code>.
   *
   * @return a <code>BooleanValue</code> object representing <code>true</code>
   *         if this object represents <code>false</code>.
   *         An object representing <code>false</code> otherwise.
   */
  @Override
  public BooleanValue negate() {
    return value ? FALSE_VALUE : TRUE_VALUE;
  }

  /**
   * Always returns <code>false</code> because <code>BooleanValue</code>
   * always stores a boolean and never a number.
   *
   * @return always <code>false</code>
   */
  @Override
  public boolean isNumericValue() {
    return false;
  }

  /**
   * Always returns <code>false</code>. <code>BooleanValue</code>
   * always stores either <code>true</code> or <code>false</code> and
   * never an unknown value.
   *
   * @return always <code>false</code>
   */
  @Override
  public boolean isUnknown() {
    return false;
  }

  /**
   * Always returns <code>true</code>. <code>BooleanValue</code>
   * always stores a specific value.
   *
   * @return always <code>true</code>
   */
  @Override
  public boolean isExplicitlyKnown() {
    return true;
  }

  /**
   * Returns a {@link NumericValue} object holding the numeric representation of this object's value.
   *
   * @return <p>Returns a <code>NumericValue</code> object with value
   *         <code>1</code>, if this object's value is <code>true</code>.
   *         Returns an object with value <code>0</code> otherwise.
   */
  @Override
  public NumericValue asNumericValue() {
    if (value) {
      return new NumericValue(1L);
    } else {
      return new NumericValue(0L);
    }
  }

  /**
   * Always throws an <code>AssertionError</code>.
   *
   * <p>There is no use for this method in the case of boolean values.</p>
   */
  @Override
  public Long asLong(CType pType) {
    throw new AssertionError("This method is not implemented");
  }

  /**
   * Returns whether the given object and this object are equal.
   *
   * Two <code>BooleanValue</code> objects are equal when they represent
   * the same boolean value.
   *
   * @param other the object to compare to this object
   * @return <code>true</code> if the objects are equal, <code>false</code>
   *         otherwise
   */
  @Override
  public boolean equals(Object other) {
    if (other instanceof BooleanValue) {
      return ((BooleanValue) other).value == value;

    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return value ? 1 : 0;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

  @Override
  public NumberInterface EMPTY() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public NumberInterface UNBOUND() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public NumberInterface BOOLEAN_INTERVAL() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public NumberInterface ZERO() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public NumberInterface ONE() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean intersects(NumberInterface pOther) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public Number getLow() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Number getHigh() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean isGreaterThan(NumberInterface pOther) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean isGreaterOrEqualThan(NumberInterface pOther) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public NumberInterface plus(NumberInterface pInterval) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public NumberInterface minus(NumberInterface pOther) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public NumberInterface times(NumberInterface pOther) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public NumberInterface divide(NumberInterface pOther) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public NumberInterface shiftLeft(NumberInterface pOffset) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public NumberInterface shiftRight(NumberInterface pOffset) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public NumberInterface unsignedDivide(NumberInterface pOther) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public NumberInterface unsignedModulo(NumberInterface pOther) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public NumberInterface unsignedShiftRight(NumberInterface pOther) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public NumberInterface modulo(NumberInterface pOther) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean isUnbound() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public NumberInterface union(NumberInterface pOther) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean contains(NumberInterface pOther) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean isEmpty() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public NumberInterface intersect(NumberInterface pOther) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public NumberInterface limitUpperBoundBy(NumberInterface pOther) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public NumberInterface limitLowerBoundBy(NumberInterface pOther) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public NumberInterface asDecimal() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public NumberInterface asInteger() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Number getNumber() {
    // TODO Auto-generated method stub
    return null;
  }
}
