from output_parser import *

goodstatus = "Status:Connected"
update_prefix = "Setting point "

steps = parse("../testing-output.txt")
for i in range(0, len(steps)):
    step = steps[i]
    act = step.action.strip()
    if act.startswith(update_prefix):
        arr = act.split()
        path = arr[2].split(":")
        val = arr[4]
        point = find_in_dsa_tree(step.dsa_tree, path)
        if point is not None and goodstatus in point.value:
            assert "Value:" + val in point.value
