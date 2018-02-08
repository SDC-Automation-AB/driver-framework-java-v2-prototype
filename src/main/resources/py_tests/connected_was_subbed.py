# from output_parser import*

subprefix = "Subscribing to /main/"
unsubprefix = "Unsubscribing from /main/"
goodstatus = "Status:Connected"
badstatus = "Status:Failed"
sbpstatus = "Status:Stopped by Parent"

should_be_subbed = set()
steps = parse("testing-output.txt")
for i in range(len(steps) - 1, -1, -1):
    step = steps[i]

    conn_or_fail = set(
        [point.value.strip().split()[1][:-1] for point in get_all_dsa_points(step.dsa_tree) if goodstatus in point.value or badstatus in point.value or sbpstatus in point.value])

    if i < len(steps) - 1:
        assert should_be_subbed.issubset(conn_or_fail)

    should_be_subbed = conn_or_fail

    if step.action.startswith(subprefix):
        should_be_subbed.remove(step.action.strip().split("/")[-1])
    elif step.action.startswith(unsubprefix):
        assert step.action.strip().split("/")[-1] not in should_be_subbed

assert len(should_be_subbed) == 0
