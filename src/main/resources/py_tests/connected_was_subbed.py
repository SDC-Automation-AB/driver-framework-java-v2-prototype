# from output_parser import *

subprefix = "Subscribing to /main/"
unsubprefix = "Unsubscribing from /main/"
goodstatus = "Status:Connected"
badstatus = "Status:Failed"
sbpstatus = "Status:Stopped by Parent"

should_be_subbed = set()
steps = parse("testing-output.txt")
tr = Tracker()
for i in range(len(steps) - 1, -1, -1):
    step = steps[i]

    conn_or_fail = set(
        [point.value.strip().split()[1][:-1] for point in get_all_dsa_points(step.dsa_tree) if goodstatus in point.value or badstatus in point.value or sbpstatus in point.value])

    if i < len(steps) - 1:
        tr.side_test(should_be_subbed.issubset(conn_or_fail), i)

    should_be_subbed = conn_or_fail

    if step.action.startswith(subprefix):
        this_sub = step.action.strip().split("/")[-1]
        test = this_sub in should_be_subbed
        tr.main_test(test, i)
        if test:
            should_be_subbed.remove(this_sub)
    elif step.action.startswith(unsubprefix):
        tr.main_test(step.action.strip().split("/")[-1] not in should_be_subbed, i)

tr.side_test(len(should_be_subbed) == 0, -666)
tr.report()
