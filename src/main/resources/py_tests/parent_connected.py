# from output_parser import *

goodstatus = "Status:Connected"
badstatus = "Status:Failed"
stoppedstatus = "Status:Stopped"

steps = parse("testing-output.txt")
for i in range(0, len(steps)):
    step = steps[i]
    for point in get_all_dsa_points(step.dsa_tree):
        if goodstatus in point.value or badstatus in point.value:
            assert goodstatus in point.parent.value
            assert goodstatus in point.parent.parent.value
    for dev in get_all_dsa_devs(step.dsa_tree):
        if goodstatus in dev.value or badstatus in dev.value:
            assert goodstatus in dev.parent.value
