// This file is part of CPAchecker,
// a tool for configurable software verification:
// https://cpachecker.sosy-lab.org
//
// SPDX-FileCopyrightText: 2007-2020 Dirk Beyer <https://www.sosy-lab.org>
//
// SPDX-License-Identifier: Apache-2.0

public class IfStatement2_true_assert {

  public static void main(String[] args) {
    int n1 = 1;
    int n2 = 2;

    if (n1 == n2) {
      assert false; // not reached

    } else {

    }
  }
}
