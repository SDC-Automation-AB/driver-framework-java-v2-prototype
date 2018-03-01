# from output_parser import *

goodstatus = "Status:Connected"
badstatus = "Status:Failed"
stoppedstatus = "Status:Stopped"

tr = Tracker()
steps = parse("testing-output.txt")
for i in range(0, len(steps)):
    step = steps[i]
    for point in get_all_dsa_points(step.dsa_tree):
        if goodstatus in point.value or badstatus in point.value:
            tr.side_test(goodstatus in point.parent.value, point.value)
            tr.main_test(goodstatus in point.parent.parent.value, point.value)
    for dev in get_all_dsa_devs(step.dsa_tree):
        if goodstatus in dev.value or badstatus in dev.value:
            tr.main_test(goodstatus in dev.parent.value, dev.value)

tr.report()