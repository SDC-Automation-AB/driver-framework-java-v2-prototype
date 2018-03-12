# from output_parser import*

goodstatus = "Status:Connected"
update_prefix = "Setting point "
tr = Tracker()

steps = parse("testing-output.txt")
for i in range(0, len(steps)):
    step = steps[i]
    act = step.action.strip()
    if act.startswith(update_prefix):
        arr = act.split()
        path = arr[2].split(":")
        devpoint = find_in_dev_tree(step.dev_tree, path)
        val = devpoint.value.split()[-1].strip()
        point = find_in_dsa_tree(step.dsa_tree, path)
        if point is not None and goodstatus in point.value:
            tr.main_test("Value:" + val in point.value, i)

tr.report()