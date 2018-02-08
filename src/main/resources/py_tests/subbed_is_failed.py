# from output_parser import*

subprefix = "Subscribing to /main/"
goodstatus = "Status:Connected"
badstatus = "Status:Failed"

steps = parse("testing-output.txt")
for i in range(0, len(steps)):
    step = steps[i]
    if step.action.startswith(subprefix):
        path = step.action[len(subprefix):].strip().split("/")
        assert len(path) == 3
        dsapoint = find_in_dsa_tree(step.dsa_tree, path)
        devpoint = find_in_dev_tree(step.dev_tree, path)
        assert dsapoint is not None
        if devpoint is None and goodstatus in dsapoint.parent.value and goodstatus in dsapoint.parent.parent.value:
            assert badstatus in dsapoint.value