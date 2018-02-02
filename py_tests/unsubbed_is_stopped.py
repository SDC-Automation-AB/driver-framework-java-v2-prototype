from output_parser import *

unsubprefix = "Unsubscribing from /main/"
goodstatus = "Status:Connected"
stoppedstatus = "Status:Stopped"

steps = parse("../testing-output.txt")
for i in range(0, len(steps)):
    step = steps[i]
    if step.action.startswith(unsubprefix):
        path = step.action[len(unsubprefix):].strip().split("/")
        assert len(path) == 3
        dsapoint = find_in_dsa_tree(step.dsa_tree, path)
        devpoint = find_in_dev_tree(step.dev_tree, path)
        assert dsapoint is not None
        if goodstatus in dsapoint.parent.value and goodstatus in dsapoint.parent.parent.value:
            assert stoppedstatus in dsapoint.value