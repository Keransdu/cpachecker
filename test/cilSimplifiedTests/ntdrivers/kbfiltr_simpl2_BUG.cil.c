/* Generated by CIL v. 1.3.6 */
/* print_CIL_Input is true */

#line 4 "kbfiltr_simpl2_BUG.cil.c"
void errorFn(void) ;
#line 5
void _BLAST_init(void) ;
#line 6
void stub_driver_init(void) ;
#line 7
int KbFilter_PnP(int DeviceObject , int Irp ) ;
#line 8
void stubMoreProcessingRequired(void) ;
#line 9
int IofCallDriver(int DeviceObject , int Irp ) ;
#line 10
void IofCompleteRequest(int Irp , int PriorityBoost ) ;
#line 11
int KeSetEvent(int Event , int Increment , int Wait ) ;
#line 12
int KeWaitForSingleObject(int Object , int WaitReason , int WaitMode , int Alertable ,
                          int Timeout ) ;
#line 14
int KbFilter_Complete(int DeviceObject , int Irp , int Context ) ;
#line 15
int KbFilter_CreateClose(int DeviceObject , int Irp ) ;
#line 16
int KbFilter_DispatchPassThrough(int DeviceObject , int Irp ) ;
#line 17
int KbFilter_Power(int DeviceObject , int Irp ) ;
#line 18
int PoCallDriver(int DeviceObject , int Irp ) ;
#line 19
int KbFilter_InternIoCtl(int DeviceObject , int Irp ) ;
#line 20 "kbfiltr_simpl2_BUG.cil.c"
int KernelMode  ;
#line 21 "kbfiltr_simpl2_BUG.cil.c"
int Executive  ;
#line 22 "kbfiltr_simpl2_BUG.cil.c"
int DevicePowerState  =    1;
#line 23 "kbfiltr_simpl2_BUG.cil.c"
void errorFn(void) 
{ 

  {
  goto ERROR;
  ERROR: 
#line 29
  return;
}
}
#line 32 "kbfiltr_simpl2_BUG.cil.c"
int s  ;
#line 33 "kbfiltr_simpl2_BUG.cil.c"
int UNLOADED  ;
#line 34 "kbfiltr_simpl2_BUG.cil.c"
int NP  ;
#line 35 "kbfiltr_simpl2_BUG.cil.c"
int DC  ;
#line 36 "kbfiltr_simpl2_BUG.cil.c"
int SKIP1  ;
#line 37 "kbfiltr_simpl2_BUG.cil.c"
int SKIP2  ;
#line 38 "kbfiltr_simpl2_BUG.cil.c"
int MPR1  ;
#line 39 "kbfiltr_simpl2_BUG.cil.c"
int MPR3  ;
#line 40 "kbfiltr_simpl2_BUG.cil.c"
int IPC  ;
#line 41 "kbfiltr_simpl2_BUG.cil.c"
int pended  ;
#line 42 "kbfiltr_simpl2_BUG.cil.c"
int compFptr  ;
#line 43 "kbfiltr_simpl2_BUG.cil.c"
int compRegistered  ;
#line 44 "kbfiltr_simpl2_BUG.cil.c"
int lowerDriverReturn  ;
#line 45 "kbfiltr_simpl2_BUG.cil.c"
int setEventCalled  ;
#line 46 "kbfiltr_simpl2_BUG.cil.c"
int customIrp  ;
#line 47 "kbfiltr_simpl2_BUG.cil.c"
int myStatus  ;
#line 48 "kbfiltr_simpl2_BUG.cil.c"
void stub_driver_init(void) 
{ 

  {
#line 52
  s = NP;
#line 53
  pended = 0;
#line 54
  compFptr = 0;
#line 55
  compRegistered = 0;
#line 56
  lowerDriverReturn = 0;
#line 57
  setEventCalled = 0;
#line 58
  customIrp = 0;
#line 59
  return;
}
}
#line 62 "kbfiltr_simpl2_BUG.cil.c"
void _BLAST_init(void) 
{ 

  {
#line 66
  UNLOADED = 0;
#line 67
  NP = 1;
#line 68
  DC = 2;
#line 69
  SKIP1 = 3;
#line 70
  SKIP2 = 4;
#line 71
  MPR1 = 5;
#line 72
  MPR3 = 6;
#line 73
  IPC = 7;
#line 74
  s = UNLOADED;
#line 75
  pended = 0;
#line 76
  compFptr = 0;
#line 77
  compRegistered = 0;
#line 78
  lowerDriverReturn = 0;
#line 79
  setEventCalled = 0;
#line 80
  customIrp = 0;
#line 81
  return;
}
}
#line 84 "kbfiltr_simpl2_BUG.cil.c"
int KbFilter_PnP(int DeviceObject , int Irp ) 
{ int devExt ;
  int irpStack ;
  int status ;
  int event ;
  int DeviceObject__DeviceExtension ;
  int Irp__Tail__Overlay__CurrentStackLocation ;
  int irpStack__MinorFunction ;
  int devExt__TopOfStack ;
  int devExt__Started ;
  int devExt__Removed ;
  int devExt__SurpriseRemoved ;
  int Irp__IoStatus__Status ;
  int Irp__IoStatus__Information ;
  int Irp__CurrentLocation ;
  int irpSp ;
  int nextIrpSp ;
  int nextIrpSp__Control ;
  int irpSp___0 ;
  int irpSp__Context ;
  int irpSp__Control ;
  long __cil_tmp23 ;

  {
#line 107
  status = 0;
#line 108
  devExt = DeviceObject__DeviceExtension;
#line 109
  irpStack = Irp__Tail__Overlay__CurrentStackLocation;
#line 110
  if (irpStack__MinorFunction == 0) {
    goto switch_0_0;
  } else {
#line 113
    if (irpStack__MinorFunction == 23) {
      goto switch_0_23;
    } else {
#line 116
      if (irpStack__MinorFunction == 2) {
        goto switch_0_2;
      } else {
#line 119
        if (irpStack__MinorFunction == 1) {
          goto switch_0_1;
        } else {
#line 122
          if (irpStack__MinorFunction == 5) {
            goto switch_0_1;
          } else {
#line 125
            if (irpStack__MinorFunction == 3) {
              goto switch_0_1;
            } else {
#line 128
              if (irpStack__MinorFunction == 6) {
                goto switch_0_1;
              } else {
#line 131
                if (irpStack__MinorFunction == 13) {
                  goto switch_0_1;
                } else {
#line 134
                  if (irpStack__MinorFunction == 4) {
                    goto switch_0_1;
                  } else {
#line 137
                    if (irpStack__MinorFunction == 7) {
                      goto switch_0_1;
                    } else {
#line 140
                      if (irpStack__MinorFunction == 8) {
                        goto switch_0_1;
                      } else {
#line 143
                        if (irpStack__MinorFunction == 9) {
                          goto switch_0_1;
                        } else {
#line 146
                          if (irpStack__MinorFunction == 12) {
                            goto switch_0_1;
                          } else {
#line 149
                            if (irpStack__MinorFunction == 10) {
                              goto switch_0_1;
                            } else {
#line 152
                              if (irpStack__MinorFunction == 11) {
                                goto switch_0_1;
                              } else {
#line 155
                                if (irpStack__MinorFunction == 15) {
                                  goto switch_0_1;
                                } else {
#line 158
                                  if (irpStack__MinorFunction == 16) {
                                    goto switch_0_1;
                                  } else {
#line 161
                                    if (irpStack__MinorFunction == 17) {
                                      goto switch_0_1;
                                    } else {
#line 164
                                      if (irpStack__MinorFunction == 18) {
                                        goto switch_0_1;
                                      } else {
#line 167
                                        if (irpStack__MinorFunction == 19) {
                                          goto switch_0_1;
                                        } else {
#line 170
                                          if (irpStack__MinorFunction == 20) {
                                            goto switch_0_1;
                                          } else {
                                            goto switch_0_1;
#line 175
                                            if (0) {
                                              switch_0_0: 
#line 177
                                              irpSp = Irp__Tail__Overlay__CurrentStackLocation;
#line 178
                                              nextIrpSp = Irp__Tail__Overlay__CurrentStackLocation - 1;
#line 179
                                              nextIrpSp__Control = 0;
#line 180
                                              if (s != NP) {
                                                {
#line 182
                                                errorFn();
                                                }
                                              } else {
#line 185
                                                if (compRegistered != 0) {
                                                  {
#line 187
                                                  errorFn();
                                                  }
                                                } else {
#line 190
                                                  compRegistered = 1;
                                                }
                                              }
                                              {
#line 194
                                              irpSp___0 = Irp__Tail__Overlay__CurrentStackLocation - 1;
#line 195
                                              irpSp__Context = event;
#line 196
                                              irpSp__Control = 0;
#line 197
                                              irpSp__Control = 64;
#line 198
                                              irpSp__Control |= 128;
#line 199
                                              irpSp__Control |= 32;
#line 200
                                              status = IofCallDriver(devExt__TopOfStack,
                                                                     Irp);
                                              }
                                              {
#line 203
                                              __cil_tmp23 = (long )status;
#line 203
                                              if (259L == __cil_tmp23) {
                                                {
#line 205
                                                KeWaitForSingleObject(event, Executive,
                                                                      KernelMode,
                                                                      0, 0);
                                                }
                                              }
                                              }
#line 212
                                              if (status >= 0) {
#line 213
                                                if (myStatus >= 0) {
#line 214
                                                  devExt__Started = 1;
#line 215
                                                  devExt__Removed = 0;
#line 216
                                                  devExt__SurpriseRemoved = 0;
                                                }
                                              }
                                              {
#line 224
                                              Irp__IoStatus__Status = status;
#line 225
                                              myStatus = status;
#line 226
                                              Irp__IoStatus__Information = 0;
#line 227
                                              IofCompleteRequest(Irp, 0);
                                              }
                                              goto switch_0_break;
                                              switch_0_23: 
#line 231
                                              devExt__SurpriseRemoved = 1;
#line 232
                                              if (s == NP) {
#line 233
                                                s = SKIP1;
                                              } else {
                                                {
#line 236
                                                errorFn();
                                                }
                                              }
                                              {
#line 240
                                              Irp__CurrentLocation ++;
#line 241
                                              Irp__Tail__Overlay__CurrentStackLocation ++;
#line 242
                                              status = IofCallDriver(devExt__TopOfStack,
                                                                     Irp);
                                              }
                                              goto switch_0_break;
                                              switch_0_2: 
#line 247
                                              devExt__Removed = 1;
#line 248
                                              if (s == NP) {
#line 249
                                                s = SKIP1;
                                              } else {
                                                {
#line 252
                                                errorFn();
                                                }
                                              }
                                              {
#line 256
                                              Irp__CurrentLocation ++;
#line 257
                                              Irp__Tail__Overlay__CurrentStackLocation ++;
#line 258
                                              IofCallDriver(devExt__TopOfStack, Irp);
#line 259
                                              status = 0;
                                              }
                                              goto switch_0_break;
                                              switch_0_1: ;
#line 281
                                              if (s == NP) {
#line 282
                                                s = SKIP1;
                                              } else {
                                                {
#line 285
                                                errorFn();
                                                }
                                              }
                                              {
#line 289
                                              Irp__CurrentLocation ++;
#line 290
                                              Irp__Tail__Overlay__CurrentStackLocation ++;
#line 291
                                              status = IofCallDriver(devExt__TopOfStack,
                                                                     Irp);
                                              }
                                              goto switch_0_break;
                                            } else {
                                              switch_0_break: ;
                                            }
                                          }
                                        }
                                      }
                                    }
                                  }
                                }
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
#line 320
  return (status);
}
}
#line 323 "kbfiltr_simpl2_BUG.cil.c"
int main(void) 
{ int status ;
  int irp ;
  int pirp ;
  int pirp__IoStatus__Status ;
  int __BLAST_NONDET ;
  int irp_choice ;
  int devobj ;
  int __cil_tmp8 ;

  {
  {
#line 334
  status = 0;
#line 335
  pirp = irp;
#line 336
  _BLAST_init();
  }
#line 338
  if (status >= 0) {
#line 339
    s = NP;
#line 340
    customIrp = 0;
#line 341
    setEventCalled = customIrp;
#line 342
    lowerDriverReturn = setEventCalled;
#line 343
    compRegistered = lowerDriverReturn;
#line 344
    pended = compRegistered;
#line 345
    pirp__IoStatus__Status = 0;
#line 346
    myStatus = 0;
#line 347
    if (irp_choice == 0) {
#line 348
      pirp__IoStatus__Status = -1073741637;
#line 349
      myStatus = -1073741637;
    }
    {
#line 354
    stub_driver_init();
    }
    {
#line 356
    __cil_tmp8 = status >= 0;
#line 356
    if (! __cil_tmp8) {
#line 357
      return (-1);
    }
    }
#line 361
    if (__BLAST_NONDET == 0) {
      goto switch_1_0;
    } else {
#line 364
      if (__BLAST_NONDET == 1) {
        goto switch_1_1;
      } else {
#line 367
        if (__BLAST_NONDET == 3) {
          goto switch_1_3;
        } else {
#line 370
          if (__BLAST_NONDET == 4) {
            goto switch_1_4;
          } else {
#line 373
            if (__BLAST_NONDET == 8) {
              goto switch_1_8;
            } else {
              goto switch_1_default;
#line 378
              if (0) {
                switch_1_0: 
                {
#line 381
                status = KbFilter_CreateClose(devobj, pirp);
                }
                goto switch_1_break;
                switch_1_1: 
                {
#line 386
                status = KbFilter_CreateClose(devobj, pirp);
                }
                goto switch_1_break;
                switch_1_3: 
                {
#line 391
                status = KbFilter_PnP(devobj, pirp);
                }
                goto switch_1_break;
                switch_1_4: 
                {
#line 396
                status = KbFilter_Power(devobj, pirp);
                }
                goto switch_1_break;
                switch_1_8: 
                {
#line 401
                status = KbFilter_InternIoCtl(devobj, pirp);
                }
                goto switch_1_break;
                switch_1_default: ;
#line 405
                return (-1);
              } else {
                switch_1_break: ;
              }
            }
          }
        }
      }
    }
  }
#line 418
  if (pended == 1) {
#line 419
    if (s == NP) {
#line 420
      s = NP;
    } else {
      goto _L___2;
    }
  } else {
    _L___2: 
#line 426
    if (pended == 1) {
#line 427
      if (s == MPR3) {
#line 428
        s = MPR3;
      } else {
        goto _L___1;
      }
    } else {
      _L___1: 
#line 434
      if (! (s == UNLOADED)) {
#line 437
        if (! (status == -1)) {
#line 440
          if (s != SKIP2) {
#line 441
            if (s != IPC) {
#line 442
              if (! (s != DC)) {
                goto _L___0;
              }
            } else {
              goto _L___0;
            }
          } else {
            _L___0: 
#line 452
            if (pended == 1) {
#line 453
              if (status != 259) {
                {
#line 455
                errorFn();
                }
              }
            } else {
#line 461
              if (s == DC) {
#line 462
                if (status == 259) {

                }
              } else {
#line 468
                if (status != lowerDriverReturn) {
                  {
#line 470
                  errorFn();
                  }
                }
              }
            }
          }
        }
      }
    }
  }
#line 482
  return (status);
}
}
#line 485 "kbfiltr_simpl2_BUG.cil.c"
void stubMoreProcessingRequired(void) 
{ 

  {
#line 489
  if (s == NP) {
#line 490
    s = MPR1;
  } else {
    {
#line 493
    errorFn();
    }
  }
#line 496
  return;
}
}
#line 499 "kbfiltr_simpl2_BUG.cil.c"
int IofCallDriver(int DeviceObject , int Irp ) 
{ int __BLAST_NONDET ;
  int returnVal2 ;
  int compRetStatus ;
  int lcontext ;
  long long __cil_tmp7 ;

  {
#line 506
  if (compRegistered) {
    {
#line 508
    compRetStatus = KbFilter_Complete(DeviceObject, Irp, lcontext);
    }
    {
#line 510
    __cil_tmp7 = (long long )compRetStatus;
#line 510
    if (__cil_tmp7 == 3221225494LL) {
      {
#line 512
      stubMoreProcessingRequired();
      }
    }
    }
  }
#line 520
  if (__BLAST_NONDET == 0) {
    goto switch_2_0;
  } else {
#line 523
    if (__BLAST_NONDET == 1) {
      goto switch_2_1;
    } else {
      goto switch_2_default;
#line 528
      if (0) {
        switch_2_0: 
#line 530
        returnVal2 = 0;
        goto switch_2_break;
        switch_2_1: 
#line 533
        returnVal2 = -1073741823;
        goto switch_2_break;
        switch_2_default: 
#line 536
        returnVal2 = 259;
        goto switch_2_break;
      } else {
        switch_2_break: ;
      }
    }
  }
#line 544
  if (s == NP) {
#line 545
    s = IPC;
#line 546
    lowerDriverReturn = returnVal2;
  } else {
#line 548
    if (s == MPR1) {
#line 549
      if (returnVal2 == 259) {
#line 550
        s = MPR3;
#line 551
        lowerDriverReturn = returnVal2;
      } else {
#line 553
        s = NP;
#line 554
        lowerDriverReturn = returnVal2;
      }
    } else {
#line 557
      if (s == SKIP1) {
#line 558
        s = SKIP2;
#line 559
        lowerDriverReturn = returnVal2;
      } else {
        {
#line 562
        errorFn();
        }
      }
    }
  }
#line 567
  return (returnVal2);
}
}
#line 570 "kbfiltr_simpl2_BUG.cil.c"
void IofCompleteRequest(int Irp , int PriorityBoost ) 
{ 

  {
#line 574
  if (s == NP) {
#line 575
    s = DC;
  } else {
    {
#line 578
    errorFn();
    }
  }
#line 581
  return;
}
}
#line 584 "kbfiltr_simpl2_BUG.cil.c"
int KeSetEvent(int Event , int Increment , int Wait ) 
{ int l ;

  {
#line 588
  setEventCalled = 1;
#line 589
  return (l);
}
}
#line 592 "kbfiltr_simpl2_BUG.cil.c"
int KeWaitForSingleObject(int Object , int WaitReason , int WaitMode , int Alertable ,
                          int Timeout ) 
{ int __BLAST_NONDET ;

  {
#line 597
  if (s == MPR3) {
#line 598
    if (setEventCalled == 1) {
#line 599
      s = NP;
#line 600
      setEventCalled = 0;
    } else {
      goto _L;
    }
  } else {
    _L: 
#line 606
    if (customIrp == 1) {
#line 607
      s = NP;
#line 608
      customIrp = 0;
    } else {
#line 610
      if (s == MPR3) {
        {
#line 612
        errorFn();
        }
      }
    }
  }
#line 619
  if (__BLAST_NONDET == 0) {
    goto switch_3_0;
  } else {
    goto switch_3_default;
#line 624
    if (0) {
      switch_3_0: 
#line 626
      return (0);
      switch_3_default: ;
#line 628
      return (-1073741823);
    } else {

    }
  }
}
}
#line 636 "kbfiltr_simpl2_BUG.cil.c"
int KbFilter_Complete(int DeviceObject , int Irp , int Context ) 
{ int event ;

  {
  {
#line 641
  event = Context;
#line 642
  KeSetEvent(event, 0, 0);
  }
#line 644
  return (-1073741802);
}
}
#line 647 "kbfiltr_simpl2_BUG.cil.c"
int KbFilter_CreateClose(int DeviceObject , int Irp ) 
{ int irpStack__MajorFunction ;
  int devExt__UpperConnectData__ClassService ;
  int Irp__IoStatus__Status ;
  int status ;
  int tmp ;

  {
#line 655
  status = myStatus;
#line 656
  if (irpStack__MajorFunction == 0) {
    goto switch_4_0;
  } else {
#line 659
    if (irpStack__MajorFunction == 2) {
      goto switch_4_2;
    } else {
#line 662
      if (0) {
        switch_4_0: ;
#line 664
        if (0 == devExt__UpperConnectData__ClassService) {
#line 665
          status = -1073741436;
        }
        goto switch_4_break;
        switch_4_2: ;
        goto switch_4_break;
      } else {
        switch_4_break: ;
      }
    }
  }
  {
#line 678
  Irp__IoStatus__Status = status;
#line 679
  myStatus = status;
#line 680
  tmp = KbFilter_DispatchPassThrough(DeviceObject, Irp);
  }
#line 682
  return (tmp);
}
}
#line 685 "kbfiltr_simpl2_BUG.cil.c"
int KbFilter_DispatchPassThrough(int DeviceObject , int Irp ) 
{ int Irp__Tail__Overlay__CurrentStackLocation ;
  int Irp__CurrentLocation ;
  int DeviceObject__DeviceExtension__TopOfStack ;
  int irpStack ;
  int tmp ;

  {
#line 693
  irpStack = Irp__Tail__Overlay__CurrentStackLocation;
#line 694
  if (s == NP) {
#line 695
    s = SKIP1;
  } else {
    {
#line 698
    errorFn();
    }
  }
  {
#line 702
  Irp__CurrentLocation ++;
#line 703
  Irp__Tail__Overlay__CurrentStackLocation ++;
#line 704
  tmp = IofCallDriver(DeviceObject__DeviceExtension__TopOfStack, Irp);
  }
#line 706
  return (tmp);
}
}
#line 709 "kbfiltr_simpl2_BUG.cil.c"
int KbFilter_Power(int DeviceObject , int Irp ) 
{ int irpStack__MinorFunction ;
  int devExt__DeviceState ;
  int powerState__DeviceState ;
  int Irp__CurrentLocation ;
  int Irp__Tail__Overlay__CurrentStackLocation ;
  int devExt__TopOfStack ;
  int powerType ;
  int tmp ;

  {
#line 720
  if (irpStack__MinorFunction == 2) {
    goto switch_5_2;
  } else {
#line 723
    if (irpStack__MinorFunction == 1) {
      goto switch_5_1;
    } else {
#line 726
      if (irpStack__MinorFunction == 0) {
        goto switch_5_0;
      } else {
#line 729
        if (irpStack__MinorFunction == 3) {
          goto switch_5_3;
        } else {
          goto switch_5_default;
#line 734
          if (0) {
            switch_5_2: ;
#line 736
            if (powerType == DevicePowerState) {
#line 737
              devExt__DeviceState = powerState__DeviceState;
            }
            switch_5_1: ;
            switch_5_0: ;
            switch_5_3: ;
            switch_5_default: ;
            goto switch_5_break;
          } else {
            switch_5_break: ;
          }
        }
      }
    }
  }
#line 754
  if (s == NP) {
#line 755
    s = SKIP1;
  } else {
    {
#line 758
    errorFn();
    }
  }
  {
#line 762
  Irp__CurrentLocation ++;
#line 763
  Irp__Tail__Overlay__CurrentStackLocation ++;
#line 764
  tmp = PoCallDriver(devExt__TopOfStack, Irp);
  }
#line 766
  return (tmp);
}
}
#line 769 "kbfiltr_simpl2_BUG.cil.c"
int PoCallDriver(int DeviceObject , int Irp ) 
{ int __BLAST_NONDET ;
  int compRetStatus ;
  int returnVal ;
  int lcontext ;
  unsigned long __cil_tmp7 ;
  long __cil_tmp8 ;

  {
#line 776
  if (compRegistered) {
    {
#line 778
    compRetStatus = KbFilter_Complete(DeviceObject, Irp, lcontext);
    }
    {
#line 780
    __cil_tmp7 = (unsigned long )compRetStatus;
#line 780
    if (__cil_tmp7 == 3221225494UL) {
      {
#line 782
      stubMoreProcessingRequired();
      }
    }
    }
  }
#line 790
  if (__BLAST_NONDET == 0) {
    goto switch_6_0;
  } else {
#line 793
    if (__BLAST_NONDET == 1) {
      goto switch_6_1;
    } else {
      goto switch_6_default;
#line 798
      if (0) {
        switch_6_0: 
#line 800
        returnVal = 0;
        goto switch_6_break;
        switch_6_1: 
#line 803
        returnVal = -1073741823;
        goto switch_6_break;
        switch_6_default: 
#line 806
        returnVal = 259;
        goto switch_6_break;
      } else {
        switch_6_break: ;
      }
    }
  }
#line 814
  if (s == NP) {
#line 815
    s = IPC;
#line 816
    lowerDriverReturn = returnVal;
  } else {
#line 818
    if (s == MPR1) {
      {
#line 819
      __cil_tmp8 = (long )returnVal;
#line 819
      if (__cil_tmp8 == 259L) {
#line 820
        s = MPR3;
#line 821
        lowerDriverReturn = returnVal;
      } else {
#line 823
        s = NP;
#line 824
        lowerDriverReturn = returnVal;
      }
      }
    } else {
#line 827
      if (s == SKIP1) {
#line 828
        s = SKIP2;
#line 829
        lowerDriverReturn = returnVal;
      } else {
        {
#line 832
        errorFn();
        }
      }
    }
  }
#line 837
  return (returnVal);
}
}
#line 840 "kbfiltr_simpl2_BUG.cil.c"
int KbFilter_InternIoCtl(int DeviceObject , int Irp ) 
{ int Irp__IoStatus__Information ;
  int irpStack__Parameters__DeviceIoControl__IoControlCode ;
  int devExt__UpperConnectData__ClassService ;
  int irpStack__Parameters__DeviceIoControl__InputBufferLength ;
  int sizeof__CONNECT_DATA ;
  int irpStack__Parameters__DeviceIoControl__Type3InputBuffer ;
  int sizeof__INTERNAL_I8042_HOOK_KEYBOARD ;
  int hookKeyboard__InitializationRoutine ;
  int hookKeyboard__IsrRoutine ;
  int Irp__IoStatus__Status ;
  int hookKeyboard ;
  int connectData ;
  int status ;
  int tmp ;
  int __cil_tmp17 ;
  int __cil_tmp18 ;
  int __cil_tmp19 ;
  int __cil_tmp20 ;
  int __cil_tmp21 ;
  int __cil_tmp22 ;
  int __cil_tmp23 ;
  int __cil_tmp24 ;
  int __cil_tmp25 ;
  int __cil_tmp26 ;
  int __cil_tmp27 ;
  int __cil_tmp28 ;
  int __cil_tmp29 ;
  int __cil_tmp30 ;
  int __cil_tmp31 ;
  int __cil_tmp32 ;
  int __cil_tmp33 ;
  int __cil_tmp34 ;
  int __cil_tmp35 ;
  int __cil_tmp36 ;
  int __cil_tmp37 ;
  int __cil_tmp38 ;
  int __cil_tmp39 ;
  int __cil_tmp40 ;
  int __cil_tmp41 ;
  int __cil_tmp42 ;
  int __cil_tmp43 ;
  int __cil_tmp44 ;
  int __cil_tmp45 ;

  {
#line 857
  status = 0;
#line 858
  Irp__IoStatus__Information = 0;
  {
#line 859
  __cil_tmp17 = 128 << 2;
#line 859
  __cil_tmp18 = 11 << 16;
#line 859
  __cil_tmp19 = __cil_tmp18 | __cil_tmp17;
#line 859
  __cil_tmp20 = __cil_tmp19 | 3;
#line 859
  if (irpStack__Parameters__DeviceIoControl__IoControlCode == __cil_tmp20) {
    goto switch_7_exp_0;
  } else {
    {
#line 862
    __cil_tmp21 = 256 << 2;
#line 862
    __cil_tmp22 = 11 << 16;
#line 862
    __cil_tmp23 = __cil_tmp22 | __cil_tmp21;
#line 862
    __cil_tmp24 = __cil_tmp23 | 3;
#line 862
    if (irpStack__Parameters__DeviceIoControl__IoControlCode == __cil_tmp24) {
      goto switch_7_exp_1;
    } else {
      {
#line 865
      __cil_tmp25 = 4080 << 2;
#line 865
      __cil_tmp26 = 11 << 16;
#line 865
      __cil_tmp27 = __cil_tmp26 | __cil_tmp25;
#line 865
      __cil_tmp28 = __cil_tmp27 | 3;
#line 865
      if (irpStack__Parameters__DeviceIoControl__IoControlCode == __cil_tmp28) {
        goto switch_7_exp_2;
      } else {
        {
#line 868
        __cil_tmp29 = 11 << 16;
#line 868
        if (irpStack__Parameters__DeviceIoControl__IoControlCode == __cil_tmp29) {
          goto switch_7_exp_3;
        } else {
          {
#line 871
          __cil_tmp30 = 32 << 2;
#line 871
          __cil_tmp31 = 11 << 16;
#line 871
          __cil_tmp32 = __cil_tmp31 | __cil_tmp30;
#line 871
          if (irpStack__Parameters__DeviceIoControl__IoControlCode == __cil_tmp32) {
            goto switch_7_exp_4;
          } else {
            {
#line 874
            __cil_tmp33 = 16 << 2;
#line 874
            __cil_tmp34 = 11 << 16;
#line 874
            __cil_tmp35 = __cil_tmp34 | __cil_tmp33;
#line 874
            if (irpStack__Parameters__DeviceIoControl__IoControlCode == __cil_tmp35) {
              goto switch_7_exp_5;
            } else {
              {
#line 877
              __cil_tmp36 = 2 << 2;
#line 877
              __cil_tmp37 = 11 << 16;
#line 877
              __cil_tmp38 = __cil_tmp37 | __cil_tmp36;
#line 877
              if (irpStack__Parameters__DeviceIoControl__IoControlCode == __cil_tmp38) {
                goto switch_7_exp_6;
              } else {
                {
#line 880
                __cil_tmp39 = 8 << 2;
#line 880
                __cil_tmp40 = 11 << 16;
#line 880
                __cil_tmp41 = __cil_tmp40 | __cil_tmp39;
#line 880
                if (irpStack__Parameters__DeviceIoControl__IoControlCode == __cil_tmp41) {
                  goto switch_7_exp_7;
                } else {
                  {
#line 883
                  __cil_tmp42 = 1 << 2;
#line 883
                  __cil_tmp43 = 11 << 16;
#line 883
                  __cil_tmp44 = __cil_tmp43 | __cil_tmp42;
#line 883
                  if (irpStack__Parameters__DeviceIoControl__IoControlCode == __cil_tmp44) {
                    goto switch_7_exp_8;
                  } else {
#line 886
                    if (0) {
                      switch_7_exp_0: ;
#line 888
                      if (devExt__UpperConnectData__ClassService != 0) {
#line 889
                        status = -1073741757;
                        goto switch_7_break;
                      } else {
#line 892
                        if (irpStack__Parameters__DeviceIoControl__InputBufferLength < sizeof__CONNECT_DATA) {
#line 893
                          status = -1073741811;
                          goto switch_7_break;
                        }
                      }
#line 899
                      connectData = irpStack__Parameters__DeviceIoControl__Type3InputBuffer;
                      goto switch_7_break;
                      switch_7_exp_1: 
#line 902
                      status = -1073741822;
                      goto switch_7_break;
                      switch_7_exp_2: ;
#line 905
                      if (irpStack__Parameters__DeviceIoControl__InputBufferLength < sizeof__INTERNAL_I8042_HOOK_KEYBOARD) {
#line 906
                        status = -1073741811;
                        goto switch_7_break;
                      }
#line 911
                      hookKeyboard = irpStack__Parameters__DeviceIoControl__Type3InputBuffer;
#line 912
                      if (hookKeyboard__InitializationRoutine) {

                      }
#line 917
                      if (hookKeyboard__IsrRoutine) {

                      }
#line 922
                      status = 0;
                      goto switch_7_break;
                      switch_7_exp_3: ;
                      switch_7_exp_4: ;
                      switch_7_exp_5: ;
                      switch_7_exp_6: ;
                      switch_7_exp_7: ;
                      switch_7_exp_8: ;
                      goto switch_7_break;
                    } else {
                      switch_7_break: ;
                    }
                  }
                  }
                }
                }
              }
              }
            }
            }
          }
          }
        }
        }
      }
      }
    }
    }
  }
  }
  {
#line 943
  __cil_tmp45 = status >= 0;
#line 943
  if (! __cil_tmp45) {
    {
#line 945
    Irp__IoStatus__Status = status;
#line 946
    myStatus = status;
#line 947
    IofCompleteRequest(Irp, 0);
    }
#line 949
    return (status);
  }
  }
  {
#line 954
  tmp = KbFilter_DispatchPassThrough(DeviceObject, Irp);
  }
#line 956
  return (tmp);
}
}
