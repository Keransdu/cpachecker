./scripts/cpa.sh -timelimit 10s -preprocess -bmc-interpolation -setprop cpa.loopbound.maxLoopIterationsUpperBound=2 -spec sv-comp-reachability example/hand-crafted/even2.c
firefox output/Report.html &