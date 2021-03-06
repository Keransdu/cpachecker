// This file is part of CPAchecker,
// a tool for configurable software verification:
// https://cpachecker.sosy-lab.org
//
// SPDX-FileCopyrightText: 2007-2020 Dirk Beyer <https://www.sosy-lab.org>
//
// SPDX-License-Identifier: Apache-2.0

import java.lang.Math;

public class NondetAssignment_false_assert {
  
  public static void main(String[] args) {
    int a = (int) (Math.random() * 15);
    int b = a;
    
    a = (int) (Math.random() * 2);

    assert a == b;
  }
}
