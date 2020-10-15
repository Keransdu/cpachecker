// This file is part of CPAchecker,
// a tool for configurable software verification:
// https://cpachecker.sosy-lab.org
//
// SPDX-FileCopyrightText: 2020 Dirk Beyer <https://www.sosy-lab.org>
//
// SPDX-License-Identifier: Apache-2.0

package org.sosy_lab.cpachecker.cpa.numeric.visitor;

import com.google.common.collect.ImmutableSet;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Optional;
import org.sosy_lab.common.log.LogManager;
import org.sosy_lab.common.rationals.ExtendedRational;
import org.sosy_lab.common.rationals.Rational;
import org.sosy_lab.cpachecker.cfa.ast.c.CAddressOfLabelExpression;
import org.sosy_lab.cpachecker.cfa.ast.c.CArraySubscriptExpression;
import org.sosy_lab.cpachecker.cfa.ast.c.CBinaryExpression;
import org.sosy_lab.cpachecker.cfa.ast.c.CCastExpression;
import org.sosy_lab.cpachecker.cfa.ast.c.CCharLiteralExpression;
import org.sosy_lab.cpachecker.cfa.ast.c.CComplexCastExpression;
import org.sosy_lab.cpachecker.cfa.ast.c.CFieldReference;
import org.sosy_lab.cpachecker.cfa.ast.c.CFloatLiteralExpression;
import org.sosy_lab.cpachecker.cfa.ast.c.CFunctionCallExpression;
import org.sosy_lab.cpachecker.cfa.ast.c.CIdExpression;
import org.sosy_lab.cpachecker.cfa.ast.c.CImaginaryLiteralExpression;
import org.sosy_lab.cpachecker.cfa.ast.c.CIntegerLiteralExpression;
import org.sosy_lab.cpachecker.cfa.ast.c.CPointerExpression;
import org.sosy_lab.cpachecker.cfa.ast.c.CRightHandSideVisitor;
import org.sosy_lab.cpachecker.cfa.ast.c.CStringLiteralExpression;
import org.sosy_lab.cpachecker.cfa.ast.c.CTypeIdExpression;
import org.sosy_lab.cpachecker.cfa.ast.c.CUnaryExpression;
import org.sosy_lab.cpachecker.cfa.model.CFAEdge;
import org.sosy_lab.cpachecker.cfa.types.c.CSimpleType;
import org.sosy_lab.cpachecker.core.defaults.precision.VariableTrackingPrecision;
import org.sosy_lab.cpachecker.cpa.numeric.NumericTransferRelation.HandleNumericTypes;
import org.sosy_lab.cpachecker.cpa.numeric.NumericVariable;
import org.sosy_lab.cpachecker.cpa.numeric.visitor.PartialState.ApplyEpsilon;
import org.sosy_lab.cpachecker.cpa.numeric.visitor.PartialState.TruthAssumption;
import org.sosy_lab.cpachecker.exceptions.UnrecognizedCodeException;
import org.sosy_lab.numericdomains.Manager;
import org.sosy_lab.numericdomains.coefficients.Coefficient;
import org.sosy_lab.numericdomains.coefficients.MpqScalar;
import org.sosy_lab.numericdomains.constraint.tree.BinaryOperator;
import org.sosy_lab.numericdomains.constraint.tree.ConstantTreeNode;
import org.sosy_lab.numericdomains.constraint.tree.RoundingType;
import org.sosy_lab.numericdomains.constraint.tree.TreeNode;
import org.sosy_lab.numericdomains.constraint.tree.UnaryOperator;
import org.sosy_lab.numericdomains.constraint.tree.VariableTreeNode;
import org.sosy_lab.numericdomains.environment.Environment;

/**
 * Creates a right hand side visitor for numeric domains.
 *
 * <p>The right hand side is interpreted as a {@link Collection} of {@link PartialState}s.
 */
public class NumericRightHandSideVisitor
    implements CRightHandSideVisitor<Collection<PartialState>, UnrecognizedCodeException> {

  /**
   * The environment of the right hand side expression. This is not necessarily equal to the
   * environment of the state.
   */
  private final Environment environment;

  private final Manager manager;

  private final HandleNumericTypes handledTypes;

  private final VariableTrackingPrecision precision;

  private final CFAEdge edge;

  private final LogManager logger;

  /**
   * Creates a right hand side visitor for numeric domains.
   *
   * @param pEnvironment environment of the state for which this is called
   * @param pManager manager used in the CPA
   * @param pHandledTypes specifies which numeric types should be handled
   * @param cfaEdge current cfaEdge
   * @param pPrecision precision of the current CPA
   */
  public NumericRightHandSideVisitor(
      Environment pEnvironment,
      Manager pManager,
      HandleNumericTypes pHandledTypes,
      CFAEdge cfaEdge,
      VariableTrackingPrecision pPrecision,
      LogManager pLogManager) {
    environment = pEnvironment;
    handledTypes = pHandledTypes;
    edge = cfaEdge;
    precision = pPrecision;
    manager = pManager;
    logger = pLogManager;
  }

  @Override
  public Collection<PartialState> visit(CBinaryExpression pIastBinaryExpression)
      throws UnrecognizedCodeException {
    // Create Partial states for the left hand side and for the right hand side.
    Collection<PartialState> leftExpression = pIastBinaryExpression.getOperand1().accept(this);
    Collection<PartialState> rightExpression = pIastBinaryExpression.getOperand2().accept(this);

    final Collection<PartialState> newExpressions;

    // Apply the arithmetic operator on each pair of partial states in the left and right hand side
    if (arithmeticOperators.contains(pIastBinaryExpression.getOperator())) {
      newExpressions =
          handleArithmeticOperator(pIastBinaryExpression, leftExpression, rightExpression);
    } else if (comparisonOperators.contains(pIastBinaryExpression.getOperator())) {
      newExpressions =
          handleComparisonOperator(pIastBinaryExpression, leftExpression, rightExpression);
    } else {
      newExpressions =
          ImmutableSet.of(
              new PartialState(
                  new ConstantTreeNode(PartialState.UNCONSTRAINED_INTERVAL), ImmutableSet.of()));
    }

    return newExpressions;
  }

  private Collection<PartialState> handleComparisonOperator(
      CBinaryExpression pIastBinaryExpression,
      Collection<PartialState> pLeftExpression,
      Collection<PartialState> pRightExpression) {

    if (checkIsFloatComparison(pIastBinaryExpression)) {
      return PartialState.applyComparisonOperator(
          pIastBinaryExpression.getOperator(),
          pLeftExpression,
          pRightExpression,
          TruthAssumption.ASSUME_EITHER,
          ApplyEpsilon.APPLY_EPSILON,
          environment);
    } else {
      return PartialState.applyComparisonOperator(
          pIastBinaryExpression.getOperator(),
          pLeftExpression,
          pRightExpression,
          TruthAssumption.ASSUME_EITHER,
          ApplyEpsilon.EXACT,
          environment);
    }
  }

  private Collection<PartialState> handleArithmeticOperator(
      CBinaryExpression pIastBinaryExpression,
      Collection<PartialState> leftExpressions,
      Collection<PartialState> rightExpressions) {
    final BinaryOperator operator;

    switch (pIastBinaryExpression.getOperator()) {
      case PLUS:
        operator = BinaryOperator.ADD;
        break;
      case MINUS:
        operator = BinaryOperator.SUBTRACT;
        break;
      case MULTIPLY:
        operator = BinaryOperator.MULTIPLY;
        break;
      case DIVIDE:
        operator = BinaryOperator.DIVIDE;
        break;
      case MODULO:
        operator = BinaryOperator.MODULO;
        break;
      default:
        throw new AssertionError(
            "'" + pIastBinaryExpression.getOperator() + "' is not an arithmetic operation.");
    }

    RoundingType roundingType;
    if (operator != BinaryOperator.MODULO) {
      roundingType = PartialState.convertToRoundingType(pIastBinaryExpression);
    } else {
      roundingType = RoundingType.NONE;
    }

    return PartialState.applyBinaryArithmeticOperator(
        operator, leftExpressions, rightExpressions, roundingType);
  }

  @Override
  public Collection<PartialState> visit(CFunctionCallExpression pIastFunctionCallExpression)
      throws UnrecognizedCodeException {
    throw new UnsupportedOperationException();
  }

  @Override
  public Collection<PartialState> visit(CCastExpression pIastCastExpression)
      throws UnrecognizedCodeException {
    RoundingType roundingType = PartialState.convertToRoundingType(pIastCastExpression);
    Collection<PartialState> operandStates = pIastCastExpression.getOperand().accept(this);
    return PartialState.applyUnaryArithmeticOperator(
        UnaryOperator.CAST, operandStates, roundingType, PartialState.DEFAULT_ROUNDING_MODE);
  }

  @Override
  public Collection<PartialState> visit(CCharLiteralExpression pIastCharLiteralExpression)
      throws UnrecognizedCodeException {
    char value = pIastCharLiteralExpression.getValue();
    Coefficient coeff = MpqScalar.of((int) value);
    PartialState partialState = new PartialState(new ConstantTreeNode(coeff), ImmutableSet.of());
    return ImmutableSet.of(partialState);
  }

  @Override
  public Collection<PartialState> visit(CFloatLiteralExpression pIastFloatLiteralExpression)
      throws UnrecognizedCodeException {
    BigDecimal decimal = pIastFloatLiteralExpression.getValue();
    ExtendedRational rational = new ExtendedRational(Rational.ofBigDecimal(decimal));
    Coefficient coeff = MpqScalar.of(rational);
    TreeNode constNode = new ConstantTreeNode(coeff);
    PartialState out = new PartialState(constNode, ImmutableSet.of());
    return ImmutableSet.of(out);
  }

  @Override
  public Collection<PartialState> visit(CIntegerLiteralExpression pIastIntegerLiteralExpression)
      throws UnrecognizedCodeException {
    Coefficient coeff = MpqScalar.of(pIastIntegerLiteralExpression.asLong());
    TreeNode constNode = new ConstantTreeNode(coeff);
    PartialState out = new PartialState(constNode, ImmutableSet.of());
    return ImmutableSet.of(out);
  }

  @Override
  public Collection<PartialState> visit(CStringLiteralExpression pIastStringLiteralExpression)
      throws UnrecognizedCodeException {
    return ImmutableSet.of(createUnconstrainedPartialState(false));
  }

  @Override
  public Collection<PartialState> visit(CTypeIdExpression pIastTypeIdExpression)
      throws UnrecognizedCodeException {
    return ImmutableSet.of(createUnconstrainedPartialState(false));
  }

  @Override
  public Collection<PartialState> visit(CUnaryExpression pIastUnaryExpression)
      throws UnrecognizedCodeException {
    CUnaryExpression.UnaryOperator operator = pIastUnaryExpression.getOperator();

    Collection<PartialState> states = pIastUnaryExpression.getOperand().accept(this);
    RoundingType roundingType = PartialState.convertToRoundingType(pIastUnaryExpression);

    switch (operator) {
      case MINUS:
        return PartialState.applyUnaryArithmeticOperator(
            UnaryOperator.NEGATE, states, roundingType, PartialState.DEFAULT_ROUNDING_MODE);
      default:
        TreeNode unconstrained = new ConstantTreeNode(PartialState.UNCONSTRAINED_INTERVAL);
        ImmutableSet.Builder<PartialState> unconstrainedStates = new ImmutableSet.Builder<>();
        for (PartialState state : states) {
          unconstrainedStates.add(new PartialState(unconstrained, state.getConstraints()));
        }
        return unconstrainedStates.build();
    }
  }

  @Override
  public Collection<PartialState> visit(CImaginaryLiteralExpression PIastLiteralExpression)
      throws UnrecognizedCodeException {
    return ImmutableSet.of(createUnconstrainedPartialState(true));
  }

  @Override
  public Collection<PartialState> visit(CAddressOfLabelExpression pAddressOfLabelExpression)
      throws UnrecognizedCodeException {
    return ImmutableSet.of(createUnconstrainedPartialState(true));
  }

  @Override
  public Collection<PartialState> visit(CArraySubscriptExpression pIastArraySubscriptExpression)
      throws UnrecognizedCodeException {
    return ImmutableSet.of(createUnconstrainedPartialState(true));
  }

  @Override
  public Collection<PartialState> visit(CFieldReference pIastFieldReference)
      throws UnrecognizedCodeException {
    return ImmutableSet.of(createUnconstrainedPartialState(true));
  }

  @Override
  public Collection<PartialState> visit(CIdExpression pIastIdExpression)
      throws UnrecognizedCodeException {

    if (pIastIdExpression.getDeclaration().getType() instanceof CSimpleType) {
      Optional<NumericVariable> variable =
          NumericVariable.valueOf(
              pIastIdExpression.getDeclaration(), edge.getSuccessor(), precision, manager, logger);
      if (variable.isPresent()
          && ((variable.get().getSimpleType().getType().isFloatingPointType()
                  && handledTypes.handleReals())
              || (variable.get().getSimpleType().getType().isIntegerType()
                  && handledTypes.handleIntegers()))) {
        TreeNode node = new VariableTreeNode(variable.get());
        PartialState out = new PartialState(node, ImmutableSet.of());
        return ImmutableSet.of(out);
      } else {
        return ImmutableSet.of(
            createUnconstrainedPartialState(variable.get().getSimpleType().isSigned()));
      }
    }
    // Type can not be handled therefore it is substituted with an unconstrained value:
    return ImmutableSet.of(createUnconstrainedPartialState(true));
  }

  private PartialState createUnconstrainedPartialState(boolean isSigned) {
    if (isSigned) {
      return new PartialState(
          new ConstantTreeNode(PartialState.UNCONSTRAINED_INTERVAL), ImmutableSet.of());
    } else {
      return new PartialState(
          new ConstantTreeNode(PartialState.UNSIGNED_UNCONSTRAINED_INTERVAL), ImmutableSet.of());
    }
  }

  @Override
  public Collection<PartialState> visit(CPointerExpression pointerExpression)
      throws UnrecognizedCodeException {
    return ImmutableSet.of(createUnconstrainedPartialState(true));
  }

  @Override
  public Collection<PartialState> visit(CComplexCastExpression complexCastExpression)
      throws UnrecognizedCodeException {
    return ImmutableSet.of(createUnconstrainedPartialState(true));
  }

  /** Defines arithmetic operators that can be interpreted in this visitor. */
  private static final EnumSet<CBinaryExpression.BinaryOperator> arithmeticOperators =
      EnumSet.of(
          CBinaryExpression.BinaryOperator.PLUS,
          CBinaryExpression.BinaryOperator.MINUS,
          CBinaryExpression.BinaryOperator.DIVIDE,
          CBinaryExpression.BinaryOperator.MULTIPLY,
          CBinaryExpression.BinaryOperator.MODULO);

  /** Defines comparison operators that can be interpreted in this visitor. */
  private static final EnumSet<CBinaryExpression.BinaryOperator> comparisonOperators =
      EnumSet.of(
          CBinaryExpression.BinaryOperator.EQUALS,
          CBinaryExpression.BinaryOperator.NOT_EQUALS,
          CBinaryExpression.BinaryOperator.GREATER_EQUAL,
          CBinaryExpression.BinaryOperator.GREATER_EQUAL,
          CBinaryExpression.BinaryOperator.GREATER_THAN,
          CBinaryExpression.BinaryOperator.LESS_EQUAL,
          CBinaryExpression.BinaryOperator.LESS_THAN);

  private boolean checkIsFloatComparison(CBinaryExpression pIastBinaryExpression) {
    if (pIastBinaryExpression.getOperand1().getExpressionType() instanceof CSimpleType
        && pIastBinaryExpression.getOperand2().getExpressionType() instanceof CSimpleType) {
      CSimpleType typeOperand1 =
          (CSimpleType) pIastBinaryExpression.getOperand1().getExpressionType();
      CSimpleType typeOperand2 =
          (CSimpleType) pIastBinaryExpression.getOperand2().getExpressionType();
      return (typeOperand1.getType().isFloatingPointType()
          || typeOperand2.getType().isFloatingPointType());
    } else {
      return false;
    }
  }
}
